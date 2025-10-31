package com.example.novashop.model;
import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;
@Entity
@Table(name = "categorias")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Categoria {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_categoria")
    private Long idCategoria;

    @NotBlank(message = "El nombre de la categoría es obligatorio")
    @Size(max = 100)
    @Column(nullable = false, length = 100)
    private String nombre;

    @Column(columnDefinition = "TEXT")
    private String descripcion;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_categoria_padre")
    @JsonBackReference("categoria-padre") // <-- AÑADE ESTA LÍNEA
    private Categoria categoriaPadre;

    @Size(max = 255)
    @Column(name = "imagen_url", length = 255)
    private String imagenUrl;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private EstadoCategoria estado = EstadoCategoria.ACTIVO;

    // Relaciones
    @OneToMany(mappedBy = "categoriaPadre")
    @ToString.Exclude
    @JsonManagedReference("categoria-padre") // <-- AÑADE ESTA LÍNEA
    private List<Categoria> subcategorias = new ArrayList<>();

    @OneToMany(mappedBy = "categoria")
    @ToString.Exclude
    @JsonManagedReference("producto-categoria") // <-- AÑADE ESTA LÍNEA
    private List<Producto> productos = new ArrayList<>();

    // Enum
    public enum EstadoCategoria {
        ACTIVO, INACTIVO
    }
}