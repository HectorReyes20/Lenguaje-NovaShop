package com.example.novashop.service;

import com.example.novashop.model.*;
import com.example.novashop.repository.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class ImagenProductoService {


    private final ImagenProductoRepository imagenRepository;
    private final String UPLOAD_DIR = "uploads/productos/";

    public List<ImagenProducto> obtenerPorProducto(Long idProducto) {
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
                .orElseThrow(() -> new RuntimeException("Imagen no encontrada con ID: " + id));

        // Eliminar archivo físico
        eliminarArchivoFisico(imagen.getUrlImagen());

        // Eliminar registro de BD
        imagenRepository.deleteById(id);

        log.info("Imagen eliminada ID: {} - Archivo: {}", id, imagen.getUrlImagen());
    }

    public void eliminarPorProductoId(Long idProducto) {
        log.info("Eliminando todas las imágenes del producto ID: {}", idProducto);

        // Obtener imágenes antes de eliminar (para eliminar archivos físicos)
        List<ImagenProducto> imagenes = obtenerPorProducto(idProducto);

        // Eliminar archivos físicos
        int eliminadas = 0;
        int errores = 0;

        for (ImagenProducto imagen : imagenes) {
            try {
                eliminarArchivoFisico(imagen.getUrlImagen());
                eliminadas++;
            } catch (Exception e) {
                errores++;
                log.warn("No se pudo eliminar archivo: {} - Error: {}",
                        imagen.getUrlImagen(), e.getMessage());
            }
        }

        // Eliminar registros de BD (1 sola query)
        imagenRepository.deleteByProductoIdProducto(idProducto);

        log.info("Imágenes eliminadas del producto {}: {} exitosas, {} con errores",
                idProducto, eliminadas, errores);
    }

    public Integer obtenerUltimoOrden(Long idProducto) {
        List<ImagenProducto> imagenes = obtenerPorProducto(idProducto);
        return imagenes.stream()
                .mapToInt(img -> img.getOrden() != null ? img.getOrden() : 0)
                .max()
                .orElse(0);
    }

    private void eliminarArchivoFisico(String urlImagen) {
        if (urlImagen == null || urlImagen.isEmpty()) {
            log.warn("URL de imagen nula o vacía, no se puede eliminar archivo físico");
            return;
        }

        try {
            // Extraer nombre del archivo de la URL
            // "/uploads/productos/abc123.jpg" -> "abc123.jpg"
            String nombreArchivo = urlImagen.substring(urlImagen.lastIndexOf("/") + 1);

            // Construir ruta completa
            Path rutaArchivo = Paths.get(UPLOAD_DIR + nombreArchivo);

            if (Files.exists(rutaArchivo)) {
                Files.delete(rutaArchivo);
                log.info("Archivo eliminado: {}", rutaArchivo);
            } else {
                log.info("Archivo no existe (ya fue eliminado): {}", rutaArchivo);
            }

        } catch (IOException e) {
            // No detenemos el proceso si falla la eliminación del archivo
            // El registro de BD sí se eliminará
            log.error("Error al eliminar archivo físico {}: {}", urlImagen, e.getMessage());
        }
    }
}