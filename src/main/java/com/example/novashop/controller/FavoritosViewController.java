package com.example.novashop.controller;
import com.example.novashop.model.Favorito;
import com.example.novashop.security.SecurityUtils;
import com.example.novashop.service.FavoritoService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import jakarta.servlet.http.HttpServletRequest; // <-- AÑADIR IMPORT

@Controller
@RequestMapping("/favoritos")
@RequiredArgsConstructor
@Slf4j
public class FavoritosViewController {

    private final FavoritoService favoritoService;
    private final SecurityUtils securityUtils;

    @GetMapping
    public String verFavoritos(
            @RequestParam(defaultValue = "0") int page,
            Model model) {

        Long idUsuario = securityUtils.getCurrentUserId()
                .orElseThrow(() -> new RuntimeException("Debe iniciar sesión"));

        Pageable pageable = PageRequest.of(page, 12);
        Page<Favorito> favoritos = favoritoService.obtenerFavoritosUsuario(idUsuario, pageable);

        model.addAttribute("favoritos", favoritos);
        model.addAttribute("titulo", "Mis Favoritos");

        return "favoritos/lista";
    }

    @PostMapping("/agregar/{idProducto}")
    public String agregarFavorito(
            @PathVariable Long idProducto,
            RedirectAttributes redirectAttributes,
            HttpServletRequest request) { // <-- AÑADIR REQUEST

        try {
            Long idUsuario = securityUtils.getCurrentUserId()
                    .orElseThrow(() -> new RuntimeException("Debe iniciar sesión"));

            favoritoService.agregar(idUsuario, idProducto);
            redirectAttributes.addFlashAttribute("mensaje", "Producto agregado a favoritos");

        } catch (Exception e) {
            log.error("Error al agregar favorito: {}", e.getMessage());
            redirectAttributes.addFlashAttribute("error", e.getMessage());
        }

        // --- CORRECCIÓN ---
        // Redirigir a la página anterior
        String referer = request.getHeader("Referer");
        return "redirect:" + (referer != null ? referer : "/");
        // --- FIN CORRECCIÓN ---
    }

    @PostMapping("/eliminar/{idProducto}")
    public String eliminarFavorito(
            @PathVariable Long idProducto,
            RedirectAttributes redirectAttributes,
            HttpServletRequest request) { // <-- AÑADIR REQUEST

        Long idUsuario = securityUtils.getCurrentUserId()
                .orElseThrow(() -> new RuntimeException("Debe iniciar sesión"));

        favoritoService.eliminar(idUsuario, idProducto);
        redirectAttributes.addFlashAttribute("mensaje", "Producto eliminado de favoritos");

        // --- CORRECCIÓN ---
        // Redirigir a la página anterior
        String referer = request.getHeader("Referer");
        return "redirect:" + (referer != null ? referer : "/");
        // --- FIN CORRECCIÓN ---
    }
}
