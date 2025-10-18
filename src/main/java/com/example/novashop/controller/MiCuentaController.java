package com.example.novashop.controller;
import com.example.novashop.model.Usuario;
import com.example.novashop.security.SecurityUtils;
import com.example.novashop.service.UsuarioService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
@Controller
@RequestMapping("/mi-cuenta")
@RequiredArgsConstructor
@Slf4j
public class MiCuentaController {

    private final UsuarioService usuarioService;
    private final SecurityUtils securityUtils;
    private final com.example.novashop.service.PedidoService pedidoService;
    private final com.example.novashop.service.DireccionService direccionService;

    @GetMapping
    public String miCuenta(Model model) {
        Usuario usuario = securityUtils.getCurrentUser()
                .orElseThrow(() -> new RuntimeException("Usuario no autenticado"));

        model.addAttribute("usuario", usuario);
        return "cuenta/perfil";
    }

    @GetMapping("/editar")
    public String mostrarEditarPerfil(Model model) {
        Usuario usuario = securityUtils.getCurrentUser()
                .orElseThrow(() -> new RuntimeException("Usuario no autenticado"));

        model.addAttribute("usuario", usuario);
        return "cuenta/editar-perfil";
    }

    @PostMapping("/editar")
    public String editarPerfil(
            @ModelAttribute("usuario") Usuario usuarioActualizado,
            BindingResult result,
            Model model,
            RedirectAttributes redirectAttributes) {

        // Log para ver qué datos llegaron
        log.info("Datos recibidos en editarPerfil: Nombre={}, Apellido={}, Telefono={}",
                usuarioActualizado.getNombre(),
                usuarioActualizado.getApellido(),
                usuarioActualizado.getTelefono()); // <-- LOG IMPORTANTE

        if (usuarioActualizado.getNombre() == null || usuarioActualizado.getNombre().isBlank()) {
            log.warn("Error manual: El nombre es obligatorio.");
            model.addAttribute("error", "El nombre es obligatorio");
            model.addAttribute("usuario", usuarioActualizado); // Reenviar datos al form
            return "cuenta/editar-perfil";
        }
        if (usuarioActualizado.getApellido() == null || usuarioActualizado.getApellido().isBlank()) {
            log.warn("Error manual: El apellido es obligatorio.");
            model.addAttribute("error", "El apellido es obligatorio");
            model.addAttribute("usuario", usuarioActualizado); // Reenviar datos al form
            return "cuenta/editar-perfil";
        }

        try {
            Long idUsuario = securityUtils.getCurrentUserId()
                    .orElseThrow(() -> new RuntimeException("Usuario no autenticado"));

            log.info("Llamando a usuarioService.actualizar para ID: {}", idUsuario);
            usuarioService.actualizar(idUsuario, usuarioActualizado); // Usamos el método original
            log.info("usuarioService.actualizar completado.");

            redirectAttributes.addFlashAttribute("mensaje", "Perfil actualizado correctamente");
            return "redirect:/mi-cuenta";

        } catch (Exception e) {
            log.error("Error CATCH al actualizar perfil: {}", e.getMessage(), e);
            model.addAttribute("error", "Ocurrió un error al actualizar: " + e.getMessage());
            model.addAttribute("usuario", usuarioActualizado); // Reenviar datos al form
            return "cuenta/editar-perfil";
        }
    }

    @GetMapping("/cambiar-password")
    public String mostrarCambiarPassword() {
        return "cuenta/cambiar-password";
    }

    @PostMapping("/cambiar-password")
    public String cambiarPassword(
            @RequestParam String passwordActual,
            @RequestParam String passwordNueva,
            @RequestParam String confirmarPassword,
            Model model,
            RedirectAttributes redirectAttributes) {

        // Validar que las nuevas contraseñas coincidan
        if (!passwordNueva.equals(confirmarPassword)) {
            model.addAttribute("error", "Las contraseñas nuevas no coinciden");
            return "cuenta/cambiar-password";
        }

        // Validar longitud
        if (passwordNueva.length() < 6) {
            model.addAttribute("error", "La contraseña debe tener al menos 6 caracteres");
            return "cuenta/cambiar-password";
        }

        try {
            Long idUsuario = securityUtils.getCurrentUserId()
                    .orElseThrow(() -> new RuntimeException("Usuario no autenticado"));

            usuarioService.cambiarPassword(idUsuario, passwordActual, passwordNueva);

            redirectAttributes.addFlashAttribute("mensaje", "Contraseña actualizada correctamente");
            return "redirect:/mi-cuenta";

        } catch (Exception e) {
            log.error("Error al cambiar contraseña: {}", e.getMessage());
            model.addAttribute("error", e.getMessage());
            return "cuenta/cambiar-password";
        }
    }

