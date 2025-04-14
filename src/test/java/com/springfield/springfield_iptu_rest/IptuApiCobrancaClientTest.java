package com.springfield.springfield_iptu_rest;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@FeignClient(name = "iptu-cobranca-test", url = "http://localhost:${local.server.port}")
public interface IptuApiCobrancaClientTest {
    @PostMapping("/iptu-cobranca/gerar/{cidadaoId}/{ano}")
    ResponseEntity<IptuDebitoAno> gerarCobranca(@PathVariable Integer cidadaoId, @PathVariable Integer ano,
            @RequestParam String tipo);

    @GetMapping("/iptu-cobranca/status/{cidadaoId}/{ano}")
    ResponseEntity<StatusIptuDTO> verStatus(@PathVariable Integer cidadaoId, @PathVariable Integer ano);

    @PutMapping("/iptu-cobranca/pagar/{cobrancaId}")
    ResponseEntity<IptuCobrancaMes> pagarCobranca(@PathVariable Long cobrancaId);
}