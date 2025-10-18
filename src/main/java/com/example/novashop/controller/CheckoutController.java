package com.example.novashop.controller;
import com.example.novashop.model.*;
import com.example.novashop.security.SecurityUtils;
import com.example.novashop.service.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.math.BigDecimal;
import java.util.List;
@Controller
@RequestMapping("/checkout")
@RequiredArgsConstructor
@Slf4j
public class CheckoutController {

    private final CarritoService carritoService;
    private final PedidoService pedidoService;
    private final DireccionService direccionService;
    private final SecurityUtils securityUtils;

    @GetMapping
    public String mostrarCheckout(Model model) {
        Usuario usuario = securityUtils.getCurrentUser()
                .orElseThrow(() -> new RuntimeException("Debe iniciar sesión"));

        // Verificar que el carrito no esté vacío
        List<Carrito> items = carritoService.obtenerCarritoUsuario(usuario.getIdUsuario());
        if (items.isEmpty()) {
            return "redirect:/carrito?error=vacio";
        }

        // Obtener direcciones del usuario
        List<Direccion> direcciones = direccionService.obtenerDireccionesUsuario(usuario.getIdUsuario());

        // Calcular totales
        BigDecimal subtotal = carritoService.calcularTotal(usuario.getIdUsuario());
        BigDecimal costoEnvio = new BigDecimal("15.00"); // Puedes hacerlo dinámico
        BigDecimal total = subtotal.add(costoEnvio);

        model.addAttribute("items", items);
        model.addAttribute("direcciones", direcciones);
        model.addAttribute("subtotal", subtotal);
        model.addAttribute("costoEnvio", costoEnvio);
        model.addAttribute("total", total);
        model.addAttribute("titulo", "Finalizar Compra");

        return "checkout/checkout";
    }

    @PostMapping("/procesar")
    public String procesarPedido(
            @RequestParam Long idDireccionEnvio,
            @RequestParam String metodoPago,
            @RequestParam(required = false) String codigoCupon,
            @RequestParam BigDecimal costoEnvio,
            RedirectAttributes redirectAttributes) {

        try {
            Long idUsuario = securityUtils.getCurrentUserId()
                    .orElseThrow(() -> new RuntimeException("Debe iniciar sesión"));

            Pedido pedido = pedidoService.crearPedido(
                    idUsuario, idDireccionEnvio, metodoPago, codigoCupon, costoEnvio);

            log.info("Pedido creado exitosamente: {}", pedido.getNumeroPedido());

            redirectAttributes.addFlashAttribute("mensaje",
                    "¡Pedido realizado con éxito! Número de pedido: " + pedido.getNumeroPedido());

            return "redirect:/pedidos/confirmacion/" + pedido.getIdPedido();

        } catch (Exception e) {
            log.error("Error al procesar pedido: {}", e.getMessage());
            redirectAttributes.addFlashAttribute("error", e.getMessage());
            return "redirect:/checkout";
        }
    }
}
