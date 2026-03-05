package com.banco.cliente.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "personas", schema = "cliente_schema")
@Inheritance(strategy = InheritanceType.JOINED)
@Getter @Setter @NoArgsConstructor
public class Persona {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "El nombre es obligatorio")
    @Column(nullable = false)
    private String nombre;

    @Column(length = 20)
    private String genero;

    @Positive(message = "La edad debe ser un valor positivo")
    private Integer edad;

    @NotBlank(message = "La identificación es obligatoria")
    @Column(unique = true, nullable = false, length = 20)
    private String identificacion;

    private String direccion;

    @Column(length = 20)
    private String telefono;
}
