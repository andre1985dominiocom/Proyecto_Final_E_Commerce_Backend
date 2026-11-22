-- =====================================================================
-- DidiStore E-Commerce - Esquema completo de base de datos
-- Motor: MySQL 8.0+ | Cotejamiento recomendado: utf8mb4
-- Generado desde las sentencias SQL de los DAOs del proyecto.
-- Ejecutar: mysql -u root -p < database/schema.sql
-- =====================================================================

DROP DATABASE IF EXISTS db_e_commerce_didistore;
CREATE DATABASE db_e_commerce_didistore
    CHARACTER SET utf8mb4
    COLLATE utf8mb4_unicode_ci;
USE db_e_commerce_didistore;

-- =====================================================================
-- MÓDULO AUTH / SEGURIDAD
-- =====================================================================

CREATE TABLE Perfiles (
    ID_Perfil           INT AUTO_INCREMENT PRIMARY KEY,
    Nombre_perfil       VARCHAR(50)  NOT NULL UNIQUE,
    Descripcion_perfil  VARCHAR(255) NULL
) ENGINE=InnoDB;

CREATE TABLE Permisos (
    ID_Permiso          INT AUTO_INCREMENT PRIMARY KEY,
    Nombre_permiso      VARCHAR(100) NOT NULL UNIQUE,
    Descripcion_permiso VARCHAR(255) NULL
) ENGINE=InnoDB;

CREATE TABLE Perfil_permiso (
    ID_Perfil   INT NOT NULL,
    ID_Permiso  INT NOT NULL,
    PRIMARY KEY (ID_Perfil, ID_Permiso),
    CONSTRAINT fk_perfilpermiso_perfil FOREIGN KEY (ID_Perfil)  REFERENCES Perfiles(ID_Perfil)  ON DELETE CASCADE,
    CONSTRAINT fk_perfilpermiso_permiso FOREIGN KEY (ID_Permiso) REFERENCES Permisos(ID_Permiso) ON DELETE CASCADE
) ENGINE=InnoDB;

CREATE TABLE usuarios (
    id_Usuario          INT AUTO_INCREMENT PRIMARY KEY,
    email               VARCHAR(150) NOT NULL UNIQUE,
    contrasena          VARCHAR(100) NOT NULL,
    nombre              VARCHAR(100) NOT NULL,
    apellido            VARCHAR(100) NOT NULL,
    documento           VARCHAR(30)  NULL UNIQUE,
    tipo_documento       VARCHAR(20)  NULL,          -- CC, CE, TI, Pasaporte
    perfil_id           INT          NOT NULL,
    estado              VARCHAR(20)  NOT NULL DEFAULT 'Activo',  -- Activo, Inactivo, Bloqueado
    email_verificado    TINYINT(1)   NOT NULL DEFAULT 0,
    fecha_creacion      TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP,
    fecha_actualizacion TIMESTAMP    NULL ON UPDATE CURRENT_TIMESTAMP,
    fecha_ultimo_login  TIMESTAMP    NULL,
    intentos_fallidos   INT          NOT NULL DEFAULT 0,   -- RF-01 bloqueo tras 5 intentos
    bloqueado_hasta     DATETIME     NULL,                 -- RF-01 bloqueo temporal 15 min
    CONSTRAINT fk_usuarios_perfil FOREIGN KEY (perfil_id) REFERENCES Perfiles(ID_Perfil)
) ENGINE=InnoDB;

CREATE INDEX idx_usuarios_email ON usuarios(email);
CREATE INDEX idx_usuarios_perfil ON usuarios(perfil_id);

CREATE TABLE Sesiones (
    ID_Sesion         INT AUTO_INCREMENT PRIMARY KEY,
    Usuario_ID        INT          NOT NULL,
    Token_sesion      VARCHAR(255) NOT NULL UNIQUE,
    Fecha_Creacion    TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP,
    Fecha_expiracion  TIMESTAMP    NOT NULL,
    IP                VARCHAR(45)  NULL,
    User_agent        VARCHAR(255) NULL,
    Revocada          TINYINT(1)   NOT NULL DEFAULT 0,
    CONSTRAINT fk_sesiones_usuario FOREIGN KEY (Usuario_ID) REFERENCES usuarios(id_Usuario) ON DELETE CASCADE
) ENGINE=InnoDB;

