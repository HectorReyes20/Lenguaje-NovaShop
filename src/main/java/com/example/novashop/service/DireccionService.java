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
public class DireccionService {

    private final DireccionRepository direccionRepository;

    public List<Direccion> obtenerDireccionesUsuario(Long idUsuario) {
        return direccionRepository.findByUsuarioIdUsuario(idUsuario);
    }

    public Optional<Direccion> obtenerPorId(Long id) {
        return direccionRepository.findById(id);
    }

    public Optional<Direccion> obtenerPredeterminada(Long idUsuario) {
        return direccionRepository.findByUsuarioIdUsuarioAndEsPredeterminadaTrue(idUsuario);
    }

    public Direccion guardar(Direccion direccion) {
        log.info("Guardando dirección para usuario: {}", direccion.getUsuario().getIdUsuario());

        // Si esta dirección es predeterminada, quitar el flag de las demás
        if (Boolean.TRUE.equals(direccion.getEsPredeterminada())) {
            List<Direccion> direcciones = direccionRepository
                    .findByUsuarioIdUsuario(direccion.getUsuario().getIdUsuario());

            direcciones.forEach(d -> {
                d.setEsPredeterminada(false);
                direccionRepository.save(d);
            });
        }

        return direccionRepository.save(direccion);
    }

    public Direccion actualizar(Long id, Direccion direccionActualizada) {
        log.info("Actualizando dirección con ID: {}", id);

        Direccion direccion = direccionRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Dirección no encontrada"));

        direccion.setNombreCompleto(direccionActualizada.getNombreCompleto());
        direccion.setDireccionLinea1(direccionActualizada.getDireccionLinea1());
        direccion.setDireccionLinea2(direccionActualizada.getDireccionLinea2());
        direccion.setCiudad(direccionActualizada.getCiudad());
        direccion.setDepartamento(direccionActualizada.getDepartamento());
        direccion.setCodigoPostal(direccionActualizada.getCodigoPostal());
        direccion.setTelefono(direccionActualizada.getTelefono());

        return direccionRepository.save(direccion);
    }

    public void eliminar(Long id) {
        log.info("Eliminando dirección con ID: {}", id);
        direccionRepository.deleteById(id);
    }
}
