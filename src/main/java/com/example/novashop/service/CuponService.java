package com.example.novashop.service;

import com.example.novashop.model.Cupon;
import com.example.novashop.repository.CuponRepository;
import com.example.novashop.security.SecurityUtils; //
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

@Service
@RequiredArgsConstructor
@Slf4j
public class CuponService {

    private final CuponRepository cuponRepository;
    private final CarritoService carritoService;
    private final SecurityUtils securityUtils;

    /**
     * Busca y valida un cupón contra el carrito actual del usuario.
     * * @param codigo El código del cupón (ej. "CYBERWOW30")
     * @return El objeto Cupon si es válido
     * @throws RuntimeException si el cupón no es válido por alguna razón
     */
    public Cupon validarCupon(String codigo) {
        Long idUsuario = securityUtils.getCurrentUserId()
                .orElseThrow(() -> new RuntimeException("Usuario no autenticado"));

        // 1. Buscar el cupón por su código
        Cupon cupon = cuponRepository.findByCodigo(codigo)
                .orElseThrow(() -> new RuntimeException("El cupón '" + codigo + "' no existe."));

        // 2. Usar el método 'esValido()' de tu entidad Cupon
        if (!cupon.esValido()) {
            throw new RuntimeException("El cupón no está activo o ha expirado.");
        }

        // 3. Obtener el subtotal actual del carrito
        BigDecimal subtotal = carritoService.calcularTotal(idUsuario); //

        // 4. Usar el método 'puedeAplicarse()' de tu entidad Cupon
        if (!cupon.puedeAplicarse(subtotal)) {
            throw new RuntimeException("Este cupón requiere un monto mínimo de S/ " + cupon.getMontoMinimo());
        }

        // 5. Incrementar usos (opcional, pero recomendado)
        // cupon.setUsosActuales(cupon.getUsosActuales() + 1);
        // cuponRepository.save(cupon);

        log.info("Cupón '{}' validado exitosamente para usuario {}", codigo, idUsuario);
        return cupon;
    }
}