# 🫖 Casa del Té — Kouchiku Bayashi

**E-Commerce Teahouse · Arquitectura Desacoplada**
*Java 17 · Spring Boot 3.5 · Angular 21 · MySQL 8 · Docker*

---

## 1. Descripción del Proyecto

Casa del Té es una plataforma de comercio electrónico diseñada para una tetería artesanal. El sistema utiliza una arquitectura de microservicios dividida en tres módulos independientes para garantizar la seguridad y escalabilidad, implementando flujos de **OAuth2 Authorization Code** y **JWT firmado con RSA 2048**.

### Módulos Principales

| Módulo | Puerto | Descripción |
| :--- | :---: | :--- |
| **auth-server** | `9000` | Servidor de autorización OAuth2/OIDC. Emite tokens JWT firmados con RSA. |
| **api-service** | `8080` | API REST de negocio: productos, pedidos, inventario, facturas, clientes, empleados y proveedores. |
| **browser-interface** | `4200` | SPA Angular: tienda online, carrito, checkout, panel de administración. |

### Funcionalidades Clave

- Catálogo de productos con categorías, búsqueda y filtros por precio.
- Carrito de compras (localStorage) con flujo de checkout y pago simulado.
- Sistema de pedidos dual: **pedidos de clientes** (TAKEAWAY/TABLE/DELIVERY) y **pedidos a proveedores** (reposición de stock).
- Facturación para clientes y proveedores con seguimiento de pagos.
- Gestión de inventario con alertas de stock bajo.
- Control de acceso por roles: Admin, Empleado, Cliente, Proveedor.
- Panel de administración para gestión de entidades.

---

## 2. 🚀 Despliegue Rápido con Docker

### Requisitos Previos

- Docker y Docker Compose instalados.

### Pasos para el Arranque

```bash
# 1. Clonar el repositorio
git clone https://github.com/PawelHomenda/e-commerce-project-tea-house
cd e-commerce-project-tea-house

# 2. Levantar toda la infraestructura
docker-compose up --build
```

### Acceso a los Servicios

