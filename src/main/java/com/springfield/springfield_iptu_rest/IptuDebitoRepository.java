package com.springfield.springfield_iptu_rest;

import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface IptuDebitoRepository extends JpaRepository<IptuDebitoAno, Long> {
    Optional<IptuDebitoAno> findByIdCidadaoAndAnoRef(Integer idCidadao, Integer anoRef);

    boolean existsByIdCidadaoAndAnoRef(Integer idCidadao, Integer anoRef);
}