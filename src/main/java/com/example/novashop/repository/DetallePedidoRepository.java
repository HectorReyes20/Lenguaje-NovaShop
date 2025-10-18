package com.example.novashop.repository;
import com.example.novashop.model.*;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
@Repository
public interface DetallePedidoRepository extends JpaRepository<DetallePedido, Long> {

    List<DetallePedido> findByPedidoIdPedido(Long idPedido);

    // Verificar si un usuario compró un producto (para reseñas verificadas)
    @Query("SELECT CASE WHEN COUNT(dp) > 0 THEN true ELSE false END " +
            "FROM DetallePedido dp " +
            "WHERE dp.pedido.usuario.idUsuario = :idUsuario " +
            "AND dp.variante.producto.idProducto = :idProducto " +
            "AND dp.pedido.estado = :estado")
    boolean usuarioComproProducto(@Param("idUsuario") Long idUsuario,
                                  @Param("idProducto") Long idProducto,
                                  @Param("estado") Pedido.EstadoPedido estado);
}
