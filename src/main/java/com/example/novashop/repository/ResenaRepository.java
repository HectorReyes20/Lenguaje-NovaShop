package com.example.novashop.repository;
import com.example.novashop.model.*;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ResenaRepository extends JpaRepository<Resena, Long> {

    List<Resena> findByProductoIdProductoOrderByFechaResenaDesc(Long idProducto);

    Page<Resena> findByProductoIdProductoOrderByFechaResenaDesc(Long idProducto, Pageable pageable);

    List<Resena> findByUsuarioIdUsuario(Long idUsuario);

    // Promedio de calificación de un producto
    @Query("SELECT AVG(r.calificacion) FROM Resena r WHERE r.producto.idProducto = :idProducto")
    Double calcularPromedioCalificacion(@Param("idProducto") Long idProducto);

    // Contar reseñas por producto
    Long countByProductoIdProducto(Long idProducto);

    // Verificar si usuario ya reseñó un producto
    boolean existsByUsuarioIdUsuarioAndProductoIdProducto(Long idUsuario, Long idProducto);
}