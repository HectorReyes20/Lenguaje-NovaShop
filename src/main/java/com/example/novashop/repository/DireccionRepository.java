package com.example.novashop.repository;
import com.example.novashop.model.*;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
@Repository
public interface DireccionRepository extends JpaRepository<Direccion, Long> {

    List<Direccion> findByUsuarioIdUsuario(Long idUsuario);

    Optional<Direccion> findByUsuarioIdUsuarioAndEsPredeterminadaTrue(Long idUsuario);

    List<Direccion> findByUsuarioIdUsuarioAndTipo(Long idUsuario, Direccion.TipoDireccion tipo);

    long countByUsuarioIdUsuario(Long idUsuario);
}
