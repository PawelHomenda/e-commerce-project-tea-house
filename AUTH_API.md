# 🔐 Auth Server API Reference

**Base URL:** `http://localhost:9000`
**Protocolo:** OAuth2 / OpenID Connect (OIDC)
**Formato Token:** JWT firmado con RSA 2048
**Expiración Token:** 1 hora

---

## 1. Autenticación Directa (REST)

Ideal para aplicaciones móviles, pruebas en Postman o login desde SPA sin flujo de navegador OAuth2.

### POST `/api/auth/login`

Intercambia credenciales por un JWT firmado.

**Request Body:**
```json
{
  "username": "admin",
  "password": "1234"
}
```

**Response (200 OK):**
```json
{
  "token": "eyJhbGciOiJSUzI1NiIsInR5cCI6IkpXVCJ9...",
  "username": "admin",
  "roles": ["ROLE_ADMIN"]
}
```

**Response (401 Unauthorized):**
```json
{
  "error": "Credenciales inválidas"
}
```

---

## 2. Flujo OAuth2 Authorization Code

Flujo completo para la integración con el frontend Angular. El navegador se redirige al auth server para login y consentimiento.

### Paso 1 — Iniciar Autorización

```
GET /oauth2/authorize
  ?response_type=code
  &client_id=client-app
  &redirect_uri=http://localhost:4200/authorized
  &scope=openid profile email user:client
```

El usuario es redirigido a la página de login (`/login`) y luego a la pantalla de consentimiento.

### Paso 2 — Callback con Código

Tras la aprobación, el auth server redirige al `redirect_uri` con un código:

```
http://localhost:4200/authorized?code=AUTHORIZATION_CODE
```

### Paso 3 — Intercambiar Código por Token

Desde el frontend, se envía el código al API Service para que lo intercambie:

```
POST http://localhost:8080/api/auth/token
Content-Type: application/json

{
  "code": "AUTHORIZATION_CODE"
}
```

El API Service intercambia el código directamente con el Auth Server (`POST /oauth2/token`) usando las credenciales del cliente registrado.

**Response (200 OK):**
```json
{
  "access_token": "eyJhbGciOiJSUzI1NiIsInR5cCI6IkpXVCJ9...",
  "token_type": "Bearer",
  "expires_in": 3600,
  "scope": "openid profile email user:client"
}
```

---

## 3. Endpoints OpenID Connect / Discovery

| Método | Endpoint | Descripción |
| :--- | :--- | :--- |
| `GET` | `/.well-known/openid-configuration` | Configuración de descubrimiento OIDC. |
| `GET` | `/oauth2/jwks` | Claves públicas JWK Set para validar tokens. |
| `GET` | `/oauth2/authorize` | Inicio del flujo de autorización. |
| `POST` | `/oauth2/token` | Intercambio de código por Access Token. |
| `GET` | `/userinfo` | Información del usuario autenticado (OIDC). |

### GET `/.well-known/openid-configuration`

**Response (200 OK):**
```json
{
  "issuer": "http://localhost:9000",
  "authorization_endpoint": "http://localhost:9000/oauth2/authorize",
  "token_endpoint": "http://localhost:9000/oauth2/token",
  "jwks_uri": "http://localhost:9000/oauth2/jwks",
  "response_types_supported": ["code"],
  "scopes_supported": ["openid", "profile", "email", "read", "write", "user:client", "user:employee", "user:provider", "admin"]
}
```

### GET `/oauth2/jwks`

**Response (200 OK):**
```json
{
  "keys": [
    {
      "kty": "RSA",
      "n": "v9... (clave pública)",
      "e": "AQAB",
      "alg": "RS256",
      "use": "sig"
    }
  ]
}
```

---

## 4. Páginas Web (Form Login)

| Método | Ruta | Descripción |
| :--- | :--- | :--- |
| `GET` | `/` | Redirige a `/login`. |
| `GET` | `/login` | Formulario de login HTML. |
| `GET` | `/logout` | Página de cierre de sesión. |
| `GET` | `/oauth2/consent` | Pantalla de consentimiento OAuth2. |

---

## 5. Estructura del JWT (Claims)

El token JWT emitido contiene los siguientes claims:

```json
{
  "sub": "admin",
  "scope": "admin",
  "iss": "http://localhost:9000",
  "iat": 1711288800,
  "exp": 1711292400
}
```

| Claim | Descripción |
| :--- | :--- |
| `sub` | Nombre de usuario (también usado como `oauth2Id` en el API Service). |
| `scope` | Rol del usuario en minúsculas sin prefijo `ROLE_` (ej. `admin`, `user:client`). |
| `iss` | Emisor del token. En Docker: `http://localhost:9000`. |
| `iat` | Timestamp de emisión. |
| `exp` | Timestamp de expiración (1 hora después de `iat`). |

---

## 6. Cliente OAuth2 Registrado

| Campo | Valor |
| :--- | :--- |
| **Client ID** | `client-app` |
| **Client Secret** | `1234` (stored as `{noop}1234`) |
| **Grant Types** | `authorization_code`, `refresh_token` |
| **Redirect URIs** | `http://127.0.0.1:8080/authorized`, `http://localhost:4200/authorized` |
| **Scopes Permitidos** | `openid`, `profile`, `email`, `read`, `write`, `user:client`, `user:employee`, `user:provider`, `admin` |
| **Requiere Consentimiento** | Sí |
| **PKCE** | No requerido |

---

## 7. Usuarios de Prueba

Todos usan contraseña: **`1234`**

| Username | Rol | Scope JWT |
| :--- | :--- | :--- |
| `admin` | ADMIN | `admin` |
| `client1` | CLIENT | `user:client` |
| `client2` | CLIENT | `user:client` |
| `client3` | CLIENT | `user:client` |
| `employee1` | EMPLOYEE | `user:employee` |
| `employee2` | EMPLOYEE | `user:employee` |
| `employee3` | EMPLOYEE | `user:employee` |
| `provider1` | PROVIDER | `user:provider` |
| `provider2` | PROVIDER | `user:provider` |
| `provider3` | PROVIDER | `user:provider` |

---

## 8. Health Check

### GET `/actuator/health`

**Response (200 OK):**
```json
{
  "status": "UP"
}
```

---

## 9. Configuración de Red Docker

Dentro de la red Docker (`teahouse-network`), el Auth Server se comunica como:
- **Issuer interno:** `http://auth-server:9000`
- **JWK Set URI (usado por api-service):** `http://auth-server:9000/oauth2/jwks`

Desde el navegador/host, se accede como:
- **Issuer externo:** `http://localhost:9000`