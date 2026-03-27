# ☕ Business API Reference (api-service)

**Base URL:** `http://localhost:8080/api`
**Seguridad:** Bearer Token (JWT) requerido en rutas protegidas.
**Swagger UI:** `http://localhost:8080/swagger-ui/index.html`

---

## 1. Autenticación (API Service)

Estos endpoints son públicos y se usan para el flujo OAuth2 desde el frontend.

| Método | Endpoint | Auth | Descripción |
| :--- | :--- | :--- | :--- |
| `GET` | `/authorized` | Público | Callback OAuth2. Recibe `?code=` o `?token=`. |
| `POST` | `/api/auth/token` | Público | Intercambia código de autorización por token JWT. |

### POST `/api/auth/token`

**Request Body:**
```json
{
  "code": "AUTHORIZATION_CODE_FROM_AUTH_SERVER"
}
```

**Response (200 OK):**
```json
{
  "access_token": "eyJhbGciOiJSUzI1NiJ9...",
  "token_type": "Bearer",
  "expires_in": 3600,
  "scope": "openid profile email user:client"
}
```

---

## 2. Perfil de Usuario

### GET `/api/users/me` — Auth: JWT (cualquier rol)

Devuelve los datos del usuario autenticado según su rol. Los campos varían según el tipo de usuario.

**Response (200 OK) — Cliente:**
```json
{
  "username": "client1",
  "role": "CLIENT",
  "isActive": true,
  "firstName": "Juan",
  "lastName": "García",
  "email": "juan@example.com",
  "phone": "612345678",
  "address": "Calle Mayor 1",
  "id": 1
}
```

**Response (200 OK) — Empleado:**
```json
{
  "username": "employee1",
  "role": "EMPLOYEE",
  "isActive": true,
  "firstName": "Takeshi",
  "lastName": "Tanaka",
  "email": "tanaka@teahouse.com",
  "phone": "612345678",
  "id": 1
}
```

---

## 3. Productos

Lectura pública. Escritura solo para administradores.

| Método | Endpoint | Auth | Descripción |
| :--- | :--- | :--- | :--- |
| `GET` | `/api/products` | Público | Lista todos los productos. |
| `GET` | `/api/products/{id}` | Público | Detalle de un producto. |
| `GET` | `/api/products/category/{categoryId}` | Público | Filtrar por categoría. |
| `GET` | `/api/products/active` | Público | Solo productos activos. |
| `GET` | `/api/products/inactive` | Público | Solo productos inactivos. |
| `GET` | `/api/products/price-range?minPrice=&maxPrice=` | Público | Filtrar por rango de precio. |
| `GET` | `/api/products/search?name=` | Público | Buscar por nombre. |
| `GET` | `/api/products/sorted/asc` | Público | Ordenar por precio ascendente. |
| `GET` | `/api/products/sorted/desc` | Público | Ordenar por precio descendente. |
| `POST` | `/api/products` | SCOPE_admin | Crear producto. |
| `PUT` | `/api/products/{id}` | SCOPE_admin | Actualizar producto. |
| `PATCH` | `/api/products/{id}/activate` | SCOPE_admin | Activar producto. |
| `PATCH` | `/api/products/{id}/deactivate` | SCOPE_admin | Desactivar producto. |
| `DELETE` | `/api/products/{id}` | SCOPE_admin | Eliminar producto. |

### Modelo: Product
```json
{
  "id": 1,
  "name": "Té Verde Matcha",
  "description": "Té verde japonés en polvo de alta calidad",
  "price": 12.50,
  "measureUnit": "100g",
  "active": true,
  "category": {
    "id": 1,
    "name": "DRINK"
  },
  "inventory": {
    "id": 1,
    "currentQuantity": 45,
    "minimumQuantity": 10
  }
}
```

### POST `/api/products` — Crear Producto

**Request Body:**
```json
{
  "name": "Té Negro Darjeeling",
  "description": "Té de alta montaña con aroma floral",
  "price": 15.00,
  "measureUnit": "100g",
  "category": { "id": 2 }
}
```

**Response (201 Created):** El producto creado con `id` asignado.

---

## 4. Categorías

Lectura pública. Escritura solo para administradores.

