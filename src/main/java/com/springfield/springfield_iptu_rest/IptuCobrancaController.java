package com.springfield.springfield_iptu_rest;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/iptu-cobranca")
public class IptuCobrancaController {
    private final IptuProcessadorService processador;

    public IptuCobrancaController(IptuProcessadorService p) {
        this.processador = p;
    }

    @PostMapping("/gerar/{cidadaoId}/{ano}")
    public ResponseEntity<?> gerarCobranca(@PathVariable Integer cidadaoId, @PathVariable Integer ano,
            @RequestParam String tipo) {
        try {
            return ResponseEntity.status(HttpStatus.CREATED).body(processador.criarDebitoAnual(cidadaoId, ano, tipo));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @GetMapping("/status/{cidadaoId}/{ano}")
    public ResponseEntity<?> verStatus(@PathVariable Integer cidadaoId, @PathVariable Integer ano) {
        try {
            return ResponseEntity.ok(processador.checarStatusDebito(cidadaoId, ano));
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }

    @PutMapping("/pagar/{cobrancaId}")
    public ResponseEntity<?> pagarCobranca(@PathVariable Long cobrancaId) {
        try {
            return ResponseEntity.ok(processador.marcarCobrancaPaga(cobrancaId));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
}