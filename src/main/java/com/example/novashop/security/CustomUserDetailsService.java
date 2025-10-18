package com.example.novashop.security;

import com.example.novashop.model.Usuario;
import com.example.novashop.repository.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {

    private final UsuarioRepository usuarioRepository;

    @Override
    @Transactional(readOnly = true)
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        Usuario usuario = usuarioRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException(
                        "Usuario no encontrado con email: " + email));

        // Verificar si está activo
        if (usuario.getEstado() != Usuario.EstadoUsuario.ACTIVO) {
            throw new UsernameNotFoundException("Usuario inactivo o bloqueado");
        }

        return new User(
                usuario.getEmail(),
                usuario.getPassword(),
                getAuthorities(usuario)
        );
    }

    private Collection<? extends GrantedAuthority> getAuthorities(Usuario usuario) {
        List<GrantedAuthority> authorities = new ArrayList<>();

        // Agregar rol
        authorities.add(new SimpleGrantedAuthority("ROLE_" + usuario.getRol().name()));

        // Puedes agregar más permisos específicos aquí si los necesitas

        return authorities;
    }
}