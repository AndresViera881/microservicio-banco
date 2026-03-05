package com.banco.movimiento.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data @Builder @NoArgsConstructor @AllArgsConstructor
public class MovimientoDTO {
    private Long id;

    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime fecha;

    @NotBlank(message = "El tipo de movimiento es obligatorio")
    private String tipoMovimiento;

    @NotNull(message = "El valor es obligatorio")
    private BigDecimal valor;

    private BigDecimal saldo;

    @NotBlank(message = "El número de cuenta es obligatorio")
    private String numeroCuenta;

    private String clienteNombre;
}
