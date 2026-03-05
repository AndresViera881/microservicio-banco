package com.banco.cliente.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.*;

@Data @Builder @NoArgsConstructor @AllArgsConstructor
public class ClienteDTO {
    private Long id;

    @NotBlank(message = "El nombre es obligatorio")
    private String nombre;
    private String genero;

    @Positive(message = "La edad debe ser positiva")
    private Integer edad;

    @NotBlank(message = "La identificación es obligatoria")
    private String identificacion;
    private String direccion;
    private String telefono;

    @NotBlank(message = "El clienteId es obligatorio")
    private String clienteId;

    @NotBlank(message = "La contraseña es obligatoria")
    private String contrasena;

    @NotNull(message = "El estado es obligatorio")
    private Boolean estado;
}
