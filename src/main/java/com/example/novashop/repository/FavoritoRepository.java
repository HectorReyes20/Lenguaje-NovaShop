package com.example.novashop.repository;
import com.example.novashop.model.*;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
@Repository
public interface FavoritoRepository extends JpaRepository<Favorito, Long> {

    List<Favorito> findByUsuarioIdUsuarioOrderByFechaAgregadoDesc(Long idUsuario);

    Page<Favorito> findByUsuarioIdUsuarioOrderByFechaAgregadoDesc(Long idUsuario, Pageable pageable);

    Optional<Favorito> findByUsuarioIdUsuarioAndProductoIdProducto(Long idUsuario, Long idProducto);

    boolean existsByUsuarioIdUsuarioAndProductoIdProducto(Long idUsuario, Long idProducto);

    void deleteByUsuarioIdUsuarioAndProductoIdProducto(Long idUsuario, Long idProducto);

    // Contar favoritos de un usuario
    Long countByUsuarioIdUsuario(Long idUsuario);
}