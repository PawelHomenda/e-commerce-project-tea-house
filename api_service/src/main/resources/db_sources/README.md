# Casa de Té E-Commerce - Base de Datos

## Estructura de Inicialización Automática

Spring Boot ejecuta automáticamente dos archivos SQL en orden:

1. **schema.sql** - Crea la estructura de tablas (ejecutado primero)
2. **data.sql** - Inserta los datos de prueba (ejecutado después)

### Archivos de Configuración

- **application.yaml**: Contiene `spring.jpa.hibernate.ddl-auto: create` que ordena a Hibernate:
  - Eliminar todas las tablas (si existen)
  - Crear nuevas tablas basadas en las entidades JPA
  - Ejecutar schema.sql
  - Ejecutar data.sql

### Proceso de Inicialización

```
Inicio de Spring Boot
    ↓
Hibernate crea tablas base (mediante @Entity)
    ↓
Ejecuta schema.sql (mejoras adicionales en el esquema)
    ↓
Ejecuta data.sql (inserta datos de prueba)
    ↓
Aplicación lista en puerto 8080
```

**Tiempo total de inicio**: ~18-20 segundos (incluye inicialización DB)

## Archivos de Base de Datos

### 1. schema.sql (src/main/resources/)
- **Propósito**: Define estructura base para tablas que necesitan ajustes adicionales
- **Auto-ejecutado**: SÍ, por Spring Boot
- **Contenido**: Constraints adicionales, índices, vistas
- **Nota**: Hibernate ya crea las tablas base; este archivo solo añade mejoras

### 2. data.sql (src/main/resources/)
- **Propósito**: Inserta datos de prueba automáticamente
- **Auto-ejecutado**: SÍ, por Spring Boot
- **Contiene**: 73 registros distribuidos en 11 tablas
- **Ejecución**: Después de schema.sql
- **Uso**: Desarrollo y testing

### 3. casa_te_datos.sql (src/main/resources/db_sources/)
- **Propósito**: Referencia/documentación para ejecución manual
- **Auto-ejecutado**: NO (solo referencia)
- **Uso**: `mysql -u root -p casa_te < casa_te_datos.sql`
- **Ventaja**: Puede ejecutarse múltiples veces (usa INSERT IGNORE)

### 4. casa_te.sql (src/main/resources/db_sources/)
- **Propósito**: Script completo histórico de creación de BD
- **Auto-ejecutado**: NO (solo referencia)
- **Contenido**: Esquema completo (tablas + datos)

## Datos de Prueba

### Resumen de Registros Iniciales

| Entidad | Cantidad | OAuth2 | Notas |
|---------|----------|--------|-------|
| Empleados | 5 | Sí (keycloak) | Credenciales: user, tanaka, employee4-6 |
| Clientes | 10 | Sí (keycloak) | Credenciales: client1-10 |
| Proveedores | 5 | Sí (keycloak) | Credenciales: provider1-5 |
| Productos | 23 | No | 10 bebidas, 13 postres |
| Inventario | 23 | No | 1 por cada producto |
| Pedidos Clientes | 10 | No | Estados: DELIVERED, PREPARING, PENDENT, CANCELED |
| Detalles Ped. Clientes | 29 | No | Líneas de productos por pedido |
| Facturas Clientes | 8 | No | Estados de pago: PAID, NULL (pendiente) |
| Pedidos Proveedores | 7 | No | Reabastecimiento |
| Detalles Ped. Proveedores | 30 | No | Líneas de reabastecimiento |
| Facturas Proveedores | 7 | No | Estados: PAID, PENDENT |
| **TOTAL** | **157** | | |

### Estructura de Datos por Sección

#### Sección 1: EMPLEADOS (5 registros)
- Juan Pérez García (oauth2_id: **user**)
- María López Martínez (oauth2_id: **tanaka**)
- Carlos García Rodríguez (oauth2_id: **employee4**)
- Ana Martínez Sánchez (oauth2_id: **employee5**)
- Luis Fernández Díaz (oauth2_id: **employee6**)

