package com.example.novashop.repository;

import com.example.novashop.model.*;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor; // <-- 1. IMPORTAR
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;


import java.util.List;
import java.util.Optional;
@Repository
public interface UsuarioRepository extends JpaRepository<Usuario, Long>,
        JpaSpecificationExecutor<Usuario> { // <-- 2. AÑADIR ESTO

    Optional<Usuario> findByEmail(String email);

    boolean existsByEmail(String email);

    List<Usuario> findByEstado(Usuario.EstadoUsuario estado);

    List<Usuario> findByRol(Usuario.RolUsuario rol);

    // Este método ya no es necesario, lo reemplazaremos con Specifications
    // @Query("SELECT u FROM Usuario u WHERE u.nombre LIKE %:keyword% OR u.apellido LIKE %:keyword% OR u.email LIKE %:keyword%")
    // Page<Usuario> buscarUsuarios(@Param("keyword") String keyword, Pageable pageable);
}