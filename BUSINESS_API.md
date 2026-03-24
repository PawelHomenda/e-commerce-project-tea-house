# ☕ Business API Reference (api-service)
**Base URL:** `http://localhost:8080/api`

## 1. Productos

### GET `/products`
Lista paginada de productos.

**Response Body (200 OK):**
```json
{
  "content": [
    {
      "id": 1,
      "name": "Té Verde Matcha",
      "price": 12.50,
      "category": "Té Verde",
      "stock": 45
    }
  ],
  "totalPages": 1,
  "totalElements": 1
}
```

### POST `/products` (Auth: ADMIN)
Crear un nuevo producto.

**Request Body:**
```json
{
  "name": "Té Negro Darjeeling",
  "description": "Té de alta montaña",
  "price": 15.00,
  "categoryId": 2,
  "stock": 20
}
```

**Response Body (201 Created):**
```json
{
  "id": 5,
  "name": "Té Negro Darjeeling",
  "createdAt": "2025-03-24T14:00:00Z"
}
```

---

## 2. Gestión de Clientes

### GET `/auth/me` (Auth: JWT)
Obtiene el perfil del usuario a partir del token Bearer.

**Response Body (200 OK):**
```json
{
  "username": "client1",
  "email": "client1@example.com",
  "roles": ["ROLE_CLIENT"],
  "lastLogin": "2025-03-24T10:30:00Z"
}
```

---

## 3. Inventario

### PUT `/inventory/{id}` (Auth: ADMIN/EMP)
Actualizar existencias.

**Request Body:**
```json
{
  "quantityChange": -5,
  "reason": "Venta directa en tienda"
}
```

**Response Body (200 OK):**
```json
{
  "productId": 1,
  "previousStock": 45,
  "newStock": 40,
  "updatedBy": "employee1"
}
```

## 4. Gestión de Errores (Ejemplo)

**Response Body (403 Forbidden):**
```json
{
  "timestamp": "2025-03-24T14:05:00Z",
  "status": 403,
  "error": "Forbidden",
  "message": "Access Denied: You do not have the required role (ROLE_ADMIN)",
  "path": "/api/products"
}
```
```

### ¿Qué aporta esto a tu perfil?
1.  **Claridad:** Elimina la adivinación. Cualquiera puede importar estos JSON en Postman y empezar a probar.
2.  **Profesionalismo:** Estás siguiendo el estándar de las grandes APIs (como la de Stripe o GitHub).
3.  **Habilidad técnica:** El ejemplo del error 403 demuestra que has configurado correctamente el manejo de excepciones en Spring Boot (`ControllerAdvice`).

¿Te gustaría que añadamos también el ejemplo de cómo se vería el **Header de Autorización** en las peticiones de la API de negocios?