| Servicio | URL | Notas |
| :--- | :--- | :--- |
| **Frontend** | [http://localhost:4200](http://localhost:4200) | SPA Angular (nginx) |
| **API de Negocio** | [http://localhost:8080/api](http://localhost:8080/api) | Swagger: `/swagger-ui/index.html` |
| **Auth Server** | [http://localhost:9000](http://localhost:9000) | Login form / API REST |
| **MySQL** | `localhost:3307` | DB: `casa_te`, user: `root`, pass via `.env` |

### Puertos de Depuración (JDWP)

| Servicio | Puerto Debug |
| :--- | :---: |
| api-service | `5005` |
| auth-server | `5006` |

### Servicios Docker

| Servicio | Contenedor | Imagen |
| :--- | :--- | :--- |
| `db` | `teahouse-db` | `mysql:8.0` |
| `auth-server` | `teahouse-auth` | Build desde `./auth_server` |
| `business-server` | `teahouse-business` | Build desde `./api_service` |
| `browser-interface` | `teahouse-frontend` | Build desde `./browser-interface` |

> **Nota:** El `docker-compose.yml` usa valores hardcoded. El archivo `.env.example` existe como referencia pero las variables no están conectadas al compose actual.

---

## 3. 🛠️ Stack Tecnológico

| Capa | Tecnología | Versión |
| :--- | :--- | :--- |
| **Backend - Auth** | Spring Boot + OAuth2 Authorization Server | 3.5.x |
| **Backend - API** | Spring Boot + Spring Security (Resource Server) + JPA/Hibernate | 3.5.x |
| **Frontend** | Angular + TypeScript | 18.x |
| **Base de Datos** | MySQL | 8.0 |
| **Seguridad** | JWT (RSA 2048) + OAuth2 Authorization Code | - |
| **Contenedores** | Docker & Docker Compose | - |
| **Web Server** | Nginx (frontend en producción) | - |

---

## 4. 🔑 Seguridad

### Flujo de Autenticación

```
┌──────────┐     1. Login/OAuth2     ┌─────────────┐
│ Frontend  │ ──────────────────────► │ Auth Server  │
│  :4200    │ ◄────────────────────── │   :9000      │
│           │     2. JWT Token        │              │
│           │                         └──────────────┘
│           │     3. API Request                │
│           │        + Bearer Token             │ Validates JWT
│           │ ──────────────────────► ┌──────────────┐
│           │ ◄────────────────────── │ API Service  │
└───────────┘     4. Response         │   :8080      │
                                      └──────────────┘
```

### Métodos de Autenticación

1. **Login directo (REST):** `POST /api/auth/login` → devuelve JWT.
2. **OAuth2 Authorization Code:** Flujo completo con consentimiento vía navegador.

### Roles y Scopes

| Scope JWT | Rol | Acceso Principal |
| :--- | :--- | :--- |
| `admin` | Administrador | Acceso total a todos los recursos. |
| `user:employee` | Empleado | Gestión de pedidos, inventario, empleados. Ve todas las órdenes. |
| `user:client` | Cliente | Sus propios pedidos, perfil y facturas. |
| `user:provider` | Proveedor | Sus propias órdenes de suministro y facturas. |

### Endpoints Públicos (sin autenticación)

- `GET /api/products/**` — Catálogo de productos.
- `GET /api/categories/**` — Categorías.
- `POST /api/auth/token` — Intercambio de código OAuth2.
- `GET /authorized` — Callback OAuth2.
- `GET /swagger-ui/**` — Documentación Swagger.
- `GET /actuator/**` — Health checks.

> Ver [AUTH_API.md](AUTH_API.md) y [BUSINESS_API.md](BUSINESS_API.md) para la referencia completa de endpoints.

---

## 5. 📂 Estructura del Repositorio

```text
e-commerce-project-tea-house/
├── auth_server/                  # Servidor de Autorización OAuth2/OIDC
│   ├── src/main/java/es/springbootcourse/auth_server/
│   │   ├── auth/                 #   SecurityConfig, RSA keys
│   │   ├── controllers/          #   AuthRestController, LoginController
│   │   ├── dto/                  #   LoginRequest
│   │   └── service/              #   JwtTokenService
│   └── src/main/resources/
│       ├── application.properties
│       └── templates/            #   login.html, logout.html, oauth2-consent.html
│
├── api_service/                  # API REST de Negocio
│   ├── src/main/java/es/kohchiku_bayashi/e_commerce_teahouse/
│   │   ├── auth/                 #   SecurityConfig (Resource Server)
│   │   ├── controller/           #   14 controladores REST
│   │   ├── model/                #   Entidades JPA + enums
│   │   ├── repository/           #   JPA Repositories
│   │   └── service/              #   Lógica de negocio
│   └── src/main/resources/
│       ├── application.yaml      #   Config principal
│       ├── application-dev.yaml  #   Override para Docker
│       ├── data.sql              #   Datos de semilla (23 productos, 10 pedidos, etc.)
│       └── schema.sql            #   DDL adicional
│
├── browser-interface/            # Frontend Angular SPA
│   ├── src/app/
│   │   ├── components/           #   home, login, profile, cart, checkout,
│   │   │                         #   order-confirmation, orders, product, admin,
│   │   │                         #   authorized, navbar
│   │   ├── guards/               #   AuthGuard, AdminGuard
│   │   ├── models/               #   TypeScript interfaces
│   │   └── services/             #   auth, product, category, order, cart,
│   │                             #   admin, employee, provider
│   └── src/environments/         #   API URLs config
│
├── docker-compose.yml            # Orquestación de 4 servicios
├── AUTH_API.md                   # Referencia API Auth Server
├── BUSINESS_API.md               # Referencia API de Negocio
├── ENDPOINT.md                   # Resumen rápido de todos los endpoints
└── IMPLEMENTATION_GUIDE.md       # Guía de implementación del control de acceso
```

---

## 6. 📊 Modelo de Datos

```text
Category ──1:N── Product ──1:1── Inventory
                    │
        ┌───────────┴───────────┐
        ▼                       ▼
DetailOrderClient         DetailOrderProvider
        │                       │
        ▼                       ▼
  OrderClient              OrderProvider
   │       │                │       │
   ▼       ▼                ▼       ▼
Client  Employee         Provider  Employee
   │                        │
   ▼                        ▼
InvoiceClient          InvoiceProvider
```

### Enums

| Enum | Valores |
| :--- | :--- |
| **OrderState** | `PENDENT`, `PREPARING`, `DELIVERED`, `CANCELED` |
| **ServiceType** | `TAKEAWAY`, `TABLE`, `DELIVERY` |
| **PaymentMethod** | `METALIC`, `CREDIT` |
| **PaymentState** | `PAID`, `PENDENT` |

---

## 7. 👥 Usuarios de Prueba (Entorno Dev)

Todos los usuarios usan la contraseña: **`1234`**

| Usuario | Rol | Scope JWT |
| :--- | :--- | :--- |
| `admin` | ADMIN | `admin` |
| `employee1` | EMPLOYEE | `user:employee` |
| `employee2` | EMPLOYEE | `user:employee` |
| `employee3` | EMPLOYEE | `user:employee` |
| `client1` | CLIENT | `user:client` |
| `client2` | CLIENT | `user:client` |
| `client3` | CLIENT | `user:client` |
| `provider1` | PROVIDER | `user:provider` |
| `provider2` | PROVIDER | `user:provider` |
| `provider3` | PROVIDER | `user:provider` |

### Cliente OAuth2 Registrado

| Campo | Valor |
| :--- | :--- |
| Client ID | `client-app` |
| Client Secret | `1234` |
| Grant Types | `authorization_code`, `refresh_token` |
| Redirect URIs | `http://127.0.0.1:8080/authorized`, `http://localhost:4200/authorized` |
| Scopes | `openid`, `profile`, `email`, `read`, `write`, `user:client`, `user:employee`, `user:provider`, `admin` |

---

## 8. 🗂️ Datos de Semilla

La base de datos se inicializa automáticamente con:

| Entidad | Cantidad |
| :--- | :--- |
| Categorías | 2 (DRINK, DESSERT) |
| Productos | 23 (10 bebidas, 13 postres) |
| Inventario | 23 registros |
| Empleados | 5 |
| Proveedores | 5 |
| Clientes | 10 |
| Pedidos de Cliente | 10 |
| Detalles de Pedido Cliente | 29 |
| Facturas de Cliente | 8 |
| Pedidos a Proveedor | 7 |
| Detalles de Pedido Proveedor | 30 |
| Facturas de Proveedor | 7 |

---

## 9. � Desarrollo Local (sin Docker)

Para quienes prefieran ejecutar cada servicio por separado en su máquina.

### Requisitos Previos

| Herramienta | Versión mínima | Verificar con |
| :--- | :--- | :--- |
| **Java JDK** | 17+ | `java -version` |
| **Maven** | 3.8+ | `mvn -version` |
| **Node.js** | 18+ | `node -v` |
| **npm** | 9+ | `npm -v` |
| **MySQL** | 8.0 | `mysql --version` |

### Paso 1 — Preparar la Base de Datos

```bash
# Conectar a MySQL y crear la base de datos
mysql -u root -p
```
```sql
CREATE DATABASE IF NOT EXISTS casa_te;
```

> Las tablas y datos de semilla se crean automáticamente al arrancar el `api-service` (JPA `ddl-auto: create` + `data.sql`).

### Paso 2 — Arrancar Auth Server (Puerto 9000)

```bash
cd auth_server
./mvnw spring-boot:run
```

Verificar: [http://localhost:9000/actuator/health](http://localhost:9000/actuator/health) → `{"status":"UP"}`

### Paso 3 — Arrancar API Service (Puerto 8080)

```bash
cd api_service
./mvnw spring-boot:run
```

> Requiere que MySQL esté corriendo en `localhost:3306` con usuario `root` y la contraseña configurada en `application.yaml` (por defecto usa la variable de entorno `SPRING_DATASOURCE_PASSWORD`).

Verificar: [http://localhost:8080/api/products](http://localhost:8080/api/products) → lista de productos.

### Paso 4 — Arrancar Frontend Angular (Puerto 4200)

```bash
cd browser-interface
npm install
npm start
```

Verificar: [http://localhost:4200](http://localhost:4200) → página de inicio de la tienda.

### Orden de Arranque

```
1. MySQL       → la BD debe estar disponible antes del api-service.
2. Auth Server → emite los tokens JWT.
3. API Service → valida tokens contra el auth-server.
4. Frontend    → consume la API y el auth-server.
```

### Configuración Local vs Docker

| Config | Local (default) | Docker (env vars) |
| :--- | :--- | :--- |
| MySQL host | `localhost:3306` | `db:3306` |
| JWT validation | `issuer-uri: http://localhost:9000` | `jwk-set-uri: http://auth-server:9000/oauth2/jwks` |
| Frontend proxy | Directo a `localhost:8080` | Nginx reverse proxy |

---

## 10. 🧪 Probando la API (Ejemplos cURL)

### Obtener un Token JWT

```bash
# Login directo como admin
curl -s -X POST http://localhost:9000/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{"username": "admin", "password": "1234"}'
```

Respuesta:
```json
{
  "token": "eyJhbGciOiJSUzI1NiJ9...",
  "username": "admin",
  "roles": ["ROLE_ADMIN"]
}
```

### Consultar Productos (público, sin token)

```bash
curl -s http://localhost:8080/api/products | jq
```

### Crear un Pedido (como cliente autenticado)

```bash
# 1. Obtener token de client1
TOKEN=$(curl -s -X POST http://localhost:9000/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{"username": "client1", "password": "1234"}' | jq -r '.token')

# 2. Crear pedido
curl -s -X POST http://localhost:8080/api/orders/clients \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer $TOKEN" \
  -d '{
    "client": { "id": 1 },
    "orderDate": "2026-03-27",
    "orderState": "PENDENT",
    "serviceType": "DELIVERY",
    "detailOrderClients": [
      { "product": { "id": 1 }, "quantity": 2, "unitPrice": 3.50 },
      { "product": { "id": 5 }, "quantity": 1, "unitPrice": 4.00 }
    ]
  }' | jq
```

### Ver Mis Pedidos (como cliente)

```bash
curl -s http://localhost:8080/api/orders/clients \
  -H "Authorization: Bearer $TOKEN" | jq
```

### Gestionar Inventario (como empleado)

```bash
# Login como empleado
TOKEN=$(curl -s -X POST http://localhost:9000/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{"username": "employee1", "password": "1234"}' | jq -r '.token')

# Ver productos con stock bajo
curl -s http://localhost:8080/api/inventory/low-stock \
  -H "Authorization: Bearer $TOKEN" | jq

# Añadir stock a un producto
curl -s -X PATCH "http://localhost:8080/api/inventory/product/1/add-stock?quantity=50" \
  -H "Authorization: Bearer $TOKEN" | jq
```

> **Tip:** Si no tienes `jq` instalado, omite `| jq` del final de cada comando. Es solo para formatear el JSON.

---

## 11. �📖 Documentación Adicional

| Documento | Descripción |
| :--- | :--- |
| [AUTH_API.md](AUTH_API.md) | Referencia completa del Auth Server (login, OAuth2, JWT). |
| [BUSINESS_API.md](BUSINESS_API.md) | Referencia completa de la API de negocio (14 controladores, ~100 endpoints). |
| [ENDPOINT.md](ENDPOINT.md) | Tabla resumen rápida de todos los endpoints con roles requeridos. |
| [IMPLEMENTATION_GUIDE.md](IMPLEMENTATION_GUIDE.md) | Guía de implementación del sistema de control de acceso por roles. |

