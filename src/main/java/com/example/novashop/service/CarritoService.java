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
public class CarritoService {

    private final CarritoRepository carritoRepository;
    private final VarianteProductoRepository varianteRepository;

    public List<Carrito> obtenerCarritoUsuario(Long idUsuario) {
        return carritoRepository.findByUsuarioIdUsuario(idUsuario);
    }

    public Carrito agregarAlCarrito(Long idUsuario, Long idVariante, Integer cantidad) {
        log.info("Agregando producto al carrito. Usuario: {}, Variante: {}, Cantidad: {}",
                idUsuario, idVariante, cantidad);

        // Verificar stock disponible
        VarianteProducto variante = varianteRepository.findById(idVariante)
                .orElseThrow(() -> new RuntimeException("Variante no encontrada"));

        if (!variante.hayStock(cantidad)) {
            throw new RuntimeException("Stock insuficiente. Disponible: " + variante.getStock());
        }

        // Verificar si ya existe en el carrito
        Optional<Carrito> carritoExistente = carritoRepository
                .findByUsuarioIdUsuarioAndVarianteIdVariante(idUsuario, idVariante);

        if (carritoExistente.isPresent()) {
            // Actualizar cantidad
            Carrito carrito = carritoExistente.get();
            int nuevaCantidad = carrito.getCantidad() + cantidad;

            if (!variante.hayStock(nuevaCantidad)) {
                throw new RuntimeException("Stock insuficiente para la cantidad solicitada");
            }

            carrito.setCantidad(nuevaCantidad);
            return carritoRepository.save(carrito);
        } else {
            // Crear nuevo item
            Carrito nuevoCarrito = Carrito.builder()
                    .variante(variante)
                    .cantidad(cantidad)
                    .build();
            return carritoRepository.save(nuevoCarrito);
        }
    }

    public Carrito actualizarCantidad(Long idCarrito, Integer cantidad) {
        log.info("Actualizando cantidad del carrito. ID: {}, Nueva cantidad: {}",
                idCarrito, cantidad);

        Carrito carrito = carritoRepository.findById(idCarrito)
                .orElseThrow(() -> new RuntimeException("Item no encontrado en el carrito"));

        // Verificar stock
        if (!carrito.getVariante().hayStock(cantidad)) {
            throw new RuntimeException("Stock insuficiente");
        }

        carrito.setCantidad(cantidad);
        return carritoRepository.save(carrito);
    }

    public void eliminarDelCarrito(Long idCarrito) {
        log.info("Eliminando item del carrito. ID: {}", idCarrito);
        carritoRepository.deleteById(idCarrito);
    }

    public void limpiarCarrito(Long idUsuario) {
        log.info("Limpiando carrito del usuario: {}", idUsuario);
        carritoRepository.deleteByUsuarioIdUsuario(idUsuario);
    }

    public BigDecimal calcularTotal(Long idUsuario) {
        BigDecimal total = carritoRepository.calcularTotalCarrito(idUsuario);
        return total != null ? total : BigDecimal.ZERO;
    }

    public Long contarItems(Long idUsuario) {
        return carritoRepository.contarItemsCarrito(idUsuario);
    }
}
