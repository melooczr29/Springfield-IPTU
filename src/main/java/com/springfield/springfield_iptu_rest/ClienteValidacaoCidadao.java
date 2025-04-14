package com.springfield.springfield_iptu_rest;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "servico-cidadao", url = "${servico.cidadao.url}")
public interface ClienteValidacaoCidadao {
    @GetMapping("/cidadaos/{id}")
    void verificarCidadaoExiste(@PathVariable("id") Integer id);
}