CREATE INDEX idx_sesiones_usuario ON Sesiones(Usuario_ID);

CREATE TABLE telefonos (
    ID_Telefono     INT AUTO_INCREMENT PRIMARY KEY,
    Usuario_ID      INT          NOT NULL,
    tipo            VARCHAR(20)  NOT NULL DEFAULT 'Principal', -- Principal, Secundario, Trabajo, Emergencia
    numero          VARCHAR(20)  NOT NULL,
    es_verificado   TINYINT(1)   NOT NULL DEFAULT 0,
    fecha_agregado  TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_telefonos_usuario FOREIGN KEY (Usuario_ID) REFERENCES usuarios(id_Usuario) ON DELETE CASCADE
) ENGINE=InnoDB;

CREATE INDEX idx_telefonos_usuario ON telefonos(Usuario_ID);

CREATE TABLE tokens_recuperacion (
    ID_Token          INT AUTO_INCREMENT PRIMARY KEY,
    usuario_ID        INT          NOT NULL,
    token_hash        VARCHAR(64)  NOT NULL UNIQUE,
    fecha_creacion    TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP,
    fecha_expiracion  TIMESTAMP    NOT NULL,
    usado             TINYINT(1)   NOT NULL DEFAULT 0,
    intentos          INT          NOT NULL DEFAULT 0,
    CONSTRAINT fk_tokens_usuario FOREIGN KEY (usuario_ID) REFERENCES usuarios(id_Usuario) ON DELETE CASCADE
) ENGINE=InnoDB;

CREATE INDEX idx_tokens_usuario ON tokens_recuperacion(usuario_ID);

-- =====================================================================
-- MÓDULO CATALOG
-- =====================================================================

CREATE TABLE Categorias (
    ID_Categoria       INT AUTO_INCREMENT PRIMARY KEY,
    nombre_Categoria   VARCHAR(100) NOT NULL,
    Descripcion        VARCHAR(255) NULL,
    Categoria_padre_ID INT          NULL,
    Fecha_creacion     TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_categorias_padre FOREIGN KEY (Categoria_padre_ID) REFERENCES Categorias(ID_Categoria) ON DELETE SET NULL
) ENGINE=InnoDB;

CREATE INDEX idx_categorias_padre ON Categorias(Categoria_padre_ID);
CREATE INDEX idx_categorias_nombre ON Categorias(nombre_Categoria);

CREATE TABLE Productos (
    id_Producto          INT AUTO_INCREMENT PRIMARY KEY,
    nombre_Producto      VARCHAR(150)   NOT NULL,
    descripcion_Corta    VARCHAR(255)   NULL,
    descripcion_Larga    TEXT           NULL,
    precio               DECIMAL(12,2)  NOT NULL CHECK (precio >= 0),
    SKU                  VARCHAR(50)    NOT NULL UNIQUE,
    talla                VARCHAR(10)    NULL,          -- XS, S, M, L, XL, XXL
    color                VARCHAR(50)    NULL,
    categoria_Id         INT            NOT NULL,
    estado               VARCHAR(20)    NOT NULL DEFAULT 'Activo', -- Activo, Inactivo, Agotado
    es_Destacado         TINYINT(1)     NOT NULL DEFAULT 0,
    fecha_Creacion       TIMESTAMP      NOT NULL DEFAULT CURRENT_TIMESTAMP,
    fecha_Actualizacion  TIMESTAMP      NULL ON UPDATE CURRENT_TIMESTAMP,
    CONSTRAINT fk_productos_categoria FOREIGN KEY (categoria_Id) REFERENCES Categorias(ID_Categoria)
) ENGINE=InnoDB;

CREATE INDEX idx_productos_categoria ON Productos(categoria_Id);
CREATE INDEX idx_productos_estado ON Productos(estado);

