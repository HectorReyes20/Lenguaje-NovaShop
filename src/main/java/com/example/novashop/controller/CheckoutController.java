package com.example.novashop.controller;
import com.example.novashop.model.*;
import com.example.novashop.security.SecurityUtils;
import com.example.novashop.service.*;
import jakarta.servlet.http.HttpSession;
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
    // --- 2. INYECTAR SERVICIOS NECESARIOS ---
    private final CuponService cuponService;
    private final HttpSession httpSession; // Para guardar el cupón aplicado
    private static final String CUPON_SESSION_KEY = "cuponAplicado";
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

        // --- 3. LÓGICA DE CUPÓN Y TOTALES (REESCRITA) ---
        BigDecimal subtotal = carritoService.calcularTotal(usuario.getIdUsuario()); //
        BigDecimal costoEnvio = new BigDecimal("15.00"); // TODO: Hacerlo dinámico
        BigDecimal descuento = BigDecimal.ZERO;

        // Buscar si hay un cupón en la sesión
        String codigoCuponEnSesion = (String) httpSession.getAttribute(CUPON_SESSION_KEY);
        Cupon cuponAplicado = null;

        if (codigoCuponEnSesion != null) {
            try {
                // Validar el cupón de la sesión contra el carrito actual
                cuponAplicado = cuponService.validarCupon(codigoCuponEnSesion);
                // Si es válido, calcular el descuento
                descuento = cuponAplicado.calcularDescuento(subtotal);

                // Lógica para Envío Gratis (Ejemplo)
                if (codigoCuponEnSesion.equals("CYBERENVIO")) {
                    costoEnvio = BigDecimal.ZERO;
                    // El descuento del cupón CYBERENVIO podría ser 0 o un valor fijo
                    // Aquí asumimos que solo pone el envío en 0
                }

            } catch (Exception e) {
                // Si el cupón en sesión ya no es válido (ej. cambió el carrito), se quita
                httpSession.removeAttribute(CUPON_SESSION_KEY);
                log.warn("Cupón en sesión '{}' ya no es válido: {}", codigoCuponEnSesion, e.getMessage());
            }
        }

        BigDecimal total = subtotal.subtract(descuento).add(costoEnvio);
        // --- FIN DE LÓGICA DE CUPÓN ---
        model.addAttribute("items", items);
        model.addAttribute("direcciones", direcciones);
        model.addAttribute("subtotal", subtotal);
        model.addAttribute("costoEnvio", costoEnvio);
        model.addAttribute("descuento", descuento); // <-- 4. PASAR DESCUENTO
        model.addAttribute("total", total);         // <-- 4. PASAR TOTAL (con descuento)
        model.addAttribute("cuponAplicado", cuponAplicado); // <-- 4. PASAR CUPÓN
        model.addAttribute("titulo", "Finalizar Compra");

        return "checkout/checkout";
    }
    // --- 5. NUEVO MÉTODO PARA APLICAR CUPÓN ---
    @PostMapping("/aplicar-cupon")
    public String aplicarCupon(@RequestParam("codigo") String codigo,
                               RedirectAttributes redirectAttributes) {
        try {
            // Validar el cupón
            Cupon cupon = cuponService.validarCupon(codigo);
            // Guardar en sesión
            httpSession.setAttribute(CUPON_SESSION_KEY, cupon.getCodigo());
            redirectAttributes.addFlashAttribute("mensaje", "¡Cupón '" + cupon.getCodigo() + "' aplicado!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/checkout"; // Recargar la página
    }

    // --- 6. NUEVO MÉTODO PARA QUITAR CUPÓN ---
    @PostMapping("/quitar-cupon")
    public String quitarCupon(RedirectAttributes redirectAttributes) {
        httpSession.removeAttribute(CUPON_SESSION_KEY);
        redirectAttributes.addFlashAttribute("info", "Cupón quitado.");
        return "redirect:/checkout";
    }

    @PostMapping("/procesar")
    public String procesarPedido(
            @RequestParam Long idDireccionEnvio,
            @RequestParam String metodoPago,
            // @RequestParam(required = false) String codigoCupon, // Ya no lo leemos del form
            @RequestParam BigDecimal costoEnvio, // Este costo ya viene calculado
            RedirectAttributes redirectAttributes) {

        try {
            Long idUsuario = securityUtils.getCurrentUserId()
                    .orElseThrow(() -> new RuntimeException("Debe iniciar sesión"));

            // --- 7. OBTENER CUPÓN DESDE LA SESIÓN ---
            String codigoCupon = (String) httpSession.getAttribute(CUPON_SESSION_KEY);

            Pedido pedido = pedidoService.crearPedido(
                    idUsuario, idDireccionEnvio, metodoPago, codigoCupon, costoEnvio);

            log.info("Pedido creado exitosamente: {}", pedido.getNumeroPedido());

            // Limpiar cupón de la sesión después de usarlo
            httpSession.removeAttribute(CUPON_SESSION_KEY);

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
