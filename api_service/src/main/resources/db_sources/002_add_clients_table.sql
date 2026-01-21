-- ============================================================
-- Script de migración: Agregar tabla CLIENTS y vincularla
-- ============================================================

-- 1. Crear tabla CLIENTS
CREATE TABLE IF NOT EXISTS clients (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    first_name VARCHAR(50) NOT NULL,
    last_name VARCHAR(50) NOT NULL,
    email VARCHAR(50) UNIQUE NOT NULL,
    phone_number VARCHAR(15),
    address VARCHAR(100),
    oauth2_id VARCHAR(255) UNIQUE NOT NULL,
    oauth2_provider VARCHAR(50),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    INDEX idx_oauth2_id (oauth2_id),
    INDEX idx_email (email)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- 2. Modificar tabla ORDERS_CLIENTS para agregar FK a CLIENTS
ALTER TABLE orders_clients 
ADD COLUMN id_client BIGINT NOT NULL AFTER id;

-- 3. Crear índice para la FK
ALTER TABLE orders_clients 
ADD FOREIGN KEY (id_client) REFERENCES clients(id);

-- 4. Hacer que id_employee sea opcional (nullable)
-- Nota: Esto es opcional, solo si quieres permitir pedidos sin empleado asignado
ALTER TABLE orders_clients 
MODIFY COLUMN id_employee BIGINT NULL;

-- ============================================================
-- Datos de ejemplo (opcional)
-- ============================================================
INSERT INTO clients (first_name, last_name, email, phone_number, oauth2_id, oauth2_provider)
VALUES 
('Juan', 'Pérez', 'juan@example.com', '123456789', 'oauth2_user_1', 'keycloak'),
('María', 'García', 'maria@example.com', '987654321', 'oauth2_user_2', 'keycloak');

-- ============================================================
-- Para restaurar datos existentes (si es necesario):
-- ============================================================
-- Si ya tienes datos en orders_clients, debes crear clientes asociados:
INSERT INTO clients (first_name, last_name, email, oauth2_id, oauth2_provider)
SELECT 
     e.first_name, 
     e.last_name, 
     e.email, 
    CONCAT('migration_', e.id), 
     'keycloak'
 FROM employees e
 WHERE NOT EXISTS (SELECT 1 FROM clients c WHERE c.email = e.email);