CREATE TABLE Imagenes_Productos (
    id_imagen    INT AUTO_INCREMENT PRIMARY KEY,
    producto_id  INT           NOT NULL,
    url          VARCHAR(500)  NOT NULL,
    formato      VARCHAR(10)   NULL,                   -- JPG, PNG, WEBP
    CONSTRAINT fk_imagenes_producto FOREIGN KEY (producto_id) REFERENCES Productos(id_Producto) ON DELETE CASCADE
) ENGINE=InnoDB;

CREATE INDEX idx_imagenes_producto ON Imagenes_Productos(producto_id);

CREATE TABLE Inventarios (
    ID_Inventario       INT AUTO_INCREMENT PRIMARY KEY,
    Producto_ID         INT NOT NULL UNIQUE,
    Stock_actual        INT NOT NULL DEFAULT 0 CHECK (Stock_actual >= 0),
    Stock_minimo        INT NOT NULL DEFAULT 0 CHECK (Stock_minimo >= 0),
    Stock_reservado     INT NOT NULL DEFAULT 0 CHECK (Stock_reservado >= 0),
    Fecha_creacion      TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    Fecha_actualizacion TIMESTAMP NULL ON UPDATE CURRENT_TIMESTAMP,
    CONSTRAINT fk_inventarios_producto FOREIGN KEY (Producto_ID) REFERENCES Productos(id_Producto) ON DELETE CASCADE
) ENGINE=InnoDB;

-- Movimientos de inventario (RF-11 historial de movimientos)
CREATE TABLE historial_inventario (
    ID_Movimiento     INT AUTO_INCREMENT PRIMARY KEY,
    inventario_id     INT          NULL,
    producto_id       INT          NOT NULL,
    usuario_id        INT          NULL,
    tipo_movimiento   VARCHAR(30)  NOT NULL,  -- ENTRADA, SALIDA, AJUSTE, RESERVA, LIBERACION, REEMBOLSO
    stock_anterior    INT          NOT NULL,
    stock_nuevo       INT          NOT NULL,
    cantidad          INT          NOT NULL,
    motivo            VARCHAR(255) NULL,
    fecha             TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_histinv_producto FOREIGN KEY (producto_id)  REFERENCES Productos(id_Producto) ON DELETE CASCADE,
    CONSTRAINT fk_histinv_usuario  FOREIGN KEY (usuario_id)   REFERENCES usuarios(id_Usuario) ON DELETE SET NULL
) ENGINE=InnoDB;

CREATE INDEX idx_histinv_producto ON historial_inventario(producto_id);
CREATE INDEX idx_histinv_fecha ON historial_inventario(fecha);

CREATE TABLE resenas (
    ID_Resena     INT AUTO_INCREMENT PRIMARY KEY,
    Usuario_ID    INT           NOT NULL,
    Producto_ID   INT           NOT NULL,
    Calificacion  TINYINT       NOT NULL CHECK (Calificacion BETWEEN 1 AND 5),
    Comentario    VARCHAR(500)  NULL,
    Estado        VARCHAR(20)   NOT NULL DEFAULT 'Pendiente', -- Pendiente, Aprobada, Rechazada
    Fecha_creacion TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_resenas_usuario  FOREIGN KEY (Usuario_ID)  REFERENCES usuarios(id_Usuario)  ON DELETE CASCADE,
    CONSTRAINT fk_resenas_producto FOREIGN KEY (Producto_ID) REFERENCES Productos(id_Producto) ON DELETE CASCADE,
    UNIQUE KEY uq_resena_usuario_producto (Usuario_ID, Producto_ID)
) ENGINE=InnoDB;

CREATE INDEX idx_resenas_producto ON resenas(Producto_ID);
CREATE INDEX idx_resenas_estado ON resenas(Estado);

CREATE TABLE Wishlist (
    ID_Wishlist  INT AUTO_INCREMENT PRIMARY KEY,
    Usuario_ID   INT NOT NULL,
    Producto_ID  INT NOT NULL,
    fecha_agregado TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_wishlist_usuario  FOREIGN KEY (Usuario_ID)  REFERENCES usuarios(id_Usuario)  ON DELETE CASCADE,
    CONSTRAINT fk_wishlist_producto FOREIGN KEY (Producto_ID) REFERENCES Productos(id_Producto) ON DELETE CASCADE,
    UNIQUE KEY uq_wishlist_usuario_producto (Usuario_ID, Producto_ID)
) ENGINE=InnoDB;

