package com.example.novashop.service;
import com.example.novashop.model.*;
import com.example.novashop.repository.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class UsuarioService {

    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder; //

    public List<Usuario> obtenerTodos() {
        return usuarioRepository.findAll();
    }

    public Optional<Usuario> obtenerPorId(Long id) {
        return usuarioRepository.findById(id);
    }

    public Optional<Usuario> obtenerPorEmail(String email) {
        return usuarioRepository.findByEmail(email);
    }

    public Usuario registrar(Usuario usuario) {
        log.info("Registrando nuevo usuario: {}", usuario.getEmail());

        if (usuarioRepository.existsByEmail(usuario.getEmail())) {
            throw new RuntimeException("El email ya está registrado");
        }

        // ENCRIPTAR CONTRASEÑA
        usuario.setPassword(passwordEncoder.encode(usuario.getPassword()));

        usuario.setEstado(Usuario.EstadoUsuario.ACTIVO);
        usuario.setRol(Usuario.RolUsuario.CLIENTE);

        return usuarioRepository.save(usuario);
    }

    public Usuario actualizar(Long id, Usuario usuarioActualizado) {
        log.info("Actualizando usuario con ID: {}", id);

        Usuario usuario = usuarioRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        usuario.setNombre(usuarioActualizado.getNombre());
        usuario.setApellido(usuarioActualizado.getApellido());
        usuario.setTelefono(usuarioActualizado.getTelefono());

        return usuarioRepository.save(usuario);
    }

    public void cambiarPassword(Long id, String passwordActual, String passwordNueva) {
        log.info("Cambiando contraseña del usuario: {}", id);

        Usuario usuario = usuarioRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        // Verificar password actual
        if (!passwordEncoder.matches(passwordActual, usuario.getPassword())) {
            throw new RuntimeException("La contraseña actual es incorrecta");
        }

        // Actualizar con nueva contraseña encriptada
        usuario.setPassword(passwordEncoder.encode(passwordNueva));
        usuarioRepository.save(usuario);
    }

    public void cambiarEstado(Long id, Usuario.EstadoUsuario nuevoEstado) {
        log.info("Cambiando estado del usuario {} a {}", id, nuevoEstado);

        Usuario usuario = usuarioRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        usuario.setEstado(nuevoEstado);
        usuarioRepository.save(usuario);
    }

    public boolean existeEmail(String email) {
        return usuarioRepository.existsByEmail(email);
    }
}