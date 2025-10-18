package com.example.novashop.model;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.*;

@Entity
@Table(name = "direcciones")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Direccion {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_direccion")
    private Long idDireccion;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_usuario", nullable = false)
    private Usuario usuario;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private TipoDireccion tipo = TipoDireccion.ENVIO;

    @NotBlank
    @Size(max = 200)
    @Column(name = "nombre_completo", nullable = false, length = 200)
    private String nombreCompleto;

    @NotBlank
    @Size(max = 255)
    @Column(name = "direccion_linea1", nullable = false, length = 255)
    private String direccionLinea1;

    @Size(max = 255)
    @Column(name = "direccion_linea2", length = 255)
    private String direccionLinea2;

    @NotBlank
    @Size(max = 100)
    @Column(nullable = false, length = 100)
    private String ciudad;

    @NotBlank
    @Size(max = 100)
    @Column(nullable = false, length = 100)
    private String departamento;

    @Size(max = 10)
    @Column(name = "codigo_postal", length = 10)
    private String codigoPostal;

    @Size(max = 50)
    @Column(length = 50)
    private String pais = "Perú";

    @NotBlank
    @Size(max = 20)
    @Column(nullable = false, length = 20)
    private String telefono;

    @Column(name = "es_predeterminada")
    private Boolean esPredeterminada = false;

    public enum TipoDireccion {
        ENVIO, FACTURACION, AMBOS
    }
}