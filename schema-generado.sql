
    create table auditoria (
        fecha datetime(6) not null,
        id_auditoria bigint not null auto_increment,
        id_registro bigint,
        id_usuario bigint,
        ip varchar(45),
        entidad varchar(50),
        detalle varchar(500),
        accion enum ('ACTUALIZAR','CAMBIO_ESTADO','CREAR','ELIMINAR','EXPORTAR','LOGIN','LOGOUT','RESPALDO') not null,
        primary key (id_auditoria)
    ) engine=InnoDB;

    create table autores (
        activo bit not null,
        id_autor bigint not null auto_increment,
        nombre varchar(150) not null,
        primary key (id_autor)
    ) engine=InnoDB;

    create table categorias (
        activo bit not null,
        id_categoria bigint not null auto_increment,
        nombre varchar(100) not null,
        primary key (id_categoria)
    ) engine=InnoDB;

    create table copias_seguridad (
        exitosa bit not null,
        creado_en datetime(6) not null,
        creado_por bigint not null,
        id_copia bigint not null auto_increment,
        tamano_bytes bigint,
        mensaje_error varchar(500),
        nombre_archivo varchar(255) not null,
        primary key (id_copia)
    ) engine=InnoDB;

    create table editoriales (
        activo bit not null,
        id_editorial bigint not null auto_increment,
        nombre varchar(150) not null,
        primary key (id_editorial)
    ) engine=InnoDB;

    create table favoritos (
        id_libro bigint not null,
        id_usuario bigint not null,
        primary key (id_libro, id_usuario)
    ) engine=InnoDB;

    create table generos (
        activo bit not null,
        id_genero bigint not null auto_increment,
        nombre varchar(100) not null,
        primary key (id_genero)
    ) engine=InnoDB;

    create table libros (
        activo bit not null,
        fecha_publicacion date,
        stock_disponible integer not null,
        stock_total integer not null,
        actualizado_en datetime(6) not null,
        creado_en datetime(6) not null,
        id_categoria bigint not null,
        id_editorial bigint,
        id_genero bigint,
        id_libro bigint not null auto_increment,
        version bigint not null,
        isbn varchar(20),
        ubicacion varchar(100),
        portada_url varchar(500),
        descripcion TEXT,
        titulo varchar(255) not null,
        primary key (id_libro),
        check (stock_disponible >= 0 AND stock_disponible <= stock_total)
    ) engine=InnoDB;

    create table libros_autores (
        id_autor bigint not null,
        id_libro bigint not null,
        primary key (id_autor, id_libro)
    ) engine=InnoDB;

    create table prestamos (
        renovaciones integer not null,
        fecha_devolucion datetime(6),
        fecha_limite datetime(6) not null,
        fecha_prestamo datetime(6) not null,
        id_libro bigint not null,
        id_prestamo bigint not null auto_increment,
        id_reserva bigint,
        id_usuario bigint not null,
        recibido_por bigint,
        registrado_por bigint not null,
        estado enum ('ACTIVO','ANULADO','DEVUELTO','PERDIDO') not null,
        primary key (id_prestamo),
        check (fecha_limite >= fecha_prestamo AND (fecha_devolucion IS NULL OR fecha_devolucion >= fecha_prestamo))
    ) engine=InnoDB;

    create table proveedores (
        activo bit not null,
        actualizado_en datetime(6) not null,
        creado_en datetime(6) not null,
        id_proveedor bigint not null auto_increment,
        telefono varchar(20),
        nit varchar(30),
        correo varchar(150),
        nombre varchar(150) not null,
        persona_contacto varchar(150),
        direccion varchar(255),
        primary key (id_proveedor)
    ) engine=InnoDB;

    create table reservas (
        fecha_limite_retiro datetime(6),
        fecha_reserva datetime(6) not null,
        id_libro bigint not null,
        id_reserva bigint not null auto_increment,
        id_usuario bigint not null,
        resuelta_en datetime(6),
        resuelta_por bigint,
        observaciones varchar(255),
        estado enum ('APROBADA','CANCELADA','PENDIENTE','RECHAZADA','RETIRADA','VENCIDA') not null,
        primary key (id_reserva),
        check (fecha_limite_retiro IS NULL OR fecha_limite_retiro >= fecha_reserva)
    ) engine=InnoDB;

    create table sanciones (
        dias_retraso integer,
        importe decimal(10,2) not null,
        fecha_emision datetime(6) not null,
        fecha_pago datetime(6),
        id_prestamo bigint,
        id_sancion bigint not null auto_increment,
        id_usuario bigint not null,
        registrado_por bigint,
        detalle varchar(255),
        estado enum ('ANULADA','PAGADA','PENDIENTE') not null,
        tipo enum ('LIBRO_DANADO','LIBRO_PERDIDO','MORA','OTRO') not null,
        primary key (id_sancion)
    ) engine=InnoDB;

    create table usuarios (
        actualizado_en datetime(6) not null,
        creado_en datetime(6) not null,
        id_usuario bigint not null auto_increment,
        documento varchar(20) not null,
        telefono varchar(20),
        apellidos varchar(100) not null,
        nombre varchar(100) not null,
        correo varchar(150) not null,
        contrasena_hash varchar(255) not null,
        estado enum ('ACTIVO','INACTIVO','SUSPENDIDO') not null,
        rol enum ('ADMINISTRADOR','BIBLIOTECARIO','LECTOR') not null,
        tipo_documento enum ('CC','CE','PA','TI') not null,
        primary key (id_usuario)
    ) engine=InnoDB;

    create index idx_auditoria_fecha 
       on auditoria (fecha);

    create index idx_auditoria_usuario_fecha 
       on auditoria (id_usuario, fecha);

    create index idx_auditoria_entidad 
       on auditoria (entidad, id_registro);

    alter table autores 
       add constraint UKbmqig88fabri853omysia6j3t unique (nombre);

    alter table categorias 
       add constraint UKqcog8b7hps1hioi9onqwjdt6y unique (nombre);

    create index idx_copias_creado_en 
       on copias_seguridad (creado_en);

    alter table copias_seguridad 
       add constraint UKf2qgx7up9vwifo7ar4tqcifp7 unique (nombre_archivo);

    alter table editoriales 
       add constraint UKa1g9sws2edh4alrvuaok2no2f unique (nombre);

    alter table generos 
       add constraint UK9u5buu0dehagjdoqm3wv6orm3 unique (nombre);

    create index idx_libros_titulo 
       on libros (titulo);

    alter table libros 
       add constraint UK278ekmuxh4b6i98mqypvacdcu unique (isbn);

    create index idx_prestamos_estado_limite 
       on prestamos (estado, fecha_limite);

    alter table prestamos 
       add constraint UKqfi4dtwjv0xu7ot1kxxw6pv0c unique (id_reserva);

    alter table proveedores 
       add constraint UK4viyt15dd71w7i0wu9684deq7 unique (nit);

    alter table proveedores 
       add constraint UK3ts51lg5ducpe3ag146g6sye7 unique (nombre);

    create index idx_reservas_estado_limite 
       on reservas (estado, fecha_limite_retiro);

    alter table sanciones 
       add constraint uk_sanciones_prestamo_tipo unique (id_prestamo, tipo);

    alter table usuarios 
       add constraint uk_usuarios_documento unique (tipo_documento, documento);

    alter table usuarios 
       add constraint UKcdmw5hxlfj78uf4997i3qyyw5 unique (correo);

    alter table auditoria 
       add constraint FKmc60a5takcm6k0hp4psrdnttm 
       foreign key (id_usuario) 
       references usuarios (id_usuario);

    alter table copias_seguridad 
       add constraint FK46npagfh1tn1lkwkmb9w5yxnr 
       foreign key (creado_por) 
       references usuarios (id_usuario);

    alter table favoritos 
       add constraint FKmfvhjx1mvi7drncnwk5ajfn3s 
       foreign key (id_libro) 
       references libros (id_libro);

    alter table favoritos 
       add constraint FK8ixb8kaxp9dq6p40cgvhqc96w 
       foreign key (id_usuario) 
       references usuarios (id_usuario);

    alter table libros 
       add constraint FKgxl2q4i4hj2wnbdjfgo5h2b1b 
       foreign key (id_categoria) 
       references categorias (id_categoria);

    alter table libros 
       add constraint FK1pnx393ebw2kcvs9tkpeh32xx 
       foreign key (id_editorial) 
       references editoriales (id_editorial);

    alter table libros 
       add constraint FKma109k9g6238y4ntopcu6iw1y 
       foreign key (id_genero) 
       references generos (id_genero);

    alter table libros_autores 
       add constraint FK9r7n05moyk8e83br7e8hnlury 
       foreign key (id_autor) 
       references autores (id_autor);

    alter table libros_autores 
       add constraint FKcc58n8nwp6ei7of1pp1s1seol 
       foreign key (id_libro) 
       references libros (id_libro);

    alter table prestamos 
       add constraint FKsifvu6veoseewjhlmnmva95oh 
       foreign key (id_libro) 
       references libros (id_libro);

    alter table prestamos 
       add constraint FK2pk8c1e0jvdyf43nj8faoalav 
       foreign key (recibido_por) 
       references usuarios (id_usuario);

    alter table prestamos 
       add constraint FKs9bi03vkqhh6iprtvrp0nnmoy 
       foreign key (registrado_por) 
       references usuarios (id_usuario);

    alter table prestamos 
       add constraint FKdc31rspf0xofnmyltyw27rgvd 
       foreign key (id_reserva) 
       references reservas (id_reserva);

    alter table prestamos 
       add constraint FK86qrtaakkw8dli50at9fpo51x 
       foreign key (id_usuario) 
       references usuarios (id_usuario);

    alter table reservas 
       add constraint FKldwjl9x4njptx74aqi4gchny 
       foreign key (id_libro) 
       references libros (id_libro);

    alter table reservas 
       add constraint FKdeuvrh9vdtt3hxcx65wi30v60 
       foreign key (resuelta_por) 
       references usuarios (id_usuario);

    alter table reservas 
       add constraint FKhjryje6u1cr0d4dubad1jja6 
       foreign key (id_usuario) 
       references usuarios (id_usuario);

    alter table sanciones 
       add constraint FKby60s54dk9ocyri2nc9xck4ib 
       foreign key (id_prestamo) 
       references prestamos (id_prestamo);

    alter table sanciones 
       add constraint FKan4x91rygwdjcytonnjc638gt 
       foreign key (registrado_por) 
       references usuarios (id_usuario);

    alter table sanciones 
       add constraint FKf7v2a0ytf020l510rwo8c5f1i 
       foreign key (id_usuario) 
       references usuarios (id_usuario);

    create table auditoria (
        fecha datetime(6) not null,
        id_auditoria bigint not null auto_increment,
        id_registro bigint,
        id_usuario bigint,
        ip varchar(45),
        entidad varchar(50),
        detalle varchar(500),
        accion enum ('ACTUALIZAR','CAMBIO_ESTADO','CREAR','ELIMINAR','EXPORTAR','LOGIN','LOGOUT','RESPALDO') not null,
        primary key (id_auditoria)
    ) engine=InnoDB;

    create table autores (
        activo bit not null,
        id_autor bigint not null auto_increment,
        nombre varchar(150) not null,
        primary key (id_autor)
    ) engine=InnoDB;

    create table categorias (
        activo bit not null,
        id_categoria bigint not null auto_increment,
        nombre varchar(100) not null,
        primary key (id_categoria)
    ) engine=InnoDB;

    create table copias_seguridad (
        exitosa bit not null,
        creado_en datetime(6) not null,
        creado_por bigint not null,
        id_copia bigint not null auto_increment,
        tamano_bytes bigint,
        mensaje_error varchar(500),
        nombre_archivo varchar(255) not null,
        primary key (id_copia)
    ) engine=InnoDB;

    create table editoriales (
        activo bit not null,
        id_editorial bigint not null auto_increment,
        nombre varchar(150) not null,
        primary key (id_editorial)
    ) engine=InnoDB;

    create table favoritos (
        id_libro bigint not null,
        id_usuario bigint not null,
        primary key (id_libro, id_usuario)
    ) engine=InnoDB;

    create table generos (
        activo bit not null,
        id_genero bigint not null auto_increment,
        nombre varchar(100) not null,
        primary key (id_genero)
    ) engine=InnoDB;

    create table libros (
        activo bit not null,
        fecha_publicacion date,
        stock_disponible integer not null,
        stock_total integer not null,
        actualizado_en datetime(6) not null,
        creado_en datetime(6) not null,
        id_categoria bigint not null,
        id_editorial bigint,
        id_genero bigint,
        id_libro bigint not null auto_increment,
        version bigint not null,
        isbn varchar(20),
        ubicacion varchar(100),
        portada_url varchar(500),
        descripcion TEXT,
        titulo varchar(255) not null,
        primary key (id_libro),
        check (stock_disponible >= 0 AND stock_disponible <= stock_total)
    ) engine=InnoDB;

    create table libros_autores (
        id_autor bigint not null,
        id_libro bigint not null,
        primary key (id_autor, id_libro)
    ) engine=InnoDB;

    create table prestamos (
        renovaciones integer not null,
        fecha_devolucion datetime(6),
        fecha_limite datetime(6) not null,
        fecha_prestamo datetime(6) not null,
        id_libro bigint not null,
        id_prestamo bigint not null auto_increment,
        id_reserva bigint,
        id_usuario bigint not null,
        recibido_por bigint,
        registrado_por bigint not null,
        estado enum ('ACTIVO','ANULADO','DEVUELTO','PERDIDO') not null,
        primary key (id_prestamo),
        check (fecha_limite >= fecha_prestamo AND (fecha_devolucion IS NULL OR fecha_devolucion >= fecha_prestamo))
    ) engine=InnoDB;

    create table proveedores (
        activo bit not null,
        actualizado_en datetime(6) not null,
        creado_en datetime(6) not null,
        id_proveedor bigint not null auto_increment,
        telefono varchar(20),
        nit varchar(30),
        correo varchar(150),
        nombre varchar(150) not null,
        persona_contacto varchar(150),
        direccion varchar(255),
        primary key (id_proveedor)
    ) engine=InnoDB;

    create table reservas (
        fecha_limite_retiro datetime(6),
        fecha_reserva datetime(6) not null,
        id_libro bigint not null,
        id_reserva bigint not null auto_increment,
        id_usuario bigint not null,
        resuelta_en datetime(6),
        resuelta_por bigint,
        observaciones varchar(255),
        estado enum ('APROBADA','CANCELADA','PENDIENTE','RECHAZADA','RETIRADA','VENCIDA') not null,
        primary key (id_reserva),
        check (fecha_limite_retiro IS NULL OR fecha_limite_retiro >= fecha_reserva)
    ) engine=InnoDB;

    create table sanciones (
        dias_retraso integer,
        importe decimal(10,2) not null,
        fecha_emision datetime(6) not null,
        fecha_pago datetime(6),
        id_prestamo bigint,
        id_sancion bigint not null auto_increment,
        id_usuario bigint not null,
        registrado_por bigint,
        detalle varchar(255),
        estado enum ('ANULADA','PAGADA','PENDIENTE') not null,
        tipo enum ('LIBRO_DANADO','LIBRO_PERDIDO','MORA','OTRO') not null,
        primary key (id_sancion)
    ) engine=InnoDB;

    create table usuarios (
        actualizado_en datetime(6) not null,
        creado_en datetime(6) not null,
        id_usuario bigint not null auto_increment,
        documento varchar(20) not null,
        telefono varchar(20),
        apellidos varchar(100) not null,
        nombre varchar(100) not null,
        correo varchar(150) not null,
        contrasena_hash varchar(255) not null,
        estado enum ('ACTIVO','INACTIVO','SUSPENDIDO') not null,
        rol enum ('ADMINISTRADOR','BIBLIOTECARIO','LECTOR') not null,
        tipo_documento enum ('CC','CE','PA','TI') not null,
        primary key (id_usuario)
    ) engine=InnoDB;

    create index idx_auditoria_fecha 
       on auditoria (fecha);

    create index idx_auditoria_usuario_fecha 
       on auditoria (id_usuario, fecha);

    create index idx_auditoria_entidad 
       on auditoria (entidad, id_registro);

    alter table autores 
       add constraint UKbmqig88fabri853omysia6j3t unique (nombre);

    alter table categorias 
       add constraint UKqcog8b7hps1hioi9onqwjdt6y unique (nombre);

    create index idx_copias_creado_en 
       on copias_seguridad (creado_en);

    alter table copias_seguridad 
       add constraint UKf2qgx7up9vwifo7ar4tqcifp7 unique (nombre_archivo);

    alter table editoriales 
       add constraint UKa1g9sws2edh4alrvuaok2no2f unique (nombre);

    alter table generos 
       add constraint UK9u5buu0dehagjdoqm3wv6orm3 unique (nombre);

    create index idx_libros_titulo 
       on libros (titulo);

    alter table libros 
       add constraint UK278ekmuxh4b6i98mqypvacdcu unique (isbn);

    create index idx_prestamos_estado_limite 
       on prestamos (estado, fecha_limite);

    alter table prestamos 
       add constraint UKqfi4dtwjv0xu7ot1kxxw6pv0c unique (id_reserva);

    alter table proveedores 
       add constraint UK4viyt15dd71w7i0wu9684deq7 unique (nit);

    alter table proveedores 
       add constraint UK3ts51lg5ducpe3ag146g6sye7 unique (nombre);

    create index idx_reservas_estado_limite 
       on reservas (estado, fecha_limite_retiro);

    alter table sanciones 
       add constraint uk_sanciones_prestamo_tipo unique (id_prestamo, tipo);

    alter table usuarios 
       add constraint uk_usuarios_documento unique (tipo_documento, documento);

    alter table usuarios 
       add constraint UKcdmw5hxlfj78uf4997i3qyyw5 unique (correo);

    alter table auditoria 
       add constraint FKmc60a5takcm6k0hp4psrdnttm 
       foreign key (id_usuario) 
       references usuarios (id_usuario);

    alter table copias_seguridad 
       add constraint FK46npagfh1tn1lkwkmb9w5yxnr 
       foreign key (creado_por) 
       references usuarios (id_usuario);

    alter table favoritos 
       add constraint FKmfvhjx1mvi7drncnwk5ajfn3s 
       foreign key (id_libro) 
       references libros (id_libro);

    alter table favoritos 
       add constraint FK8ixb8kaxp9dq6p40cgvhqc96w 
       foreign key (id_usuario) 
       references usuarios (id_usuario);

    alter table libros 
       add constraint FKgxl2q4i4hj2wnbdjfgo5h2b1b 
       foreign key (id_categoria) 
       references categorias (id_categoria);

    alter table libros 
       add constraint FK1pnx393ebw2kcvs9tkpeh32xx 
       foreign key (id_editorial) 
       references editoriales (id_editorial);

    alter table libros 
       add constraint FKma109k9g6238y4ntopcu6iw1y 
       foreign key (id_genero) 
       references generos (id_genero);

    alter table libros_autores 
       add constraint FK9r7n05moyk8e83br7e8hnlury 
       foreign key (id_autor) 
       references autores (id_autor);

    alter table libros_autores 
       add constraint FKcc58n8nwp6ei7of1pp1s1seol 
       foreign key (id_libro) 
       references libros (id_libro);

    alter table prestamos 
       add constraint FKsifvu6veoseewjhlmnmva95oh 
       foreign key (id_libro) 
       references libros (id_libro);

    alter table prestamos 
       add constraint FK2pk8c1e0jvdyf43nj8faoalav 
       foreign key (recibido_por) 
       references usuarios (id_usuario);

    alter table prestamos 
       add constraint FKs9bi03vkqhh6iprtvrp0nnmoy 
       foreign key (registrado_por) 
       references usuarios (id_usuario);

    alter table prestamos 
       add constraint FKdc31rspf0xofnmyltyw27rgvd 
       foreign key (id_reserva) 
       references reservas (id_reserva);

    alter table prestamos 
       add constraint FK86qrtaakkw8dli50at9fpo51x 
       foreign key (id_usuario) 
       references usuarios (id_usuario);

    alter table reservas 
       add constraint FKldwjl9x4njptx74aqi4gchny 
       foreign key (id_libro) 
       references libros (id_libro);

    alter table reservas 
       add constraint FKdeuvrh9vdtt3hxcx65wi30v60 
       foreign key (resuelta_por) 
       references usuarios (id_usuario);

    alter table reservas 
       add constraint FKhjryje6u1cr0d4dubad1jja6 
       foreign key (id_usuario) 
       references usuarios (id_usuario);

    alter table sanciones 
       add constraint FKby60s54dk9ocyri2nc9xck4ib 
       foreign key (id_prestamo) 
       references prestamos (id_prestamo);

    alter table sanciones 
       add constraint FKan4x91rygwdjcytonnjc638gt 
       foreign key (registrado_por) 
       references usuarios (id_usuario);

    alter table sanciones 
       add constraint FKf7v2a0ytf020l510rwo8c5f1i 
       foreign key (id_usuario) 
       references usuarios (id_usuario);

    create table auditoria (
        fecha datetime(6) not null,
        id_auditoria bigint not null auto_increment,
        id_registro bigint,
        id_usuario bigint,
        ip varchar(45),
        entidad varchar(50),
        detalle varchar(500),
        accion enum ('ACTUALIZAR','CAMBIO_ESTADO','CREAR','ELIMINAR','EXPORTAR','LOGIN','LOGOUT','RESPALDO') not null,
        primary key (id_auditoria)
    ) engine=InnoDB;

    create table autores (
        activo bit not null,
        id_autor bigint not null auto_increment,
        nombre varchar(150) not null,
        primary key (id_autor)
    ) engine=InnoDB;

    create table categorias (
        activo bit not null,
        id_categoria bigint not null auto_increment,
        nombre varchar(100) not null,
        primary key (id_categoria)
    ) engine=InnoDB;

    create table copias_seguridad (
        exitosa bit not null,
        creado_en datetime(6) not null,
        creado_por bigint not null,
        id_copia bigint not null auto_increment,
        tamano_bytes bigint,
        mensaje_error varchar(500),
        nombre_archivo varchar(255) not null,
        primary key (id_copia)
    ) engine=InnoDB;

    create table editoriales (
        activo bit not null,
        id_editorial bigint not null auto_increment,
        nombre varchar(150) not null,
        primary key (id_editorial)
    ) engine=InnoDB;

    create table favoritos (
        id_libro bigint not null,
        id_usuario bigint not null,
        primary key (id_libro, id_usuario)
    ) engine=InnoDB;

    create table generos (
        activo bit not null,
        id_genero bigint not null auto_increment,
        nombre varchar(100) not null,
        primary key (id_genero)
    ) engine=InnoDB;

    create table libros (
        activo bit not null,
        fecha_publicacion date,
        stock_disponible integer not null,
        stock_total integer not null,
        actualizado_en datetime(6) not null,
        creado_en datetime(6) not null,
        id_categoria bigint not null,
        id_editorial bigint,
        id_genero bigint,
        id_libro bigint not null auto_increment,
        version bigint not null,
        isbn varchar(20),
        ubicacion varchar(100),
        portada_url varchar(500),
        descripcion TEXT,
        titulo varchar(255) not null,
        primary key (id_libro),
        check (stock_disponible >= 0 AND stock_disponible <= stock_total)
    ) engine=InnoDB;

    create table libros_autores (
        id_autor bigint not null,
        id_libro bigint not null,
        primary key (id_autor, id_libro)
    ) engine=InnoDB;

    create table prestamos (
        renovaciones integer not null,
        fecha_devolucion datetime(6),
        fecha_limite datetime(6) not null,
        fecha_prestamo datetime(6) not null,
        id_libro bigint not null,
        id_prestamo bigint not null auto_increment,
        id_reserva bigint,
        id_usuario bigint not null,
        recibido_por bigint,
        registrado_por bigint not null,
        estado enum ('ACTIVO','ANULADO','DEVUELTO','PERDIDO') not null,
        primary key (id_prestamo),
        check (fecha_limite >= fecha_prestamo AND (fecha_devolucion IS NULL OR fecha_devolucion >= fecha_prestamo))
    ) engine=InnoDB;

    create table proveedores (
        activo bit not null,
        actualizado_en datetime(6) not null,
        creado_en datetime(6) not null,
        id_proveedor bigint not null auto_increment,
        telefono varchar(20),
        nit varchar(30),
        correo varchar(150),
        nombre varchar(150) not null,
        persona_contacto varchar(150),
        direccion varchar(255),
        primary key (id_proveedor)
    ) engine=InnoDB;

    create table reservas (
        fecha_limite_retiro datetime(6),
        fecha_reserva datetime(6) not null,
        id_libro bigint not null,
        id_reserva bigint not null auto_increment,
        id_usuario bigint not null,
        resuelta_en datetime(6),
        resuelta_por bigint,
        observaciones varchar(255),
        estado enum ('APROBADA','CANCELADA','PENDIENTE','RECHAZADA','RETIRADA','VENCIDA') not null,
        primary key (id_reserva),
        check (fecha_limite_retiro IS NULL OR fecha_limite_retiro >= fecha_reserva)
    ) engine=InnoDB;

    create table sanciones (
        dias_retraso integer,
        importe decimal(10,2) not null,
        fecha_emision datetime(6) not null,
        fecha_pago datetime(6),
        id_prestamo bigint,
        id_sancion bigint not null auto_increment,
        id_usuario bigint not null,
        registrado_por bigint,
        detalle varchar(255),
        estado enum ('ANULADA','PAGADA','PENDIENTE') not null,
        tipo enum ('LIBRO_DANADO','LIBRO_PERDIDO','MORA','OTRO') not null,
        primary key (id_sancion)
    ) engine=InnoDB;

    create table usuarios (
        actualizado_en datetime(6) not null,
        creado_en datetime(6) not null,
        id_usuario bigint not null auto_increment,
        documento varchar(20) not null,
        telefono varchar(20),
        apellidos varchar(100) not null,
        nombre varchar(100) not null,
        correo varchar(150) not null,
        contrasena_hash varchar(255) not null,
        estado enum ('ACTIVO','INACTIVO','SUSPENDIDO') not null,
        rol enum ('ADMINISTRADOR','BIBLIOTECARIO','LECTOR') not null,
        tipo_documento enum ('CC','CE','PA','TI') not null,
        primary key (id_usuario)
    ) engine=InnoDB;

    create index idx_auditoria_fecha 
       on auditoria (fecha);

    create index idx_auditoria_usuario_fecha 
       on auditoria (id_usuario, fecha);

    create index idx_auditoria_entidad 
       on auditoria (entidad, id_registro);

    alter table autores 
       add constraint UKbmqig88fabri853omysia6j3t unique (nombre);

    alter table categorias 
       add constraint UKqcog8b7hps1hioi9onqwjdt6y unique (nombre);

    create index idx_copias_creado_en 
       on copias_seguridad (creado_en);

    alter table copias_seguridad 
       add constraint UKf2qgx7up9vwifo7ar4tqcifp7 unique (nombre_archivo);

    alter table editoriales 
       add constraint UKa1g9sws2edh4alrvuaok2no2f unique (nombre);

    alter table generos 
       add constraint UK9u5buu0dehagjdoqm3wv6orm3 unique (nombre);

    create index idx_libros_titulo 
       on libros (titulo);

    alter table libros 
       add constraint UK278ekmuxh4b6i98mqypvacdcu unique (isbn);

    create index idx_prestamos_estado_limite 
       on prestamos (estado, fecha_limite);

    alter table prestamos 
       add constraint UKqfi4dtwjv0xu7ot1kxxw6pv0c unique (id_reserva);

    alter table proveedores 
       add constraint UK4viyt15dd71w7i0wu9684deq7 unique (nit);

    alter table proveedores 
       add constraint UK3ts51lg5ducpe3ag146g6sye7 unique (nombre);

    create index idx_reservas_estado_limite 
       on reservas (estado, fecha_limite_retiro);

    alter table sanciones 
       add constraint uk_sanciones_prestamo_tipo unique (id_prestamo, tipo);

    alter table usuarios 
       add constraint uk_usuarios_documento unique (tipo_documento, documento);

    alter table usuarios 
       add constraint UKcdmw5hxlfj78uf4997i3qyyw5 unique (correo);

    alter table auditoria 
       add constraint FKmc60a5takcm6k0hp4psrdnttm 
       foreign key (id_usuario) 
       references usuarios (id_usuario);

    alter table copias_seguridad 
       add constraint FK46npagfh1tn1lkwkmb9w5yxnr 
       foreign key (creado_por) 
       references usuarios (id_usuario);

    alter table favoritos 
       add constraint FKmfvhjx1mvi7drncnwk5ajfn3s 
       foreign key (id_libro) 
       references libros (id_libro);

    alter table favoritos 
       add constraint FK8ixb8kaxp9dq6p40cgvhqc96w 
       foreign key (id_usuario) 
       references usuarios (id_usuario);

    alter table libros 
       add constraint FKgxl2q4i4hj2wnbdjfgo5h2b1b 
       foreign key (id_categoria) 
       references categorias (id_categoria);

    alter table libros 
       add constraint FK1pnx393ebw2kcvs9tkpeh32xx 
       foreign key (id_editorial) 
       references editoriales (id_editorial);

    alter table libros 
       add constraint FKma109k9g6238y4ntopcu6iw1y 
       foreign key (id_genero) 
       references generos (id_genero);

    alter table libros_autores 
       add constraint FK9r7n05moyk8e83br7e8hnlury 
       foreign key (id_autor) 
       references autores (id_autor);

    alter table libros_autores 
       add constraint FKcc58n8nwp6ei7of1pp1s1seol 
       foreign key (id_libro) 
       references libros (id_libro);

    alter table prestamos 
       add constraint FKsifvu6veoseewjhlmnmva95oh 
       foreign key (id_libro) 
       references libros (id_libro);

    alter table prestamos 
       add constraint FK2pk8c1e0jvdyf43nj8faoalav 
       foreign key (recibido_por) 
       references usuarios (id_usuario);

    alter table prestamos 
       add constraint FKs9bi03vkqhh6iprtvrp0nnmoy 
       foreign key (registrado_por) 
       references usuarios (id_usuario);

    alter table prestamos 
       add constraint FKdc31rspf0xofnmyltyw27rgvd 
       foreign key (id_reserva) 
       references reservas (id_reserva);

    alter table prestamos 
       add constraint FK86qrtaakkw8dli50at9fpo51x 
       foreign key (id_usuario) 
       references usuarios (id_usuario);

    alter table reservas 
       add constraint FKldwjl9x4njptx74aqi4gchny 
       foreign key (id_libro) 
       references libros (id_libro);

    alter table reservas 
       add constraint FKdeuvrh9vdtt3hxcx65wi30v60 
       foreign key (resuelta_por) 
       references usuarios (id_usuario);

    alter table reservas 
       add constraint FKhjryje6u1cr0d4dubad1jja6 
       foreign key (id_usuario) 
       references usuarios (id_usuario);

    alter table sanciones 
       add constraint FKby60s54dk9ocyri2nc9xck4ib 
       foreign key (id_prestamo) 
       references prestamos (id_prestamo);

    alter table sanciones 
       add constraint FKan4x91rygwdjcytonnjc638gt 
       foreign key (registrado_por) 
       references usuarios (id_usuario);

    alter table sanciones 
       add constraint FKf7v2a0ytf020l510rwo8c5f1i 
       foreign key (id_usuario) 
       references usuarios (id_usuario);

    create table auditoria (
        fecha datetime(6) not null,
        id_auditoria bigint not null auto_increment,
        id_registro bigint,
        id_usuario bigint,
        ip varchar(45),
        entidad varchar(50),
        detalle varchar(500),
        accion enum ('ACTUALIZAR','CAMBIO_ESTADO','CREAR','ELIMINAR','EXPORTAR','LOGIN','LOGOUT','RESPALDO') not null,
        primary key (id_auditoria)
    ) engine=InnoDB;

    create table autores (
        activo bit not null,
        id_autor bigint not null auto_increment,
        nombre varchar(150) not null,
        primary key (id_autor)
    ) engine=InnoDB;

    create table categorias (
        activo bit not null,
        id_categoria bigint not null auto_increment,
        nombre varchar(100) not null,
        primary key (id_categoria)
    ) engine=InnoDB;

    create table copias_seguridad (
        exitosa bit not null,
        creado_en datetime(6) not null,
        creado_por bigint not null,
        id_copia bigint not null auto_increment,
        tamano_bytes bigint,
        mensaje_error varchar(500),
        nombre_archivo varchar(255) not null,
        primary key (id_copia)
    ) engine=InnoDB;

    create table editoriales (
        activo bit not null,
        id_editorial bigint not null auto_increment,
        nombre varchar(150) not null,
        primary key (id_editorial)
    ) engine=InnoDB;

    create table favoritos (
        id_libro bigint not null,
        id_usuario bigint not null,
        primary key (id_libro, id_usuario)
    ) engine=InnoDB;

    create table generos (
        activo bit not null,
        id_genero bigint not null auto_increment,
        nombre varchar(100) not null,
        primary key (id_genero)
    ) engine=InnoDB;

    create table libros (
        activo bit not null,
        fecha_publicacion date,
        stock_disponible integer not null,
        stock_total integer not null,
        actualizado_en datetime(6) not null,
        creado_en datetime(6) not null,
        id_categoria bigint not null,
        id_editorial bigint,
        id_genero bigint,
        id_libro bigint not null auto_increment,
        version bigint not null,
        isbn varchar(20),
        ubicacion varchar(100),
        portada_url varchar(500),
        descripcion TEXT,
        titulo varchar(255) not null,
        primary key (id_libro),
        check (stock_disponible >= 0 AND stock_disponible <= stock_total)
    ) engine=InnoDB;

    create table libros_autores (
        id_autor bigint not null,
        id_libro bigint not null,
        primary key (id_autor, id_libro)
    ) engine=InnoDB;

    create table prestamos (
        renovaciones integer not null,
        fecha_devolucion datetime(6),
        fecha_limite datetime(6) not null,
        fecha_prestamo datetime(6) not null,
        id_libro bigint not null,
        id_prestamo bigint not null auto_increment,
        id_reserva bigint,
        id_usuario bigint not null,
        recibido_por bigint,
        registrado_por bigint not null,
        estado enum ('ACTIVO','ANULADO','DEVUELTO','PERDIDO') not null,
        primary key (id_prestamo),
        check (fecha_limite >= fecha_prestamo AND (fecha_devolucion IS NULL OR fecha_devolucion >= fecha_prestamo))
    ) engine=InnoDB;

    create table proveedores (
        activo bit not null,
        actualizado_en datetime(6) not null,
        creado_en datetime(6) not null,
        id_proveedor bigint not null auto_increment,
        telefono varchar(20),
        nit varchar(30),
        correo varchar(150),
        nombre varchar(150) not null,
        persona_contacto varchar(150),
        direccion varchar(255),
        primary key (id_proveedor)
    ) engine=InnoDB;

    create table reservas (
        fecha_limite_retiro datetime(6),
        fecha_reserva datetime(6) not null,
        id_libro bigint not null,
        id_reserva bigint not null auto_increment,
        id_usuario bigint not null,
        resuelta_en datetime(6),
        resuelta_por bigint,
        observaciones varchar(255),
        estado enum ('APROBADA','CANCELADA','PENDIENTE','RECHAZADA','RETIRADA','VENCIDA') not null,
        primary key (id_reserva),
        check (fecha_limite_retiro IS NULL OR fecha_limite_retiro >= fecha_reserva)
    ) engine=InnoDB;

    create table sanciones (
        dias_retraso integer,
        importe decimal(10,2) not null,
        fecha_emision datetime(6) not null,
        fecha_pago datetime(6),
        id_prestamo bigint,
        id_sancion bigint not null auto_increment,
        id_usuario bigint not null,
        registrado_por bigint,
        detalle varchar(255),
        estado enum ('ANULADA','PAGADA','PENDIENTE') not null,
        tipo enum ('LIBRO_DANADO','LIBRO_PERDIDO','MORA','OTRO') not null,
        primary key (id_sancion)
    ) engine=InnoDB;

    create table usuarios (
        actualizado_en datetime(6) not null,
        creado_en datetime(6) not null,
        id_usuario bigint not null auto_increment,
        documento varchar(20) not null,
        telefono varchar(20),
        apellidos varchar(100) not null,
        nombre varchar(100) not null,
        correo varchar(150) not null,
        contrasena_hash varchar(255) not null,
        estado enum ('ACTIVO','INACTIVO','SUSPENDIDO') not null,
        rol enum ('ADMINISTRADOR','BIBLIOTECARIO','LECTOR') not null,
        tipo_documento enum ('CC','CE','PA','TI') not null,
        primary key (id_usuario)
    ) engine=InnoDB;

    create index idx_auditoria_fecha 
       on auditoria (fecha);

    create index idx_auditoria_usuario_fecha 
       on auditoria (id_usuario, fecha);

    create index idx_auditoria_entidad 
       on auditoria (entidad, id_registro);

    alter table autores 
       add constraint UKbmqig88fabri853omysia6j3t unique (nombre);

    alter table categorias 
       add constraint UKqcog8b7hps1hioi9onqwjdt6y unique (nombre);

    create index idx_copias_creado_en 
       on copias_seguridad (creado_en);

    alter table copias_seguridad 
       add constraint UKf2qgx7up9vwifo7ar4tqcifp7 unique (nombre_archivo);

    alter table editoriales 
       add constraint UKa1g9sws2edh4alrvuaok2no2f unique (nombre);

    alter table generos 
       add constraint UK9u5buu0dehagjdoqm3wv6orm3 unique (nombre);

    create index idx_libros_titulo 
       on libros (titulo);

    alter table libros 
       add constraint UK278ekmuxh4b6i98mqypvacdcu unique (isbn);

    create index idx_prestamos_estado_limite 
       on prestamos (estado, fecha_limite);

    alter table prestamos 
       add constraint UKqfi4dtwjv0xu7ot1kxxw6pv0c unique (id_reserva);

    alter table proveedores 
       add constraint UK4viyt15dd71w7i0wu9684deq7 unique (nit);

    alter table proveedores 
       add constraint UK3ts51lg5ducpe3ag146g6sye7 unique (nombre);

    create index idx_reservas_estado_limite 
       on reservas (estado, fecha_limite_retiro);

    alter table sanciones 
       add constraint uk_sanciones_prestamo_tipo unique (id_prestamo, tipo);

    alter table usuarios 
       add constraint uk_usuarios_documento unique (tipo_documento, documento);

    alter table usuarios 
       add constraint UKcdmw5hxlfj78uf4997i3qyyw5 unique (correo);

    alter table auditoria 
       add constraint FKmc60a5takcm6k0hp4psrdnttm 
       foreign key (id_usuario) 
       references usuarios (id_usuario);

    alter table copias_seguridad 
       add constraint FK46npagfh1tn1lkwkmb9w5yxnr 
       foreign key (creado_por) 
       references usuarios (id_usuario);

    alter table favoritos 
       add constraint FKmfvhjx1mvi7drncnwk5ajfn3s 
       foreign key (id_libro) 
       references libros (id_libro);

    alter table favoritos 
       add constraint FK8ixb8kaxp9dq6p40cgvhqc96w 
       foreign key (id_usuario) 
       references usuarios (id_usuario);

    alter table libros 
       add constraint FKgxl2q4i4hj2wnbdjfgo5h2b1b 
       foreign key (id_categoria) 
       references categorias (id_categoria);

    alter table libros 
       add constraint FK1pnx393ebw2kcvs9tkpeh32xx 
       foreign key (id_editorial) 
       references editoriales (id_editorial);

    alter table libros 
       add constraint FKma109k9g6238y4ntopcu6iw1y 
       foreign key (id_genero) 
       references generos (id_genero);

    alter table libros_autores 
       add constraint FK9r7n05moyk8e83br7e8hnlury 
       foreign key (id_autor) 
       references autores (id_autor);

    alter table libros_autores 
       add constraint FKcc58n8nwp6ei7of1pp1s1seol 
       foreign key (id_libro) 
       references libros (id_libro);

    alter table prestamos 
       add constraint FKsifvu6veoseewjhlmnmva95oh 
       foreign key (id_libro) 
       references libros (id_libro);

    alter table prestamos 
       add constraint FK2pk8c1e0jvdyf43nj8faoalav 
       foreign key (recibido_por) 
       references usuarios (id_usuario);

    alter table prestamos 
       add constraint FKs9bi03vkqhh6iprtvrp0nnmoy 
       foreign key (registrado_por) 
       references usuarios (id_usuario);

    alter table prestamos 
       add constraint FKdc31rspf0xofnmyltyw27rgvd 
       foreign key (id_reserva) 
       references reservas (id_reserva);

    alter table prestamos 
       add constraint FK86qrtaakkw8dli50at9fpo51x 
       foreign key (id_usuario) 
       references usuarios (id_usuario);

    alter table reservas 
       add constraint FKldwjl9x4njptx74aqi4gchny 
       foreign key (id_libro) 
       references libros (id_libro);

    alter table reservas 
       add constraint FKdeuvrh9vdtt3hxcx65wi30v60 
       foreign key (resuelta_por) 
       references usuarios (id_usuario);

    alter table reservas 
       add constraint FKhjryje6u1cr0d4dubad1jja6 
       foreign key (id_usuario) 
       references usuarios (id_usuario);

    alter table sanciones 
       add constraint FKby60s54dk9ocyri2nc9xck4ib 
       foreign key (id_prestamo) 
       references prestamos (id_prestamo);

    alter table sanciones 
       add constraint FKan4x91rygwdjcytonnjc638gt 
       foreign key (registrado_por) 
       references usuarios (id_usuario);

    alter table sanciones 
       add constraint FKf7v2a0ytf020l510rwo8c5f1i 
       foreign key (id_usuario) 
       references usuarios (id_usuario);
