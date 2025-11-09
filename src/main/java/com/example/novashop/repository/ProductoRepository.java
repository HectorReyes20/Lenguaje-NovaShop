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

    // --- MÉTODOS PAGINADOS CORREGIDOS CON AMBOS JOIN FETCH ---

    /**
     * ¡SOLUCIÓN!
     * Sobreescribimos el método findAll(Pageable) por defecto para que
     * SIEMPRE cargue las imágenes Y la categoría.
     */
    @Query(value = "SELECT DISTINCT p FROM Producto p " +
            "LEFT JOIN FETCH p.imagenes " +
            "LEFT JOIN FETCH p.categoria",
            countQuery = "SELECT COUNT(p) FROM Producto p")
    @Override
    Page<Producto> findAll(Pageable pageable);


    // Buscar productos activos
    @Query(value = "SELECT DISTINCT p FROM Producto p " +
            "LEFT JOIN FETCH p.imagenes " +
            "LEFT JOIN FETCH p.categoria " +
            "WHERE p.estado = :estado",
            countQuery = "SELECT COUNT(DISTINCT p) FROM Producto p WHERE p.estado = :estado")
    Page<Producto> findByEstado(@Param("estado") Producto.EstadoProducto estado, Pageable pageable);

    // Productos destacados
    @Query(value = "SELECT DISTINCT p FROM Producto p " +
            "LEFT JOIN FETCH p.imagenes " +
            "LEFT JOIN FETCH p.categoria " +
            "WHERE p.destacado = true AND p.estado = :estado",
            countQuery = "SELECT COUNT(DISTINCT p) FROM Producto p " +
                    "WHERE p.destacado = true AND p.estado = :estado")
    Page<Producto> findByDestacadoTrueAndEstado(
            @Param("estado") Producto.EstadoProducto estado, Pageable pageable);

    // Buscar por categoría
    @Query(value = "SELECT DISTINCT p FROM Producto p " +
            "LEFT JOIN FETCH p.imagenes " +
            "LEFT JOIN FETCH p.categoria " +
            "WHERE p.categoria.idCategoria = :idCategoria AND p.estado = :estado",
            countQuery = "SELECT COUNT(DISTINCT p) FROM Producto p " +
                    "WHERE p.categoria.idCategoria = :idCategoria AND p.estado = :estado")
    Page<Producto> findByCategoriaIdCategoriaAndEstado(
            @Param("idCategoria") Long idCategoria,
            @Param("estado") Producto.EstadoProducto estado,
            Pageable pageable);

    // Buscar por género
    @Query(value = "SELECT DISTINCT p FROM Producto p " +
            "LEFT JOIN FETCH p.imagenes " +
            "LEFT JOIN FETCH p.categoria " +
            "WHERE p.genero = :genero AND p.estado = :estado",
            countQuery = "SELECT COUNT(DISTINCT p) FROM Producto p " +
                    "WHERE p.genero = :genero AND p.estado = :estado")
    Page<Producto> findByGeneroAndEstado(
            @Param("genero") Producto.GeneroProducto genero,
            @Param("estado") Producto.EstadoProducto estado,
            Pageable pageable);

    // Buscar productos (por nombre, descripción o marca)
    @Query(value = "SELECT DISTINCT p FROM Producto p " +
            "LEFT JOIN FETCH p.imagenes " +
            "LEFT JOIN FETCH p.categoria " +
            "WHERE (LOWER(p.nombre) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
            "LOWER(p.descripcion) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
            "LOWER(p.marca) LIKE LOWER(CONCAT('%', :keyword, '%'))) AND " +
            "p.estado = :estado",
            countQuery = "SELECT COUNT(DISTINCT p) FROM Producto p " +
                    "WHERE (LOWER(p.nombre) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
                    "LOWER(p.descripcion) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
                    "LOWER(p.marca) LIKE LOWER(CONCAT('%', :keyword, '%'))) AND " +
                    "p.estado = :estado")
    Page<Producto> buscarProductos(@Param("keyword") String keyword,
                                   @Param("estado") Producto.EstadoProducto estado,
                                   Pageable pageable);

    // Productos con oferta
    @Query(value = "SELECT DISTINCT p FROM Producto p " +
            "LEFT JOIN FETCH p.imagenes " +
            "LEFT JOIN FETCH p.categoria " +
            "WHERE p.precioOferta IS NOT NULL AND " +
            "p.precioOferta < p.precioBase AND " +
            "p.estado = :estado",
            countQuery = "SELECT COUNT(DISTINCT p) FROM Producto p " +
                    "WHERE p.precioOferta IS NOT NULL AND " +
                    "p.precioOferta < p.precioBase AND " +
                    "p.estado = :estado")
    Page<Producto> findProductosConOferta(@Param("estado") Producto.EstadoProducto estado,
                                          Pageable pageable);

    // --- MÉTODOS DE LISTA (NO PAGINADOS) CORREGIDOS ---

    // Productos relacionados (misma categoría)
    @Query("SELECT DISTINCT p FROM Producto p " +
            "LEFT JOIN FETCH p.imagenes " +
            "LEFT JOIN FETCH p.categoria " +
            "WHERE p.categoria.idCategoria = :idCategoria AND " +
            "p.idProducto != :idProductoActual AND " +
            "p.estado = :estado " +
            "ORDER BY p.fechaCreacion DESC")
    List<Producto> findProductosRelacionados(@Param("idCategoria") Long idCategoria,
                                             @Param("idProductoActual") Long idProductoActual,
                                             @Param("estado") Producto.EstadoProducto estado,
                                             Pageable pageable);

    // Productos más vendidos
    @Query("SELECT DISTINCT p FROM Producto p " +
            "LEFT JOIN FETCH p.imagenes " +
            "LEFT JOIN FETCH p.categoria " + // <-- AÑADIDO
            "JOIN p.variantes v " +
            "JOIN DetallePedido dp ON dp.variante.idVariante = v.idVariante " +
            "WHERE p.estado = :estado " +
            "GROUP BY p.idProducto, p.nombre, p.descripcion, p.categoria, " +
            "p.precioBase, p.precioOferta, p.marca, p.genero, " +
            "p.material, p.fechaCreacion, p.estado, p.destacado " +
            "ORDER BY SUM(dp.cantidad) DESC")
    List<Producto> findProductosMasVendidos(@Param("estado") Producto.EstadoProducto estado,
                                            Pageable pageable);

    // Productos con stock bajo
    @Query("SELECT DISTINCT p FROM Producto p " +
            "LEFT JOIN FETCH p.imagenes " +
            "LEFT JOIN FETCH p.categoria " + // <-- AÑADIDO
            "JOIN p.variantes v " +
            "WHERE p.estado = :estado AND " +
            "v.stock > 0 AND v.stock < :stockMinimo " +
            "ORDER BY v.stock ASC")
    List<Producto> findProductosStockBajo(@Param("stockMinimo") Integer stockMinimo,
                                          @Param("estado") Producto.EstadoProducto estado,
                                          Pageable pageable);

    // --- MÉTODO PARA EL DASHBOARD (SOLO VARIANTES) ---

    @Query("SELECT DISTINCT p FROM Producto p LEFT JOIN FETCH p.variantes")
    List<Producto> findAllConVariantes();

    // --- MÉTODO SIN CAMBIOS ---
    Long countByCategoriaIdCategoriaAndEstado(Long idCategoria, Producto.EstadoProducto estado);

    long countByCategoriaIdCategoria(Long idCategoria);
}