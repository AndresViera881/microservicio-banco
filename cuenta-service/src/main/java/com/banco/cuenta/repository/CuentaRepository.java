package com.banco.cuenta.repository;

import com.banco.cuenta.model.Cuenta;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CuentaRepository extends JpaRepository<Cuenta, Long> {
    Optional<Cuenta> findByNumeroCuenta(String numeroCuenta);
    boolean existsByNumeroCuenta(String numeroCuenta);
    List<Cuenta> findByClienteId(String clienteId);

    @Query("SELECT c FROM Cuenta c WHERE " +
           "LOWER(c.numeroCuenta) LIKE LOWER(CONCAT('%',:q,'%')) OR " +
           "LOWER(c.tipoCuenta) LIKE LOWER(CONCAT('%',:q,'%')) OR " +
           "LOWER(c.clienteId) LIKE LOWER(CONCAT('%',:q,'%'))")
    List<Cuenta> search(@Param("q") String q);
}
