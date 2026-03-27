# � Endpoint Quick Reference

Resumen rápido de todos los endpoints del sistema. Para detalles completos, modelos y ejemplos, ver [AUTH_API.md](AUTH_API.md) y [BUSINESS_API.md](BUSINESS_API.md).

**Leyenda de Auth:**
- 🌐 = Público (sin autenticación)
- 🔒 = Requiere JWT (cualquier rol autenticado)
- 👤 = Scope específico requerido

---

## Auth Server (Puerto 9000)

| Método | Endpoint | Auth | Descripción |
| :--- | :--- | :--- | :--- |
| `POST` | `/api/auth/login` | 🌐 | Login directo → JWT. |
| `GET` | `/.well-known/openid-configuration` | 🌐 | Discovery OIDC. |
| `GET` | `/oauth2/jwks` | 🌐 | Claves públicas JWK. |
| `GET` | `/oauth2/authorize` | 🌐 | Iniciar flujo OAuth2. |
| `POST` | `/oauth2/token` | 🌐 | Intercambiar código → token. |
| `GET` | `/actuator/health` | 🌐 | Health check. |

---

## API Service (Puerto 8080) — Autenticación

| Método | Endpoint | Auth | Descripción |
| :--- | :--- | :--- | :--- |
| `GET` | `/authorized` | 🌐 | Callback OAuth2. |
| `POST` | `/api/auth/token` | 🌐 | Intercambiar código por token. |
| `GET` | `/api/users/me` | 🔒 | Perfil del usuario autenticado. |

---

## Productos (`/api/products`)

| Método | Endpoint | Auth | Descripción |
| :--- | :--- | :--- | :--- |
| `GET` | `/api/products` | 🌐 | Listar todos. |
| `GET` | `/api/products/{id}` | 🌐 | Detalle por ID. |
| `GET` | `/api/products/category/{categoryId}` | 🌐 | Por categoría. |
| `GET` | `/api/products/active` | 🌐 | Solo activos. |
| `GET` | `/api/products/inactive` | 🌐 | Solo inactivos. |
| `GET` | `/api/products/price-range?minPrice=&maxPrice=` | 🌐 | Rango de precio. |
| `GET` | `/api/products/search?name=` | 🌐 | Buscar por nombre. |
| `GET` | `/api/products/sorted/asc` | 🌐 | Precio ascendente. |
| `GET` | `/api/products/sorted/desc` | 🌐 | Precio descendente. |
| `POST` | `/api/products` | 👤 admin | Crear producto. |
| `PUT` | `/api/products/{id}` | 👤 admin | Actualizar producto. |
| `PATCH` | `/api/products/{id}/activate` | 👤 admin | Activar. |
| `PATCH` | `/api/products/{id}/deactivate` | 👤 admin | Desactivar. |
| `DELETE` | `/api/products/{id}` | 👤 admin | Eliminar. |

---

## Categorías (`/api/categories`)

| Método | Endpoint | Auth | Descripción |
| :--- | :--- | :--- | :--- |
| `GET` | `/api/categories` | 🌐 | Listar todas. |
| `GET` | `/api/categories/active` | 🌐 | Solo activas. |
| `GET` | `/api/categories/{id}` | 🌐 | Detalle por ID. |
| `GET` | `/api/categories/search?query=` | 🌐 | Buscar por nombre. |
| `POST` | `/api/categories` | 👤 admin | Crear categoría. |
| `PUT` | `/api/categories/{id}` | 👤 admin | Actualizar. |
| `PATCH` | `/api/categories/{id}/activate` | 👤 admin | Activar. |
| `PATCH` | `/api/categories/{id}/deactivate` | 👤 admin | Desactivar. |
| `DELETE` | `/api/categories/{id}` | 👤 admin | Eliminar. |

---

## Pedidos de Cliente (`/api/orders/clients`)

