package com.example.novashop.service; // Tu package está correcto

import com.example.novashop.model.*;
import com.example.novashop.repository.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service; // <-- AÑADIR
import org.springframework.transaction.annotation.Transactional; // <-- AÑADIR

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Optional;

@Service // <-- AÑADIR
@RequiredArgsConstructor // <-- AÑADIR
@Transactional // <-- AÑADIR
@Slf4j // <-- AÑADIR
public class ImagenProductoService {

    // --- INYECCIÓN DEL REPOSITORIO ---
    private final ImagenProductoRepository imagenRepository;

    // NOTA: Es mejor poner esto en application.properties
    private final String UPLOAD_DIR = "uploads/productos/";

    public List<ImagenProducto> obtenerPorProducto(Long idProducto) {
        // Usamos el método de tu repositorio
        return imagenRepository.findByProductoIdProductoOrderByOrdenAsc(idProducto);
    }

    public Optional<ImagenProducto> obtenerPorId(Long id) {
        return imagenRepository.findById(id);
    }

    public ImagenProducto guardar(ImagenProducto imagen) {
        log.info("Guardando imagen para producto ID: {}", imagen.getProducto().getIdProducto());
        return imagenRepository.save(imagen);
    }

    public void eliminarImagen(Long id) {
        ImagenProducto imagen = imagenRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Imagen no encontrada"));

        eliminarArchivoFisico(imagen.getUrlImagen());
        imagenRepository.deleteById(id);
        log.info("Imagen eliminada ID: {}", id);
    }

    public void eliminarPorProductoId(Long idProducto) {
        log.info("Eliminando todas las imágenes del producto ID: {}", idProducto);
        List<ImagenProducto> imagenes = obtenerPorProducto(idProducto);
        for (ImagenProducto imagen : imagenes) {
            eliminarArchivoFisico(imagen.getUrlImagen());
        }
        // Usamos el método de tu repositorio
        imagenRepository.deleteByProductoIdProducto(idProducto);
    }

    public Integer obtenerUltimoOrden(Long idProducto) {
        List<ImagenProducto> imagenes = obtenerPorProducto(idProducto);
        return imagenes.stream()
                .mapToInt(img -> img.getOrden() != null ? img.getOrden() : 0)
                .max()
                .orElse(0);
    }

    private void eliminarArchivoFisico(String urlImagen) {
        try {
            if (urlImagen == null || urlImagen.isEmpty()) return;
            String nombreArchivo = urlImagen.substring(urlImagen.lastIndexOf("/") + 1);
            Path rutaArchivo = Paths.get(UPLOAD_DIR + nombreArchivo);

            if (Files.exists(rutaArchivo)) {
                Files.delete(rutaArchivo);
            }
        } catch (IOException e) {
            log.error("Error al eliminar archivo físico: {}", e.getMessage());
        }
    }
}