-- =====================================================================
-- MÓDULO SALES
-- =====================================================================

CREATE TABLE Carrito_compras (
    ID_Carrito          INT AUTO_INCREMENT PRIMARY KEY,
    Usuario_ID          INT          NULL,             -- NULL para anónimos
    Sesion_ID           VARCHAR(255) NULL,             -- sesión HTTP para anónimos
    Fecha_creacion      TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP,
    Fecha_actualizacion TIMESTAMP    NULL ON UPDATE CURRENT_TIMESTAMP,
    CONSTRAINT fk_carrito_usuario FOREIGN KEY (Usuario_ID) REFERENCES usuarios(id_Usuario) ON DELETE CASCADE
) ENGINE=InnoDB;

CREATE INDEX idx_carrito_usuario ON Carrito_compras(Usuario_ID);
CREATE INDEX idx_carrito_sesion ON Carrito_compras(Sesion_ID);

CREATE TABLE Item_Carrito (
    ID_Item          INT AUTO_INCREMENT PRIMARY KEY,
    Carrito_ID       INT            NOT NULL,
    Producto_ID      INT            NOT NULL,
    Cantidad         INT            NOT NULL CHECK (Cantidad > 0),
    Precio_unitario  DECIMAL(12,2)  NOT NULL CHECK (Precio_unitario >= 0),
    Subtotal         DECIMAL(12,2)  GENERATED ALWAYS AS (Cantidad * Precio_unitario) STORED,
    CONSTRAINT fk_item_carrito FOREIGN KEY (Carrito_ID)  REFERENCES Carrito_compras(ID_Carrito) ON DELETE CASCADE,
    CONSTRAINT fk_item_producto FOREIGN KEY (Producto_ID) REFERENCES Productos(id_Producto)
) ENGINE=InnoDB;

CREATE INDEX idx_item_carrito ON Item_Carrito(Carrito_ID);

-- =====================================================================
-- MÓDULO LOGISTICS (catálogos geográficos - requeridos por Direcciones)
-- =====================================================================

CREATE TABLE departamentos (
    id_Departamento INT AUTO_INCREMENT PRIMARY KEY,
    departamento    VARCHAR(100) NOT NULL UNIQUE
) ENGINE=InnoDB;

CREATE TABLE ciudades (
    id_Ciudad       INT AUTO_INCREMENT PRIMARY KEY,
    nombre_Ciudad   VARCHAR(100) NOT NULL,
    departamento_Id INT          NOT NULL,
    codigo_Postal   VARCHAR(10)  NULL,
    CONSTRAINT fk_ciudades_depto FOREIGN KEY (departamento_Id) REFERENCES departamentos(id_Departamento)
) ENGINE=InnoDB;

CREATE INDEX idx_ciudades_depto ON ciudades(departamento_Id);

CREATE TABLE Direcciones (
    ID_Direccion    INT AUTO_INCREMENT PRIMARY KEY,
    Direccion       VARCHAR(255) NOT NULL,
    Barrio          VARCHAR(100) NULL,
    Referencia      VARCHAR(255) NULL,
    Ciudad_ID       INT          NOT NULL,
    Usuario_ID      INT          NOT NULL,
    Estado          VARCHAR(20)  NOT NULL DEFAULT 'Activa', -- Activa, Inactiva
    Fecha_creacion  TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP,
    Es_principal    TINYINT(1)   NOT NULL DEFAULT 0,
    CONSTRAINT fk_direcciones_ciudad  FOREIGN KEY (Ciudad_ID)  REFERENCES ciudades(id_Ciudad),
    CONSTRAINT fk_direcciones_usuario FOREIGN KEY (Usuario_ID) REFERENCES usuarios(id_Usuario) ON DELETE CASCADE
) ENGINE=InnoDB;

CREATE INDEX idx_direcciones_usuario ON Direcciones(Usuario_ID);

