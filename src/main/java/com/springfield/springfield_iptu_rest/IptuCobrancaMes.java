package com.springfield.springfield_iptu_rest;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class IptuCobrancaMes {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private Integer mesNumero;
    private Double valorMes;
    private LocalDate dataLimite;
    private Boolean statusPago = false;
    private LocalDateTime dataPagamento;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "debito_ano_id")
    @JsonIgnore
    private IptuDebitoAno iptuDebitoAno;
}