| Método | Endpoint | Auth | Descripción |
| :--- | :--- | :--- | :--- |
| `GET` | `/api/categories` | Público | Lista todas las categorías. |
| `GET` | `/api/categories/active` | Público | Solo categorías activas. |
| `GET` | `/api/categories/{id}` | Público | Detalle de una categoría. |
| `GET` | `/api/categories/search?query=` | Público | Buscar categorías por nombre. |
| `POST` | `/api/categories` | SCOPE_admin | Crear categoría. |
| `PUT` | `/api/categories/{id}` | SCOPE_admin | Actualizar categoría. |
| `PATCH` | `/api/categories/{id}/activate` | SCOPE_admin | Activar categoría. |
| `PATCH` | `/api/categories/{id}/deactivate` | SCOPE_admin | Desactivar categoría. |
| `DELETE` | `/api/categories/{id}` | SCOPE_admin | Eliminar categoría. |

### Modelo: Category
```json
{
  "id": 1,
  "name": "DRINK",
  "description": "Bebidas de té y infusiones",
  "imageUrl": "https://example.com/drinks.jpg",
  "active": true,
  "createdAt": "2026-03-26T10:00:00",
  "updatedAt": "2026-03-26T10:00:00"
}
```

---

## 5. Pedidos de Cliente (OrderClient)

Acceso basado en rol: clientes ven solo sus pedidos; empleados y admin ven todos.

| Método | Endpoint | Auth | Descripción |
| :--- | :--- | :--- | :--- |
| `GET` | `/api/orders/clients` | client/employee/admin | Mis pedidos (o todos si admin/employee). |
| `GET` | `/api/orders/clients/admin/all` | SCOPE_admin | Todos los pedidos (admin only). |
| `GET` | `/api/orders/clients/{id}` | client/employee/admin | Detalle de un pedido (con verificación de propiedad). |
| `GET` | `/api/orders/clients/state/{state}` | client/employee/admin | Filtrar por estado (`PENDENT`, `PREPARING`, `DELIVERED`, `CANCELED`). |
| `GET` | `/api/orders/clients/service-type/{type}` | client/employee/admin | Filtrar por tipo de servicio (`TAKEAWAY`, `TABLE`, `DELIVERY`). |
| `GET` | `/api/orders/clients/active` | client/employee/admin | Pedidos activos (no DELIVERED/CANCELED). |
| `GET` | `/api/orders/clients/date-range?startDate=&endDate=` | client/employee/admin | Filtrar por rango de fechas (ISO: `2026-01-01`). |
| `POST` | `/api/orders/clients` | client/admin | Crear pedido. El cliente se vincula automáticamente vía JWT. |
| `PUT` | `/api/orders/clients/{id}` | SCOPE_admin | Actualizar pedido completo. |
| `PATCH` | `/api/orders/clients/{id}/state?newState=` | Authenticated | Cambiar estado del pedido. |
| `DELETE` | `/api/orders/clients/{id}` | SCOPE_admin | Eliminar pedido. |

### Modelo: OrderClient
```json
{
  "id": 1,
  "client": { "id": 1, "firstName": "Juan", "lastName": "García" },
  "employee": null,
  "orderDate": "2026-03-26",
  "orderState": "PENDENT",
  "serviceType": "DELIVERY",
  "discountPercentage": 0.0,
  "subtotal": 37.50,
  "total": 37.50,
  "detailOrderClients": [
    {
      "id": 1,
      "product": { "id": 1, "name": "Té Verde Matcha", "price": 12.50 },
      "quantity": 2,
      "unitPrice": 12.50,
      "discountPercentage": 0.0,
      "finalUnitPrice": 12.50,
      "subtotal": 25.00
    },
    {
      "id": 2,
      "product": { "id": 3, "name": "Mochi de Matcha", "price": 8.00 },
      "quantity": 1,
      "unitPrice": 8.00,
      "discountPercentage": 0.0,
      "finalUnitPrice": 8.00,
      "subtotal": 8.00
    }
  ],
  "invoiceClient": null
}
```

### POST `/api/orders/clients` — Crear Pedido

El campo `client` se asigna automáticamente desde el JWT (`sub` → `oauth2Id`).

**Request Body:**
```json
{
  "client": { "id": 1 },
  "orderDate": "2026-03-26",
  "orderState": "PENDENT",
  "serviceType": "DELIVERY",
  "detailOrderClients": [
    { "product": { "id": 1 }, "quantity": 2, "unitPrice": 12.50 },
    { "product": { "id": 3 }, "quantity": 1, "unitPrice": 8.00 }
  ]
}
```

**Response (201 Created):** El pedido completo con `id` asignado y cálculos de `subtotal`/`total`.

