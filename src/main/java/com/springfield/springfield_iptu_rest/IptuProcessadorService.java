package com.springfield.springfield_iptu_rest;

import feign.FeignException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.TemporalAdjusters;
import java.util.ArrayList;
import java.util.List;

@Service
public class IptuProcessadorService {
    private final IptuDebitoRepository debitoRepo;
    private final IptuCobrancaRepository cobrancaRepo;
    private final ClienteValidacaoCidadao cidadaoValidador;

    public IptuProcessadorService(IptuDebitoRepository d, IptuCobrancaRepository c, ClienteValidacaoCidadao v) {
        this.debitoRepo = d;
        this.cobrancaRepo = c;
        this.cidadaoValidador = v;
    }

    @Transactional
    public IptuDebitoAno criarDebitoAnual(Integer cidadaoId, Integer ano, String tipo) {
        try {
            cidadaoValidador.verificarCidadaoExiste(cidadaoId);
        } catch (FeignException.NotFound e) {
            throw new RuntimeException("Cidadao nao existe: " + cidadaoId);
        } catch (Exception e) {
            throw new RuntimeException("Erro ao validar cidadao: " + e.getMessage());
        }

        if (debitoRepo.existsByIdCidadaoAndAnoRef(cidadaoId, ano)) {
            throw new RuntimeException("Debito IPTU ja existe.");
        }
        if (!tipo.equalsIgnoreCase("UNICA") && !tipo.equalsIgnoreCase("PARCELADA")) {
            throw new RuntimeException("Tipo invalido.");
        }

        IptuDebitoAno debito = new IptuDebitoAno();
        debito.setIdCidadao(cidadaoId);
        debito.setAnoRef(ano);
        debito.setTipoCobranca(tipo.toUpperCase());
        List<IptuCobrancaMes> cobrancas = new ArrayList<>();
        for (int m = 1; m <= 12; m++) {
            IptuCobrancaMes c = new IptuCobrancaMes();
            c.setMesNumero(m);
            c.setDataLimite(LocalDate.of(ano, m, 1).with(TemporalAdjusters.lastDayOfMonth()));
            c.setValorMes(tipo.equalsIgnoreCase("UNICA") ? (m == 1 ? 1000.0 : 0.0) : 1000.0);
            c.setIptuDebitoAno(debito);
            cobrancas.add(c);
        }
        debito.setCobrancas(cobrancas);
        return debitoRepo.save(debito);
    }

    public StatusIptuDTO checarStatusDebito(Integer cidadaoId, Integer ano) {
        IptuDebitoAno debito = debitoRepo.findByIdCidadaoAndAnoRef(cidadaoId, ano)
                .orElseThrow(() -> new RuntimeException("Debito IPTU nao localizado."));
        List<IptuCobrancaMes> cobrancas = debito.getCobrancas() != null ? debito.getCobrancas() : List.of();
        double pago = cobrancas.stream().filter(IptuCobrancaMes::getStatusPago)
                .mapToDouble(IptuCobrancaMes::getValorMes).sum();
        double total = cobrancas.stream().mapToDouble(IptuCobrancaMes::getValorMes).sum();
        return new StatusIptuDTO(pago, total - pago, cobrancas);
    }

    @Transactional
    public IptuCobrancaMes marcarCobrancaPaga(Long cobrancaId) {
        IptuCobrancaMes cobranca = cobrancaRepo.findById(cobrancaId)
                .orElseThrow(() -> new RuntimeException("Cobranca IPTU nao encontrada."));
        if (cobranca.getStatusPago()) {
            throw new RuntimeException("Cobranca ja esta paga.");
        }
        cobranca.setStatusPago(true);
        cobranca.setDataPagamento(LocalDateTime.now());
        return cobrancaRepo.save(cobranca);
    }
}