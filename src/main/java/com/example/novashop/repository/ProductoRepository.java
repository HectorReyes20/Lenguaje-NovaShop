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

@Repository
public interface ProductoRepository extends JpaRepository<Producto, Long> {

    // Búsqueda por estado
    List<Producto> findByEstado(Producto.EstadoProducto estado);

    Page<Producto> findByEstado(Producto.EstadoProducto estado, Pageable pageable);

    // Productos destacados
    List<Producto> findByDestacadoTrue();

    Page<Producto> findByDestacadoTrueAndEstado(Producto.EstadoProducto estado, Pageable pageable);

    // Búsqueda por categoría
    List<Producto> findByCategoriaIdCategoriaAndEstado(Long idCategoria, Producto.EstadoProducto estado);

    Page<Producto> findByCategoriaIdCategoriaAndEstado(Long idCategoria, Producto.EstadoProducto estado, Pageable pageable);

    // Búsqueda por género
    Page<Producto> findByGeneroAndEstado(Producto.GeneroProducto genero, Producto.EstadoProducto estado, Pageable pageable);

    // Búsqueda por marca
    Page<Producto> findByMarcaAndEstado(String marca, Producto.EstadoProducto estado, Pageable pageable);

    // Búsqueda por rango de precio
    @Query("SELECT p FROM Producto p WHERE p.precioBase BETWEEN :min AND :max AND p.estado = :estado")
    Page<Producto> findByRangoPrecio(@Param("min") BigDecimal min, @Param("max") BigDecimal max,
                                     @Param("estado") Producto.EstadoProducto estado, Pageable pageable);

    // Búsqueda general
    @Query("SELECT p FROM Producto p WHERE (LOWER(p.nombre) LIKE LOWER(CONCAT('%', :keyword, '%')) " +
            "OR LOWER(p.descripcion) LIKE LOWER(CONCAT('%', :keyword, '%')) " +
            "OR LOWER(p.marca) LIKE LOWER(CONCAT('%', :keyword, '%'))) " +
            "AND p.estado = :estado")
    Page<Producto> buscarProductos(@Param("keyword") String keyword,
                                   @Param("estado") Producto.EstadoProducto estado,
                                   Pageable pageable);

    // Productos con ofertas
    @Query("SELECT p FROM Producto p WHERE p.precioOferta IS NOT NULL AND p.precioOferta < p.precioBase AND p.estado = :estado")
    Page<Producto> findProductosConOferta(@Param("estado") Producto.EstadoProducto estado, Pageable pageable);

    // Productos más vendidos
    @Query("SELECT p FROM Producto p JOIN DetallePedido dp ON dp.variante.producto.idProducto = p.idProducto " +
            "GROUP BY p.idProducto ORDER BY SUM(dp.cantidad) DESC")
    List<Producto> findProductosMasVendidos(Pageable pageable);

    // Productos relacionados por categoría
    @Query("SELECT p FROM Producto p WHERE p.categoria.idCategoria = :idCategoria " +
            "AND p.idProducto != :idProducto AND p.estado = :estado")
    List<Producto> findProductosRelacionados(@Param("idCategoria") Long idCategoria,
                                             @Param("idProducto") Long idProducto,
                                             @Param("estado") Producto.EstadoProducto estado,
                                             Pageable pageable);
}