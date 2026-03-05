package com.banco.movimiento.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "movimientos", schema = "movimiento_schema")
@Getter @Setter @NoArgsConstructor
public class Movimiento {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private LocalDateTime fecha;

    @NotBlank(message = "El tipo de movimiento es obligatorio")
    @Column(name = "tipo_movimiento", nullable = false, length = 20)
    private String tipoMovimiento;

    @NotNull(message = "El valor es obligatorio")
    @Column(nullable = false, precision = 15, scale = 2)
    private BigDecimal valor;

    @Column(nullable = false, precision = 15, scale = 2)
    private BigDecimal saldo;

    /**
     * Referencia lógica al número de cuenta (cross-service, sin JPA join).
     */
    @NotBlank(message = "El número de cuenta es obligatorio")
    @Column(name = "numero_cuenta", nullable = false, length = 20)
    private String numeroCuenta;
}
