package com.example.novashop.repository;
import com.example.novashop.model.*;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
@Repository
public interface PedidoRepository extends JpaRepository<Pedido, Long> {

    Optional<Pedido> findByNumeroPedido(String numeroPedido);

    // Pedidos por usuario
    List<Pedido> findByUsuarioIdUsuarioOrderByFechaPedidoDesc(Long idUsuario);

    Page<Pedido> findByUsuarioIdUsuarioOrderByFechaPedidoDesc(Long idUsuario, Pageable pageable);

    // Pedidos por estado
    List<Pedido> findByEstadoOrderByFechaPedidoDesc(Pedido.EstadoPedido estado);

    Page<Pedido> findByEstadoOrderByFechaPedidoDesc(Pedido.EstadoPedido estado, Pageable pageable);

    // Pedidos por usuario y estado
    List<Pedido> findByUsuarioIdUsuarioAndEstado(Long idUsuario, Pedido.EstadoPedido estado);

    // Último pedido del usuario
    Optional<Pedido> findFirstByUsuarioIdUsuarioOrderByFechaPedidoDesc(Long idUsuario);

    // Contar pedidos por estado
    @Query("SELECT COUNT(p) FROM Pedido p WHERE p.estado = :estado")
    Long contarPedidosPorEstado(@Param("estado") Pedido.EstadoPedido estado);

    // Total de ventas
    @Query("SELECT SUM(p.total) FROM Pedido p WHERE p.estado IN :estados")
    BigDecimal calcularTotalVentas(@Param("estados") List<Pedido.EstadoPedido> estados);

    // Generar número de pedido único
    @Query("SELECT COUNT(p) FROM Pedido p")
    Long contarTotalPedidos();

    long countByUsuarioIdUsuario(Long idUsuario);
}