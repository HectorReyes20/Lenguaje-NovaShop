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
public class ResenaService {

    private final ResenaRepository resenaRepository;
    private final DetallePedidoRepository detallePedidoRepository;

    public Page<Resena> obtenerResenasProducto(Long idProducto, Pageable pageable) {
        return resenaRepository.findByProductoIdProductoOrderByFechaResenaDesc(
                idProducto, pageable);
    }

    public List<Resena> obtenerResenasUsuario(Long idUsuario) {
        return resenaRepository.findByUsuarioIdUsuario(idUsuario);
    }

    public Resena crearResena(Long idUsuario, Long idProducto, Resena resena) {
        log.info("Creando reseña. Usuario: {}, Producto: {}", idUsuario, idProducto);

        // Verificar si ya reseñó
        if (resenaRepository.existsByUsuarioIdUsuarioAndProductoIdProducto(idUsuario, idProducto)) {
            throw new RuntimeException("Ya has reseñado este producto");
        }

        // Verificar si compró el producto
        boolean compro = detallePedidoRepository.usuarioComproProducto(
                idUsuario, idProducto, Pedido.EstadoPedido.ENTREGADO);

        resena.setVerificada(compro);

        return resenaRepository.save(resena);
    }

    public Double obtenerPromedioCalificacion(Long idProducto) {
        Double promedio = resenaRepository.calcularPromedioCalificacion(idProducto);
        return promedio != null ? promedio : 0.0;
    }

    public Long contarResenas(Long idProducto) {
        return resenaRepository.countByProductoIdProducto(idProducto);
    }
}