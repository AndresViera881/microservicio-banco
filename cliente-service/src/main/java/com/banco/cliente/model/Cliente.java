package com.banco.cliente.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "clientes", schema = "cliente_schema")
@PrimaryKeyJoinColumn(name = "persona_id")
@Getter @Setter @NoArgsConstructor
public class Cliente extends Persona {

    @NotBlank(message = "El clienteId es obligatorio")
    @Column(name = "cliente_id", unique = true, nullable = false, length = 50)
    private String clienteId;

    @NotBlank(message = "La contraseña es obligatoria")
    @Column(nullable = false)
    private String contrasena;

    @NotNull(message = "El estado es obligatorio")
    @Column(nullable = false)
    private Boolean estado;
}
