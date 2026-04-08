# Implementación de Control de Acceso por Rol

## Resumen de cambios

Se ha implementado un sistema de control de acceso basado en 3 tipos de usuarios + Admin:

### 1. **CLIENTE** (`SCOPE_user:client`)
- ✅ Ve solo sus propios pedidos
- ✅ Descarga sus facturas
- ✅ No puede ver datos de otros clientes
- ✅ Crear pedidos asociados a su cuenta

### 2. **EMPLEADO** (`SCOPE_user:employee`)
- ✅ Ve todos los pedidos de clientes y proveedores
- ✅ Gestiona las órdenes (actualiza estado)
- ✅ Acceso al inventario
- ✅ Genera facturas

### 3. **PROVEEDOR** (`SCOPE_user:provider`)
- ✅ Ve solo sus órdenes y facturas
- ✅ Recibe pedidos de proveedores
- ✅ Consulta sus facturas

### 4. **ADMIN** (`SCOPE_admin`)
- ✅ Acceso total a todas las funcionalidades

---

## Archivos modificados

### 1. **Nuevas Entidades**
- `Client.java` - Nueva tabla de clientes vinculada con OAuth2

### 2. **Entidades modificadas**
- `OrderClient.java` - Agregó FK a `Client` y hizo opcional `Employee`

### 3. **Servicios**
- `ClientService.java` - Nuevo servicio para gestionar clientes
- `OrderClientService.java` - Agregó método `findByClientOauth2Id()`

### 4. **Repositorios**
- `ClientRepository.java` - Nuevo repositorio
- `OrderClientRepository.java` - Agregó query por oauth2Id

### 5. **Controladores**
- `OrderClientController.java` - Implementó seguridad con `@AuthenticationPrincipal Jwt`

### 6. **Configuración**
- `SecurityConfig.java` - Definió roles y permisos por endpoint

### 7. **Base de datos**
- `002_add_clients_table.sql` - Script de migración

---

## Cómo usar en el Auth Server (Keycloak)

### Crear roles en Keycloak

1. **En la consola de Keycloak**, ve a tu realm
2. **Crea los roles:**
   - `user:client`
   - `user:employee`
   - `user:provider`
   - `admin`

### Asignar permisos en el token JWT

**En los Mappers de Keycloak** (o en el Auth Server):

```json
{
  "scope": ["user:client"],  // O "user:employee", "user:provider", "admin"
  "sub": "user-oauth2-id",
  "email": "cliente@example.com"
}
```

---

## Flujo de implementación

### Paso 1: Ejecutar la migración SQL
```sql
-- Ejecutar el script 002_add_clients_table.sql
-- Esto crea la tabla clients y modifica orders_clients
```

### Paso 2: Cambiar tu Auth Server
Asegúrate de que cuando un usuario se autentica, el token incluya:
```json
{
  "sub": "unique-oauth2-id",
  "scope": "user:client",  // Depende del tipo de usuario
  "email": "user@example.com"
}
```

### Paso 3: Actualizar la forma de crear pedidos

**ANTES:** Necesitabas especificar un `Employee`
```json
{
  "employee": { "id": 1 },
  "orderDate": "2026-01-16"
}
```

**AHORA:** El cliente se vincula automáticamente
```java
@PostMapping
public ResponseEntity<OrderClient> createOrder(
        @Valid @RequestBody OrderClient orderClient,
        @AuthenticationPrincipal Jwt jwt) {
    
    // 1. Obtener el cliente desde OAuth2
    String oauth2Id = jwt.getClaimAsString("sub");
    Client client = clientService.findByOauth2Id(oauth2Id);
    
    // 2. Asignar el cliente
    orderClient.setClient(client);
    
    // 3. El employee es opcional (puede ser asignado después)
    // orderClient.setEmployee(employeeService.findById(...));
    
    return ResponseEntity.status(HttpStatus.CREATED)
        .body(orderClientService.save(orderClient));
}
```

### Paso 4: Primeros clientes

Hay dos opciones:

#### Opción A: Crear clientes manualmente
```sql
INSERT INTO clients (first_name, last_name, email, phone_number, oauth2_id, oauth2_provider)
VALUES ('Juan', 'Pérez', 'juan@example.com', '123456789', 'oauth2_user_1', 'keycloak');
```

#### Opción B: Auto-creación (Recomendado)
Modificar `OrderClientController.createOrder()`:
```java
@PostMapping
public ResponseEntity<OrderClient> createOrder(
        @Valid @RequestBody OrderClient orderClient,
        @AuthenticationPrincipal Jwt jwt) {
    
    String oauth2Id = jwt.getClaimAsString("sub");
    String email = jwt.getClaimAsString("email");
    String firstName = jwt.getClaimAsString("given_name");
    String lastName = jwt.getClaimAsString("family_name");
    
    // Auto-crear cliente si no existe
    Client client = clientService.getOrCreateClientFromOAuth2(
        oauth2Id, email, firstName, lastName, "keycloak"
    );
    
    orderClient.setClient(client);
    return ResponseEntity.status(HttpStatus.CREATED)
        .body(orderClientService.save(orderClient));
}
```

---

## Endpoints por rol

### Cliente
```
GET    /api/orders/clients               → Ver sus pedidos
GET    /api/orders/clients/{id}          → Ver un pedido (si es suyo)
GET    /api/orders/clients/active        → Ver pedidos activos
POST   /api/orders/clients               → Crear pedido
GET    /api/invoices/clients/{id}        → Ver sus facturas
```

### Empleado
```
GET    /api/orders/clients/admin/all     → Ver todos los pedidos
GET    /api/orders/providers/**          → Ver órdenes de proveedores
PUT    /api/orders/clients/{id}          → Actualizar pedido
PATCH  /api/orders/clients/{id}/state    → Cambiar estado
GET    /api/inventory/**                 → Ver inventario
```

### Proveedor
```
GET    /api/orders/providers/**          → Ver sus órdenes
GET    /api/invoices/providers/**        → Ver sus facturas
```

### Admin
```
/* Acceso total */
```

---

## Troubleshooting

### Error: "No tienes acceso a este pedido"
- El cliente intenta acceder a un pedido que no es suyo
- El token no incluye el `sub` correcto

### Error: "Cliente no encontrado"
- El `oauth2Id` del token no existe en la BD
- Solución: Registrar el cliente primero o usar auto-creación

### Token mal formado
- Verifica que Keycloak o tu Auth Server incluya:
  - `sub` (OAuth2 user ID)
  - `scope` (roles del usuario)
  - `email` (opcional pero recomendado)

---

## Próximos pasos

1. **Crear controladores para Provider y Employee** (similar a OrderClientController)
2. **Implementar auditoría**: Registrar qué usuario accedió a qué datos
3. **Rate limiting**: Limitar requests por usuario
4. **Filtros avanzados**: Por fecha, estado, etc.

