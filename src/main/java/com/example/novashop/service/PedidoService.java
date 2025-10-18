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
public class PedidoService {

    private final PedidoRepository pedidoRepository;
    private final DetallePedidoRepository detallePedidoRepository;
    private final CarritoRepository carritoRepository;
    private final VarianteProductoRepository varianteRepository;
    private final CuponRepository cuponRepository;

    public Page<Pedido> obtenerPedidosUsuario(Long idUsuario, Pageable pageable) {
        return pedidoRepository.findByUsuarioIdUsuarioOrderByFechaPedidoDesc(
                idUsuario, pageable);
    }

    public Optional<Pedido> obtenerPorId(Long id) {
        return pedidoRepository.findById(id);
    }

    public Optional<Pedido> obtenerPorNumeroPedido(String numeroPedido) {
        return pedidoRepository.findByNumeroPedido(numeroPedido);
    }

    public Page<Pedido> obtenerPorEstado(Pedido.EstadoPedido estado, Pageable pageable) {
        return pedidoRepository.findByEstadoOrderByFechaPedidoDesc(estado, pageable);
    }

    public Pedido crearPedido(Long idUsuario, Long idDireccionEnvio, String metodoPago,
                              String codigoCupon, BigDecimal costoEnvio) {
        log.info("Creando pedido para usuario: {}", idUsuario);

        // Obtener items del carrito
        List<Carrito> itemsCarrito = carritoRepository.findByUsuarioIdUsuario(idUsuario);

        if (itemsCarrito.isEmpty()) {
            throw new RuntimeException("El carrito está vacío");
        }

        // Calcular subtotal
        BigDecimal subtotal = itemsCarrito.stream()
                .map(Carrito::getSubtotal)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        // Aplicar cupón si existe
        BigDecimal descuento = BigDecimal.ZERO;
        if (codigoCupon != null && !codigoCupon.isEmpty()) {
            Optional<Cupon> cuponOpt = cuponRepository.findByCodigo(codigoCupon);
            if (cuponOpt.isPresent()) {
                Cupon cupon = cuponOpt.get();
                if (cupon.puedeAplicarse(subtotal)) {
                    descuento = cupon.calcularDescuento(subtotal);
                    // Incrementar usos del cupón
                    cupon.setUsosActuales(cupon.getUsosActuales() + 1);
                    cuponRepository.save(cupon);
                }
            }
        }

        // Calcular total
        BigDecimal total = subtotal.add(costoEnvio).subtract(descuento);

        // Generar número de pedido
        String numeroPedido = generarNumeroPedido();

        // Crear pedido
        Pedido pedido = Pedido.builder()
                .numeroPedido(numeroPedido)
                .subtotal(subtotal)
                .costoEnvio(costoEnvio)
                .descuento(descuento)
                .total(total)
                .estado(Pedido.EstadoPedido.PENDIENTE)
                .metodoPago(metodoPago)
                .build();

        pedido = pedidoRepository.save(pedido);

        // Crear detalles del pedido y actualizar stock
        for (Carrito item : itemsCarrito) {
            VarianteProducto variante = item.getVariante();

            // Verificar stock una última vez
            if (!variante.hayStock(item.getCantidad())) {
                throw new RuntimeException("Stock insuficiente para: " +
                        variante.getProducto().getNombre());
            }

            // Crear detalle
            DetallePedido detalle = DetallePedido.builder()
                    .pedido(pedido)
                    .variante(variante)
                    .cantidad(item.getCantidad())
                    .precioUnitario(variante.getProducto().getPrecioFinal())
                    .subtotal(item.getSubtotal())
                    .build();

            detallePedidoRepository.save(detalle);

            // Actualizar stock
            variante.setStock(variante.getStock() - item.getCantidad());
            varianteRepository.save(variante);
        }

        // Limpiar carrito
        carritoRepository.deleteByUsuarioIdUsuario(idUsuario);

        log.info("Pedido creado exitosamente: {}", numeroPedido);
        return pedido;
    }

    public Pedido actualizarEstado(Long idPedido, Pedido.EstadoPedido nuevoEstado) {
        log.info("Actualizando estado del pedido {} a {}", idPedido, nuevoEstado);

        Pedido pedido = pedidoRepository.findById(idPedido)
                .orElseThrow(() -> new RuntimeException("Pedido no encontrado"));

        pedido.setEstado(nuevoEstado);
        return pedidoRepository.save(pedido);
    }

    private String generarNumeroPedido() {
        Long totalPedidos = pedidoRepository.contarTotalPedidos();
        String fecha = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMM"));
        return String.format("NS-%s-%06d", fecha, totalPedidos + 1);
    }

    public BigDecimal calcularTotalVentas() {
        List<Pedido.EstadoPedido> estadosValidos = List.of(
                Pedido.EstadoPedido.CONFIRMADO,
                Pedido.EstadoPedido.PROCESANDO,
                Pedido.EstadoPedido.ENVIADO,
                Pedido.EstadoPedido.ENTREGADO
        );
        BigDecimal total = pedidoRepository.calcularTotalVentas(estadosValidos);
        return total != null ? total : BigDecimal.ZERO;
    }
}