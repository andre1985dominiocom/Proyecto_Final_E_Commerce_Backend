# DidiStore — E-Commerce Backend

Plataforma e-commerce académica construida con **Java 17 + Jakarta EE 10** (Servlets y JDBC puros, sin Spring ni Hibernate), frontend estático HTML/CSS/JS empaquetado en el WAR y **MySQL**.

Repositorio: `Proyecto_Final_E_Commerce_Backend`

---

## Características principales

| RF | Función |
|----|---------|
| RF-01 | Login seguro con BCrypt, bloqueo por intentos fallidos |
| RF-04 | Carrito de compras (agregar, actualizar, vaciar) |
| RF-06 | Checkout con direcciones guardadas / nuevas |
| RF-09 | Cupones y promociones (código, % o monto fijo) |
| RF-10 | Seguimiento de estados del pedido (máquina de estados) |
| RF-12 | Historial de cambios de estado + pagos mock |
| RF-13 | Gestión de usuarios (CRUD admin + derecho al olvido) |
| RF-14 | Pagos simulados (mock de pasarela) |
| RF-17 | Historial de compras con filtros, factura, rastreo, recompra |
| RF-21 | Reseñas y calificaciones (solo compradores verificados + moderación) |
| RF-22 | Panel administrativo (productos, categorías, pedidos, usuarios, promos, reseñas) |

Fuera de alcance (mocks razonables): Mercado Pago real, SMTP, JWT/HTTPS, notificaciones push, exportación PDF/Excel nativa.

---

## Requisitos

- **JDK 17**
- **Maven 3.8+**
- **MySQL 8.x / 9.x**
- Servlet container: **Tomcat 10.1** (Jakarta EE 10) o NetBeans con Tomcat
- Navegador moderno (módulos ES)

---

## Puesta en marcha

### 1. Base de datos

```bash
# Crear la BD y cargar esquema + datos semilla
mysql -u root -p < database/schema.sql
mysql -u root -p < database/seed.sql
```

### 2. Configuración de conexión

Copia la plantilla y edita tus credenciales (**no subas** el archivo real a Git):

```bash
copy database\database.properties.example src\main\resources\database.properties
```

También puedes usar variables de entorno (tienen prioridad):

| Variable | Descripción |
|----------|-------------|
| `DB_HOST` | Host MySQL (default `localhost`) |
| `DB_PORT` | Puerto (default `3306`) |
| `DB_NAME` | `db_e_commerce_didistore` |
| `DB_USER` | Usuario MySQL |
| `DB_PASSWORD` | Contraseña MySQL |

### 3. Compilar y ejecutar

```bash
# Compilar + pruebas
mvn test

# Empaquetar WAR
mvn package -DskipTests

# Resultado: target/didistorebackend-1.0-SNAPSHOT.war
```

Despliega el WAR en Tomcat 10.1 (contexto por ejemplo `/didistorebackend`).

### 4. Usuarios semilla (seed)

| Rol | Email | Contraseña |
|-----|-------|------------|
| Administrador | `admin@didistore.com` | `Admin1234` |
| Empleado | (ver `database/seed.sql`) | `Empleado1234` |
| Cliente | (ver `database/seed.sql`) | `Cliente1234` |

---

## Estructura del proyecto

```
Proyecto_Final_E_Commerce_Backend/
├── database/
│   ├── schema.sql                 # 29 tablas
│   ├── seed.sql                   # Datos iniciales
│   └── database.properties.example # Plantilla de configuración
├── pom.xml
├── src/main/java/com/didistore/
│   ├── config/                    # Conexion, CORS, filtros Auth/Role
│   ├── controller/                # Lógica de negocio
│   ├── dao/                       # Interfaces + impl JDBC
│   ├── model/                     # Entidades y enums
│   ├── servlet/                   # Endpoints HTTP (@WebServlet)
│   └── util/                      # PasswordUtil, AuditLog, máquina de estados
├── src/main/webapp/
│   ├── html/                      # Vistas (auth, admin, cart, orders…)
│   ├── js/                        # Frontend modular (ES modules)
│   └── WEB-INF/web.xml            # Filtros en orden: Auth → Role → Cors
└── src/test/java/                 # Tests JUnit 5
```

---

## Arquitectura de seguridad

1. **AuthFilter** — recursos públicos vs sesión requerida  
2. **RoleFilter** — control por rol (`ADMIN` / `EMPLEADO` / `CLIENTE`)  
3. **CorsFilter** — cabeceras CORS  

Contraseñas con **BCrypt** (costo 12). Auditoría de eventos relevantes en `AuditLog`.

---

## API principal (resumen)

| Método | Ruta | Descripción |
|--------|------|-------------|
| POST | `/login` | Inicio de sesión |
| POST/GET | `/logout` | Cierre de sesión |
| GET/POST/PUT/DELETE | `/admin/usuarios` | CRUD usuarios + anonimizar (DELETE + `motivo`) |
| GET | `/sales/pedido?accion=historial` | Historial con filtros fecha/estado |
| POST | `/sales/pedido?accion=checkout` | Crear pedido |
| POST | `/sales/pedido?accion=recompra` | Recomprar items de un pedido |
| POST | `/sales/pagos?accion=pagar` | Pago mock |
| POST | `/sales/estados-pedido?accion=cambiar` | Cambiar estado (staff) |
| GET | `/catalog/resena?productoId=` | Reseñas aprobadas + promedio |
| POST | `/catalog/resena` | Dejar reseña (compra verificada) |
| GET/PUT | `/admin/resenas` | Moderación (aprobar/rechazar) |
| GET | `/catalog/cupon` / `/catalog/promociones` | Cupones y banner públicos |

---

## Pruebas

```bash
mvn test
```

Cobertura actual (62 tests, sin BD):

- Máquina de estados de pedidos (RF-12)
- Validación de contraseñas / BCrypt
- Cupones: descuentos y validaciones (RF-09)
- Reseñas: rangos, filtro de lenguaje, moderación (RF-21)
- Anonimización de usuarios con DAO falso (RF-13)
- Enums de dominio

---

## Cómo revisar los cambios en local

```bash
git status
git diff
git log --oneline -10
```

---

## Solución de problemas

### Bucle al cerrar sesión (corregido)

**Síntoma:** desde el dashboard de admin, *Cerrar sesión* recargaba la página en bucle.

**Causa:** `dashboard.js` redirigía a `dashboard.html` después de limpiar la sesión local (y también si no había sesión), generando un bucle de redirecciones.

**Solución:** el logout ahora llama a `POST /logout`, limpia `localStorage` y redirige a `/html/auth/login.html` con `location.replace`.

Si el problema persiste tras desplegar el WAR nuevo:

1. Haz **hard refresh** (`Ctrl+F5`) para limpiar caché del navegador.  
2. Borra datos del sitio (localStorage) o prueba en ventana incógnita.  
3. Confirma que el WAR desplegado es el último (`target/didistorebackend-1.0-SNAPSHOT.war`).

---

## Autor

Proyecto final — Programación de Software / ADSO  
DidiStore E-Commerce Backend
