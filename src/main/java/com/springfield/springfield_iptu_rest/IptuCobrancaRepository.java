package com.springfield.springfield_iptu_rest;

import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface IptuCobrancaRepository extends JpaRepository<IptuCobrancaMes, Long> {
    List<IptuCobrancaMes> findByIptuDebitoAnoIdOrderByMesNumeroAsc(Long debitoId);
}
