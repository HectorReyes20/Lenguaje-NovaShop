package com.example.novashop.model;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.math.BigDecimal;
import java.time.LocalDateTime;
@Entity
@Table(name = "carrito", uniqueConstraints = {
        @UniqueConstraint(columnNames = {"id_usuario", "id_variante"})
})
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Carrito {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_carrito")
    private Long idCarrito;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_usuario", nullable = false)
    private Usuario usuario;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_variante", nullable = false)
    private VarianteProducto variante;

    @Min(1)
    @Column(nullable = false)
    private Integer cantidad = 1;

    @CreationTimestamp
    @Column(name = "fecha_agregado", nullable = false, updatable = false)
    private LocalDateTime fechaAgregado;

    // Método helper
    public BigDecimal getSubtotal() {
        if (variante != null && variante.getProducto() != null) {
            BigDecimal precio = variante.getProducto().getPrecioFinal()
                    .add(variante.getPrecioAdicional());
            return precio.multiply(BigDecimal.valueOf(cantidad));
        }
        return BigDecimal.ZERO;
    }
}