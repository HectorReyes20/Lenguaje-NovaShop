package com.example.novashop.service;

import com.example.novashop.model.*;
import com.example.novashop.repository.*;
import jakarta.persistence.criteria.Predicate; // <-- 1. IMPORTAR
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page; // <-- 2. IMPORTAR
import org.springframework.data.domain.Pageable; // <-- 3. IMPORTAR
import org.springframework.data.jpa.domain.Specification; // <-- 4. IMPORTAR
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList; // <-- 5. IMPORTAR
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class UsuarioService {

    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;

    /**
     * MÉTODO NUEVO: Reemplaza a obtenerTodos().
     * Lista usuarios con paginación y filtros dinámicos.
     */
    @Transactional(readOnly = true)
    public Page<Usuario> listarUsuarios(String buscar, Usuario.RolUsuario rol, Usuario.EstadoUsuario estado, Pageable pageable) {

        // Usamos Specification para construir una consulta dinámica
        Specification<Usuario> spec = (root, query, criteriaBuilder) -> {
            List<Predicate> predicates = new ArrayList<>();

            // 1. Filtro de Búsqueda (keyword)
            if (buscar != null && !buscar.trim().isEmpty()) {
                String keywordLike = "%" + buscar.trim().toLowerCase() + "%";
                predicates.add(
                        criteriaBuilder.or(
                                criteriaBuilder.like(criteriaBuilder.lower(root.get("nombre")), keywordLike),
                                criteriaBuilder.like(criteriaBuilder.lower(root.get("apellido")), keywordLike),
                                criteriaBuilder.like(criteriaBuilder.lower(root.get("email")), keywordLike)
                        )
                );
            }

            // 2. Filtro de Rol
            if (rol != null) {
                predicates.add(criteriaBuilder.equal(root.get("rol"), rol));
            }

            // 3. Filtro de Estado
            if (estado != null) {
                predicates.add(criteriaBuilder.equal(root.get("estado"), estado));
            }

            return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
        };

        return usuarioRepository.findAll(spec, pageable);
    }

    // El método obtenerTodos() se elimina
    // public List<Usuario> obtenerTodos() { ... }

    @Transactional(readOnly = true)
    public long contarTotalUsuarios() {
        return usuarioRepository.count();
    }

    public Optional<Usuario> obtenerPorId(Long id) {
        return usuarioRepository.findById(id);
    }

    public Optional<Usuario> obtenerPorEmail(String email) {
        return usuarioRepository.findByEmail(email);
    }

    public Usuario registrar(Usuario usuario) {
        // ... (Tu lógica de registro no cambia)
        log.info("Registrando nuevo usuario: {}", usuario.getEmail());

        if (usuarioRepository.existsByEmail(usuario.getEmail())) {
            throw new RuntimeException("El email ya está registrado");
        }
        usuario.setPassword(passwordEncoder.encode(usuario.getPassword()));
        usuario.setEstado(Usuario.EstadoUsuario.ACTIVO);
        usuario.setRol(Usuario.RolUsuario.CLIENTE);

        return usuarioRepository.save(usuario);
    }

    public Usuario actualizar(Long id, Usuario usuarioActualizado) {
        // ... (Tu lógica de actualizar no cambia)
        log.info("Actualizando usuario con ID: {}", id);

        Usuario usuario = usuarioRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        usuario.setNombre(usuarioActualizado.getNombre());
        usuario.setApellido(usuarioActualizado.getApellido());
        usuario.setTelefono(usuarioActualizado.getTelefono());

        return usuarioRepository.save(usuario);
    }

    public void cambiarPassword(Long id, String passwordActual, String passwordNueva) {
        // ... (Tu lógica de cambiarPassword no cambia)
        log.info("Cambiando contraseña del usuario: {}", id);

        Usuario usuario = usuarioRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        if (!passwordEncoder.matches(passwordActual, usuario.getPassword())) {
            throw new RuntimeException("La contraseña actual es incorrecta");
        }

        usuario.setPassword(passwordEncoder.encode(passwordNueva));
        usuarioRepository.save(usuario);
    }

    public void cambiarEstado(Long id, Usuario.EstadoUsuario nuevoEstado) {
        // ... (Tu lógica de cambiarEstado no cambia)
        log.info("Cambiando estado del usuario {} a {}", id, nuevoEstado);

        Usuario usuario = usuarioRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        usuario.setEstado(nuevoEstado);
        usuarioRepository.save(usuario);
    }

    /**
     * MÉTODO NUEVO: Para cambiar el rol de un usuario.
     */
    public void cambiarRol(Long id, Usuario.RolUsuario nuevoRol) {
        log.info("Cambiando rol del usuario {} a {}", id, nuevoRol);

        Usuario usuario = usuarioRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        usuario.setRol(nuevoRol);
        usuarioRepository.save(usuario);
    }

    /**
     * MÉTODO NUEVO: Para guardar un usuario (nuevo o existente) desde el panel de admin.
     */
    public Usuario adminGuardarUsuario(Usuario usuario) {

        // --- LÓGICA PARA CREAR USUARIO (id es null) ---
        if (usuario.getIdUsuario() == null) {
            log.info("Admin creando nuevo usuario: {}", usuario.getEmail());

            // 1. Validar email duplicado
            if (usuarioRepository.existsByEmail(usuario.getEmail())) {
                throw new RuntimeException("El email ya está registrado");
            }
            // 2. Validar contraseña
            if (usuario.getPassword() == null || usuario.getPassword().isEmpty()) {
                throw new RuntimeException("La contraseña es obligatoria para usuarios nuevos");
            }
            // 3. Encriptar contraseña
            usuario.setPassword(passwordEncoder.encode(usuario.getPassword()));

            // El Rol y Estado son los que el admin seleccionó en el formulario

            // --- LÓGICA PARA ACTUALIZAR USUARIO (id NO es null) ---
        } else {
            log.info("Admin actualizando usuario: {}", usuario.getIdUsuario());

            // 1. Obtener usuario existente
            Usuario usuarioExistente = usuarioRepository.findById(usuario.getIdUsuario())
                    .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

            // 2. Validar email duplicado (si se cambió)
            if (!usuario.getEmail().equals(usuarioExistente.getEmail())) {
                if (usuarioRepository.existsByEmail(usuario.getEmail())) {
                    throw new RuntimeException("El nuevo email ya está registrado por otro usuario");
                }
            }

            // 3. Actualizar solo los campos permitidos
            usuarioExistente.setNombre(usuario.getNombre());
            usuarioExistente.setApellido(usuario.getApellido());
            usuarioExistente.setEmail(usuario.getEmail());
            usuarioExistente.setTelefono(usuario.getTelefono());
            usuarioExistente.setRol(usuario.getRol());
            usuarioExistente.setEstado(usuario.getEstado());

            // 4. MUY IMPORTANTE: No tocamos la contraseña.
            // La contraseña de un usuario existente NO se debe cambiar desde este formulario.
            // Se debe mantener la contraseña encriptada que ya tenía.
            usuario = usuarioExistente;
        }

        return usuarioRepository.save(usuario);
    }

    public boolean existeEmail(String email) {
        return usuarioRepository.existsByEmail(email);
    }
}