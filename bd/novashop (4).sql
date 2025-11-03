-- phpMyAdmin SQL Dump
-- version 5.2.1
-- https://www.phpmyadmin.net/
--
-- Servidor: 127.0.0.1:3306
-- Tiempo de generación: 01-11-2025 a las 22:18:46
-- Versión del servidor: 9.1.0
-- Versión de PHP: 8.3.14

SET SQL_MODE = "NO_AUTO_VALUE_ON_ZERO";
START TRANSACTION;
SET time_zone = "+00:00";


/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!40101 SET NAMES utf8mb4 */;

--
-- Base de datos: `novashop`
--

-- --------------------------------------------------------

--
-- Estructura de tabla para la tabla `carrito`
--

DROP TABLE IF EXISTS `carrito`;
CREATE TABLE IF NOT EXISTS `carrito` (
  `id_carrito` bigint NOT NULL AUTO_INCREMENT,
  `id_usuario` bigint NOT NULL,
  `id_variante` bigint NOT NULL,
  `cantidad` int DEFAULT '1',
  `fecha_agregado` datetime DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id_carrito`),
  UNIQUE KEY `unique_carrito` (`id_usuario`,`id_variante`),
  UNIQUE KEY `UKoei4f3dy13f4twuknm4cjq0mf` (`id_usuario`,`id_variante`),
  KEY `idx_usuario` (`id_usuario`),
  KEY `idx_variante` (`id_variante`)
) ENGINE=InnoDB AUTO_INCREMENT=2 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- --------------------------------------------------------

--
-- Estructura de tabla para la tabla `categorias`
--

DROP TABLE IF EXISTS `categorias`;
CREATE TABLE IF NOT EXISTS `categorias` (
  `id_categoria` bigint NOT NULL AUTO_INCREMENT,
  `nombre` varchar(100) COLLATE utf8mb4_unicode_ci NOT NULL,
  `descripcion` text COLLATE utf8mb4_unicode_ci,
  `id_categoria_padre` bigint DEFAULT NULL,
  `imagen_url` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `estado` enum('ACTIVO','INACTIVO') COLLATE utf8mb4_unicode_ci NOT NULL,
  PRIMARY KEY (`id_categoria`),
  KEY `idx_padre` (`id_categoria_padre`),
  KEY `idx_estado` (`estado`)
) ENGINE=InnoDB AUTO_INCREMENT=8 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

--
-- Volcado de datos para la tabla `categorias`
--

INSERT INTO `categorias` (`id_categoria`, `nombre`, `descripcion`, `id_categoria_padre`, `imagen_url`, `estado`) VALUES
(1, 'Hombres', 'Ropa para hombres', NULL, NULL, 'ACTIVO'),
(2, 'Mujeres', 'Ropa para mujeres', NULL, NULL, 'ACTIVO'),
(3, 'Niños', 'Ropa para niños', NULL, NULL, 'ACTIVO'),
(4, 'Camisetas', 'Camisetas y polos', 1, NULL, 'ACTIVO'),
(5, 'Pantalones', 'Pantalones y jeans', 1, NULL, 'ACTIVO'),
(6, 'Vestidos', 'Vestidos elegantes y casuales', 2, NULL, 'ACTIVO'),
(7, 'Blusas', 'Blusas y tops', 2, NULL, 'ACTIVO');

-- --------------------------------------------------------

--
-- Estructura de tabla para la tabla `cupones`
--

DROP TABLE IF EXISTS `cupones`;
CREATE TABLE IF NOT EXISTS `cupones` (
  `id_cupon` bigint NOT NULL AUTO_INCREMENT,
  `codigo` varchar(50) COLLATE utf8mb4_unicode_ci NOT NULL,
  `tipo_descuento` enum('MONTO_FIJO','PORCENTAJE') COLLATE utf8mb4_unicode_ci NOT NULL,
  `valor_descuento` decimal(10,2) NOT NULL,
  `fecha_inicio` datetime DEFAULT CURRENT_TIMESTAMP,
  `fecha_expiracion` datetime NOT NULL,
  `usos_maximos` int DEFAULT '0',
  `usos_actuales` int DEFAULT '0',
  `monto_minimo` decimal(10,2) DEFAULT NULL,
  `estado` enum('ACTIVO','INACTIVO') COLLATE utf8mb4_unicode_ci NOT NULL,
  PRIMARY KEY (`id_cupon`),
  UNIQUE KEY `codigo` (`codigo`),
  KEY `idx_codigo` (`codigo`),
  KEY `idx_estado` (`estado`)
) ENGINE=InnoDB AUTO_INCREMENT=3 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

--
-- Volcado de datos para la tabla `cupones`
--

INSERT INTO `cupones` (`id_cupon`, `codigo`, `tipo_descuento`, `valor_descuento`, `fecha_inicio`, `fecha_expiracion`, `usos_maximos`, `usos_actuales`, `monto_minimo`, `estado`) VALUES
(1, 'BIENVENIDA20', 'PORCENTAJE', 20.00, '2025-10-17 17:33:53', '2025-12-31 23:59:59', 100, 0, 50.00, 'ACTIVO'),
(2, 'ENVIOGRATIS', 'MONTO_FIJO', 15.00, '2025-10-17 17:33:53', '2025-10-31 23:59:59', 50, 0, 100.00, 'ACTIVO');

-- --------------------------------------------------------

--
-- Estructura de tabla para la tabla `detalle_pedido`
--

DROP TABLE IF EXISTS `detalle_pedido`;
CREATE TABLE IF NOT EXISTS `detalle_pedido` (
  `id_detalle` bigint NOT NULL AUTO_INCREMENT,
  `id_pedido` bigint NOT NULL,
  `id_variante` bigint NOT NULL,
  `cantidad` int NOT NULL,
  `precio_unitario` decimal(10,2) NOT NULL,
  `subtotal` decimal(10,2) NOT NULL,
  PRIMARY KEY (`id_detalle`),
  KEY `idx_pedido` (`id_pedido`),
  KEY `idx_variante` (`id_variante`)
) ENGINE=InnoDB AUTO_INCREMENT=2 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

--
-- Volcado de datos para la tabla `detalle_pedido`
--

INSERT INTO `detalle_pedido` (`id_detalle`, `id_pedido`, `id_variante`, `cantidad`, `precio_unitario`, `subtotal`) VALUES
(1, 1, 1, 1, 89.90, 89.90);

-- --------------------------------------------------------

--
-- Estructura de tabla para la tabla `direcciones`
--

DROP TABLE IF EXISTS `direcciones`;
CREATE TABLE IF NOT EXISTS `direcciones` (
  `id_direccion` bigint NOT NULL AUTO_INCREMENT,
  `id_usuario` bigint NOT NULL,
  `tipo` enum('AMBOS','ENVIO','FACTURACION') COLLATE utf8mb4_unicode_ci NOT NULL,
  `nombre_completo` varchar(200) COLLATE utf8mb4_unicode_ci NOT NULL,
  `direccion_linea1` varchar(255) COLLATE utf8mb4_unicode_ci NOT NULL,
  `direccion_linea2` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `ciudad` varchar(100) COLLATE utf8mb4_unicode_ci NOT NULL,
  `departamento` varchar(100) COLLATE utf8mb4_unicode_ci NOT NULL,
  `codigo_postal` varchar(10) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `pais` varchar(50) COLLATE utf8mb4_unicode_ci DEFAULT 'Perú',
  `telefono` varchar(20) COLLATE utf8mb4_unicode_ci NOT NULL,
  `es_predeterminada` tinyint(1) DEFAULT '0',
  PRIMARY KEY (`id_direccion`),
  KEY `idx_usuario` (`id_usuario`)
) ENGINE=InnoDB AUTO_INCREMENT=4 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

--
-- Volcado de datos para la tabla `direcciones`
--

INSERT INTO `direcciones` (`id_direccion`, `id_usuario`, `tipo`, `nombre_completo`, `direccion_linea1`, `direccion_linea2`, `ciudad`, `departamento`, `codigo_postal`, `pais`, `telefono`, `es_predeterminada`) VALUES
(1, 3, 'ENVIO', 'Hector Reyes', 'Av.Lima', 'Entre las calles x y o', 'Lima', 'Lima', '15001', 'Perú', '997256008', 1),
(2, 4, 'ENVIO', 'Luis Meza Castillo', 'Av.Lima', 'Entre las calles x y o', 'Lima', 'Lima', '15001', 'Perú', '987456125', 1),
(3, 4, 'FACTURACION', 'Luis Meza Castillo', 'Av. Cesar Vallejo Sector 2', 'Mercado Plaza Villa Sur', 'Lima', 'Lima', '15001', 'Perú', '987456125', 0);

-- --------------------------------------------------------

--
-- Estructura de tabla para la tabla `favoritos`
--

DROP TABLE IF EXISTS `favoritos`;
CREATE TABLE IF NOT EXISTS `favoritos` (
  `id_favorito` bigint NOT NULL AUTO_INCREMENT,
  `id_usuario` bigint NOT NULL,
  `id_producto` bigint NOT NULL,
  `fecha_agregado` datetime DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id_favorito`),
  UNIQUE KEY `unique_favorito` (`id_usuario`,`id_producto`),
  UNIQUE KEY `UKkqa9nhyi4y7exdwicjjd2h7jn` (`id_usuario`,`id_producto`),
  KEY `idx_usuario` (`id_usuario`),
  KEY `idx_producto` (`id_producto`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- --------------------------------------------------------

--
-- Estructura de tabla para la tabla `imagenes_producto`
--

DROP TABLE IF EXISTS `imagenes_producto`;
CREATE TABLE IF NOT EXISTS `imagenes_producto` (
  `id_imagen` bigint NOT NULL AUTO_INCREMENT,
  `id_producto` bigint NOT NULL,
  `url_imagen` varchar(255) COLLATE utf8mb4_unicode_ci NOT NULL,
  `orden` int DEFAULT '0',
  `es_principal` tinyint(1) DEFAULT '0',
  PRIMARY KEY (`id_imagen`),
  KEY `idx_producto` (`id_producto`)
) ENGINE=InnoDB AUTO_INCREMENT=9 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

--
-- Volcado de datos para la tabla `imagenes_producto`
--

INSERT INTO `imagenes_producto` (`id_imagen`, `id_producto`, `url_imagen`, `orden`, `es_principal`) VALUES
(1, 1, '/images/productos/polo-azul-front.jpg', 1, 1),
(2, 1, '/images/productos/polo-azul-back.jpg', 2, 0),
(3, 2, '/images/productos/jean-slim-front.jpg', 1, 1),
(4, 3, '/images/productos/vestido-floral-front.jpg', 1, 1),
(7, 6, '/uploads/productos/5fd330e3-096c-47d3-8172-eec2db7b3563.jpg', 1, 0),
(8, 6, '/uploads/productos/05bb0feb-07d7-4361-af45-24ec795b8ecf.jpg', 2, 0);

-- --------------------------------------------------------

--
-- Estructura de tabla para la tabla `pedidos`
--

DROP TABLE IF EXISTS `pedidos`;
CREATE TABLE IF NOT EXISTS `pedidos` (
  `id_pedido` bigint NOT NULL AUTO_INCREMENT,
  `id_usuario` bigint NOT NULL,
  `numero_pedido` varchar(50) COLLATE utf8mb4_unicode_ci NOT NULL,
  `id_direccion_envio` bigint NOT NULL,
  `subtotal` decimal(10,2) NOT NULL,
  `costo_envio` decimal(10,2) DEFAULT '0.00',
  `descuento` decimal(10,2) DEFAULT '0.00',
  `total` decimal(10,2) NOT NULL,
  `estado` enum('CANCELADO','CONFIRMADO','ENTREGADO','ENVIADO','PENDIENTE','PROCESANDO') COLLATE utf8mb4_unicode_ci NOT NULL,
  `metodo_pago` varchar(50) COLLATE utf8mb4_unicode_ci NOT NULL,
  `fecha_pedido` datetime DEFAULT CURRENT_TIMESTAMP,
  `fecha_actualizacion` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `notas` text COLLATE utf8mb4_unicode_ci,
  PRIMARY KEY (`id_pedido`),
  UNIQUE KEY `numero_pedido` (`numero_pedido`),
  KEY `id_direccion_envio` (`id_direccion_envio`),
  KEY `idx_usuario` (`id_usuario`),
  KEY `idx_numero_pedido` (`numero_pedido`),
  KEY `idx_estado` (`estado`),
  KEY `idx_fecha` (`fecha_pedido`)
) ENGINE=InnoDB AUTO_INCREMENT=2 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

--
-- Volcado de datos para la tabla `pedidos`
--

INSERT INTO `pedidos` (`id_pedido`, `id_usuario`, `numero_pedido`, `id_direccion_envio`, `subtotal`, `costo_envio`, `descuento`, `total`, `estado`, `metodo_pago`, `fecha_pedido`, `fecha_actualizacion`, `notas`) VALUES
(1, 4, 'NS-202510-000001', 2, 89.90, 15.00, 0.00, 104.90, 'ENTREGADO', 'TARJETA', '2025-10-31 18:47:54', '2025-11-01 10:40:20', NULL);

-- --------------------------------------------------------

--
-- Estructura de tabla para la tabla `productos`
--

DROP TABLE IF EXISTS `productos`;
CREATE TABLE IF NOT EXISTS `productos` (
  `id_producto` bigint NOT NULL AUTO_INCREMENT,
  `nombre` varchar(200) COLLATE utf8mb4_unicode_ci NOT NULL,
  `descripcion` text COLLATE utf8mb4_unicode_ci NOT NULL,
  `id_categoria` bigint NOT NULL,
  `precio_base` decimal(10,2) NOT NULL,
  `precio_oferta` decimal(10,2) DEFAULT NULL,
  `marca` varchar(100) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `genero` enum('HOMBRE','MUJER','NINA','NINO','UNISEX') COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `material` varchar(100) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `fecha_creacion` datetime DEFAULT CURRENT_TIMESTAMP,
  `estado` enum('ACTIVO','AGOTADO','INACTIVO') COLLATE utf8mb4_unicode_ci NOT NULL,
  `destacado` tinyint(1) DEFAULT '0',
  PRIMARY KEY (`id_producto`),
  KEY `idx_categoria` (`id_categoria`),
  KEY `idx_estado` (`estado`),
  KEY `idx_destacado` (`destacado`),
  KEY `idx_genero` (`genero`)
) ENGINE=InnoDB AUTO_INCREMENT=7 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

--
-- Volcado de datos para la tabla `productos`
--

INSERT INTO `productos` (`id_producto`, `nombre`, `descripcion`, `id_categoria`, `precio_base`, `precio_oferta`, `marca`, `genero`, `material`, `fecha_creacion`, `estado`, `destacado`) VALUES
(1, 'Camiseta Polo Clásica', 'Polo de algodón 100% con corte clásico', 4, 89.90, NULL, 'NovaStyle', 'HOMBRE', NULL, '2025-10-17 17:33:53', 'ACTIVO', 1),
(2, 'Jean Slim Fit', 'Jean de mezclilla con ajuste slim', 5, 149.90, 109.90, 'NovaJeans', 'HOMBRE', '', '2025-10-17 17:33:53', 'ACTIVO', 1),
(3, 'Vestido Floral Elegante', 'Vestido largo con estampado floral', 6, 199.90, NULL, 'NovaFemme', 'MUJER', NULL, '2025-10-17 17:33:53', 'ACTIVO', 1),
(6, 'Polo Clasico', 'Polo con corte clasico', 4, 40.00, NULL, 'Peruvian', 'HOMBRE', 'Franela', '2025-10-30 09:16:10', 'ACTIVO', 0);

-- --------------------------------------------------------

--
-- Estructura de tabla para la tabla `resenas`
--

DROP TABLE IF EXISTS `resenas`;
CREATE TABLE IF NOT EXISTS `resenas` (
  `id_resena` bigint NOT NULL AUTO_INCREMENT,
  `id_producto` bigint NOT NULL,
  `id_usuario` bigint NOT NULL,
  `calificacion` int NOT NULL,
  `titulo` varchar(200) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `comentario` text COLLATE utf8mb4_unicode_ci,
  `fecha_resena` datetime DEFAULT CURRENT_TIMESTAMP,
  `verificada` tinyint(1) DEFAULT '0',
  PRIMARY KEY (`id_resena`),
  KEY `idx_producto` (`id_producto`),
  KEY `idx_usuario` (`id_usuario`),
  KEY `idx_calificacion` (`calificacion`)
) ;

-- --------------------------------------------------------

--
-- Estructura de tabla para la tabla `usuarios`
--

DROP TABLE IF EXISTS `usuarios`;
CREATE TABLE IF NOT EXISTS `usuarios` (
  `id_usuario` bigint NOT NULL AUTO_INCREMENT,
  `nombre` varchar(100) COLLATE utf8mb4_unicode_ci NOT NULL,
  `apellido` varchar(100) COLLATE utf8mb4_unicode_ci NOT NULL,
  `email` varchar(150) COLLATE utf8mb4_unicode_ci NOT NULL,
  `password` varchar(255) COLLATE utf8mb4_unicode_ci NOT NULL,
  `telefono` varchar(20) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `fecha_registro` datetime DEFAULT CURRENT_TIMESTAMP,
  `estado` enum('ACTIVO','BLOQUEADO','INACTIVO') COLLATE utf8mb4_unicode_ci NOT NULL,
  `rol` enum('ADMIN','CLIENTE') COLLATE utf8mb4_unicode_ci NOT NULL,
  PRIMARY KEY (`id_usuario`),
  UNIQUE KEY `email` (`email`),
  KEY `idx_email` (`email`),
  KEY `idx_estado` (`estado`)
) ENGINE=InnoDB AUTO_INCREMENT=6 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

--
-- Volcado de datos para la tabla `usuarios`
--

INSERT INTO `usuarios` (`id_usuario`, `nombre`, `apellido`, `email`, `password`, `telefono`, `fecha_registro`, `estado`, `rol`) VALUES
(1, 'Admin', 'NovaShop', 'admin@novashop.com', '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy', '999888777', '2025-10-17 17:33:53', 'ACTIVO', 'ADMIN'),
(2, 'Juan', 'Pérez', 'juan@example.com', '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy', '987654321', '2025-10-17 17:33:53', 'ACTIVO', 'CLIENTE'),
(3, 'Hector', 'Reye', 'fatama@gmail.com', '$2a$10$bvxUCE.tLkY4HlSf1UuKcu.TGlbU4zE.7ZJzXYWoKLEMshfKd3tFu', '987456125', '2025-10-17 17:45:33', 'ACTIVO', 'CLIENTE'),
(4, 'Luis', 'Meza', 'meza@gmail.com', '$2a$10$6giEPOXj3tjzFKW8YyhV6e9yWpIRqfydlgCV9mxPq0t68AOzN1MzC', '951357456', '2025-10-20 17:06:26', 'ACTIVO', 'CLIENTE'),
(5, 'Pao', 'Garcia', 'pao@gmail.com', '$2a$10$BGxE0jqF0zIWcuaxopQg4uwHRKzRjX7THReIyBmRDzVfHc7/sECv.', '987456125', '2025-10-28 19:10:11', 'ACTIVO', 'ADMIN');

-- --------------------------------------------------------

--
-- Estructura de tabla para la tabla `variantes_producto`
--

DROP TABLE IF EXISTS `variantes_producto`;
CREATE TABLE IF NOT EXISTS `variantes_producto` (
  `id_variante` bigint NOT NULL AUTO_INCREMENT,
  `id_producto` bigint NOT NULL,
  `talla` varchar(10) COLLATE utf8mb4_unicode_ci NOT NULL,
  `color` varchar(50) COLLATE utf8mb4_unicode_ci NOT NULL,
  `codigo_sku` varchar(100) COLLATE utf8mb4_unicode_ci NOT NULL,
  `stock` int DEFAULT '0',
  `precio_adicional` decimal(10,2) DEFAULT '0.00',
  PRIMARY KEY (`id_variante`),
  UNIQUE KEY `codigo_sku` (`codigo_sku`),
  KEY `idx_producto` (`id_producto`),
  KEY `idx_sku` (`codigo_sku`),
  KEY `idx_stock` (`stock`)
) ENGINE=InnoDB AUTO_INCREMENT=12 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

--
-- Volcado de datos para la tabla `variantes_producto`
--

INSERT INTO `variantes_producto` (`id_variante`, `id_producto`, `talla`, `color`, `codigo_sku`, `stock`, `precio_adicional`) VALUES
(1, 1, 'M', 'Azul', 'POL-M-AZU', 24, 0.00),
(2, 1, 'M', 'Rojo', 'POL-M-ROJ', 15, 0.00),
(3, 1, 'L', 'Azul', 'POL-L-AZU', 30, 0.00),
(4, 1, 'L', 'Rojo', 'POL-L-ROJ', 10, 0.00),
(5, 2, '32', 'Azul Oscuro', 'JEA-32-AZO', 20, 0.00),
(6, 2, '34', 'Azul Oscuro', 'JEA-34-AZO', 18, 0.00),
(7, 3, 'S', 'Multicolor', 'VES-S-MUL', 12, 0.00),
(8, 3, 'M', 'Multicolor', 'VES-M-MUL', 15, 0.00),
(11, 6, 'M', 'Blanco', 'CLA', 2, 0.00);

--
-- Restricciones para tablas volcadas
--

--
-- Filtros para la tabla `carrito`
--
ALTER TABLE `carrito`
  ADD CONSTRAINT `carrito_ibfk_1` FOREIGN KEY (`id_usuario`) REFERENCES `usuarios` (`id_usuario`) ON DELETE CASCADE,
  ADD CONSTRAINT `carrito_ibfk_2` FOREIGN KEY (`id_variante`) REFERENCES `variantes_producto` (`id_variante`) ON DELETE CASCADE;

--
-- Filtros para la tabla `categorias`
--
ALTER TABLE `categorias`
  ADD CONSTRAINT `categorias_ibfk_1` FOREIGN KEY (`id_categoria_padre`) REFERENCES `categorias` (`id_categoria`) ON DELETE SET NULL;

--
-- Filtros para la tabla `detalle_pedido`
--
ALTER TABLE `detalle_pedido`
  ADD CONSTRAINT `detalle_pedido_ibfk_1` FOREIGN KEY (`id_pedido`) REFERENCES `pedidos` (`id_pedido`) ON DELETE CASCADE,
  ADD CONSTRAINT `detalle_pedido_ibfk_2` FOREIGN KEY (`id_variante`) REFERENCES `variantes_producto` (`id_variante`) ON DELETE RESTRICT;

--
-- Filtros para la tabla `direcciones`
--
ALTER TABLE `direcciones`
  ADD CONSTRAINT `direcciones_ibfk_1` FOREIGN KEY (`id_usuario`) REFERENCES `usuarios` (`id_usuario`) ON DELETE CASCADE;

--
-- Filtros para la tabla `favoritos`
--
ALTER TABLE `favoritos`
  ADD CONSTRAINT `favoritos_ibfk_1` FOREIGN KEY (`id_usuario`) REFERENCES `usuarios` (`id_usuario`) ON DELETE CASCADE,
  ADD CONSTRAINT `favoritos_ibfk_2` FOREIGN KEY (`id_producto`) REFERENCES `productos` (`id_producto`) ON DELETE CASCADE;

--
-- Filtros para la tabla `imagenes_producto`
--
ALTER TABLE `imagenes_producto`
  ADD CONSTRAINT `imagenes_producto_ibfk_1` FOREIGN KEY (`id_producto`) REFERENCES `productos` (`id_producto`) ON DELETE CASCADE;

--
-- Filtros para la tabla `pedidos`
--
ALTER TABLE `pedidos`
  ADD CONSTRAINT `pedidos_ibfk_1` FOREIGN KEY (`id_usuario`) REFERENCES `usuarios` (`id_usuario`) ON DELETE RESTRICT,
  ADD CONSTRAINT `pedidos_ibfk_2` FOREIGN KEY (`id_direccion_envio`) REFERENCES `direcciones` (`id_direccion`) ON DELETE RESTRICT;

--
-- Filtros para la tabla `productos`
--
ALTER TABLE `productos`
  ADD CONSTRAINT `productos_ibfk_1` FOREIGN KEY (`id_categoria`) REFERENCES `categorias` (`id_categoria`) ON DELETE RESTRICT;

--
-- Filtros para la tabla `resenas`
--
ALTER TABLE `resenas`
  ADD CONSTRAINT `resenas_ibfk_1` FOREIGN KEY (`id_producto`) REFERENCES `productos` (`id_producto`) ON DELETE CASCADE,
  ADD CONSTRAINT `resenas_ibfk_2` FOREIGN KEY (`id_usuario`) REFERENCES `usuarios` (`id_usuario`) ON DELETE CASCADE;

--
-- Filtros para la tabla `variantes_producto`
--
ALTER TABLE `variantes_producto`
  ADD CONSTRAINT `variantes_producto_ibfk_1` FOREIGN KEY (`id_producto`) REFERENCES `productos` (`id_producto`) ON DELETE CASCADE;
COMMIT;

/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
