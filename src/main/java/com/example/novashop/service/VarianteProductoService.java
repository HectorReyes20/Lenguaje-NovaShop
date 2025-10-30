package com.example.novashop.service;

import com.example.novashop.model.VarianteProducto;
import com.example.novashop.repository.VarianteProductoRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class VarianteProductoService {

    private final VarianteProductoRepository varianteRepository;


    public List<VarianteProducto> obtenerPorProducto(Long idProducto) {
        return varianteRepository.findByProductoIdProducto(idProducto);
    }

    public Optional<VarianteProducto> obtenerPorId(Long id) {
        return varianteRepository.findById(id);
    }

    public Optional<VarianteProducto> buscarPorSku(String codigoSku) {
        return varianteRepository.findByCodigoSku(codigoSku);
    }

    public List<VarianteProducto> obtenerConStockDisponible(Long idProducto) {
        return varianteRepository.findByProductoIdProductoAndStockGreaterThan(idProducto, 0);
    }

    public Integer obtenerStockTotal(Long idProducto) {
        List<VarianteProducto> variantes = varianteRepository.findByProductoIdProducto(idProducto);
        return variantes.stream()
                .mapToInt(v -> v.getStock() != null ? v.getStock() : 0)
                .sum();
    }

    public VarianteProducto guardar(VarianteProducto variante) {
        log.info("Guardando variante SKU: {}", variante.getCodigoSku());

        if (variante.getIdVariante() != null) {
            // Si ya existe, recuperamos desde la BD
            VarianteProducto existente = varianteRepository.findById(variante.getIdVariante())
                    .orElseThrow(() -> new IllegalArgumentException("La variante no existe en BD (ID: " + variante.getIdVariante() + ")"));

            // Actualizamos solo los campos editables
            existente.setTalla(variante.getTalla());
            existente.setColor(variante.getColor());
            existente.setCodigoSku(variante.getCodigoSku());
            existente.setStock(variante.getStock());
            existente.setProducto(variante.getProducto());

            return varianteRepository.save(existente);
        } else {
            // Si es nueva, se guarda normalmente
            return varianteRepository.save(variante);
        }
    }

    public void eliminar(Long id) {
        log.info("Eliminando variante ID: {}", id);
        varianteRepository.deleteById(id);
    }

    public void eliminarPorProductoId(Long idProducto) {
        log.info("Eliminando todas las variantes del producto ID: {}", idProducto);


        List<VarianteProducto> variantes = varianteRepository.findByProductoIdProducto(idProducto);
        int cantidad = variantes.size();


        varianteRepository.deleteByProductoIdProducto(idProducto);

        log.info("Eliminadas {} variantes del producto {}", cantidad, idProducto);
    }
}
