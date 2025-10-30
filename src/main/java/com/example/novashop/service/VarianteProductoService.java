package com.example.novashop.service; // Tu package está correcto

import com.example.novashop.model.*;
import com.example.novashop.repository.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service; // <-- AÑADIR
import org.springframework.transaction.annotation.Transactional; // <-- AÑADIR

import java.util.List;
import java.util.Optional;

@Service // <-- AÑADIR
@RequiredArgsConstructor // <-- AÑADIR
@Transactional // <-- AÑADIR
@Slf4j // <-- AÑADIR
public class VarianteProductoService {

    // --- INYECCIÓN DEL REPOSITORIO ---
    private final VarianteProductoRepository varianteRepository;

    public List<VarianteProducto> obtenerPorProducto(Long idProducto) {
        // Usamos el método de tu repositorio
        return varianteRepository.findByProductoIdProducto(idProducto);
    }

    public Optional<VarianteProducto> obtenerPorId(Long id) {
        return varianteRepository.findById(id);
    }

    public VarianteProducto guardar(VarianteProducto variante) {
        log.info("Guardando variante SKU: {}", variante.getCodigoSku());
        return varianteRepository.save(variante);
    }

    public void eliminar(Long id) {
        log.info("Eliminando variante ID: {}", id);
        varianteRepository.deleteById(id);
    }

    public void eliminarPorProductoId(Long idProducto) {
        log.info("Eliminando todas las variantes del producto ID: {}", idProducto);
        List<VarianteProducto> variantes = varianteRepository.findByProductoIdProducto(idProducto);
        varianteRepository.deleteAll(variantes);
    }

    public Optional<VarianteProducto> buscarPorSku(String codigoSku) {
        return varianteRepository.findByCodigoSku(codigoSku);
    }

    public List<VarianteProducto> obtenerConStockDisponible(Long idProducto) {
        // Usamos el método de tu repositorio
        return varianteRepository.findByProductoIdProductoAndStockGreaterThan(idProducto, 0);
    }

    public Integer obtenerStockTotal(Long idProducto) {
        List<VarianteProducto> variantes = varianteRepository.findByProductoIdProducto(idProducto);
        return variantes.stream()
                .mapToInt(v -> v.getStock() != null ? v.getStock() : 0)
                .sum();
    }
}