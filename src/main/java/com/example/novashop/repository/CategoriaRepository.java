package com.example.novashop.repository;
import com.example.novashop.model.*;

import org.springframework.data.jpa.repository.JpaRepository;

import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
@Repository
public interface CategoriaRepository extends JpaRepository<Categoria, Long> {

    List<Categoria> findByEstado(Categoria.EstadoCategoria estado);

    // Categorías principales (sin padre)
    List<Categoria> findByCategoriaPadreIsNullAndEstado(Categoria.EstadoCategoria estado);

    // Subcategorías de una categoría padre
    List<Categoria> findByCategoriaPadreIdCategoriaAndEstado(Long idCategoriaPadre, Categoria.EstadoCategoria estado);

    Optional<Categoria> findByNombre(String nombre);

    boolean existsByNombre(String nombre);
}