| Método | Endpoint | Auth | Descripción |
| :--- | :--- | :--- | :--- |
| `GET` | `/api/orders/clients` | 👤 client/employee/admin | Mis pedidos (todos si admin/employee). |
| `GET` | `/api/orders/clients/admin/all` | 👤 admin | Todos (admin only). |
| `GET` | `/api/orders/clients/{id}` | 👤 client/employee/admin | Por ID (verificación propiedad). |
| `GET` | `/api/orders/clients/state/{state}` | 👤 client/employee/admin | Por estado. |
| `GET` | `/api/orders/clients/service-type/{type}` | 👤 client/employee/admin | Por tipo de servicio. |
| `GET` | `/api/orders/clients/active` | 👤 client/employee/admin | Pedidos activos. |
| `GET` | `/api/orders/clients/date-range?startDate=&endDate=` | 👤 client/employee/admin | Por rango de fechas. |
| `POST` | `/api/orders/clients` | 👤 client/admin | Crear pedido. |
| `PUT` | `/api/orders/clients/{id}` | 👤 admin | Actualizar pedido. |
| `PATCH` | `/api/orders/clients/{id}/state?newState=` | 🔒 | Cambiar estado. |
| `DELETE` | `/api/orders/clients/{id}` | 👤 admin | Eliminar. |

---

## Pedidos a Proveedor (`/api/orders/providers`)

| Método | Endpoint | Auth | Descripción |
| :--- | :--- | :--- | :--- |
| `GET` | `/api/orders/providers` | 👤 provider/employee/admin | Mis órdenes (todas si admin/employee). |
| `GET` | `/api/orders/providers/{id}` | 👤 provider/employee/admin | Por ID. |
| `GET` | `/api/orders/providers/date-range?startDate=&endDate=` | 👤 provider/employee/admin | Por rango de fechas. |
| `GET` | `/api/orders/providers/month/{month}/year/{year}` | 👤 provider/employee/admin | Por mes y año. |
| `GET` | `/api/orders/providers/total-cost` | 👤 provider/employee/admin | Coste total. |
| `GET` | `/api/orders/providers/total-cost/month/{month}` | 👤 provider/employee/admin | Coste total por mes. |
| `POST` | `/api/orders/providers` | 👤 employee/admin | Crear orden. |
| `PUT` | `/api/orders/providers/{id}` | 👤 admin | Actualizar. |
| `DELETE` | `/api/orders/providers/{id}` | 👤 admin | Eliminar. |

---

## Detalles Pedido Cliente (`/api/details/orders/clients`)

| Método | Endpoint | Auth | Descripción |
| :--- | :--- | :--- | :--- |
| `GET` | `/api/details/orders/clients` | 🔒 | Mis detalles (todos si admin/employee). |
| `GET` | `/api/details/orders/clients/admin/all` | 🔒 | Todos los detalles. |
| `GET` | `/api/details/orders/clients/{id}` | 🔒 | Por ID. |
| `POST` | `/api/details/orders/clients` | 🔒 | Crear detalle. |
| `PUT` | `/api/details/orders/clients/{id}` | 🔒 | Actualizar. |
| `DELETE` | `/api/details/orders/clients/{id}` | 🔒 | Eliminar. |
| `GET` | `.../statistics/top5-revenue` | 🔒 | Top 5 productos por ingresos. |
| `GET` | `.../statistics/top5-quantity` | 🔒 | Top 5 productos por cantidad. |
| `GET` | `.../statistics/by-service-type` | 🔒 | Ventas por tipo de servicio. |
| `GET` | `.../statistics/products-without-sales` | 🔒 | Productos sin ventas. |

---

## Detalles Pedido Proveedor (`/api/details/orders/providers`)

