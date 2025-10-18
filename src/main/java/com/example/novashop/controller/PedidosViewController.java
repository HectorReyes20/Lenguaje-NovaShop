package com.example.novashop.controller;
import com.example.novashop.model.Pedido;
import com.example.novashop.security.SecurityUtils;
import com.example.novashop.service.PedidoService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.math.BigDecimal;
import java.util.List;
@Controller
@RequestMapping("/pedidos")
@RequiredArgsConstructor
@Slf4j
public class PedidosViewController {

    private final PedidoService pedidoService;
    private final SecurityUtils securityUtils;

    @GetMapping("/confirmacion/{idPedido}")
    public String confirmacion(@PathVariable Long idPedido, Model model) {
        Pedido pedido = pedidoService.obtenerPorId(idPedido)
                .orElseThrow(() -> new RuntimeException("Pedido no encontrado"));

        // Verificar que el pedido pertenezca al usuario actual
        Long idUsuario = securityUtils.getCurrentUserId()
                .orElseThrow(() -> new RuntimeException("Debe iniciar sesión"));

        if (!pedido.getUsuario().getIdUsuario().equals(idUsuario)) {
            throw new RuntimeException("No tienes permiso para ver este pedido");
        }

        model.addAttribute("pedido", pedido);
        model.addAttribute("titulo", "Confirmación de Pedido");

        return "pedidos/confirmacion";
    }

    @GetMapping("/{idPedido}")
    public String detallePedido(@PathVariable Long idPedido, Model model) {
        Pedido pedido = pedidoService.obtenerPorId(idPedido)
                .orElseThrow(() -> new RuntimeException("Pedido no encontrado"));

        // Verificar permisos
        Long idUsuario = securityUtils.getCurrentUserId()
                .orElseThrow(() -> new RuntimeException("Debe iniciar sesión"));

        if (!pedido.getUsuario().getIdUsuario().equals(idUsuario) && !securityUtils.isAdmin()) {
            throw new RuntimeException("No tienes permiso para ver este pedido");
        }

        model.addAttribute("pedido", pedido);
        model.addAttribute("titulo", "Detalle del Pedido #" + pedido.getNumeroPedido());

        return "pedidos/detalle";
    }

    @GetMapping("/rastrear")
    public String mostrarRastreo() {
        return "pedidos/rastrear";
    }

    @PostMapping("/rastrear")
    public String rastrearPedido(
            @RequestParam String numeroPedido,
            Model model,
            RedirectAttributes redirectAttributes) {

        return pedidoService.obtenerPorNumeroPedido(numeroPedido)
                .map(pedido -> {
                    model.addAttribute("pedido", pedido);
                    model.addAttribute("titulo", "Rastreo de Pedido");
                    return "pedidos/detalle";
                })
                .orElseGet(() -> {
                    redirectAttributes.addFlashAttribute("error",
                            "No se encontró ningún pedido con ese número");
                    return "redirect:/pedidos/rastrear";
                });
    }
}