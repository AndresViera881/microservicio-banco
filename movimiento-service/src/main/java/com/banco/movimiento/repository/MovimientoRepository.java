package com.banco.movimiento.repository;

import com.banco.movimiento.model.Movimiento;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface MovimientoRepository extends JpaRepository<Movimiento, Long> {

    List<Movimiento> findByNumeroCuentaOrderByFechaDesc(String numeroCuenta);

    @Query("SELECT m FROM Movimiento m WHERE m.numeroCuenta = :nc ORDER BY m.fecha DESC LIMIT 1")
    Optional<Movimiento> findLastByNumeroCuenta(@Param("nc") String numeroCuenta);

    @Query("SELECT m FROM Movimiento m WHERE m.numeroCuenta = :nc " +
           "AND m.fecha BETWEEN :inicio AND :fin ORDER BY m.fecha ASC")
    List<Movimiento> findByNumeroCuentaAndFechaRange(
            @Param("nc") String numeroCuenta,
            @Param("inicio") LocalDateTime inicio,
            @Param("fin") LocalDateTime fin);

    @Query("SELECT m FROM Movimiento m WHERE " +
           "LOWER(m.tipoMovimiento) LIKE LOWER(CONCAT('%',:q,'%')) OR " +
           "LOWER(m.numeroCuenta) LIKE LOWER(CONCAT('%',:q,'%'))")
    List<Movimiento> search(@Param("q") String q);
}
