package com.example.novashop.security;

import com.example.novashop.model.Usuario;
import com.example.novashop.repository.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
@RequiredArgsConstructor
public class SecurityUtils {

    private final UsuarioRepository usuarioRepository;

    /**
     * Obtiene el email del usuario autenticado actualmente
     */
    public Optional<String> getCurrentUserEmail() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null || !authentication.isAuthenticated()) {
            return Optional.empty();
        }

        Object principal = authentication.getPrincipal();

        if (principal instanceof UserDetails) {
            return Optional.of(((UserDetails) principal).getUsername());
        } else if (principal instanceof String) {
            return Optional.of((String) principal);
        }

        return Optional.empty();
    }

    /**
     * Obtiene el usuario completo autenticado actualmente
     */
    public Optional<Usuario> getCurrentUser() {
        return getCurrentUserEmail()
                .flatMap(usuarioRepository::findByEmail);
    }

    /**
     * Obtiene el ID del usuario autenticado
     */
    public Optional<Long> getCurrentUserId() {
        return getCurrentUser()
                .map(Usuario::getIdUsuario);
    }

    /**
     * Verifica si el usuario actual es administrador
     */
    public boolean isAdmin() {
        return getCurrentUser()
                .map(user -> user.getRol() == Usuario.RolUsuario.ADMIN)
                .orElse(false);
    }

    /**
     * Verifica si hay un usuario autenticado
     */
    public boolean isAuthenticated() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        return authentication != null
                && authentication.isAuthenticated()
                && !(authentication.getPrincipal() instanceof String);
    }
}