---

## 6. Pedidos a Proveedor (OrderProvider)

Para gestión de reposición de inventario.

| Método | Endpoint | Auth | Descripción |
| :--- | :--- | :--- | :--- |
| `GET` | `/api/orders/providers` | provider/employee/admin | Mis órdenes (o todas si admin/employee). |
| `GET` | `/api/orders/providers/{id}` | provider/employee/admin | Detalle de una orden. |
| `GET` | `/api/orders/providers/date-range?startDate=&endDate=` | provider/employee/admin | Filtrar por rango de fechas. |
| `GET` | `/api/orders/providers/month/{month}/year/{year}` | provider/employee/admin | Filtrar por mes y año. |
| `GET` | `/api/orders/providers/total-cost` | provider/employee/admin | Coste total de todas las órdenes. |
| `GET` | `/api/orders/providers/total-cost/month/{month}` | provider/employee/admin | Coste total por mes. |
| `POST` | `/api/orders/providers` | employee/admin | Crear orden a proveedor. |
| `PUT` | `/api/orders/providers/{id}` | SCOPE_admin | Actualizar orden. |
| `DELETE` | `/api/orders/providers/{id}` | SCOPE_admin | Eliminar orden. |

### Modelo: OrderProvider
```json
{
  "id": 1,
  "provider": { "id": 1, "name": "Tea Imports Co." },
  "employee": { "id": 1, "firstName": "Takeshi" },
  "orderDate": "2026-03-20",
  "total": 500.00,
  "observations": "Pedido urgente de matcha",
  "discountPercentage": 5.0,
  "detailOrderProviders": [
    { "id": 1, "product": { "id": 1 }, "quantity": 50, "unitPrice": 10.00 }
  ]
}
```

---

## 7. Detalles de Pedido de Cliente

Líneas individuales de cada pedido de cliente.

| Método | Endpoint | Auth | Descripción |
| :--- | :--- | :--- | :--- |
| `GET` | `/api/details/orders/clients` | Authenticated | Mis detalles (o todos si admin/employee). |
| `GET` | `/api/details/orders/clients/admin/all` | Authenticated | Todos los detalles. |
| `GET` | `/api/details/orders/clients/{id}` | Authenticated | Detalle por ID. |
| `POST` | `/api/details/orders/clients` | Authenticated | Crear línea de detalle. |
| `PUT` | `/api/details/orders/clients/{id}` | Authenticated | Actualizar detalle. |
| `DELETE` | `/api/details/orders/clients/{id}` | Authenticated | Eliminar detalle. |

### Estadísticas de Ventas

| Método | Endpoint | Auth | Descripción |
| :--- | :--- | :--- | :--- |
| `GET` | `/api/details/orders/clients/statistics/top5-revenue` | Authenticated | Top 5 productos por ingresos. |
| `GET` | `/api/details/orders/clients/statistics/top5-quantity` | Authenticated | Top 5 productos por cantidad vendida. |
| `GET` | `/api/details/orders/clients/statistics/by-service-type` | Authenticated | Cantidad de productos vendidos por tipo de servicio. |
| `GET` | `/api/details/orders/clients/statistics/products-without-sales` | Authenticated | Productos sin ventas. |

---

## 8. Detalles de Pedido a Proveedor

| Método | Endpoint | Auth | Descripción |
| :--- | :--- | :--- | :--- |
| `GET` | `/api/details/orders/providers` | Authenticated | Mis detalles (o todos si admin/employee). |
| `GET` | `/api/details/orders/providers/admin/all` | Authenticated | Todos los detalles. |
| `GET` | `/api/details/orders/providers/{id}` | Authenticated | Detalle por ID. |
| `POST` | `/api/details/orders/providers` | Authenticated | Crear detalle. |
| `PUT` | `/api/details/orders/providers/{id}` | Authenticated | Actualizar detalle. |
| `DELETE` | `/api/details/orders/providers/{id}` | Authenticated | Eliminar detalle. |

### Estadísticas de Compras

| Método | Endpoint | Auth | Descripción |
| :--- | :--- | :--- | :--- |
| `GET` | `/api/details/orders/providers/statistics/products-purchased` | employee/admin | Total de productos comprados a proveedores. |
| `GET` | `/api/details/orders/providers/statistics/products-purchased/month/{month}` | employee/admin | Total de productos comprados por mes. |

---

## 9. Clientes

