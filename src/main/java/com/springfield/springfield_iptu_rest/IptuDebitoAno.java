package com.springfield.springfield_iptu_rest;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class IptuDebitoAno {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private Integer idCidadao;
    private Integer anoRef;
    private String tipoCobranca;
    private Double valorTotal = 12000.0;
    private LocalDateTime dataGeracao = LocalDateTime.now();
    @OneToMany(mappedBy = "iptuDebitoAno", cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    private List<IptuCobrancaMes> cobrancas;
}