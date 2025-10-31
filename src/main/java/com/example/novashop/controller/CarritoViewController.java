package com.example.novashop.controller;

import com.example.novashop.model.Carrito;
import com.example.novashop.security.SecurityUtils;
import com.example.novashop.service.CarritoService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.math.BigDecimal;
import java.util.List;

@Controller
@RequestMapping("/carrito")
@RequiredArgsConstructor
@Slf4j
public class CarritoViewController {

    private final CarritoService carritoService;
    private final SecurityUtils securityUtils;

    @GetMapping
    public String verCarrito(Model model) {
        Long idUsuario = securityUtils.getCurrentUserId()
                .orElseThrow(() -> new RuntimeException("Debe iniciar sesión"));

        List<Carrito> items = carritoService.obtenerCarritoUsuario(idUsuario);
        BigDecimal total = carritoService.calcularTotal(idUsuario);

        model.addAttribute("items", items);
        model.addAttribute("total", total);
        model.addAttribute("titulo", "Mi Carrito");

        return "carrito/ver";
    }

    @PostMapping("/agregar/{idVariante}")
    public String agregarAlCarrito(
            @PathVariable Long idVariante,
            @RequestParam(defaultValue = "1") Integer cantidad,
            @RequestParam(required = false) String from,
            RedirectAttributes redirectAttributes) {

        try {
            Long idUsuario = securityUtils.getCurrentUserId()
                    .orElseThrow(() -> new RuntimeException("Debe iniciar sesión"));

            // Agregar al carrito
            carritoService.agregarAlCarrito(idUsuario, idVariante, cantidad);

            redirectAttributes.addFlashAttribute("mensaje",
                    "Producto agregado al carrito correctamente");

        } catch (RuntimeException e) {
            log.error("Error al agregar al carrito: {}", e.getMessage());
            redirectAttributes.addFlashAttribute("error", e.getMessage());

            // Si viene de detalle de producto, redirigir allí
            if ("detalle".equals(from)) {
                return "redirect:" + redirectAttributes.getAttribute("referer");
            }
        } catch (Exception e) {
            log.error("Error inesperado al agregar al carrito: {}", e.getMessage(), e);
            redirectAttributes.addFlashAttribute("error",
                    "Ocurrió un error inesperado. Por favor, inténtalo de nuevo.");
        }

        return "redirect:/carrito";
    }

    @PostMapping("/actualizar/{idCarrito}")
    public String actualizarCantidad(
            @PathVariable Long idCarrito,
            @RequestParam Integer cantidad,
            RedirectAttributes redirectAttributes) {

        try {
            carritoService.actualizarCantidad(idCarrito, cantidad);
            redirectAttributes.addFlashAttribute("mensaje", "Cantidad actualizada");

        } catch (Exception e) {
            log.error("Error al actualizar cantidad: {}", e.getMessage());
            redirectAttributes.addFlashAttribute("error", e.getMessage());
        }

        return "redirect:/carrito";
    }

    @PostMapping("/eliminar/{idCarrito}")
    public String eliminarDelCarrito(
            @PathVariable Long idCarrito,
            RedirectAttributes redirectAttributes) {

        carritoService.eliminarDelCarrito(idCarrito);
        redirectAttributes.addFlashAttribute("mensaje", "Producto eliminado del carrito");

        return "redirect:/carrito";
    }

    @PostMapping("/limpiar")
    public String limpiarCarrito(RedirectAttributes redirectAttributes) {
        Long idUsuario = securityUtils.getCurrentUserId()
                .orElseThrow(() -> new RuntimeException("Debe iniciar sesión"));

        carritoService.limpiarCarrito(idUsuario);
        redirectAttributes.addFlashAttribute("mensaje", "Carrito vaciado");

        return "redirect:/carrito";
    }
    @PostMapping("/agregar")
    public String agregarAlCarritoDesdeDetalle(
            @RequestParam Long idVariante,
            @RequestParam(defaultValue = "1") Integer cantidad,
            @RequestParam(required = false) Long productoId,
            RedirectAttributes redirectAttributes) {

        try {
            Long idUsuario = securityUtils.getCurrentUserId()
                    .orElseThrow(() -> new RuntimeException("Debe iniciar sesión"));

            // Validar cantidad
            if (cantidad <= 0) {
                throw new IllegalArgumentException("La cantidad debe ser mayor a 0");
            }

            // Agregar al carrito
            carritoService.agregarAlCarrito(idUsuario, idVariante, cantidad);

            redirectAttributes.addFlashAttribute("mensaje",
                    "¡Producto agregado al carrito! (" + cantidad + " unidad" + (cantidad > 1 ? "es" : "") + ")");

            // Redirigir de vuelta al producto si se proporciona el ID
            if (productoId != null) {
                return "redirect:/productos/" + productoId;
            }

        } catch (IllegalArgumentException e) {
            log.warn("Validación fallida al agregar al carrito: {}", e.getMessage());
            redirectAttributes.addFlashAttribute("error", e.getMessage());

            if (productoId != null) {
                return "redirect:/productos/" + productoId;
            }

        } catch (RuntimeException e) {
            log.error("Error al agregar al carrito: {}", e.getMessage());
            redirectAttributes.addFlashAttribute("error", e.getMessage());

            if (productoId != null) {
                return "redirect:/productos/" + productoId;
            }

        } catch (Exception e) {
            log.error("Error inesperado: {}", e.getMessage(), e);
            redirectAttributes.addFlashAttribute("error",
                    "Ocurrió un error inesperado. Por favor, inténtalo de nuevo.");
        }

        return "redirect:/carrito";
    }
}