# 🔐 Auth Server API Reference
**Base URL:** `http://localhost:9000`

## 1. Autenticación Directa (REST)

### POST `/api/auth/login`
Intercambia credenciales por un JWT firmado.

**Request Body:**
```json
{
  "username": "admin",
  "password": "1234"
}
```

**Response Body (200 OK):**
```json
{
  "token": "eyJhbGciOiJSUzI1NiIsInR5cCI6IkpXVCJ9...",
  "username": "admin",
  "roles": ["ROLE_ADMIN"]
}
```

---

## 2. Flujo OAuth2 / OpenID Connect

### GET `/.well-known/openid-configuration`
**Response Body (200 OK):**
```json
{
  "issuer": "http://auth-server:9000",
  "authorization_endpoint": "http://localhost:9000/oauth2/authorize",
  "token_endpoint": "http://localhost:9000/oauth2/token",
  "jwks_uri": "http://localhost:9000/oauth2/jwks",
  "response_types_supported": ["code"]
}
```

### GET `/oauth2/jwks`
Exhibe las claves públicas para que el `api-service` valide los tokens.

**Response Body (200 OK):**
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