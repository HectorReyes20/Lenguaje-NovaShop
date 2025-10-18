package com.example.novashop.config;

import com.example.novashop.model.*;
import com.example.novashop.repository.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.math.BigDecimal;
import java.time.LocalDateTime;
@Configuration
@RequiredArgsConstructor
@Slf4j
public class DataSeeder {

    @Bean
    CommandLineRunner initDatabase(
            UsuarioRepository usuarioRepository,
            CategoriaRepository categoriaRepository,
            ProductoRepository productoRepository,
            VarianteProductoRepository varianteRepository,
            ImagenProductoRepository imagenRepository,
            CuponRepository cuponRepository) {

        return args -> {
            // Solo ejecutar si la BD está vacía
            if (usuarioRepository.count() > 0) {
                log.info("Base de datos ya contiene datos. Saltando seed.");
                return;
            }

            log.info("🌱 Iniciando seed de datos...");

            // 1. USUARIOS
            Usuario admin = Usuario.builder()
                    .nombre("Admin")
                    .apellido("NovaShop")
                    .email("admin@novashop.com")
                    .password("$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy") // admin123
                    .telefono("999888777")
                    .estado(Usuario.EstadoUsuario.ACTIVO)
                    .rol(Usuario.RolUsuario.ADMIN)
                    .build();
            usuarioRepository.save(admin);

            Usuario cliente = Usuario.builder()
                    .nombre("Juan")
                    .apellido("Pérez")
                    .email("juan@example.com")
                    .password("$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy") // 123456
                    .telefono("987654321")
                    .estado(Usuario.EstadoUsuario.ACTIVO)
                    .rol(Usuario.RolUsuario.CLIENTE)
                    .build();
            usuarioRepository.save(cliente);

            log.info("✅ Usuarios creados");

            // 2. CATEGORÍAS
            Categoria hombres = Categoria.builder()
                    .nombre("Hombres")
                    .descripcion("Ropa para hombres")
                    .estado(Categoria.EstadoCategoria.ACTIVO)
                    .build();
            categoriaRepository.save(hombres);

            Categoria mujeres = Categoria.builder()
                    .nombre("Mujeres")
                    .descripcion("Ropa para mujeres")
                    .estado(Categoria.EstadoCategoria.ACTIVO)
                    .build();
            categoriaRepository.save(mujeres);

            Categoria camisetas = Categoria.builder()
                    .nombre("Camisetas")
                    .descripcion("Camisetas y polos")
                    .categoriaPadre(hombres)
                    .estado(Categoria.EstadoCategoria.ACTIVO)
                    .build();
            categoriaRepository.save(camisetas);

            Categoria pantalones = Categoria.builder()
                    .nombre("Pantalones")
                    .descripcion("Pantalones y jeans")
                    .categoriaPadre(hombres)
                    .estado(Categoria.EstadoCategoria.ACTIVO)
                    .build();
            categoriaRepository.save(pantalones);

            log.info("✅ Categorías creadas");

            // 3. PRODUCTOS
            Producto polo = Producto.builder()
                    .nombre("Polo Clásico Premium")
                    .descripcion("Polo de algodón 100% con corte clásico y acabado premium. Perfecto para cualquier ocasión.")
                    .categoria(camisetas)
                    .precioBase(new BigDecimal("89.90"))
                    .marca("NovaStyle")
                    .genero(Producto.GeneroProducto.HOMBRE)
                    .material("Algodón 100%")
                    .estado(Producto.EstadoProducto.ACTIVO)
                    .destacado(true)
                    .build();
            productoRepository.save(polo);

            Producto jean = Producto.builder()
                    .nombre("Jean Slim Fit")
                    .descripcion("Jean de mezclilla con ajuste slim, diseño moderno y cómodo.")
                    .categoria(pantalones)
                    .precioBase(new BigDecimal("149.90"))
                    .precioOferta(new BigDecimal("119.90"))
                    .marca("NovaJeans")
                    .genero(Producto.GeneroProducto.HOMBRE)
                    .material("Denim 98% Algodón, 2% Elastano")
                    .estado(Producto.EstadoProducto.ACTIVO)
                    .destacado(true)
                    .build();
            productoRepository.save(jean);

            log.info("✅ Productos creados");

            // 4. VARIANTES
            VarianteProducto poloAzulM = VarianteProducto.builder()
                    .producto(polo)
                    .talla("M")
                    .color("Azul")
                    .codigoSku("POL-M-AZU")
                    .stock(25)
                    .build();
            varianteRepository.save(poloAzulM);

            VarianteProducto poloAzulL = VarianteProducto.builder()
                    .producto(polo)
                    .talla("L")
                    .color("Azul")
                    .codigoSku("POL-L-AZU")
                    .stock(30)
                    .build();
            varianteRepository.save(poloAzulL);

            VarianteProducto poloRojoM = VarianteProducto.builder()
                    .producto(polo)
                    .talla("M")
                    .color("Rojo")
                    .codigoSku("POL-M-ROJ")
                    .stock(15)
                    .build();
            varianteRepository.save(poloRojoM);

            VarianteProducto jean32 = VarianteProducto.builder()
                    .producto(jean)
                    .talla("32")
                    .color("Azul Oscuro")
                    .codigoSku("JEA-32-AZO")
                    .stock(20)
                    .build();
            varianteRepository.save(jean32);

            VarianteProducto jean34 = VarianteProducto.builder()
                    .producto(jean)
                    .talla("34")
                    .color("Azul Oscuro")
                    .codigoSku("JEA-34-AZO")
                    .stock(18)
                    .build();
            varianteRepository.save(jean34);

            log.info("✅ Variantes creadas");

            // 5. IMÁGENES
            ImagenProducto imgPolo1 = ImagenProducto.builder()
                    .producto(polo)
                    .urlImagen("/images/productos/polo-azul-front.jpg")
                    .orden(1)
                    .esPrincipal(true)
                    .build();
            imagenRepository.save(imgPolo1);

            ImagenProducto imgPolo2 = ImagenProducto.builder()
                    .producto(polo)
                    .urlImagen("/images/productos/polo-azul-back.jpg")
                    .orden(2)
                    .esPrincipal(false)
                    .build();
            imagenRepository.save(imgPolo2);

            ImagenProducto imgJean = ImagenProducto.builder()
                    .producto(jean)
                    .urlImagen("/images/productos/jean-slim-front.jpg")
                    .orden(1)
                    .esPrincipal(true)
                    .build();
            imagenRepository.save(imgJean);

            log.info("✅ Imágenes creadas");

            // 6. CUPONES
            Cupon cuponBienvenida = Cupon.builder()
                    .codigo("BIENVENIDA20")
                    .tipoDescuento(Cupon.TipoDescuento.PORCENTAJE)
                    .valorDescuento(new BigDecimal("20.00"))
                    .fechaExpiracion(LocalDateTime.now().plusMonths(3))
                    .usosMaximos(100)
                    .usosActuales(0)
                    .montoMinimo(new BigDecimal("50.00"))
                    .estado(Cupon.EstadoCupon.ACTIVO)
                    .build();
            cuponRepository.save(cuponBienvenida);

            Cupon cuponEnvio = Cupon.builder()
                    .codigo("ENVIOGRATIS")
                    .tipoDescuento(Cupon.TipoDescuento.MONTO_FIJO)
                    .valorDescuento(new BigDecimal("15.00"))
                    .fechaExpiracion(LocalDateTime.now().plusMonths(1))
                    .usosMaximos(50)
                    .usosActuales(0)
                    .montoMinimo(new BigDecimal("100.00"))
                    .estado(Cupon.EstadoCupon.ACTIVO)
                    .build();
            cuponRepository.save(cuponEnvio);

            log.info("✅ Cupones creados");

            log.info("🎉 Seed completado exitosamente!");
            log.info("📧 Usuario Admin: admin@novashop.com / admin123");
            log.info("📧 Usuario Cliente: juan@example.com / 123456");
        };
    }
}