| Método | Endpoint | Auth | Descripción |
| :--- | :--- | :--- | :--- |
| `GET` | `/api/clients` | client/admin | Mi perfil (cliente) o todos (admin). |
| `GET` | `/api/clients/admin/all` | SCOPE_admin | Lista de todos los clientes. |
| `GET` | `/api/clients/{id}` | client/admin | Cliente por ID (verificación de propiedad). |
| `GET` | `/api/clients/email/{email}` | client/admin | Cliente por email. |
| `PUT` | `/api/clients/{id}` | SCOPE_admin | Actualizar cliente. |
| `DELETE` | `/api/clients/{id}` | SCOPE_admin | Eliminar cliente. |

### Modelo: Client
```json
{
  "id": 1,
  "firstName": "Juan",
  "lastName": "García",
  "email": "juan@example.com",
  "phoneNumber": "612345678",
  "address": "Calle Mayor 1, Madrid",
  "oauth2Id": "client1",
  "oauth2Provider": "spring-auth-server"
}
```

---

## 10. Empleados

| Método | Endpoint | Auth | Descripción |
| :--- | :--- | :--- | :--- |
| `GET` | `/api/employees` | employee/admin | Mi perfil (empleado) o todos (admin). |
| `GET` | `/api/employees/admin/all` | employee/admin | Lista de todos los empleados. |
| `GET` | `/api/employees/{id}` | employee/admin | Empleado por ID. |
| `GET` | `/api/employees/email/{email}` | employee/admin | Empleado por email. |
| `GET` | `/api/employees/search?fullName=` | employee/admin | Buscar por nombre completo. |
| `GET` | `/api/employees/salary-range?minSalary=&maxSalary=` | employee/admin | Filtrar por rango salarial. |
| `POST` | `/api/employees` | employee/admin | Crear empleado. |
| `PUT` | `/api/employees/{id}` | employee/admin | Actualizar empleado. |
| `DELETE` | `/api/employees/{id}` | employee/admin | Eliminar empleado. |

### Modelo: Employee
```json
{
  "id": 1,
  "firstName": "Takeshi",
  "lastName": "Tanaka",
  "salary": 2200.00,
  "phoneNumber": "612345678",
  "email": "tanaka@teahouse.com",
  "oauth2Id": "employee1",
  "oauth2Provider": "spring-auth-server"
}
```

---

## 11. Proveedores

| Método | Endpoint | Auth | Descripción |
| :--- | :--- | :--- | :--- |
| `GET` | `/api/providers` | SCOPE_admin | Lista de proveedores (solo admin). |
| `GET` | `/api/providers/admin/all` | SCOPE_admin | Todos los proveedores. |
| `GET` | `/api/providers/{id}` | SCOPE_admin | Proveedor por ID. |
| `GET` | `/api/providers/email/{email}` | SCOPE_admin | Proveedor por email. |
| `GET` | `/api/providers/search?name=` | SCOPE_admin | Buscar por nombre. |
| `POST` | `/api/providers` | SCOPE_admin | Crear proveedor. |
| `PUT` | `/api/providers/{id}` | SCOPE_admin | Actualizar proveedor. |
| `DELETE` | `/api/providers/{id}` | SCOPE_admin | Eliminar proveedor. |

### Modelo: Provider
```json
{
  "id": 1,
  "name": "Tea Imports Co.",
  "contact": "John Smith",
  "phoneNumber": "9123456789",
  "email": "imports@teaco.com",
  "address": "123 Tea Street, China",
  "oauth2Id": "provider1",
  "oauth2Provider": "spring-auth-server"
}
```

---

## 12. Inventario

Control de stock de productos. Solo para empleados y administradores.

| Método | Endpoint | Auth | Descripción |
| :--- | :--- | :--- | :--- |
| `GET` | `/api/inventory` | employee/admin | Todo el inventario. |
| `GET` | `/api/inventory/{id}` | employee/admin | Inventario por ID. |
| `GET` | `/api/inventory/product/{productId}` | employee/admin | Inventario por producto. |
| `GET` | `/api/inventory/low-stock` | employee/admin | Productos con stock bajo. |
| `POST` | `/api/inventory` | employee/admin | Crear registro de inventario. |
| `PUT` | `/api/inventory/{id}` | employee/admin | Actualizar inventario. |
| `PATCH` | `/api/inventory/product/{productId}/add-stock?quantity=` | employee/admin | Añadir stock. |
| `PATCH` | `/api/inventory/product/{productId}/reduce-stock?quantity=` | employee/admin | Reducir stock. |
| `DELETE` | `/api/inventory/{id}` | employee/admin | Eliminar registro. |

