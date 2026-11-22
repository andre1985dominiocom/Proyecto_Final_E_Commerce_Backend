-- =====================================================================
-- DidiStore E-Commerce - Datos iniciales (seed)
-- Ejecutar DESPUÉS de schema.sql:
--   mysql -u root -p db_e_commerce_didistore < database/seed.sql
-- =====================================================================

USE db_e_commerce_didistore;

-- =====================================================================
-- PERFILES (RF-04 roles predefinidos: Administrador, Empleado, Cliente)
-- Los IDs 1/2/3 son asumidos por LoginServlet (1=ADMIN, 2=EMPLEADO, 3=CLIENTE)
-- =====================================================================
INSERT INTO Perfiles (ID_Perfil, Nombre_perfil, Descripcion_perfil) VALUES
(1, 'Administrador', 'Acceso total al sistema: catálogo, usuarios, reportes, configuración'),
(2, 'Empleado',      'Gestión operativa: inventario, pedidos, reportes limitados'),
(3, 'Cliente',       'Compra, carrito, pedidos, perfil y reseñas')
ON DUPLICATE KEY UPDATE Nombre_perfil = VALUES(Nombre_perfil);

-- =====================================================================
-- PERMISOS (RF-04 - matriz granular)
-- =====================================================================
INSERT INTO Permisos (ID_Permiso, Nombre_permiso, Descripcion_permiso) VALUES
(1,  'GESTIONAR_USUARIO',      'Permite administrar usuarios'),
(2,  'GESTIONAR_PERFILES',     'Permite administrar perfiles'),
(3,  'GESTIONAR_PERMISOS',     'Permite administrar permisos'),
(4,  'GESTIONAR_INVENTARIO',   'Permite administrar inventario'),
(5,  'CREAR_PRODUCTO',         'Permite crear producto'),
(6,  'EDITAR_PRODUCTO',        'Permite editar producto'),
(7,  'VER_PRODUCTOS',          'Permite ver productos'),
(8,  'ELIMINAR_PRODUCTO',      'Permite eliminar producto'),
(9,  'EDITAR_USUARIO',         'Permite editar usuario'),
(10, 'VER_USUARIOS',           'Permite ver usuarios'),
(11, 'ELIMINAR_USUARIO',       'Permite eliminar usuarios'),
(12, 'VER_PEDIDOS',            'Permite ver pedidos'),
(13, 'ACTUALIZAR_ESTADO_PEDIDO','Permite actualizar estado pedido'),
(14, 'CANCELAR_PEDIDO',        'Permite cancelar pedido'),
(15, 'VER_REPORTES',           'Permiso para ver reportes'),
(16, 'EXPORTAR_REPORTES',      'Permite exportar reportes'),
(17, 'CONFIGURAR_SISTEMA',     'Permite configurar sistema'),
(18, 'AGREGAR_CARRITO',        'Permite agregar productos al carrito'),
(19, 'REALIZAR_COMPRA',        'Permite realizar compras'),
(20, 'VER_MIS_PEDIDOS',        'Permite ver pedidos a los clientes'),
(21, 'EDITAR_MI_PERFIL',       'Permite editar el perfil al cliente'),
(22, 'GESTIONAR_PROMOCIONES',  'Permite crear y gestionar cupones y promociones'),
(23, 'GESTIONAR_DEVOLUCIONES', 'Permite revisar y resolver devoluciones'),
(24, 'GESTIONAR_ENVIOS',       'Permite despachar envíos y gestionar guías'),
(25, 'MODERAR_RESENAS',        'Permite aprobar o rechazar reseñas')
ON DUPLICATE KEY UPDATE Descripcion_permiso = VALUES(Descripcion_permiso);

-- =====================================================================
-- RELACIONES PERFIL-PERMISO (RF-04 asignación automática por rol)
-- =====================================================================
INSERT INTO Perfil_permiso (ID_Perfil, ID_Permiso) VALUES
-- Administrador: todos los permisos
(1, 1), (1, 2), (1, 3), (1, 4), (1, 5), (1, 6), (1, 7), (1, 8),
(1, 9), (1, 10), (1, 11), (1, 12), (1, 13), (1, 14), (1, 15), (1, 16),
(1, 17), (1, 18), (1, 19), (1, 20), (1, 21), (1, 22), (1, 23), (1, 24), (1, 25),
-- Empleado: operación sin configuración ni borrados críticos
(2, 4), (2, 6), (2, 7), (2, 10), (2, 12), (2, 13), (2, 15), (2, 23), (2, 24), (2, 25),
-- Cliente: compra y autogestión
(3, 7), (3, 18), (3, 19), (3, 20), (3, 21)
ON DUPLICATE KEY UPDATE ID_Permiso = VALUES(ID_Permiso);

-- =====================================================================
-- USUARIOS INICIALES
-- Contraseñas hasheadas con BCrypt costo 12 (RNF-01)
--   Admin1234 / Empleado1234 / Cliente1234
-- =====================================================================
INSERT INTO usuarios
(id_Usuario, email, contrasena, nombre, apellido, documento, tipo_documento, perfil_id, estado, email_verificado, fecha_creacion, fecha_actualizacion, fecha_ultimo_login, intentos_fallidos, bloqueado_hasta)
VALUES
(1, 'admin@didistore.com',
 '$2a$12$dBrtGXuhBq.Nn.6VayqG3eoIMmwqHPAD9xecUXtAaksMF69h6zhRu',
 'Admin', 'DidiStore', '1000000001', 'CC', 1, 'Activo', 1, NOW(), NOW(), NULL, 0, NULL)
ON DUPLICATE KEY UPDATE email = VALUES(email);

