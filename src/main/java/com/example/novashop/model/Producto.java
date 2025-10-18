package com.example.novashop.model;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
@Entity
@Table(name = "productos")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Producto {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_producto")
    private Long idProducto;

    @NotBlank(message = "El nombre del producto es obligatorio")
    @Size(max = 200)
    @Column(nullable = false, length = 200)
    private String nombre;

    @NotBlank(message = "La descripción es obligatoria")
    @Column(nullable = false, columnDefinition = "TEXT")
    private String descripcion;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_categoria", nullable = false)
    private Categoria categoria;

    @NotNull(message = "El precio base es obligatorio")
    @DecimalMin(value = "0.0", inclusive = false)
    @Column(name = "precio_base", nullable = false, precision = 10, scale = 2)
    private BigDecimal precioBase;

    @DecimalMin(value = "0.0", inclusive = false)
    @Column(name = "precio_oferta", precision = 10, scale = 2)
    private BigDecimal precioOferta;

    @Size(max = 100)
    @Column(length = 100)
    private String marca;

    @Enumerated(EnumType.STRING)
    @Column(length = 20)
    private GeneroProducto genero = GeneroProducto.UNISEX;

    @Size(max = 100)
    @Column(length = 100)
    private String material;

    @CreationTimestamp
    @Column(name = "fecha_creacion", nullable = false, updatable = false)
    private LocalDateTime fechaCreacion;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private EstadoProducto estado = EstadoProducto.ACTIVO;

    @Column(nullable = false)
    private Boolean destacado = false;

    // Relaciones
    @OneToMany(mappedBy = "producto", cascade = CascadeType.ALL, orphanRemoval = true)
    @ToString.Exclude
    private List<VarianteProducto> variantes = new ArrayList<>();

    @OneToMany(mappedBy = "producto", cascade = CascadeType.ALL, orphanRemoval = true)
    @ToString.Exclude
    private List<ImagenProducto> imagenes = new ArrayList<>();

    @OneToMany(mappedBy = "producto", cascade = CascadeType.ALL, orphanRemoval = true)
    @ToString.Exclude
    private List<Resena> resenas = new ArrayList<>();

    // Métodos helper
    public BigDecimal getPrecioFinal() {
        return precioOferta != null ? precioOferta : precioBase;
    }

    public boolean tieneOferta() {
        return precioOferta != null && precioOferta.compareTo(precioBase) < 0;
    }

    // Enums
    public enum GeneroProducto {
        HOMBRE, MUJER, UNISEX, NINO, NINA
    }

    public enum EstadoProducto {
        ACTIVO, INACTIVO, AGOTADO
    }
}

