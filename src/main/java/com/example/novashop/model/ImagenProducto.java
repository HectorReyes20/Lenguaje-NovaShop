package com.example.novashop.model;
import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.*;

@Entity
@Table(name = "imagenes_producto")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ImagenProducto {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_imagen")
    private Long idImagen;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_producto", nullable = false)
    @JsonBackReference("producto-imagen")
    private Producto producto;

    @NotBlank
    @Size(max = 255)
    @Column(name = "url_imagen", nullable = false, length = 255)
    private String urlImagen;

    @Column(nullable = false)
    private Integer orden = 0;

    @Column(name = "es_principal")
    private Boolean esPrincipal = false;
}