| Método | Endpoint | Auth | Descripción |
| :--- | :--- | :--- | :--- |
| `GET` | `/api/details/orders/providers` | 🔒 | Mis detalles (todos si admin/employee). |
| `GET` | `/api/details/orders/providers/admin/all` | 🔒 | Todos los detalles. |
| `GET` | `/api/details/orders/providers/{id}` | 🔒 | Por ID. |
| `POST` | `/api/details/orders/providers` | 🔒 | Crear detalle. |
| `PUT` | `/api/details/orders/providers/{id}` | 🔒 | Actualizar. |
| `DELETE` | `/api/details/orders/providers/{id}` | 🔒 | Eliminar. |
| `GET` | `.../statistics/products-purchased` | 👤 employee/admin | Total productos comprados. |
| `GET` | `.../statistics/products-purchased/month/{month}` | 👤 employee/admin | Compras por mes. |

---

## Clientes (`/api/clients`)

| Método | Endpoint | Auth | Descripción |
| :--- | :--- | :--- | :--- |
| `GET` | `/api/clients` | 👤 client/admin | Mi perfil (todos si admin). |
| `GET` | `/api/clients/admin/all` | 👤 admin | Todos los clientes. |
| `GET` | `/api/clients/{id}` | 👤 client/admin | Por ID (verificación propiedad). |
| `GET` | `/api/clients/email/{email}` | 👤 client/admin | Por email. |
| `PUT` | `/api/clients/{id}` | 👤 admin | Actualizar. |
| `DELETE` | `/api/clients/{id}` | 👤 admin | Eliminar. |

---

## Empleados (`/api/employees`)

| Método | Endpoint | Auth | Descripción |
| :--- | :--- | :--- | :--- |
| `GET` | `/api/employees` | 👤 employee/admin | Mi perfil (todos si admin). |
| `GET` | `/api/employees/admin/all` | 👤 employee/admin | Todos los empleados. |
| `GET` | `/api/employees/{id}` | 👤 employee/admin | Por ID. |
| `GET` | `/api/employees/email/{email}` | 👤 employee/admin | Por email. |
| `GET` | `/api/employees/search?fullName=` | 👤 employee/admin | Buscar por nombre. |
| `GET` | `/api/employees/salary-range?minSalary=&maxSalary=` | 👤 employee/admin | Rango salarial. |
| `POST` | `/api/employees` | 👤 employee/admin | Crear. |
| `PUT` | `/api/employees/{id}` | 👤 employee/admin | Actualizar. |
| `DELETE` | `/api/employees/{id}` | 👤 employee/admin | Eliminar. |

---

## Proveedores (`/api/providers`)

| Método | Endpoint | Auth | Descripción |
| :--- | :--- | :--- | :--- |
| `GET` | `/api/providers` | 👤 admin | Lista (solo admin). |
| `GET` | `/api/providers/admin/all` | 👤 admin | Todos. |
| `GET` | `/api/providers/{id}` | 👤 admin | Por ID. |
| `GET` | `/api/providers/email/{email}` | 👤 admin | Por email. |
| `GET` | `/api/providers/search?name=` | 👤 admin | Buscar por nombre. |
| `POST` | `/api/providers` | 👤 admin | Crear. |
| `PUT` | `/api/providers/{id}` | 👤 admin | Actualizar. |
| `DELETE` | `/api/providers/{id}` | 👤 admin | Eliminar. |

---

## Inventario (`/api/inventory`)

| Método | Endpoint | Auth | Descripción |
| :--- | :--- | :--- | :--- |
| `GET` | `/api/inventory` | 👤 employee/admin | Todo el inventario. |
| `GET` | `/api/inventory/{id}` | 👤 employee/admin | Por ID. |
| `GET` | `/api/inventory/product/{productId}` | 👤 employee/admin | Por producto. |
| `GET` | `/api/inventory/low-stock` | 👤 employee/admin | Stock bajo. |
| `POST` | `/api/inventory` | 👤 employee/admin | Crear registro. |
| `PUT` | `/api/inventory/{id}` | 👤 employee/admin | Actualizar. |
| `PATCH` | `/api/inventory/product/{productId}/add-stock?quantity=` | 👤 employee/admin | Añadir stock. |
| `PATCH` | `/api/inventory/product/{productId}/reduce-stock?quantity=` | 👤 employee/admin | Reducir stock. |
| `DELETE` | `/api/inventory/{id}` | 👤 employee/admin | Eliminar. |