CREATE TABLE Pedidos (
    ID_Pedido           INT AUTO_INCREMENT PRIMARY KEY,
    Numero_pedido       VARCHAR(30)   NOT NULL UNIQUE,
    Usuario_ID          INT           NOT NULL,
    Direccion_envio_ID  INT           NULL,
    Estado_pedido       VARCHAR(20)   NOT NULL DEFAULT 'Pendiente_Pago',
    Subtotal            DECIMAL(12,2) NOT NULL DEFAULT 0,
    Descuento           DECIMAL(12,2) NOT NULL DEFAULT 0,
    IVA                 DECIMAL(12,2) NOT NULL DEFAULT 0,
    Costo_envio         DECIMAL(12,2) NOT NULL DEFAULT 0,
    Monto_Total         DECIMAL(12,2) NOT NULL DEFAULT 0,
    Cupon_ID            INT           NULL,
    Fecha_pedido        TIMESTAMP     NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_pedidos_usuario FOREIGN KEY (Usuario_ID) REFERENCES usuarios(id_Usuario),
    CONSTRAINT fk_pedidos_direccion FOREIGN KEY (Direccion_envio_ID) REFERENCES Direcciones(ID_Direccion) ON DELETE SET NULL
) ENGINE=InnoDB;

CREATE INDEX idx_pedidos_usuario ON Pedidos(Usuario_ID);
CREATE INDEX idx_pedidos_estado ON Pedidos(Estado_pedido);
CREATE INDEX idx_pedidos_fecha ON Pedidos(Fecha_pedido);

CREATE TABLE Detalles_Pedidos (
    ID_Detalle       INT AUTO_INCREMENT PRIMARY KEY,
    Pedido_ID        INT           NOT NULL,
    Producto_ID      INT           NOT NULL,
    Cantidad         INT           NOT NULL CHECK (Cantidad > 0),
    Precio_unitario  DECIMAL(12,2) NOT NULL,
    Subtotal         DECIMAL(12,2) NOT NULL,
    CONSTRAINT fk_detalles_pedido FOREIGN KEY (Pedido_ID)   REFERENCES Pedidos(ID_Pedido) ON DELETE CASCADE,
    CONSTRAINT fk_detalles_producto FOREIGN KEY (Producto_ID) REFERENCES Productos(id_Producto)
) ENGINE=InnoDB;

CREATE INDEX idx_detalles_pedido ON Detalles_Pedidos(Pedido_ID);

CREATE TABLE Pagos (
    ID_Pago                 INT AUTO_INCREMENT PRIMARY KEY,
    Pedido_ID               INT           NOT NULL,
    Metodo_pago             VARCHAR(30)   NULL,  -- TarjetaCredito, TarjetaDebito, PSE, Efectivo, Transferencia
    Estado_pago             VARCHAR(20)   NOT NULL DEFAULT 'Pendiente', -- Pendiente, EnProceso, Aprobado, Rechazado, Reembolsado, Expirado
    Monto                   DECIMAL(12,2) NOT NULL CHECK (Monto >= 0),
    Referencia_transaccion  VARCHAR(100)  NULL,
    Referencia_interna      VARCHAR(100)  NULL,
    Datos_pasarela          TEXT          NULL,
    Fecha_pago              TIMESTAMP     NULL,
    Fecha_creacion          TIMESTAMP     NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_pagos_pedido FOREIGN KEY (Pedido_ID) REFERENCES Pedidos(ID_Pedido) ON DELETE CASCADE
) ENGINE=InnoDB;

CREATE INDEX idx_pagos_pedido ON Pagos(Pedido_ID);

CREATE TABLE Historial_Estados_Pedido (
    ID_Historial     INT AUTO_INCREMENT PRIMARY KEY,
    Pedido_ID        INT          NOT NULL,
    Estado_anterior  VARCHAR(20)  NULL,
    Estado_nuevo     VARCHAR(20)  NULL,
    Usuario_ID       INT          NULL,
    Fecha_cambio     TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP,
    Notas            VARCHAR(255) NULL,
    CONSTRAINT fk_historial_pedido FOREIGN KEY (Pedido_ID)  REFERENCES Pedidos(ID_Pedido) ON DELETE CASCADE,
    CONSTRAINT fk_historial_usuario FOREIGN KEY (Usuario_ID) REFERENCES usuarios(id_Usuario) ON DELETE SET NULL
) ENGINE=InnoDB;

