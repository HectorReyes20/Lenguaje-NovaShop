package com.example.novashop.repository;
import com.example.novashop.model.*;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface ImagenProductoRepository extends JpaRepository<ImagenProducto, Long> {

    List<ImagenProducto> findByProductoIdProductoOrderByOrdenAsc(Long idProducto);

    Optional<ImagenProducto> findByProductoIdProductoAndEsPrincipalTrue(Long idProducto);

    void deleteByProductoIdProducto(Long idProducto);
}
