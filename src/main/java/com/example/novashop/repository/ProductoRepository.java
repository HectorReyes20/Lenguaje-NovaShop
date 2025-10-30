package com.example.novashop.repository;

import com.example.novashop.model.Producto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProductoRepository extends JpaRepository<Producto, Long> {

    // Buscar productos activos
    Page<Producto> findByEstado(Producto.EstadoProducto estado, Pageable pageable);

    // Productos destacados
    Page<Producto> findByDestacadoTrueAndEstado(
            Producto.EstadoProducto estado, Pageable pageable);

    // Buscar por categoría
    Page<Producto> findByCategoriaIdCategoriaAndEstado(
            Long idCategoria, Producto.EstadoProducto estado, Pageable pageable);

    // Buscar por género
    Page<Producto> findByGeneroAndEstado(
            Producto.GeneroProducto genero, Producto.EstadoProducto estado, Pageable pageable);

    // Buscar productos (por nombre, descripción o marca)
    @Query("SELECT p FROM Producto p WHERE " +
            "(LOWER(p.nombre) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
            "LOWER(p.descripcion) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
            "LOWER(p.marca) LIKE LOWER(CONCAT('%', :keyword, '%'))) AND " +
            "p.estado = :estado")
    Page<Producto> buscarProductos(@Param("keyword") String keyword,
                                   @Param("estado") Producto.EstadoProducto estado,
                                   Pageable pageable);

    // Productos con oferta
    @Query("SELECT p FROM Producto p WHERE " +
            "p.precioOferta IS NOT NULL AND " +
            "p.precioOferta < p.precioBase AND " +
            "p.estado = :estado")
    Page<Producto> findProductosConOferta(@Param("estado") Producto.EstadoProducto estado,
                                          Pageable pageable);

    // Productos relacionados (misma categoría)
    @Query("SELECT p FROM Producto p WHERE " +
            "p.categoria.idCategoria = :idCategoria AND " +
            "p.idProducto != :idProductoActual AND " +
            "p.estado = :estado " +
            "ORDER BY p.fechaCreacion DESC")
    List<Producto> findProductosRelacionados(@Param("idCategoria") Long idCategoria,
                                             @Param("idProductoActual") Long idProductoActual,
                                             @Param("estado") Producto.EstadoProducto estado,
                                             Pageable pageable);

    // Productos más vendidos (necesita tabla de pedidos)
    @Query("SELECT p FROM Producto p " +
            "JOIN p.variantes v " +
            "JOIN DetallePedido dp ON dp.variante.idVariante = v.idVariante " +
            "WHERE p.estado = :estado " +
            "GROUP BY p.idProducto " +
            "ORDER BY SUM(dp.cantidad) DESC")
    List<Producto> findProductosMasVendidos(@Param("estado") Producto.EstadoProducto estado,
                                            Pageable pageable);

    // Contar productos por categoría
    Long countByCategoriaIdCategoriaAndEstado(Long idCategoria, Producto.EstadoProducto estado);

    // Productos con stock bajo
    @Query("SELECT DISTINCT p FROM Producto p " +
            "JOIN p.variantes v " +
            "WHERE p.estado = :estado AND " +
            "v.stock > 0 AND v.stock < :stockMinimo " +
            "ORDER BY v.stock ASC")
    List<Producto> findProductosStockBajo(@Param("stockMinimo") Integer stockMinimo,
                                          @Param("estado") Producto.EstadoProducto estado,
                                          Pageable pageable);
}