CREATE INDEX idx_historial_pedido ON Historial_Estados_Pedido(Pedido_ID);

-- =====================================================================
-- MÓDULO LOGISTICS (envíos / rastreo)
-- =====================================================================

-- Envíos (guías, transportadoras, rastreo) - RF-12
CREATE TABLE Envios (
    ID_Envio               INT AUTO_INCREMENT PRIMARY KEY,
    Pedido_ID              INT          NOT NULL UNIQUE,
    Direccion_ID           INT          NULL,
    metodo_envio           VARCHAR(20)  NULL,  -- Estandar, Express
    transportadora         VARCHAR(30)  NULL,  -- Servientrega, Coordinadora, Deprisa, Otro
    numero_guia            VARCHAR(50)  NULL UNIQUE,
    estado                 VARCHAR(20)  NOT NULL DEFAULT 'Pendiente',
    fecha_despacho         TIMESTAMP    NULL,
    fecha_entrega_estimada TIMESTAMP    NULL,
    fecha_entrega_real     TIMESTAMP    NULL,
    observaciones          TEXT         NULL,
    fecha_creacion         TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP,
    fecha_actualizacion    TIMESTAMP    NULL ON UPDATE CURRENT_TIMESTAMP,
    CONSTRAINT fk_envios_pedido FOREIGN KEY (Pedido_ID)    REFERENCES Pedidos(ID_Pedido) ON DELETE CASCADE,
    CONSTRAINT fk_envios_direccion FOREIGN KEY (Direccion_ID) REFERENCES Direcciones(ID_Direccion) ON DELETE SET NULL
) ENGINE=InnoDB;

CREATE INDEX idx_envios_estado ON Envios(estado);

-- =====================================================================
-- MÓDULO MARKETING (RF-09 cupones/promociones, RF-20 devoluciones)
-- =====================================================================

CREATE TABLE Cupones (
    ID_Cupon              INT AUTO_INCREMENT PRIMARY KEY,
    codigo                VARCHAR(50)   NOT NULL UNIQUE,
    tipo_descuento        VARCHAR(20)   NOT NULL,  -- Porcentaje, MontoFijo
    valor_descuento       DECIMAL(12,2) NOT NULL CHECK (valor_descuento >= 0),
    monto_minimo          DECIMAL(12,2) NOT NULL DEFAULT 0,
    cantidad_maxima_usos  INT           NOT NULL DEFAULT 1,
    cantidad_usos_actuales INT          NOT NULL DEFAULT 0,
    fecha_inicio          DATETIME      NOT NULL,
    fecha_fin             DATETIME      NOT NULL,
    estado                VARCHAR(20)   NOT NULL DEFAULT 'Activo', -- Activo, Inactivo
    fecha_creacion        TIMESTAMP     NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CHECK (fecha_fin > fecha_inicio)
) ENGINE=InnoDB;

CREATE TABLE Promociones (
    ID_Promocion      INT AUTO_INCREMENT PRIMARY KEY,
    nombre_promocion  VARCHAR(150)  NOT NULL,
    descripcion       VARCHAR(500)  NULL,
    tipo_descuento    VARCHAR(20)   NOT NULL,  -- Porcentaje, MontoFijo
    valor_descuento   DECIMAL(12,2) NOT NULL CHECK (valor_descuento >= 0),
    producto_id       INT           NULL,
    categoria_id      INT           NULL,
    fecha_inicio      DATETIME      NOT NULL,
    fecha_fin         DATETIME      NOT NULL,
    estado            VARCHAR(20)   NOT NULL DEFAULT 'Activo', -- Activo, Inactivo, Expirado
    aplica_con_otros  TINYINT(1)    NOT NULL DEFAULT 0,
    fecha_creacion    TIMESTAMP     NOT NULL DEFAULT CURRENT_TIMESTAMP,
    fecha_actualizacion TIMESTAMP   NULL ON UPDATE CURRENT_TIMESTAMP,
    CONSTRAINT fk_promociones_producto FOREIGN KEY (producto_id)  REFERENCES Productos(id_Producto) ON DELETE CASCADE,
    CONSTRAINT fk_promociones_categoria FOREIGN KEY (categoria_id) REFERENCES Categorias(ID_Categoria) ON DELETE CASCADE,
    CHECK (fecha_fin > fecha_inicio)
) ENGINE=InnoDB;