### Modelo: Inventory
```json
{
  "id": 1,
  "product": { "id": 1, "name": "Té Verde Matcha" },
  "currentQuantity": 45,
  "minimumQuantity": 10,
  "lowStock": false
}
```

---

## 13. Facturas de Cliente

| Método | Endpoint | Auth | Descripción |
| :--- | :--- | :--- | :--- |
| `GET` | `/api/invoices/clients` | client/employee/admin | Mis facturas (o todas si admin/employee). |
| `GET` | `/api/invoices/clients/admin/all` | client/employee/admin | Todas las facturas. |
| `GET` | `/api/invoices/clients/{id}` | client/employee/admin | Factura por ID. |
| `GET` | `/api/invoices/clients/pending` | employee/admin | Facturas pendientes de pago. |
| `GET` | `/api/invoices/clients/total-income` | employee/admin | Ingresos totales. |
| `GET` | `/api/invoices/clients/total-income/month/{month}` | employee/admin | Ingresos por mes. |
| `POST` | `/api/invoices/clients` | SCOPE_admin | Crear factura. |
| `PUT` | `/api/invoices/clients/{id}` | SCOPE_admin | Actualizar factura. |
| `PATCH` | `/api/invoices/clients/{id}/mark-paid?paymentDate=` | SCOPE_admin | Marcar factura como pagada. |
| `DELETE` | `/api/invoices/clients/{id}` | SCOPE_admin | Eliminar factura. |

### Modelo: InvoiceClient
```json
{
  "id": 1,
  "orderClient": { "id": 1 },
  "invoiceNumber": "FC-001",
  "invoiceDate": "2026-03-26",
  "total": 37.50,
  "paymentMethod": "METALIC",
  "paymentDate": "2026-03-26"
}
```

**Enums:**
- `PaymentMethod`: `METALIC`, `CREDIT`

---

## 14. Facturas de Proveedor

| Método | Endpoint | Auth | Descripción |
| :--- | :--- | :--- | :--- |
| `GET` | `/api/invoices/providers` | provider/employee/admin | Mis facturas (o todas si admin/employee). |
| `GET` | `/api/invoices/providers/admin/all` | provider/employee/admin | Todas las facturas. |
| `GET` | `/api/invoices/providers/{id}` | provider/employee/admin | Factura por ID. |
| `GET` | `/api/invoices/providers/pending` | employee/admin | Facturas pendientes de pago. |
| `POST` | `/api/invoices/providers` | SCOPE_admin | Crear factura. |
| `PUT` | `/api/invoices/providers/{id}` | SCOPE_admin | Actualizar factura. |
| `PATCH` | `/api/invoices/providers/{id}/mark-paid?paymentDate=` | SCOPE_admin | Marcar factura como pagada. |
| `DELETE` | `/api/invoices/providers/{id}` | SCOPE_admin | Eliminar factura. |

### Modelo: InvoiceProvider
```json
{
  "id": 1,
  "orderProvider": { "id": 1 },
  "invoiceNumber": "FP-001",
  "invoiceDate": "2026-03-20",
  "total": 500.00,
  "paymentState": "PAID",
  "paymentDate": "2026-03-25"
}
```

**Enums:**
- `PaymentState`: `PAID`, `PENDENT`

---

## 15. Códigos de Estado HTTP

| Código | Significado |
| :--- | :--- |
| `200 OK` | Operación exitosa. |
| `201 Created` | Recurso creado (POST). |
| `204 No Content` | Recurso eliminado (DELETE). |
| `400 Bad Request` | Datos de entrada inválidos (validación fallida). |
| `401 Unauthorized` | Token ausente o inválido. |
| `403 Forbidden` | Token válido pero sin permisos de rol. |
| `404 Not Found` | El recurso solicitado no existe. |

### Ejemplo de Error (403)
```json
{
  "timestamp": "2026-03-26T14:05:00Z",
  "status": 403,
  "error": "Forbidden",
  "message": "Access Denied",
  "path": "/api/products"
}
```

### Ejemplo de Error (401)
```json
{
  "timestamp": "2026-03-26T14:05:00Z",
  "status": 401,
  "error": "Unauthorized",
  "message": "Token inválido o expirado",
  "path": "/api/orders/clients"
}
```