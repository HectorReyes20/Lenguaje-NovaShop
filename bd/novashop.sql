-- MySQL dump 10.13  Distrib 8.0.43, for Win64 (x86_64)
--
-- Host: localhost    Database: novashop
-- ------------------------------------------------------
-- Server version	9.4.0

/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!50503 SET NAMES utf8 */;
/*!40103 SET @OLD_TIME_ZONE=@@TIME_ZONE */;
/*!40103 SET TIME_ZONE='+00:00' */;
/*!40014 SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0 */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;
/*!40111 SET @OLD_SQL_NOTES=@@SQL_NOTES, SQL_NOTES=0 */;

--
-- Table structure for table `carrito`
--

DROP TABLE IF EXISTS `carrito`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `carrito` (
  `id_carrito` bigint NOT NULL AUTO_INCREMENT,
  `id_usuario` bigint NOT NULL,
  `id_variante` bigint NOT NULL,
  `cantidad` int DEFAULT '1',
  `fecha_agregado` datetime DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id_carrito`),
  UNIQUE KEY `unique_carrito` (`id_usuario`,`id_variante`),
  UNIQUE KEY `UKoei4f3dy13f4twuknm4cjq0mf` (`id_usuario`,`id_variante`),
  KEY `idx_usuario` (`id_usuario`),
  KEY `idx_variante` (`id_variante`),
  CONSTRAINT `carrito_ibfk_1` FOREIGN KEY (`id_usuario`) REFERENCES `usuarios` (`id_usuario`) ON DELETE CASCADE,
  CONSTRAINT `carrito_ibfk_2` FOREIGN KEY (`id_variante`) REFERENCES `variantes_producto` (`id_variante`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `carrito`
--

LOCK TABLES `carrito` WRITE;
/*!40000 ALTER TABLE `carrito` DISABLE KEYS */;
/*!40000 ALTER TABLE `carrito` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `categorias`
--

DROP TABLE IF EXISTS `categorias`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `categorias` (
  `id_categoria` bigint NOT NULL AUTO_INCREMENT,
  `nombre` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL,
  `descripcion` text CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci,
  `id_categoria_padre` bigint DEFAULT NULL,
  `imagen_url` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `estado` enum('ACTIVO','INACTIVO') CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL,
  PRIMARY KEY (`id_categoria`),
  KEY `idx_padre` (`id_categoria_padre`),
  KEY `idx_estado` (`estado`),
  CONSTRAINT `categorias_ibfk_1` FOREIGN KEY (`id_categoria_padre`) REFERENCES `categorias` (`id_categoria`) ON DELETE SET NULL
) ENGINE=InnoDB AUTO_INCREMENT=8 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `categorias`
--

LOCK TABLES `categorias` WRITE;
/*!40000 ALTER TABLE `categorias` DISABLE KEYS */;
INSERT INTO `categorias` VALUES (1,'Hombres','Ropa para hombres',NULL,NULL,'ACTIVO'),(2,'Mujeres','Ropa para mujeres',NULL,NULL,'ACTIVO'),(3,'Niños','Ropa para niños',NULL,NULL,'ACTIVO'),(4,'Camisetas','Camisetas y polos',1,NULL,'ACTIVO'),(5,'Pantalones','Pantalones y jeans',1,NULL,'ACTIVO'),(6,'Vestidos','Vestidos elegantes y casuales',2,NULL,'ACTIVO'),(7,'Blusas','Blusas y tops',2,NULL,'ACTIVO');
/*!40000 ALTER TABLE `categorias` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `cupones`
--

DROP TABLE IF EXISTS `cupones`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `cupones` (
  `id_cupon` bigint NOT NULL AUTO_INCREMENT,
  `codigo` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL,
  `tipo_descuento` enum('MONTO_FIJO','PORCENTAJE') CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL,
  `valor_descuento` decimal(10,2) NOT NULL,
  `fecha_inicio` datetime DEFAULT CURRENT_TIMESTAMP,
  `fecha_expiracion` datetime NOT NULL,
  `usos_maximos` int DEFAULT '0',
  `usos_actuales` int DEFAULT '0',
  `monto_minimo` decimal(10,2) DEFAULT NULL,
  `estado` enum('ACTIVO','INACTIVO') CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL,
  PRIMARY KEY (`id_cupon`),
  UNIQUE KEY `codigo` (`codigo`),
  KEY `idx_codigo` (`codigo`),
  KEY `idx_estado` (`estado`)
) ENGINE=InnoDB AUTO_INCREMENT=3 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `cupones`
--

LOCK TABLES `cupones` WRITE;
/*!40000 ALTER TABLE `cupones` DISABLE KEYS */;
INSERT INTO `cupones` VALUES (1,'BIENVENIDA20','PORCENTAJE',20.00,'2025-10-17 17:33:53','2025-12-31 23:59:59',100,0,50.00,'ACTIVO'),(2,'ENVIOGRATIS','MONTO_FIJO',15.00,'2025-10-17 17:33:53','2025-10-31 23:59:59',50,0,100.00,'ACTIVO');
/*!40000 ALTER TABLE `cupones` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `detalle_pedido`
--

DROP TABLE IF EXISTS `detalle_pedido`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `detalle_pedido` (
  `id_detalle` bigint NOT NULL AUTO_INCREMENT,
  `id_pedido` bigint NOT NULL,
  `id_variante` bigint NOT NULL,
  `cantidad` int NOT NULL,
  `precio_unitario` decimal(10,2) NOT NULL,
  `subtotal` decimal(10,2) NOT NULL,
  PRIMARY KEY (`id_detalle`),
  KEY `idx_pedido` (`id_pedido`),
  KEY `idx_variante` (`id_variante`),
  CONSTRAINT `detalle_pedido_ibfk_1` FOREIGN KEY (`id_pedido`) REFERENCES `pedidos` (`id_pedido`) ON DELETE CASCADE,
  CONSTRAINT `detalle_pedido_ibfk_2` FOREIGN KEY (`id_variante`) REFERENCES `variantes_producto` (`id_variante`) ON DELETE RESTRICT
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `detalle_pedido`
--

LOCK TABLES `detalle_pedido` WRITE;
/*!40000 ALTER TABLE `detalle_pedido` DISABLE KEYS */;
/*!40000 ALTER TABLE `detalle_pedido` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `direcciones`
--

DROP TABLE IF EXISTS `direcciones`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `direcciones` (
  `id_direccion` bigint NOT NULL AUTO_INCREMENT,
  `id_usuario` bigint NOT NULL,
  `tipo` enum('AMBOS','ENVIO','FACTURACION') CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL,
  `nombre_completo` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL,
  `direccion_linea1` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL,
  `direccion_linea2` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `ciudad` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL,
  `departamento` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL,
  `codigo_postal` varchar(10) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `pais` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT 'Perú',
  `telefono` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL,
  `es_predeterminada` tinyint(1) DEFAULT '0',
  PRIMARY KEY (`id_direccion`),
  KEY `idx_usuario` (`id_usuario`),
  CONSTRAINT `direcciones_ibfk_1` FOREIGN KEY (`id_usuario`) REFERENCES `usuarios` (`id_usuario`) ON DELETE CASCADE
) ENGINE=InnoDB AUTO_INCREMENT=3 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `direcciones`
--

LOCK TABLES `direcciones` WRITE;
/*!40000 ALTER TABLE `direcciones` DISABLE KEYS */;
INSERT INTO `direcciones` VALUES (1,3,'ENVIO','Hector Reyes','Av.Lima','Entre las calles x y o','Lima','Lima','15001','Perú','997256008',1),(2,4,'FACTURACION','Joao Moises','AAHH Villa los Rosales Mz E lt 1','Casa verde','LIMA','Lima','12005','Perú','980234567',1);
/*!40000 ALTER TABLE `direcciones` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `favoritos`
--

DROP TABLE IF EXISTS `favoritos`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `favoritos` (
  `id_favorito` bigint NOT NULL AUTO_INCREMENT,
  `id_usuario` bigint NOT NULL,
  `id_producto` bigint NOT NULL,
  `fecha_agregado` datetime DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id_favorito`),
  UNIQUE KEY `unique_favorito` (`id_usuario`,`id_producto`),
  UNIQUE KEY `UKkqa9nhyi4y7exdwicjjd2h7jn` (`id_usuario`,`id_producto`),
  KEY `idx_usuario` (`id_usuario`),
  KEY `idx_producto` (`id_producto`),
  CONSTRAINT `favoritos_ibfk_1` FOREIGN KEY (`id_usuario`) REFERENCES `usuarios` (`id_usuario`) ON DELETE CASCADE,
  CONSTRAINT `favoritos_ibfk_2` FOREIGN KEY (`id_producto`) REFERENCES `productos` (`id_producto`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `favoritos`
--

LOCK TABLES `favoritos` WRITE;
/*!40000 ALTER TABLE `favoritos` DISABLE KEYS */;
/*!40000 ALTER TABLE `favoritos` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `imagenes_producto`
--

DROP TABLE IF EXISTS `imagenes_producto`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `imagenes_producto` (
  `id_imagen` bigint NOT NULL AUTO_INCREMENT,
  `id_producto` bigint NOT NULL,
  `url_imagen` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL,
  `orden` int DEFAULT '0',
  `es_principal` tinyint(1) DEFAULT '0',
  PRIMARY KEY (`id_imagen`),
  KEY `idx_producto` (`id_producto`),
  CONSTRAINT `imagenes_producto_ibfk_1` FOREIGN KEY (`id_producto`) REFERENCES `productos` (`id_producto`) ON DELETE CASCADE
) ENGINE=InnoDB AUTO_INCREMENT=35 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `imagenes_producto`
--

LOCK TABLES `imagenes_producto` WRITE;
/*!40000 ALTER TABLE `imagenes_producto` DISABLE KEYS */;
INSERT INTO `imagenes_producto` VALUES (1,1,'/images/productos/polo-azul-front.jpg',1,1),(2,1,'/images/productos/polo-azul-back.jpg',2,0),(3,2,'/images/productos/jean-slim-front.jpg',1,1),(4,3,'/images/productos/vestido-floral-front.jpg',1,1),(5,4,'/images/productos/polo-basico-negro.jpg',0,1),(6,5,'/images/productos/polo-estampado-floral.jpg',0,1),(7,6,'/images/productos/polo-pique-infantil.jpg',0,1),(8,7,'/images/productos/polo-manga-larga-gris.jpg',0,1),(9,8,'/images/productos/pantalon-chino-beige.jpg',0,1),(10,9,'/images/productos/leggings-deportivos-negros.jpg',0,1),(11,10,'/images/productos/jogger-cargo-nino.jpg',0,1),(12,11,'/images/productos/pantalon-palazzo-estampado.jpg',0,1),(13,12,'/images/productos/vestido-camisero-rayas.jpg',0,1),(14,13,'/images/productos/vestido-nina-unicornio.jpg',0,1),(15,14,'/images/productos/vestido-midi-negro.jpg',0,1),(16,15,'/images/productos/casaca-bomber-verde.jpg',0,1),(17,16,'/images/productos/chaqueta-denim-clasica.jpg',0,1),(18,17,'/images/productos/cortavientos-infantil-azul.jpg',0,1),(19,18,'/images/productos/abrigo-pano-gris.jpg',0,1),(20,19,'/images/productos/zapatillas-urbanas-blancas.jpg',0,1),(21,20,'/images/productos/zapatillas-running-mujer-rosa.jpg',0,1),(22,21,'/images/productos/zapatillas-luces-nino.jpg',0,1),(23,22,'/images/productos/botines-chelsea-marrones.jpg',0,1),(24,23,'/images/productos/gorra-negra-logo.jpg',0,1),(25,24,'/images/productos/bufanda-lana-gris.jpg',0,1),(26,25,'/images/productos/mochila-infantil-dinosaurios.jpg',0,1),(27,26,'/images/productos/cinturon-cuero-marron.jpg',0,1),(28,27,'/images/productos/set-gorro-guantes.jpg',0,1),(29,28,'/images/productos/camisa-oxford-celeste.jpg',0,1),(30,29,'/images/productos/falda-midi-plisada.jpg',0,1),(31,30,'/images/productos/shorts-jean-nino.jpg',0,1),(32,31,'/images/productos/blusa-blanca-encaje.jpg',0,1),(33,32,'/images/productos/sudadera-capucha-gris.jpg',0,1),(34,33,'/images/productos/sandalias-verano-mujer.jpg',0,1);
/*!40000 ALTER TABLE `imagenes_producto` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `pedidos`
--

DROP TABLE IF EXISTS `pedidos`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `pedidos` (
  `id_pedido` bigint NOT NULL AUTO_INCREMENT,
  `id_usuario` bigint NOT NULL,
  `numero_pedido` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL,
  `id_direccion_envio` bigint NOT NULL,
  `subtotal` decimal(10,2) NOT NULL,
  `costo_envio` decimal(10,2) DEFAULT '0.00',
  `descuento` decimal(10,2) DEFAULT '0.00',
  `total` decimal(10,2) NOT NULL,
  `estado` enum('CANCELADO','CONFIRMADO','ENTREGADO','ENVIADO','PENDIENTE','PROCESANDO') CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL,
  `metodo_pago` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL,
  `fecha_pedido` datetime DEFAULT CURRENT_TIMESTAMP,
  `fecha_actualizacion` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `notas` text CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci,
  PRIMARY KEY (`id_pedido`),
  UNIQUE KEY `numero_pedido` (`numero_pedido`),
  KEY `id_direccion_envio` (`id_direccion_envio`),
  KEY `idx_usuario` (`id_usuario`),
  KEY `idx_numero_pedido` (`numero_pedido`),
  KEY `idx_estado` (`estado`),
  KEY `idx_fecha` (`fecha_pedido`),
  CONSTRAINT `pedidos_ibfk_1` FOREIGN KEY (`id_usuario`) REFERENCES `usuarios` (`id_usuario`) ON DELETE RESTRICT,
  CONSTRAINT `pedidos_ibfk_2` FOREIGN KEY (`id_direccion_envio`) REFERENCES `direcciones` (`id_direccion`) ON DELETE RESTRICT
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `pedidos`
--

LOCK TABLES `pedidos` WRITE;
/*!40000 ALTER TABLE `pedidos` DISABLE KEYS */;
/*!40000 ALTER TABLE `pedidos` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `productos`
--

DROP TABLE IF EXISTS `productos`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `productos` (
  `id_producto` bigint NOT NULL AUTO_INCREMENT,
  `nombre` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL,
  `descripcion` text CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL,
  `id_categoria` bigint NOT NULL,
  `precio_base` decimal(10,2) NOT NULL,
  `precio_oferta` decimal(10,2) DEFAULT NULL,
  `marca` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `genero` enum('HOMBRE','MUJER','NINA','NINO','UNISEX') CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `material` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `fecha_creacion` datetime DEFAULT CURRENT_TIMESTAMP,
  `estado` enum('ACTIVO','AGOTADO','INACTIVO') CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL,
  `destacado` tinyint(1) DEFAULT '0',
  PRIMARY KEY (`id_producto`),
  KEY `idx_categoria` (`id_categoria`),
  KEY `idx_estado` (`estado`),
  KEY `idx_destacado` (`destacado`),
  KEY `idx_genero` (`genero`),
  CONSTRAINT `productos_ibfk_1` FOREIGN KEY (`id_categoria`) REFERENCES `categorias` (`id_categoria`) ON DELETE RESTRICT
) ENGINE=InnoDB AUTO_INCREMENT=34 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `productos`
--

LOCK TABLES `productos` WRITE;
/*!40000 ALTER TABLE `productos` DISABLE KEYS */;
INSERT INTO `productos` VALUES (1,'Camiseta Polo Clásica','Polo de algodón 100% con corte clásico',4,89.90,NULL,'NovaStyle','HOMBRE',NULL,'2025-10-17 17:33:53','ACTIVO',1),(2,'Jean Slim Fit','Jean de mezclilla con ajuste slim',5,149.90,119.90,'NovaJeans','HOMBRE',NULL,'2025-10-17 17:33:53','ACTIVO',1),(3,'Vestido Floral Elegante','Vestido largo con estampado floral',6,199.90,NULL,'NovaFemme','MUJER',NULL,'2025-10-17 17:33:53','ACTIVO',1),(4,'Polo Básico Negro','Un polo esencial de algodón suave, perfecto para cualquier ocasión.',1,49.90,NULL,'NovaBasics','HOMBRE',NULL,'2025-10-18 12:46:55','ACTIVO',0),(5,'Polo Estampado Floral','Polo con vibrante estampado floral, ideal para el verano.',1,69.90,59.90,'NovaStyle','MUJER',NULL,'2025-10-18 12:46:55','ACTIVO',0),(6,'Polo Piqué Infantil','Polo clásico tipo piqué para niños, cómodo y duradero.',1,39.90,NULL,'NovaKids','NINO',NULL,'2025-10-18 12:46:55','ACTIVO',0),(7,'Polo Manga Larga Gris','Polo de manga larga en color gris melange, versátil y abrigador.',1,79.90,NULL,'NovaBasics','HOMBRE',NULL,'2025-10-18 12:46:55','ACTIVO',0),(8,'Pantalón Chino Beige','Pantalón chino clásico en color beige, corte slim.',2,129.90,109.90,'NovaStyle','HOMBRE',NULL,'2025-10-18 12:46:55','ACTIVO',0),(9,'Leggings Deportivos Negros','Leggings cómodos y elásticos para mujer, ideales para el gimnasio.',2,89.90,NULL,'NovaFit','MUJER',NULL,'2025-10-18 12:46:55','ACTIVO',0),(10,'Jogger Cargo Niño','Pantalón jogger con bolsillos cargo para niños.',2,69.90,NULL,'NovaKids','NINO',NULL,'2025-10-18 12:46:55','ACTIVO',0),(11,'Pantalón Palazzo Estampado','Pantalón palazzo fluido con estampado bohemio.',2,149.90,129.90,'NovaStyle','MUJER',NULL,'2025-10-18 12:46:55','ACTIVO',0),(12,'Vestido Camisero Rayas','Vestido camisero fresco con estampado de rayas verticales.',3,139.90,NULL,'NovaStyle','MUJER',NULL,'2025-10-18 12:46:55','ACTIVO',0),(13,'Vestido Niña Unicornio','Vestido infantil con estampado divertido de unicornios.',3,79.90,69.90,'NovaKids','NINO',NULL,'2025-10-18 12:46:55','ACTIVO',0),(14,'Vestido Midi Negro','Elegante vestido midi negro, un básico de armario.',3,159.90,NULL,'NovaChic','MUJER',NULL,'2025-10-18 12:46:55','ACTIVO',0),(15,'Casaca Bomber Verde','Casaca estilo bomber en color verde olivo.',4,199.90,179.90,'NovaUrban','HOMBRE',NULL,'2025-10-18 12:46:55','ACTIVO',0),(16,'Chaqueta Denim Clásica','Chaqueta de jean atemporal para mujer.',4,189.90,NULL,'NovaDenim','MUJER',NULL,'2025-10-18 12:46:55','ACTIVO',0),(17,'Cortavientos Infantil Azul','Casaca cortavientos ligera e impermeable para niños.',4,99.90,NULL,'NovaKids','NINO',NULL,'2025-10-18 12:46:55','ACTIVO',0),(18,'Abrigo de Paño Gris','Abrigo elegante de paño en color gris oscuro.',4,349.90,299.90,'NovaChic','MUJER',NULL,'2025-10-18 12:46:55','ACTIVO',0),(19,'Zapatillas Urbanas Blancas','Zapatillas casuales blancas de cuero sintético.',5,159.90,NULL,'NovaStep','HOMBRE',NULL,'2025-10-18 12:46:55','ACTIVO',0),(20,'Zapatillas Running Mujer Rosa','Zapatillas ligeras para correr, color rosa.',5,229.90,199.90,'NovaFit','MUJER',NULL,'2025-10-18 12:46:55','ACTIVO',0),(21,'Zapatillas con Luces Niño','Zapatillas infantiles con luces LED en la suela.',5,119.90,NULL,'NovaKids','NINO',NULL,'2025-10-18 12:46:55','ACTIVO',0),(22,'Botines Chelsea Marrones','Botines estilo Chelsea de cuero para hombre.',5,299.90,NULL,'NovaStep','HOMBRE',NULL,'2025-10-18 12:46:55','ACTIVO',0),(23,'Gorra Negra Logo Bordado','Gorra clásica negra con el logo NovaShop bordado.',6,59.90,49.90,'NovaAcc','HOMBRE',NULL,'2025-10-18 12:46:55','ACTIVO',0),(24,'Bufanda de Lana Gris','Bufanda suave de lana para invierno.',6,79.90,NULL,'NovaAcc','MUJER',NULL,'2025-10-18 12:46:55','ACTIVO',0),(25,'Mochila Infantil Dinosaurios','Mochila escolar con estampado de dinosaurios.',6,89.90,NULL,'NovaKids','NINO',NULL,'2025-10-18 12:46:55','ACTIVO',0),(26,'Cinturón de Cuero Marrón','Cinturón clásico de cuero genuino.',6,99.90,79.90,'NovaAcc','HOMBRE',NULL,'2025-10-18 12:46:55','ACTIVO',0),(27,'Set de Gorro y Guantes','Conjunto de gorro y guantes de punto.',6,69.90,NULL,'NovaAcc','MUJER',NULL,'2025-10-18 12:46:55','ACTIVO',0),(28,'Camisa Oxford Celeste','Camisa de vestir Oxford, corte regular.',1,119.90,NULL,'NovaStyle','HOMBRE',NULL,'2025-10-18 12:46:55','ACTIVO',0),(29,'Falda Midi Plisada','Falda plisada de largo midi, color verde esmeralda.',3,109.90,89.90,'NovaChic','MUJER',NULL,'2025-10-18 12:46:55','ACTIVO',0),(30,'Shorts de Jean Niño','Shorts de mezclilla cómodos para niño.',2,49.90,NULL,'NovaKids','NINO',NULL,'2025-10-18 12:46:55','ACTIVO',0),(31,'Blusa Blanca Encaje','Blusa elegante con detalles de encaje.',1,99.90,NULL,'NovaChic','MUJER',NULL,'2025-10-18 12:46:55','ACTIVO',0),(32,'Sudadera con Capucha Gris','Sudadera básica con capucha, color gris.',4,139.90,119.90,'NovaUrban','HOMBRE',NULL,'2025-10-18 12:46:55','ACTIVO',0),(33,'Sandalias de Verano Mujer','Sandalias planas cómodas para el verano.',5,79.90,NULL,'NovaStep','MUJER',NULL,'2025-10-18 12:46:55','ACTIVO',0);
/*!40000 ALTER TABLE `productos` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `resenas`
--

DROP TABLE IF EXISTS `resenas`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `resenas` (
  `id_resena` bigint NOT NULL AUTO_INCREMENT,
  `id_producto` bigint NOT NULL,
  `id_usuario` bigint NOT NULL,
  `calificacion` int NOT NULL,
  `titulo` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `comentario` text CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci,
  `fecha_resena` datetime DEFAULT CURRENT_TIMESTAMP,
  `verificada` tinyint(1) DEFAULT '0',
  PRIMARY KEY (`id_resena`),
  KEY `idx_producto` (`id_producto`),
  KEY `idx_usuario` (`id_usuario`),
  KEY `idx_calificacion` (`calificacion`),
  CONSTRAINT `resenas_ibfk_1` FOREIGN KEY (`id_producto`) REFERENCES `productos` (`id_producto`) ON DELETE CASCADE,
  CONSTRAINT `resenas_ibfk_2` FOREIGN KEY (`id_usuario`) REFERENCES `usuarios` (`id_usuario`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `resenas`
--

LOCK TABLES `resenas` WRITE;
/*!40000 ALTER TABLE `resenas` DISABLE KEYS */;
/*!40000 ALTER TABLE `resenas` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `usuarios`
--

DROP TABLE IF EXISTS `usuarios`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `usuarios` (
  `id_usuario` bigint NOT NULL AUTO_INCREMENT,
  `nombre` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL,
  `apellido` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL,
  `email` varchar(150) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL,
  `password` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL,
  `telefono` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `fecha_registro` datetime DEFAULT CURRENT_TIMESTAMP,
  `estado` enum('ACTIVO','BLOQUEADO','INACTIVO') CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL,
  `rol` enum('ADMIN','CLIENTE') CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL,
  PRIMARY KEY (`id_usuario`),
  UNIQUE KEY `email` (`email`),
  KEY `idx_email` (`email`),
  KEY `idx_estado` (`estado`)
) ENGINE=InnoDB AUTO_INCREMENT=5 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `usuarios`
--

LOCK TABLES `usuarios` WRITE;
/*!40000 ALTER TABLE `usuarios` DISABLE KEYS */;
INSERT INTO `usuarios` VALUES (1,'Admin','NovaShop','admin@novashop.com','$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy','999888777','2025-10-17 17:33:53','ACTIVO','ADMIN'),(2,'Juan','Pérez','juan@example.com','$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy','987654321','2025-10-17 17:33:53','ACTIVO','CLIENTE'),(3,'Hector','Reye','fatama@gmail.com','$2a$10$bvxUCE.tLkY4HlSf1UuKcu.TGlbU4zE.7ZJzXYWoKLEMshfKd3tFu','987456125','2025-10-17 17:45:33','ACTIVO','CLIENTE'),(4,'Joao','Inga','joaumoises@gmail.com','$2a$10$dgLXTBpOD1p9iyXWE.2G.u57rcx4bPe52X/dJSGUr1VyMyW/PYb3W','902644490','2025-10-18 10:26:34','ACTIVO','CLIENTE');
/*!40000 ALTER TABLE `usuarios` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `variantes_producto`
--

DROP TABLE IF EXISTS `variantes_producto`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `variantes_producto` (
  `id_variante` bigint NOT NULL AUTO_INCREMENT,
  `id_producto` bigint NOT NULL,
  `talla` varchar(10) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL,
  `color` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL,
  `codigo_sku` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL,
  `stock` int DEFAULT '0',
  `precio_adicional` decimal(10,2) DEFAULT '0.00',
  PRIMARY KEY (`id_variante`),
  UNIQUE KEY `codigo_sku` (`codigo_sku`),
  KEY `idx_producto` (`id_producto`),
  KEY `idx_sku` (`codigo_sku`),
  KEY `idx_stock` (`stock`),
  CONSTRAINT `variantes_producto_ibfk_1` FOREIGN KEY (`id_producto`) REFERENCES `productos` (`id_producto`) ON DELETE CASCADE
) ENGINE=InnoDB AUTO_INCREMENT=9 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `variantes_producto`
--

LOCK TABLES `variantes_producto` WRITE;
/*!40000 ALTER TABLE `variantes_producto` DISABLE KEYS */;
INSERT INTO `variantes_producto` VALUES (1,1,'M','Azul','POL-M-AZU',25,0.00),(2,1,'M','Rojo','POL-M-ROJ',15,0.00),(3,1,'L','Azul','POL-L-AZU',30,0.00),(4,1,'L','Rojo','POL-L-ROJ',10,0.00),(5,2,'32','Azul Oscuro','JEA-32-AZO',20,0.00),(6,2,'34','Azul Oscuro','JEA-34-AZO',18,0.00),(7,3,'S','Multicolor','VES-S-MUL',12,0.00),(8,3,'M','Multicolor','VES-M-MUL',15,0.00);
/*!40000 ALTER TABLE `variantes_producto` ENABLE KEYS */;
UNLOCK TABLES;
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

-- Dump completed on 2025-10-18 13:15:53