#### Sección 2: PROVEEDORES (5 registros)
- TéDelMundo S.L. (oauth2_id: **provider1**)
- Dulces Artesanales García (oauth2_id: **provider2**)
- Distribuciones TeaTime (oauth2_id: **provider3**)
- Infusiones Naturales Del Sur (oauth2_id: **provider4**)
- Pastelería Premium Imports (oauth2_id: **provider5**)

#### Sección 3: CLIENTES (10 registros)
- Roberto Sánchez López (oauth2_id: **client1**)
- Elena García Fernández (oauth2_id: **client2**)
- ... (até **client10**)

#### Sección 4: PRODUCTOS (23 registros)
**Bebidas (10):**
- Té Verde Sencha, Té Negro Earl Grey, Té Oolong Premium, Té Blanco Silver Needle
- Té Rooibos, Té Chai Masala, Té Matcha Latte, Té Helado de Melocotón
- Infusión de Menta, Infusión de Manzanilla

**Postres (13):**
- Pastel de Chocolate, Cheesecake de Fresa, Cheesecake de Matcha, Tarta de Zanahoria
- Brownie con Nueces, Galletas de Mantequilla, Scones con Mermelada, Macarons
- Tarta de Limón, Croissant de Almendra, Dorayaki, Mochi, Tiramisú

#### Sección 5-11: Órdenes, Detalles, Facturas
- Pedidos completos, en preparación, pendientes y cancelados
- Detalles con cantidades y precios unitarios
- Facturas con estados de pago
- Pedidos a proveedores con reabastecimiento

## Configuración de OAuth2

Todos los usuarios de prueba están configurados en **SecurityConfig.java**:

```java
.userDetailsService(userDetailsService())
  .withUser("user").password("asas1234").roles("ADMIN")
  .and()
  .withUser("tanaka").password("asas1234").roles("EMPLOYEE")
  .and()
  .withUser("employee4-6").password("asas1234").roles("EMPLOYEE")
  .and()
  .withUser("client1-10").password("asas1234").roles("USER")
  .and()
  .withUser("provider1-5").password("asas1234").roles("PROVIDER")
```

**Credencial única para todos**: `asas1234`

## Cómo Ejecutar Manualmente

Si necesitas reiniciar la base de datos sin reiniciar la aplicación:

```bash
# Recrear estructura y datos
mysql -u root -p casa_te < src/main/resources/db_sources/casa_te_datos.sql

# O solo verificar los datos insertados
mysql -u root -p casa_te -e "
  SELECT COUNT(*) as 'Total Empleados' FROM employees;
  SELECT COUNT(*) as 'Total Productos' FROM products;
  SELECT COUNT(*) as 'Total Órdenes' FROM orders_clients;
"
```

## Ciclo de Vida de Desarrollo

### Desarrollo Normal
1. Inicia Spring Boot
2. Hibernate crea tablas limpias
3. data.sql carga datos de prueba
4. Desarrolla/testea
5. Detiene aplicación (datos se pierden al reiniciar)

### Persistencia de Datos
- Cambiar en **application.yaml**: `ddl-auto: validate`
- Cambiar en **application.yaml**: `ddl-auto: none`
- Los datos se conservan entre reinicios

### Reset Completo
```bash
# En terminal MySQL
DROP DATABASE casa_te;
CREATE DATABASE casa_te;

# O cambiar en application.yaml: ddl-auto: create-drop
# (recrear base de datos vacía en cada inicio)
```

## Tabla de Referencia: Columnas por Entidad

### Employees
- id, first_name, last_name, salary, phone_number, email, oauth2_id, oauth2_provider

### Products
- id, name, description, category, price, measure_unit, active

### Orders_Clients
- id, id_client, id_employee, order_date, order_state, service_type

### Invoices_Clients
- id, id_order_client, invoice_number, invoice_date, total, payment_method, payment_date

### Inventario
- id, id_product, current_quantity, minimum_quantity

## Recursos Adicionales

- [Spring Boot Database Initialization](https://spring.io/blog/2019/02/27/jdbc-databases-and-initialization)
- [Hibernate DDL Configuration](https://hibernate.org/orm/documentation/5.4/userguide/html_single/)
- [Application Properties Reference](https://docs.spring.io/spring-boot/docs/current/reference/html/application-properties.html)

---

**Última actualización**: 2026-01-21
**Estado**: Operacional - Inicialización automática funcionando correctamente