-- =====================================================================
-- CATEGORÍAS (semilla usada por TestDB - 3 raíces + subcategorías)
-- =====================================================================
INSERT INTO Categorias (ID_Categoria, nombre_Categoria, Descripcion, Categoria_padre_ID) VALUES
(1,  'Pijamas de Mujer', 'Categoría principal de Pijamas de Mujer', NULL),
(2,  'Accesorios',       'Categoría principal de Accesorios',       NULL),
(3,  'Complementos',     'Categoría principal de Complementos',     NULL),
(4,  'Pijamas Pantalón', 'Subcategoría de Pijamas de Mujer', 1),
(5,  'Pijamas Short',    'Subcategoría de Pijamas de Mujer', 1),
(6,  'Batas',            'Subcategoría de Pijamas de Mujer', 1),
(7,  'Organizadores',    'Subcategoría de Accesorios',       2),
(8,  'Cosmetiqueras',    'Subcategoría de Accesorios',       2),
(9,  'Bolsos',           'Subcategoría de Complementos',     3),
(10, 'Morrales',         'Subcategoría de Complementos',     3)
ON DUPLICATE KEY UPDATE nombre_Categoria = VALUES(nombre_Categoria);

-- =====================================================================
-- DEPARTAMENTOS Y CIUDADES (Colombia - para direcciones de envío)
-- =====================================================================
INSERT INTO departamentos (id_Departamento, departamento) VALUES
(1, 'Bogotá D.C.'),
(2, 'Antioquia'),
(3, 'Valle del Cauca'),
(4, 'Atlántico'),
(5, 'Bolívar'),
(6, 'Cundinamarca'),
(7, 'Santander'),
(8, 'Risaralda')
ON DUPLICATE KEY UPDATE departamento = VALUES(departamento);

INSERT INTO ciudades (id_Ciudad, nombre_Ciudad, departamento_Id, codigo_Postal) VALUES
(1,  'Bogotá',        1, '110111'),
(2,  'Medellín',      2, '050001'),
(3,  'Cali',          3, '760001'),
(4,  'Barranquilla',  4, '080001'),
(5,  'Cartagena',     5, '130001'),
(6,  'Soacha',        6, '250001'),
(7,  'Bucaramanga',   7, '680001'),
(8,  'Pereira',       8, '660001'),
(9,  'Manizales',     8, '660001'),
(10, 'Ciénaga',       5, '137001')
ON DUPLICATE KEY UPDATE nombre_Ciudad = VALUES(nombre_Ciudad);

-- =====================================================================
-- CUPÓN DE EJEMPLO (para probar RF-09 en checkout)
-- =====================================================================
INSERT INTO Cupones
(codigo, tipo_descuento, valor_descuento, monto_minimo, cantidad_maxima_usos, cantidad_usos_actuales, fecha_inicio, fecha_fin, estado)
VALUES
('BIENVENIDA10', 'Porcentaje', 10.00, 50000.00, 100, 0, '2026-01-01 00:00:00', '2027-12-31 23:59:59', 'Activo'),
('ENVIOGRATIS',  'MontoFijo',  15000.00, 120000.00, 50, 0, '2026-01-01 00:00:00', '2027-12-31 23:59:59', 'Activo');

-- =====================================================================
-- PROMOCIONES ACTIVAS (banner página principal - CU-CLI-01 / RF-09)
-- =====================================================================
INSERT INTO Promociones
(nombre_promocion, descripcion, tipo_descuento, valor_descuento, producto_id, categoria_id, fecha_inicio, fecha_fin, estado, aplica_con_otros)
VALUES
('Temporada de Pijamas', 'Hasta 15% en pijamas seleccionados', 'Porcentaje', 15.00, NULL, 4, '2026-01-01 00:00:00', '2027-12-31 23:59:59', 'Activo', 0),
('Accesorios al detalle', '$5.000 de descuento en accesorios', 'MontoFijo', 5000.00, NULL, 8, '2026-01-01 00:00:00', '2027-06-30 23:59:59', 'Activo', 1);

-- =====================================================================
-- PRODUCTO DE EJEMPLO (catálogo no vacío para pruebas visuales)
-- =====================================================================
INSERT INTO Productos
(id_Producto, nombre_Producto, descripcion_Corta, descripcion_Larga, precio, SKU, talla, color, categoria_Id, estado, es_Destacado)
VALUES
(1, 'Pijama Rosa Seda', 'Pijama de pijamas cortos en rosa', 'Pijama suave de seda sintética, ideal para el verano. Incluye camiseta y pantalón.', 89900.00, 'PIJ-ROSA-M', 'M', 'Rosa', 4, 'Activo', 1),
(2, 'Cosmetiquera Floral', 'Cosmetiquera con estampado floral', 'Cosmetiquera resistente con compartimentos internos.', 45000.00, 'COS-FLOR-U', NULL, 'Multicolor', 8, 'Activo', 1);

INSERT INTO Inventarios (Producto_ID, Stock_actual, Stock_minimo, Stock_reservado) VALUES
(1, 50, 5, 0),
(2, 30, 3, 0);

INSERT INTO Imagenes_Productos (producto_id, url, formato) VALUES
(1, '/assets/images/products/pijama_sapitos.png', 'PNG'),
(2, '/assets/images/products/bolso-rumania-negro-2.jpg', 'JPG');

-- Verificación rápida
SELECT 'Seed completado' AS resultado;
SELECT COUNT(*) AS perfiles FROM Perfiles;
SELECT COUNT(*) AS permisos FROM Permisos;
SELECT COUNT(*) AS usuarios FROM usuarios;
SELECT COUNT(*) AS categorias FROM Categorias;
SELECT COUNT(*) AS productos FROM Productos;