---

## Facturas de Cliente (`/api/invoices/clients`)

| Método | Endpoint | Auth | Descripción |
| :--- | :--- | :--- | :--- |
| `GET` | `/api/invoices/clients` | 👤 client/employee/admin | Mis facturas (todas si admin/employee). |
| `GET` | `/api/invoices/clients/admin/all` | 👤 client/employee/admin | Todas. |
| `GET` | `/api/invoices/clients/{id}` | 👤 client/employee/admin | Por ID. |
| `GET` | `/api/invoices/clients/pending` | 👤 employee/admin | Pendientes de pago. |
| `GET` | `/api/invoices/clients/total-income` | 👤 employee/admin | Ingresos totales. |
| `GET` | `/api/invoices/clients/total-income/month/{month}` | 👤 employee/admin | Ingresos por mes. |
| `POST` | `/api/invoices/clients` | 👤 admin | Crear factura. |
| `PUT` | `/api/invoices/clients/{id}` | 👤 admin | Actualizar. |
| `PATCH` | `/api/invoices/clients/{id}/mark-paid?paymentDate=` | 👤 admin | Marcar como pagada. |
| `DELETE` | `/api/invoices/clients/{id}` | 👤 admin | Eliminar. |

---

## Facturas de Proveedor (`/api/invoices/providers`)

| Método | Endpoint | Auth | Descripción |
| :--- | :--- | :--- | :--- |
| `GET` | `/api/invoices/providers` | 👤 provider/employee/admin | Mis facturas (todas si admin/employee). |
| `GET` | `/api/invoices/providers/admin/all` | 👤 provider/employee/admin | Todas. |
| `GET` | `/api/invoices/providers/{id}` | 👤 provider/employee/admin | Por ID. |
| `GET` | `/api/invoices/providers/pending` | 👤 employee/admin | Pendientes. |
| `POST` | `/api/invoices/providers` | 👤 admin | Crear factura. |
| `PUT` | `/api/invoices/providers/{id}` | 👤 admin | Actualizar. |
| `PATCH` | `/api/invoices/providers/{id}/mark-paid?paymentDate=` | 👤 admin | Marcar como pagada. |
| `DELETE` | `/api/invoices/providers/{id}` | 👤 admin | Eliminar. |

---

## Resumen de Permisos por Rol

### 👤 Cliente (`SCOPE_user:client`)
```
GET    /api/orders/clients              → Solo sus pedidos
GET    /api/orders/clients/{id}         → Solo si es suyo
POST   /api/orders/clients              → Crear pedido propio
GET    /api/clients                     → Solo su perfil
GET    /api/invoices/clients            → Solo sus facturas
GET    /api/users/me                    → Su perfil
```

### 👷 Empleado (`SCOPE_user:employee`)
```
GET    /api/orders/clients/**           → Todos los pedidos de cliente
GET    /api/orders/providers/**         → Todas las órdenes de proveedor
POST   /api/orders/providers            → Crear órdenes a proveedor
PATCH  /api/orders/clients/{id}/state   → Cambiar estado de pedido
GET    /api/inventory/**                → Inventario completo
GET    /api/employees/**                → Gestión de empleados
GET    /api/invoices/**                 → Todas las facturas
```

### 📦 Proveedor (`SCOPE_user:provider`)
```
GET    /api/orders/providers            → Solo sus órdenes
GET    /api/orders/providers/{id}       → Solo si es suya
GET    /api/invoices/providers          → Solo sus facturas
```

### 🔑 Admin (`SCOPE_admin`)
```
/* Acceso total a todos los recursos (CRUD completo) */
```