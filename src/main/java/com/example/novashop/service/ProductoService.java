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
public class ProductoService {

    private final ProductoRepository productoRepository;
    private final VarianteProductoRepository varianteRepository;
    private final ImagenProductoRepository imagenRepository;
    private final ResenaRepository resenaRepository;

    public List<Producto> obtenerTodos() {
        return productoRepository.findAll();
    }

    public Page<Producto> obtenerTodosPaginados(Pageable pageable) {
        return productoRepository.findAll(pageable);
    }

    public Optional<Producto> obtenerPorId(Long id) {
        return productoRepository.findById(id);
    }

    public Page<Producto> obtenerProductosActivos(Pageable pageable) {
        return productoRepository.findByEstado(Producto.EstadoProducto.ACTIVO, pageable);
    }

    public Page<Producto> obtenerProductosDestacados(Pageable pageable) {
        return productoRepository.findByDestacadoTrueAndEstado(
                Producto.EstadoProducto.ACTIVO, pageable);
    }

    public Page<Producto> buscarProductos(String keyword, Pageable pageable) {
        return productoRepository.buscarProductos(keyword,
                Producto.EstadoProducto.ACTIVO, pageable);
    }

    public Page<Producto> obtenerPorCategoria(Long idCategoria, Pageable pageable) {
        return productoRepository.findByCategoriaIdCategoriaAndEstado(
                idCategoria, Producto.EstadoProducto.ACTIVO, pageable);
    }

    public Page<Producto> obtenerPorGenero(Producto.GeneroProducto genero, Pageable pageable) {
        return productoRepository.findByGeneroAndEstado(genero,
                Producto.EstadoProducto.ACTIVO, pageable);
    }

    public Page<Producto> obtenerConOfertas(Pageable pageable) {
        return productoRepository.findProductosConOferta(
                Producto.EstadoProducto.ACTIVO, pageable);
    }

    public List<Producto> obtenerRelacionados(Long idProducto, Long idCategoria, Pageable pageable) {
        return productoRepository.findProductosRelacionados(
                idCategoria, idProducto, Producto.EstadoProducto.ACTIVO, pageable);
    }

    public Producto guardar(Producto producto) {
        log.info("Guardando producto: {}", producto.getNombre());
        return productoRepository.save(producto);
    }

    public void eliminar(Long id) {
        log.info("Eliminando producto con ID: {}", id);
        productoRepository.deleteById(id);
    }

    // Obtener calificación promedio del producto
    public Double obtenerCalificacionPromedio(Long idProducto) {
        Double promedio = resenaRepository.calcularPromedioCalificacion(idProducto);
        return promedio != null ? promedio : 0.0;
    }

    // Verificar si hay stock disponible
    public boolean hayStockDisponible(Long idProducto) {
        List<VarianteProducto> variantes = varianteRepository
                .findByProductoIdProductoAndStockGreaterThan(idProducto, 0);
        return !variantes.isEmpty();
    }
}