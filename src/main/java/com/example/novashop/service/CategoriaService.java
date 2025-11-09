package com.example.novashop.service;

import com.example.novashop.model.Categoria;
import com.example.novashop.repository.CategoriaRepository;
import com.example.novashop.repository.ProductoRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class CategoriaService {

    private final CategoriaRepository categoriaRepository;
    private final ProductoRepository productoRepository; // Para contar productos

    // ==========================================================
    // MÉTODOS PARA EL ADMIN DASHBOARD (NUEVOS Y MEJORADOS)
    // ==========================================================

    /**
     * Obtiene una lista paginada de TODAS las categorías para el admin.
     */
    @Transactional(readOnly = true)
    public Page<Categoria> listarCategoriasPaginado(Pageable pageable) {
        return categoriaRepository.findAll(pageable);
    }

    /**
     * Guarda una categoría, ya sea nueva o actualizada.
     */
    public Categoria guardar(Categoria categoria) {
        // Lógica para verificar duplicados al crear
        if (categoria.getIdCategoria() == null) {
            log.info("Guardando NUEVA categoría: {}", categoria.getNombre());
            if (categoriaRepository.existsByNombre(categoria.getNombre())) {
                throw new RuntimeException("Ya existe una categoría con ese nombre");
            }
        } else {
            // Lógica para verificar duplicados al actualizar (opcional)
            log.info("Actualizando categoría con ID: {}", categoria.getIdCategoria());
            Optional<Categoria> existente = categoriaRepository.findByNombre(categoria.getNombre());
            if (existente.isPresent() && !existente.get().getIdCategoria().equals(categoria.getIdCategoria())) {
                throw new RuntimeException("Ya existe OTRA categoría con ese nombre");
            }
        }

        return categoriaRepository.save(categoria);
    }

    /**
     * Cambia el estado de una categoría (Activ_o -> Inactivo, o viceversa).
     * Esta es la forma SEGURA de "eliminar".
     */
    public void cambiarEstado(Long id) {
        Categoria categoria = categoriaRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Categoría no encontrada con ID: " + id));

        if (categoria.getEstado() == Categoria.EstadoCategoria.ACTIVO) {
            categoria.setEstado(Categoria.EstadoCategoria.INACTIVO);
            log.info("Desactivando categoría ID: {}", id);
        } else {
            categoria.setEstado(Categoria.EstadoCategoria.ACTIVO);
            log.info("Activando categoría ID: {}", id);
        }
        // No necesitamos guardar (save) porque @Transactional lo hará por nosotros
    }

    /**
     * Cuenta cuántos productos tiene una categoría.
     */
    @Transactional(readOnly = true)
    public long contarProductosPorCategoria(Long idCategoria) {
        return productoRepository.countByCategoriaIdCategoria(idCategoria);
    }

    // ==========================================================
    // MÉTODOS EXISTENTES (Para el frontend de la tienda)
    // ==========================================================

    @Transactional(readOnly = true)
    public List<Categoria> obtenerTodas() {
        return categoriaRepository.findAll();
    }

    @Transactional(readOnly = true)
    public List<Categoria> obtenerActivas() {
        return categoriaRepository.findByEstado(Categoria.EstadoCategoria.ACTIVO);
    }

    @Transactional(readOnly = true)
    public List<Categoria> obtenerPrincipales() {
        return categoriaRepository.findByCategoriaPadreIsNullAndEstado(
                Categoria.EstadoCategoria.ACTIVO);
    }

    @Transactional(readOnly = true)
    public List<Categoria> obtenerSubcategorias(Long idCategoriaPadre) {
        return categoriaRepository.findByCategoriaPadreIdCategoriaAndEstado(
                idCategoriaPadre, Categoria.EstadoCategoria.ACTIVO);
    }

    @Transactional(readOnly = true)
    public Optional<Categoria> obtenerPorId(Long id) {
        return categoriaRepository.findById(id);
    }
}