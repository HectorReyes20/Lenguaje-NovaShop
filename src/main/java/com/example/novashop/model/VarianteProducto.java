package com.example.novashop.model;
import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.*;


import java.math.BigDecimal;

@Entity
@Table(name = "variantes_producto")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class VarianteProducto {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_variante")
    private Long idVariante;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_producto", nullable = false)
    @JsonBackReference("producto-variante")
    private Producto producto;

    @NotBlank(message = "La talla es obligatoria")
    @Size(max = 10)
    @Column(nullable = false, length = 10)
    private String talla;

    @NotBlank(message = "El color es obligatorio")
    @Size(max = 50)
    @Column(nullable = false, length = 50)
    private String color;

    @NotBlank(message = "El SKU es obligatorio")
    @Size(max = 100)
    @Column(name = "codigo_sku", nullable = false, unique = true, length = 100)
    private String codigoSku;

    @Min(0)
    @Column(nullable = false)
    private Integer stock = 0;

    @DecimalMin(value = "0.0")
    @Column(name = "precio_adicional", precision = 10, scale = 2)
    private BigDecimal precioAdicional = BigDecimal.ZERO;

    // Método helper
    public boolean hayStock() {
        return stock != null && stock > 0;
    }

    public boolean hayStock(int cantidad) {
        return stock != null && stock >= cantidad;
    }
}
