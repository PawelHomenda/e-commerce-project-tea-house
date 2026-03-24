# 🔐 Auth Server API Reference
**Base URL:** `http://localhost:9000`  
**Protocolo:** OAuth2 / OpenID Connect (OIDC)  
**Formato Token:** JWT (Firmado con RSA 2048)

## 1. Autenticación Directa (REST)
Ideal para aplicaciones móviles o pruebas rápidas en Postman sin flujo de navegador.

| Método | Endpoint | Acceso | Descripción |
| :--- | :--- | :--- | :--- |
| `POST` | `/api/auth/login` | Público | Intercambia credenciales por un JWT. |
| `GET` | `/actuator/health` | Público | Estado de salud del servidor (UP/DOWN). |

**Ejemplo de Request (Login):**
```json
{
  "username": "admin",
  "password": "1234"
}
```

## 2. Flujo OAuth2 / OpenID Connect

Endpoints estándar para la integración con el frontend (Angular).

|Método|Endpoint|Descripción|
|---|---|---|
|GET|	/.well-known/openid-configuration|	Configuración de descubrimiento del servidor.|
GET|	/oauth2/jwks|	Exposición de claves públicas (JWK Set) para validación.|
GET|	/oauth2/authorize|	Punto de entrada para el flujo de código de autorización.|
POST|	/oauth2/token|	Intercambio del código por el Access Token.|

## 3. Estructura del JWT (Claims)

El token emitido contiene la siguiente información:


```
    sub: Nombre de usuario.

    scope: Roles del usuario (ej. admin, employee, client).

    iss: http://auth-server:9000 (en entorno Docker).

    exp: Expiración (1 hora por defecto).
```

---

### 2. Documentación: Business API (Puerto 8080)
Este módulo gestiona la lógica de negocio y los recursos de la tetería.

```markdown
# ☕ Business API Reference (api-service)
**Base URL:** `http://localhost:8080/api`  
**Seguridad:** Bearer Token (JWT) requerido en rutas protegidas.

## 1. Productos y Categorías
Acceso mayoritariamente público para clientes.

| Método | Endpoint | Auth | Descripción |
| :--- | :--- | :--- | :--- |
| `GET` | `/products` | Público | Lista paginada de todos los productos. |
| `GET` | `/products/{id}` | Público | Detalle de un producto específico. |
| `POST` | `/products` | ADMIN | Crear nuevo producto. |
| `PUT` | `/products/{id}` | ADMIN/EMP | Actualizar stock o detalles. |
| `GET` | `/categories` | Público | Listar categorías de té. |

## 2. Gestión de Clientes y Empleados
Reservado para administración.

| Método | Endpoint | Auth | Descripción |
| :--- | :--- | :--- | :--- |
| `GET` | `/clients` | ADMIN/EMP | Listado de clientes registrados. |
| `GET` | `/employees` | ADMIN | Gestión de personal de la tetería. |
| `GET` | `/auth/me` | JWT | Devuelve los datos del perfil del usuario logueado. |

## 3. Inventario y Proveedores
Operaciones críticas de logística.

| Método | Endpoint | Auth | Descripción |
| :--- | :--- | :--- | :--- |
| `GET` | `/inventory` | ADMIN/EMP | Estado actual del almacén. |
| `PUT` | `/inventory/{id}` | ADMIN/EMP | Ajuste manual de existencias. |
| `POST` | `/orders-provider` | ADMIN | Generar pedido de reposición a proveedor. |

## 4. Códigos de Estado HTTP
* `200 OK`: Éxito.
* `201 Created`: Recurso creado (POST).
* `401 Unauthorized`: Token ausente o inválido.
* `403 Forbidden`: Token válido pero sin permisos de Rol (ej. Cliente intentando borrar producto).
* `404 Not Found`: El ID solicitado no existe.