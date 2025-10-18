package com.example.novashop.model;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.math.BigDecimal;
import java.time.LocalDateTime;
@Entity
@Table(name = "cupones")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Cupon {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_cupon")
    private Long idCupon;

    @NotBlank
    @Size(max = 50)
    @Column(nullable = false, unique = true, length = 50)
    private String codigo;

    @Enumerated(EnumType.STRING)
    @Column(name = "tipo_descuento", nullable = false, length = 20)
    private TipoDescuento tipoDescuento;

    @NotNull
    @DecimalMin(value = "0.0", inclusive = false)
    @Column(name = "valor_descuento", nullable = false, precision = 10, scale = 2)
    private BigDecimal valorDescuento;

    @CreationTimestamp
    @Column(name = "fecha_inicio", nullable = false, updatable = false)
    private LocalDateTime fechaInicio;

    @NotNull
    @Column(name = "fecha_expiracion", nullable = false)
    private LocalDateTime fechaExpiracion;

    @Min(0)
    @Column(name = "usos_maximos")
    private Integer usosMaximos = 0;

    @Min(0)
    @Column(name = "usos_actuales")
    private Integer usosActuales = 0;

    @DecimalMin(value = "0.0")
    @Column(name = "monto_minimo", precision = 10, scale = 2)
    private BigDecimal montoMinimo;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private EstadoCupon estado = EstadoCupon.ACTIVO;

    // Métodos helper
    public boolean esValido() {
        return estado == EstadoCupon.ACTIVO
                && LocalDateTime.now().isBefore(fechaExpiracion)
                && (usosMaximos == 0 || usosActuales < usosMaximos);
    }

    public boolean puedeAplicarse(BigDecimal montoCompra) {
        return esValido() && (montoMinimo == null || montoCompra.compareTo(montoMinimo) >= 0);
    }

    public BigDecimal calcularDescuento(BigDecimal monto) {
        if (!puedeAplicarse(monto)) {
            return BigDecimal.ZERO;
        }

        if (tipoDescuento == TipoDescuento.PORCENTAJE) {
            return monto.multiply(valorDescuento).divide(BigDecimal.valueOf(100));
        } else {
            return valorDescuento;
        }
    }

    public enum TipoDescuento {
        PORCENTAJE, MONTO_FIJO
    }

    public enum EstadoCupon {
        ACTIVO, INACTIVO
    }
}
