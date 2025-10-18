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

// ============================================
// CONTROLLER: AuthController (Login/Registro)
// ============================================
@Controller
@RequiredArgsConstructor
@Slf4j
public class AuthController {

    private final UsuarioService usuarioService;
    private final SecurityUtils securityUtils;

    @GetMapping("/login")
    public String mostrarLogin(
            @RequestParam(required = false) String error,
            @RequestParam(required = false) String logout,
            Model model) {

        // Si ya está autenticado, redirigir
        if (securityUtils.isAuthenticated()) {
            return "redirect:/";
        }

        if (error != null) {
            model.addAttribute("error", "Email o contraseña incorrectos");
        }

        if (logout != null) {
            model.addAttribute("mensaje", "Has cerrado sesión correctamente");
        }

        return "auth/login";
    }

    @GetMapping("/registro")
    public String mostrarRegistro(Model model) {
        // Si ya está autenticado, redirigir
        if (securityUtils.isAuthenticated()) {
            return "redirect:/";
        }

        model.addAttribute("usuario", new Usuario());
        return "auth/registro";
    }

    @PostMapping("/registro")
    public String registrar(
            @Valid @ModelAttribute("usuario") Usuario usuario,
            BindingResult result,
            @RequestParam String confirmarPassword,
            Model model,
            RedirectAttributes redirectAttributes) {

        // Validar errores
        if (result.hasErrors()) {
            return "auth/registro";
        }

        // Validar que las contraseñas coincidan
        if (!usuario.getPassword().equals(confirmarPassword)) {
            model.addAttribute("error", "Las contraseñas no coinciden");
            return "auth/registro";
        }

        // Validar longitud de contraseña
        if (usuario.getPassword().length() < 6) {
            model.addAttribute("error", "La contraseña debe tener al menos 6 caracteres");
            return "auth/registro";
        }

        try {
            // Registrar usuario
            usuarioService.registrar(usuario);

            log.info("Usuario registrado exitosamente: {}", usuario.getEmail());
            redirectAttributes.addFlashAttribute("mensaje",
                    "¡Registro exitoso! Ahora puedes iniciar sesión");

            return "redirect:/login";

        } catch (RuntimeException e) {
            log.error("Error al registrar usuario: {}", e.getMessage());
            model.addAttribute("error", e.getMessage());
            return "auth/registro";
        }
    }

    @GetMapping("/acceso-denegado")
    public String accesoDenegado() {
        return "error/acceso-denegado";
    }
}
