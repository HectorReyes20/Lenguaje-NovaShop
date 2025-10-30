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
public interface ImagenProductoRepository extends JpaRepository<ImagenProducto, Long> {

    List<ImagenProducto> findByProductoIdProductoOrderByOrdenAsc(Long idProducto);

    Optional<ImagenProducto> findByProductoIdProductoAndEsPrincipalTrue(Long idProducto);

    @Modifying
    @Transactional
    @Query("DELETE FROM ImagenProducto i WHERE i.producto.idProducto = :idProducto")
    void deleteByProductoIdProducto(@Param("idProducto") Long idProducto);
}

