package com.springfield.springfield_iptu_rest;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class StatusIptuDTO {
    private double totalPago;
    private double totalRestante;
    private List<IptuCobrancaMes> listaCobrancas;
}