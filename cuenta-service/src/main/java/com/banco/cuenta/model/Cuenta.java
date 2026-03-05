package com.banco.cuenta.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "cuentas", schema = "cuenta_schema")
@Getter @Setter @NoArgsConstructor
public class Cuenta {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "El número de cuenta es obligatorio")
    @Column(name = "numero_cuenta", unique = true, nullable = false, length = 20)
    private String numeroCuenta;

    @NotBlank(message = "El tipo de cuenta es obligatorio")
    @Column(name = "tipo_cuenta", nullable = false, length = 20)
    private String tipoCuenta;

    @NotNull(message = "El saldo inicial es obligatorio")
    @PositiveOrZero(message = "El saldo inicial no puede ser negativo")
    @Column(name = "saldo_inicial", nullable = false, precision = 15, scale = 2)
    private BigDecimal saldoInicial;

    @NotNull(message = "El estado es obligatorio")
    @Column(nullable = false)
    private Boolean estado;

    /**
     * Referencia al cliente por su clienteId lógico.
     * No hay JPA join cross-service: la validación se hace vía REST (ClienteClient).
     */
    @NotBlank(message = "El clienteId es obligatorio")
    @Column(name = "cliente_id", nullable = false, length = 50)
    private String clienteId;
}