CREATE INDEX idx_promociones_estado ON Promociones(estado);
CREATE INDEX idx_promociones_vigencia ON Promociones(fecha_inicio, fecha_fin);

CREATE TABLE Devoluciones (
    ID_Devolucion     INT AUTO_INCREMENT PRIMARY KEY,
    Pedido_ID         INT           NOT NULL,
    Usuario_ID        INT           NOT NULL,
    motivo            VARCHAR(30)   NOT NULL,  -- Defectuoso, Talla_Incorrecta, No_Me_Gusto, Otro
    descripcion       VARCHAR(1000) NOT NULL,
    estado            VARCHAR(20)   NOT NULL DEFAULT 'Solicitada',
    fecha_solicitud   TIMESTAMP     NOT NULL DEFAULT CURRENT_TIMESTAMP,
    fecha_resolucion  TIMESTAMP     NULL,
    monto_reembolso   DECIMAL(12,2) NOT NULL DEFAULT 0,
    CONSTRAINT fk_devoluciones_pedido FOREIGN KEY (Pedido_ID)  REFERENCES Pedidos(ID_Pedido) ON DELETE CASCADE,
    CONSTRAINT fk_devoluciones_usuario FOREIGN KEY (Usuario_ID) REFERENCES usuarios(id_Usuario) ON DELETE CASCADE
) ENGINE=InnoDB;

CREATE INDEX idx_devoluciones_pedido ON Devoluciones(Pedido_ID);
CREATE INDEX idx_devoluciones_estado ON Devoluciones(estado);

CREATE TABLE Imagenes_Devoluciones (
    ID_Imagen      INT AUTO_INCREMENT PRIMARY KEY,
    devolucion_id  INT          NOT NULL,
    url            VARCHAR(500) NOT NULL,
    CONSTRAINT fk_imgdev_devolucion FOREIGN KEY (devolucion_id) REFERENCES Devoluciones(ID_Devolucion) ON DELETE CASCADE
) ENGINE=InnoDB;

CREATE INDEX idx_imgdev_devolucion ON Imagenes_Devoluciones(devolucion_id);

-- =====================================================================
-- AUDITORÍA (RF-10 básica - logs inmutables de acciones críticas)
-- =====================================================================

CREATE TABLE logs_auditoria (
    ID_Log        INT AUTO_INCREMENT PRIMARY KEY,
    usuario_id    INT          NULL,
    usuario_email VARCHAR(150) NULL,
    accion        VARCHAR(50)  NOT NULL,  -- LOGIN, LOGIN_FALLIDO, CREATE, UPDATE, DELETE, CAMBIO_ESTADO_PEDIDO, etc.
    modulo        VARCHAR(50)  NULL,
    entidad       VARCHAR(50)  NULL,
    entidad_id    INT          NULL,
    datos_antes   TEXT         NULL,
    datos_despues TEXT         NULL,
    ip            VARCHAR(45)  NULL,
    resultado     VARCHAR(20)  NOT NULL DEFAULT 'EXITO', -- EXITO, ERROR
    fecha         TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_audit_usuario FOREIGN KEY (usuario_id) REFERENCES usuarios(id_Usuario) ON DELETE SET NULL
) ENGINE=InnoDB;

-- Los logs de auditoría son de solo lectura a nivel de aplicación (RF-10).
-- Índices para búsqueda por usuario, fecha y acción.
CREATE INDEX idx_audit_fecha ON logs_auditoria(fecha);
CREATE INDEX idx_audit_usuario ON logs_auditoria(usuario_id);
CREATE INDEX idx_audit_accion ON logs_auditoria(accion);

-- Nota: FK de Pedidos.Cupon_ID se añade después de crear Cupones (orden de creación).
ALTER TABLE Pedidos
    ADD CONSTRAINT fk_pedidos_cupon FOREIGN KEY (Cupon_ID) REFERENCES Cupones(ID_Cupon) ON DELETE SET NULL;
