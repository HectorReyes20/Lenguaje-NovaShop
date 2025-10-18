package com.example.novashop.repository;
import com.example.novashop.model.*;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
@Repository
public interface CuponRepository extends JpaRepository<Cupon, Long> {

    Optional<Cupon> findByCodigo(String codigo);

    List<Cupon> findByEstado(Cupon.EstadoCupon estado);

    // Cupones activos y vigentes
    @Query("SELECT c FROM Cupon c WHERE c.estado = :estado " +
            "AND c.fechaExpiracion > CURRENT_TIMESTAMP " +
            "AND (c.usosMaximos = 0 OR c.usosActuales < c.usosMaximos)")
    List<Cupon> findCuponesDisponibles(@Param("estado") Cupon.EstadoCupon estado);

    boolean existsByCodigo(String codigo);
}