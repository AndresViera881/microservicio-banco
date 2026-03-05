package com.banco.cliente.repository;

import com.banco.cliente.model.Cliente;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ClienteRepository extends JpaRepository<Cliente, Long> {
    Optional<Cliente> findByClienteId(String clienteId);
    boolean existsByClienteId(String clienteId);
    boolean existsByIdentificacion(String identificacion);

    @Query("SELECT c FROM Cliente c WHERE " +
           "LOWER(c.nombre) LIKE LOWER(CONCAT('%',:q,'%')) OR " +
           "LOWER(c.clienteId) LIKE LOWER(CONCAT('%',:q,'%')) OR " +
           "LOWER(c.identificacion) LIKE LOWER(CONCAT('%',:q,'%'))")
    List<Cliente> search(@Param("q") String q);
}
