-- =====================================================================
-- WiseBooks - esquema de la base de datos (MariaDB / XAMPP)
-- Generado desde las entidades JPA (util/PruebaTablas) y exportado con
-- mysqldump --no-data. Si cambian las entidades, vuelve a generarlo.
-- =====================================================================

CREATE DATABASE IF NOT EXISTS wisebooks
  CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
USE wisebooks;

-- Las tablas van en orden alfabetico; se desactivan las llaves foraneas
-- mientras se crean para que el orden no importe.
SET FOREIGN_KEY_CHECKS = 0;

CREATE TABLE `auditoria` (
  `id_auditoria` bigint(20) NOT NULL AUTO_INCREMENT,
  `accion` enum('ACTUALIZAR','CAMBIO_ESTADO','CREAR','ELIMINAR','EXPORTAR','LOGIN','LOGOUT','RESPALDO') NOT NULL,
  `detalle` varchar(500) DEFAULT NULL,
  `entidad` varchar(50) DEFAULT NULL,
  `fecha` datetime(6) NOT NULL,
  `id_registro` bigint(20) DEFAULT NULL,
  `ip` varchar(45) DEFAULT NULL,
  `id_usuario` bigint(20) DEFAULT NULL,
  PRIMARY KEY (`id_auditoria`),
  KEY `idx_auditoria_fecha` (`fecha`),
  KEY `idx_auditoria_usuario_fecha` (`id_usuario`,`fecha`),
  KEY `idx_auditoria_entidad` (`entidad`,`id_registro`),
  CONSTRAINT `FKmc60a5takcm6k0hp4psrdnttm` FOREIGN KEY (`id_usuario`) REFERENCES `usuarios` (`id_usuario`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE `autores` (
  `id_autor` bigint(20) NOT NULL AUTO_INCREMENT,
  `activo` bit(1) NOT NULL,
  `nombre` varchar(150) NOT NULL,
  PRIMARY KEY (`id_autor`),
  UNIQUE KEY `UKbmqig88fabri853omysia6j3t` (`nombre`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE `categorias` (
  `id_categoria` bigint(20) NOT NULL AUTO_INCREMENT,
  `activo` bit(1) NOT NULL,
  `nombre` varchar(100) NOT NULL,
  PRIMARY KEY (`id_categoria`),
  UNIQUE KEY `UKqcog8b7hps1hioi9onqwjdt6y` (`nombre`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE `copias_seguridad` (
  `id_copia` bigint(20) NOT NULL AUTO_INCREMENT,
  `creado_en` datetime(6) NOT NULL,
  `exitosa` bit(1) NOT NULL,
  `mensaje_error` varchar(500) DEFAULT NULL,
  `nombre_archivo` varchar(255) NOT NULL,
  `tamano_bytes` bigint(20) DEFAULT NULL,
  `creado_por` bigint(20) NOT NULL,
  PRIMARY KEY (`id_copia`),
  UNIQUE KEY `UKf2qgx7up9vwifo7ar4tqcifp7` (`nombre_archivo`),
  KEY `idx_copias_creado_en` (`creado_en`),
  KEY `FK46npagfh1tn1lkwkmb9w5yxnr` (`creado_por`),
  CONSTRAINT `FK46npagfh1tn1lkwkmb9w5yxnr` FOREIGN KEY (`creado_por`) REFERENCES `usuarios` (`id_usuario`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE `editoriales` (
  `id_editorial` bigint(20) NOT NULL AUTO_INCREMENT,
  `activo` bit(1) NOT NULL,
  `nombre` varchar(150) NOT NULL,
  PRIMARY KEY (`id_editorial`),
  UNIQUE KEY `UKa1g9sws2edh4alrvuaok2no2f` (`nombre`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE `favoritos` (
  `id_usuario` bigint(20) NOT NULL,
  `id_libro` bigint(20) NOT NULL,
  PRIMARY KEY (`id_usuario`,`id_libro`),
  KEY `FKmfvhjx1mvi7drncnwk5ajfn3s` (`id_libro`),
  CONSTRAINT `FK8ixb8kaxp9dq6p40cgvhqc96w` FOREIGN KEY (`id_usuario`) REFERENCES `usuarios` (`id_usuario`),
  CONSTRAINT `FKmfvhjx1mvi7drncnwk5ajfn3s` FOREIGN KEY (`id_libro`) REFERENCES `libros` (`id_libro`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE `generos` (
  `id_genero` bigint(20) NOT NULL AUTO_INCREMENT,
  `activo` bit(1) NOT NULL,
  `nombre` varchar(100) NOT NULL,
  PRIMARY KEY (`id_genero`),
  UNIQUE KEY `UK9u5buu0dehagjdoqm3wv6orm3` (`nombre`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE `libros` (
  `id_libro` bigint(20) NOT NULL AUTO_INCREMENT,
  `activo` bit(1) NOT NULL,
  `actualizado_en` datetime(6) NOT NULL,
  `creado_en` datetime(6) NOT NULL,
  `descripcion` text DEFAULT NULL,
  `fecha_publicacion` date DEFAULT NULL,
  `isbn` varchar(20) DEFAULT NULL,
  `portada_url` varchar(500) DEFAULT NULL,
  `stock_disponible` int(11) NOT NULL,
  `stock_total` int(11) NOT NULL,
  `titulo` varchar(255) NOT NULL,
  `ubicacion` varchar(100) DEFAULT NULL,
  `version` bigint(20) NOT NULL,
  `id_categoria` bigint(20) NOT NULL,
  `id_editorial` bigint(20) DEFAULT NULL,
  `id_genero` bigint(20) DEFAULT NULL,
  PRIMARY KEY (`id_libro`),
  UNIQUE KEY `UK278ekmuxh4b6i98mqypvacdcu` (`isbn`),
  KEY `idx_libros_titulo` (`titulo`),
  KEY `FKgxl2q4i4hj2wnbdjfgo5h2b1b` (`id_categoria`),
  KEY `FK1pnx393ebw2kcvs9tkpeh32xx` (`id_editorial`),
  KEY `FKma109k9g6238y4ntopcu6iw1y` (`id_genero`),
  CONSTRAINT `FK1pnx393ebw2kcvs9tkpeh32xx` FOREIGN KEY (`id_editorial`) REFERENCES `editoriales` (`id_editorial`),
  CONSTRAINT `FKgxl2q4i4hj2wnbdjfgo5h2b1b` FOREIGN KEY (`id_categoria`) REFERENCES `categorias` (`id_categoria`),
  CONSTRAINT `FKma109k9g6238y4ntopcu6iw1y` FOREIGN KEY (`id_genero`) REFERENCES `generos` (`id_genero`),
  CONSTRAINT `CONSTRAINT_1` CHECK (`stock_disponible` >= 0 and `stock_disponible` <= `stock_total`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE `libros_autores` (
  `id_libro` bigint(20) NOT NULL,
  `id_autor` bigint(20) NOT NULL,
  PRIMARY KEY (`id_libro`,`id_autor`),
  KEY `FK9r7n05moyk8e83br7e8hnlury` (`id_autor`),
  CONSTRAINT `FK9r7n05moyk8e83br7e8hnlury` FOREIGN KEY (`id_autor`) REFERENCES `autores` (`id_autor`),
  CONSTRAINT `FKcc58n8nwp6ei7of1pp1s1seol` FOREIGN KEY (`id_libro`) REFERENCES `libros` (`id_libro`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE `prestamos` (
  `id_prestamo` bigint(20) NOT NULL AUTO_INCREMENT,
  `estado` enum('ACTIVO','ANULADO','DEVUELTO','PERDIDO') NOT NULL,
  `fecha_devolucion` datetime(6) DEFAULT NULL,
  `fecha_limite` datetime(6) NOT NULL,
  `fecha_prestamo` datetime(6) NOT NULL,
  `renovaciones` int(11) NOT NULL,
  `version` bigint(20) NOT NULL,
  `id_libro` bigint(20) NOT NULL,
  `recibido_por` bigint(20) DEFAULT NULL,
  `registrado_por` bigint(20) NOT NULL,
  `id_reserva` bigint(20) DEFAULT NULL,
  `id_usuario` bigint(20) NOT NULL,
  PRIMARY KEY (`id_prestamo`),
  UNIQUE KEY `UKqfi4dtwjv0xu7ot1kxxw6pv0c` (`id_reserva`),
  KEY `idx_prestamos_estado_limite` (`estado`,`fecha_limite`),
  KEY `FKsifvu6veoseewjhlmnmva95oh` (`id_libro`),
  KEY `FK2pk8c1e0jvdyf43nj8faoalav` (`recibido_por`),
  KEY `FKs9bi03vkqhh6iprtvrp0nnmoy` (`registrado_por`),
  KEY `FK86qrtaakkw8dli50at9fpo51x` (`id_usuario`),
  CONSTRAINT `FK2pk8c1e0jvdyf43nj8faoalav` FOREIGN KEY (`recibido_por`) REFERENCES `usuarios` (`id_usuario`),
  CONSTRAINT `FK86qrtaakkw8dli50at9fpo51x` FOREIGN KEY (`id_usuario`) REFERENCES `usuarios` (`id_usuario`),
  CONSTRAINT `FKdc31rspf0xofnmyltyw27rgvd` FOREIGN KEY (`id_reserva`) REFERENCES `reservas` (`id_reserva`),
  CONSTRAINT `FKs9bi03vkqhh6iprtvrp0nnmoy` FOREIGN KEY (`registrado_por`) REFERENCES `usuarios` (`id_usuario`),
  CONSTRAINT `FKsifvu6veoseewjhlmnmva95oh` FOREIGN KEY (`id_libro`) REFERENCES `libros` (`id_libro`),
  CONSTRAINT `CONSTRAINT_1` CHECK (`fecha_limite` >= `fecha_prestamo` and (`fecha_devolucion` is null or `fecha_devolucion` >= `fecha_prestamo`))
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE `proveedores` (
  `id_proveedor` bigint(20) NOT NULL AUTO_INCREMENT,
  `activo` bit(1) NOT NULL,
  `actualizado_en` datetime(6) NOT NULL,
  `correo` varchar(150) DEFAULT NULL,
  `creado_en` datetime(6) NOT NULL,
  `direccion` varchar(255) DEFAULT NULL,
  `nit` varchar(30) DEFAULT NULL,
  `nombre` varchar(150) NOT NULL,
  `persona_contacto` varchar(150) DEFAULT NULL,
  `telefono` varchar(20) DEFAULT NULL,
  PRIMARY KEY (`id_proveedor`),
  UNIQUE KEY `UK3ts51lg5ducpe3ag146g6sye7` (`nombre`),
  UNIQUE KEY `UK4viyt15dd71w7i0wu9684deq7` (`nit`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE `reservas` (
  `id_reserva` bigint(20) NOT NULL AUTO_INCREMENT,
  `estado` enum('APROBADA','CANCELADA','PENDIENTE','RECHAZADA','RETIRADA','VENCIDA') NOT NULL,
  `fecha_limite_retiro` datetime(6) DEFAULT NULL,
  `fecha_reserva` datetime(6) NOT NULL,
  `observaciones` varchar(255) DEFAULT NULL,
  `resuelta_en` datetime(6) DEFAULT NULL,
  `version` bigint(20) NOT NULL,
  `id_libro` bigint(20) NOT NULL,
  `resuelta_por` bigint(20) DEFAULT NULL,
  `id_usuario` bigint(20) NOT NULL,
  PRIMARY KEY (`id_reserva`),
  KEY `idx_reservas_estado_limite` (`estado`,`fecha_limite_retiro`),
  KEY `FKldwjl9x4njptx74aqi4gchny` (`id_libro`),
  KEY `FKdeuvrh9vdtt3hxcx65wi30v60` (`resuelta_por`),
  KEY `FKhjryje6u1cr0d4dubad1jja6` (`id_usuario`),
  CONSTRAINT `FKdeuvrh9vdtt3hxcx65wi30v60` FOREIGN KEY (`resuelta_por`) REFERENCES `usuarios` (`id_usuario`),
  CONSTRAINT `FKhjryje6u1cr0d4dubad1jja6` FOREIGN KEY (`id_usuario`) REFERENCES `usuarios` (`id_usuario`),
  CONSTRAINT `FKldwjl9x4njptx74aqi4gchny` FOREIGN KEY (`id_libro`) REFERENCES `libros` (`id_libro`),
  CONSTRAINT `CONSTRAINT_1` CHECK (`fecha_limite_retiro` is null or `fecha_limite_retiro` >= `fecha_reserva`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE `sanciones` (
  `id_sancion` bigint(20) NOT NULL AUTO_INCREMENT,
  `detalle` varchar(255) DEFAULT NULL,
  `dias_retraso` int(11) DEFAULT NULL,
  `estado` enum('ANULADA','PAGADA','PENDIENTE') NOT NULL,
  `fecha_emision` datetime(6) NOT NULL,
  `fecha_pago` datetime(6) DEFAULT NULL,
  `importe` decimal(10,2) NOT NULL,
  `tipo` enum('LIBRO_DANADO','LIBRO_PERDIDO','MORA','OTRO') NOT NULL,
  `id_prestamo` bigint(20) DEFAULT NULL,
  `registrado_por` bigint(20) DEFAULT NULL,
  `id_usuario` bigint(20) NOT NULL,
  PRIMARY KEY (`id_sancion`),
  UNIQUE KEY `uk_sanciones_prestamo_tipo` (`id_prestamo`,`tipo`),
  KEY `idx_sanciones_usuario_estado` (`id_usuario`,`estado`),
  KEY `FKan4x91rygwdjcytonnjc638gt` (`registrado_por`),
  CONSTRAINT `FKan4x91rygwdjcytonnjc638gt` FOREIGN KEY (`registrado_por`) REFERENCES `usuarios` (`id_usuario`),
  CONSTRAINT `FKby60s54dk9ocyri2nc9xck4ib` FOREIGN KEY (`id_prestamo`) REFERENCES `prestamos` (`id_prestamo`),
  CONSTRAINT `FKf7v2a0ytf020l510rwo8c5f1i` FOREIGN KEY (`id_usuario`) REFERENCES `usuarios` (`id_usuario`),
  CONSTRAINT `CONSTRAINT_1` CHECK (`importe` >= 0 and (`estado` <> 'PAGADA' or `fecha_pago` is not null) and (`tipo` <> 'MORA' or `dias_retraso` is not null and `dias_retraso` > 0))
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE `usuarios` (
  `id_usuario` bigint(20) NOT NULL AUTO_INCREMENT,
  `actualizado_en` datetime(6) NOT NULL,
  `apellidos` varchar(100) NOT NULL,
  `contrasena_hash` varchar(255) NOT NULL,
  `correo` varchar(150) NOT NULL,
  `creado_en` datetime(6) NOT NULL,
  `documento` varchar(20) NOT NULL,
  `estado` enum('ACTIVO','INACTIVO','SUSPENDIDO') NOT NULL,
  `nombre` varchar(100) NOT NULL,
  `rol` enum('ADMINISTRADOR','BIBLIOTECARIO','LECTOR') NOT NULL,
  `telefono` varchar(20) DEFAULT NULL,
  `tipo_documento` enum('CC','CE','PA','TI') NOT NULL,
  PRIMARY KEY (`id_usuario`),
  UNIQUE KEY `uk_usuarios_documento` (`tipo_documento`,`documento`),
  UNIQUE KEY `UKcdmw5hxlfj78uf4997i3qyyw5` (`correo`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

SET FOREIGN_KEY_CHECKS = 1;
