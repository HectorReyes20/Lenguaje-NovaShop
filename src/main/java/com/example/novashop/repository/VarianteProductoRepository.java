package com.example.novashop.repository;
import com.example.novashop.model.*;


import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;


import java.util.List;
import java.util.Optional;
@Repository
public interface VarianteProductoRepository extends JpaRepository<VarianteProducto, Long> {

    Optional<VarianteProducto> findByCodigoSku(String codigoSku);

    List<VarianteProducto> findByProductoIdProducto(Long idProducto);

    // Variantes con stock disponible
    List<VarianteProducto> findByProductoIdProductoAndStockGreaterThan(Long idProducto, Integer stock);

    // Variantes con stock bajo
    @Query("SELECT v FROM VarianteProducto v WHERE v.stock <= :stockMinimo AND v.stock > 0")
    List<VarianteProducto> findVariantesConStockBajo(@Param("stockMinimo") Integer stockMinimo);

    // Verificar disponibilidad
    @Query("SELECT CASE WHEN v.stock >= :cantidad THEN true ELSE false END FROM VarianteProducto v WHERE v.idVariante = :idVariante")
    boolean hayStockDisponible(@Param("idVariante") Long idVariante, @Param("cantidad") Integer cantidad);
    @Modifying
    @Transactional
    @Query("DELETE FROM VarianteProducto v WHERE v.producto.idProducto = :idProducto")
    void deleteByProductoIdProducto(@Param("idProducto") Long idProducto);
}