    @GetMapping("/pedidos")
    public String misPedidos(
            @RequestParam(defaultValue = "0") int page,
            Model model) {

        Long idUsuario = securityUtils.getCurrentUserId()
                .orElseThrow(() -> new RuntimeException("Usuario no autenticado"));

        org.springframework.data.domain.Pageable pageable =
                org.springframework.data.domain.PageRequest.of(page, 10);

        org.springframework.data.domain.Page<com.example.novashop.model.Pedido> pedidos =
                pedidoService.obtenerPedidosUsuario(idUsuario, pageable);

        model.addAttribute("pedidos", pedidos);
        return "cuenta/mis-pedidos";
    }

    @GetMapping("/direcciones")
    public String misDirecciones(Model model) {
        Long idUsuario = securityUtils.getCurrentUserId()
                .orElseThrow(() -> new RuntimeException("Usuario no autenticado"));

        var direcciones = direccionService.obtenerDireccionesUsuario(idUsuario);

        model.addAttribute("direcciones", direcciones);
        return "cuenta/mis-direcciones";
    }

    @GetMapping("/direcciones/nueva")
    public String nuevaDireccion(Model model) {
        model.addAttribute("direccion", new com.example.novashop.model.Direccion());
        return "cuenta/direccion-form";
    }

    @PostMapping("/direcciones/guardar")
    public String guardarDireccion(
            @Valid @ModelAttribute("direccion") com.example.novashop.model.Direccion direccion,
            BindingResult result,
            Model model,
            RedirectAttributes redirectAttributes) {

        if (result.hasErrors()) {
            return "cuenta/direccion-form";
        }

        try {
            Usuario usuario = securityUtils.getCurrentUser()
                    .orElseThrow(() -> new RuntimeException("Usuario no autenticado"));

            direccion.setUsuario(usuario);
            direccionService.guardar(direccion);

            redirectAttributes.addFlashAttribute("mensaje", "Dirección guardada correctamente");
            return "redirect:/mi-cuenta/direcciones";

        } catch (Exception e) {
            log.error("Error al guardar dirección: {}", e.getMessage());
            model.addAttribute("error", e.getMessage());
            return "cuenta/direccion-form";
        }
    }

    @GetMapping("/direcciones/editar/{id}")
    public String editarDireccion(@PathVariable Long id, Model model) {
        var direccion = direccionService.obtenerPorId(id)
                .orElseThrow(() -> new RuntimeException("Dirección no encontrada"));

        // Verificar que la dirección pertenezca al usuario actual
        Long idUsuario = securityUtils.getCurrentUserId()
                .orElseThrow(() -> new RuntimeException("Usuario no autenticado"));

        if (!direccion.getUsuario().getIdUsuario().equals(idUsuario)) {
            throw new RuntimeException("No tienes permiso para editar esta dirección");
        }

        model.addAttribute("direccion", direccion);
        return "cuenta/direccion-form";
    }

    @PostMapping("/direcciones/eliminar/{id}")
    public String eliminarDireccion(
            @PathVariable Long id,
            RedirectAttributes redirectAttributes) {

        try {
            // Verificar que pertenezca al usuario actual
            Long idUsuario = securityUtils.getCurrentUserId()
                    .orElseThrow(() -> new RuntimeException("Usuario no autenticado"));

            var direccion = direccionService.obtenerPorId(id)
                    .orElseThrow(() -> new RuntimeException("Dirección no encontrada"));

            if (!direccion.getUsuario().getIdUsuario().equals(idUsuario)) {
                throw new RuntimeException("No tienes permiso para eliminar esta dirección");
            }

            direccionService.eliminar(id);

            redirectAttributes.addFlashAttribute("mensaje", "Dirección eliminada correctamente");

        } catch (Exception e) {
            log.error("Error al eliminar dirección: {}", e.getMessage());
            redirectAttributes.addFlashAttribute("error", e.getMessage());
        }

        return "redirect:/mi-cuenta/direcciones";
    }
}
