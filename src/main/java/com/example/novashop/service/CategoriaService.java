package com.example.novashop.service;
import com.example.novashop.model.*;
import com.example.novashop.repository.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;
@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class CategoriaService {

    private final CategoriaRepository categoriaRepository;

    public List<Categoria> obtenerTodas() {
        return categoriaRepository.findAll();
    }

    public List<Categoria> obtenerActivas() {
        return categoriaRepository.findByEstado(Categoria.EstadoCategoria.ACTIVO);
    }

    public List<Categoria> obtenerPrincipales() {
        return categoriaRepository.findByCategoriaPadreIsNullAndEstado(
                Categoria.EstadoCategoria.ACTIVO);
    }

    public List<Categoria> obtenerSubcategorias(Long idCategoriaPadre) {
        return categoriaRepository.findByCategoriaPadreIdCategoriaAndEstado(
                idCategoriaPadre, Categoria.EstadoCategoria.ACTIVO);
    }

    public Optional<Categoria> obtenerPorId(Long id) {
        return categoriaRepository.findById(id);
    }

    public Categoria guardar(Categoria categoria) {
        log.info("Guardando categoría: {}", categoria.getNombre());

        if (categoriaRepository.existsByNombre(categoria.getNombre())) {
            throw new RuntimeException("Ya existe una categoría con ese nombre");
        }

        return categoriaRepository.save(categoria);
    }

    public Categoria actualizar(Long id, Categoria categoriaActualizada) {
        log.info("Actualizando categoría con ID: {}", id);

        Categoria categoria = categoriaRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Categoría no encontrada"));

        categoria.setNombre(categoriaActualizada.getNombre());
        categoria.setDescripcion(categoriaActualizada.getDescripcion());
        categoria.setImagenUrl(categoriaActualizada.getImagenUrl());

        return categoriaRepository.save(categoria);
    }

    public void eliminar(Long id) {
        log.info("Eliminando categoría con ID: {}", id);
        categoriaRepository.deleteById(id);
    }
}
