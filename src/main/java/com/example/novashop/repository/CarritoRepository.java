package com.example.novashop.repository;
import com.example.novashop.model.*;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
@Repository
public interface CarritoRepository extends JpaRepository<Carrito, Long> {

    List<Carrito> findByUsuarioIdUsuario(Long idUsuario);

    Optional<Carrito> findByUsuarioIdUsuarioAndVarianteIdVariante(Long idUsuario, Long idVariante);

    // Contar items del carrito
    @Query("SELECT COUNT(c) FROM Carrito c WHERE c.usuario.idUsuario = :idUsuario")
    Long contarItemsCarrito(@Param("idUsuario") Long idUsuario);

    // Calcular total del carrito
    @Query("SELECT SUM((v.producto.precioBase + v.precioAdicional) * c.cantidad) " +
            "FROM Carrito c JOIN c.variante v WHERE c.usuario.idUsuario = :idUsuario")
    BigDecimal calcularTotalCarrito(@Param("idUsuario") Long idUsuario);

    // Limpiar carrito
    void deleteByUsuarioIdUsuario(Long idUsuario);

    // Verificar si existe item en carrito
    boolean existsByUsuarioIdUsuarioAndVarianteIdVariante(Long idUsuario, Long idVariante);
}
