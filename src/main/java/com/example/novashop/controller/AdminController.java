package com.example.novashop.controller;
import com.example.novashop.model.*;
import com.example.novashop.service.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.ui.Model;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;


@Controller
@RequestMapping("/admin")
@RequiredArgsConstructor
@Slf4j
public class AdminController {

    private final ProductoService productoService;
    private final PedidoService pedidoService;

    @GetMapping
    public String dashboard(Model model) {
        // Estadísticas básicas
        model.addAttribute("totalVentas", pedidoService.calcularTotalVentas());
        return "admin/dashboard";
    }

    @GetMapping("/productos")
    public String gestionProductos(
            @RequestParam(defaultValue = "0") int page,
            Model model) {

        Pageable pageable = PageRequest.of(page, 20);
        Page<Producto> productos = productoService.obtenerTodosPaginados(pageable);

        model.addAttribute("productos", productos);
        return "admin/productos";
    }

    @GetMapping("/productos/nuevo")
    public String nuevoProducto(Model model) {
        model.addAttribute("producto", new Producto());
        return "admin/producto-form";
    }

    @GetMapping("/productos/editar/{id}")
    public String editarProducto(@PathVariable Long id, Model model) {
        Producto producto = productoService.obtenerPorId(id)
                .orElseThrow(() -> new RuntimeException("Producto no encontrado"));

        model.addAttribute("producto", producto);
        return "admin/producto-form";
    }

    @GetMapping("/pedidos")
    public String gestionPedidos(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(required = false) String estado,
            Model model) {

        Pageable pageable = PageRequest.of(page, 20);
        Page<Pedido> pedidos;

        if (estado != null) {
            Pedido.EstadoPedido estadoPedido = Pedido.EstadoPedido.valueOf(estado);
            pedidos = pedidoService.obtenerPorEstado(estadoPedido, pageable);
        } else {
            // Obtener todos los pedidos (necesitarías agregar este método)
            pedidos = Page.empty();
        }

        model.addAttribute("pedidos", pedidos);
        return "admin/pedidos";
    }

    @GetMapping("/pedidos/{id}")
    public String detallePedido(@PathVariable Long id, Model model) {
        Pedido pedido = pedidoService.obtenerPorId(id)
                .orElseThrow(() -> new RuntimeException("Pedido no encontrado"));

        model.addAttribute("pedido", pedido);
        return "admin/pedido-detalle";
    }
}
