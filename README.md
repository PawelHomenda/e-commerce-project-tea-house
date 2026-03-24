# 🫖 Casa del Té — Kouchiku Bayashi
**E-Commerce Teahouse · Arquitectura Decoplada** *Java 17 · Spring Boot 3.5 · Angular 18 · MySQL 8 · Docker*

## 1. Descripción del Proyecto
Casa del Té es una plataforma de comercio electrónico diseñada para una tetería artesanal. El sistema utiliza una arquitectura de microservicios dividida en tres módulos independientes para garantizar la seguridad y escalabilidad, implementando flujos de **OAuth2** y **JWT (RSA 2048)**.

### Módulos Principales:
* **auth-server (9000):** Servidor de autorización OAuth2 y emisión de tokens JWT.
* **api-service (8080):** API REST de negocio que gestiona productos, pedidos e inventario.
* **frontend (4200):** Interfaz de usuario SPA desarrollada en Angular.

---

## 2. 🚀 Despliegue Rápido con Docker

Esta es la forma más sencilla de ejecutar el ecosistema completo sin necesidad de configurar bases de datos o entornos Java locales.

### Requisitos previos:
* Docker y Docker Compose instalados.
* Archivo `.env` configurado en la raíz (ver sección de configuración).

### Pasos para el arranque:

1.  **Configurar variables de entorno:**
    Crea un archivo `.env` basado en el `.env.example` proporcionado:
    ```bash
    cp .env.example .env
    ```

2.  **Levantar la infraestructura:**
    Ejecuta el siguiente comando en la raíz del proyecto:
    ```bash
    docker-compose up --build
    ```

3.  **Acceso a los servicios:**
    * **Frontend:** [http://localhost:4200](http://localhost:4200)
    * **API de Negocio:** [http://localhost:8080/api](http://localhost:8080/api)
    * **Auth Server:** [http://localhost:9000](http://localhost:9000)

---

## 3. 🛠️ Stack Tecnológico

| Capa | Tecnología | Versión |
| :--- | :--- | :--- |
| **Backend - Auth** | Spring Boot + OAuth2 Auth Server | 3.5.7 |
| **Backend - API** | Spring Boot + Spring Security + JPA | 3.5.8 |
| **Frontend** | Angular + TypeScript | 18.x |
| **Base de Datos** | MySQL | 8.x |
| **Seguridad** | JWT + OAuth2 Authorization Code | - |
| **Contenedores** | Docker & Docker Compose | 3.8 |

---

## 4. 🔑 Seguridad y Endpoints

### Auth Server (Puerto 9000)
Gestiona la identidad y los permisos. Emite tokens firmados con RSA.
* `POST /api/auth/login`: Login directo para clientes REST.
* `GET /oauth2/jwks`: Exposición de claves públicas para validación de tokens.

### API Service (Puerto 8080)
Requiere un Bearer Token válido para la mayoría de operaciones.
* `GET /api/products`: Público.
* `POST /api/products`: Protegido (Rol: ADMIN/EMPLOYEE).
* `GET /api/inventory`: Protegido (Rol: ADMIN/EMPLOYEE).

---

## 5. 📂 Estructura del Repositorio
```text
e-commerce-project-tea-house/
├── auth_server/          # Lógica de seguridad y usuarios
├── api_service/          # Lógica de negocio y productos
├── browser-interface/    # Código fuente Angular
├── docker-compose.yml    # Orquestación de contenedores
└── .env.example          # Plantilla de configuración
```

## 6. 👥 Usuarios de Prueba (Entorno Dev)


|Usuario|Contraseña|Rol|
|:-:|---|---|
|admin|1234|ADMIN|
|employee1|1234|EMPLOYEE|
|client1|1234|EMPLOYEE|

