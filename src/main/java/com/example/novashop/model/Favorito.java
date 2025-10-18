package com.example.novashop.model;
import jakarta.persistence.*;
import lombok.*;
import java.util.Date;
@Entity
@Table(name = "favoritos", uniqueConstraints = {
        @UniqueConstraint(columnNames = {"id_usuario", "id_producto"})
})
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Favorito {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_favorito")
    private Long idFavorito;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_usuario", nullable = false)
    private Usuario usuario;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_producto", nullable = false)
    private Producto producto;
    @Temporal(TemporalType.TIMESTAMP)
    private Date fechaAgregado;

    @PrePersist
    protected void onCreate() {
        this.fechaAgregado = new Date();
    }

}
