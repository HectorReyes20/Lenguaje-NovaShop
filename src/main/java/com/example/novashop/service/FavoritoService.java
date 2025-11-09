package com.example.novashop.service;
import com.example.novashop.model.*;
import com.example.novashop.repository.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;
@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class FavoritoService {

    private final FavoritoRepository favoritoRepository;

    // --- INICIO DE LA CORRECCIÓN ---
    // 1. Añade los repositorios que faltan
    private final UsuarioRepository usuarioRepository;
    private final ProductoRepository productoRepository;
    // --- FIN DE LA CORRECCIÓN ---
    public Page<Favorito> obtenerFavoritosUsuario(Long idUsuario, Pageable pageable) {
        return favoritoRepository.findByUsuarioIdUsuarioOrderByFechaAgregadoDesc(
                idUsuario, pageable);
    }

    public Favorito agregar(Long idUsuario, Long idProducto) {
        log.info("Agregando a favoritos. Usuario: {}, Producto: {}", idUsuario, idProducto);

        if (favoritoRepository.existsByUsuarioIdUsuarioAndProductoIdProducto(idUsuario, idProducto)) {
            throw new RuntimeException("El producto ya está en favoritos");
        }

        // --- ESTA ES LA LÓGICA QUE FALTA ---
        Usuario usuario = usuarioRepository.findById(idUsuario)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
        Producto producto = productoRepository.findById(idProducto)
                .orElseThrow(() -> new RuntimeException("Producto no encontrado"));

        Favorito favorito = Favorito.builder()
                .usuario(usuario)
                .producto(producto)
                .build();
        // --- FIN DE LA LÓGICA QUE FALTA ---

        // El @PrePersist en el modelo Favorito se encargará de la fechaAgregado

        return favoritoRepository.save(favorito);
    }
    // --- FIN DE LA CORRECCIÓN ---

    public void eliminar(Long idUsuario, Long idProducto) {
        log.info("Eliminando de favoritos. Usuario: {}, Producto: {}", idUsuario, idProducto);
        favoritoRepository.deleteByUsuarioIdUsuarioAndProductoIdProducto(idUsuario, idProducto);
    }

    public boolean esFavorito(Long idUsuario, Long idProducto) {
        return favoritoRepository.existsByUsuarioIdUsuarioAndProductoIdProducto(
                idUsuario, idProducto);
    }

    public Long contarFavoritos(Long idUsuario) {
        return favoritoRepository.countByUsuarioIdUsuario(idUsuario);
    }
}
