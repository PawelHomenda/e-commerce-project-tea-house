-- ============================================================
-- Script de migración: Agregar oauth2_id y oauth2_provider
-- a Provider y Employee
-- ============================================================

-- 1. Agregar columnas a PROVIDERS
ALTER TABLE providers 
ADD COLUMN oauth2_id VARCHAR(255) UNIQUE NOT NULL,
ADD COLUMN oauth2_provider VARCHAR(50);

-- 2. Agregar columnas a EMPLOYEES
ALTER TABLE employees 
ADD COLUMN oauth2_id VARCHAR(255) UNIQUE NOT NULL,
ADD COLUMN oauth2_provider VARCHAR(50);

-- 3. Crear índices para mejor performance
CREATE INDEX idx_providers_oauth2_id ON providers(oauth2_id);
CREATE INDEX idx_employees_oauth2_id ON employees(oauth2_id);

-- ============================================================
-- Datos de ejemplo (opcional)
-- ============================================================
-- INSERT INTO providers (name, contact, phone_number, email, address, oauth2_id, oauth2_provider)
-- VALUES 
-- ('Tea Imports', 'Carlos López', '1234567890', 'carlos@teaimports.com', 'Calle Principal 123', 'oauth2_provider_1', 'keycloak');

-- INSERT INTO employees (first_name, last_name, salary, phone_number, email, oauth2_id, oauth2_provider)
-- VALUES 
-- ('Pedro', 'García', 2500.00, '987654321', 'pedro@teahouse.com', 'oauth2_employee_1', 'keycloak');

-- ============================================================
-- Para restaurar datos existentes (si es necesario):
-- ============================================================
-- Si ya tienes datos sin oauth2_id, debes generar valores únicos:
-- UPDATE providers 
-- SET oauth2_id = CONCAT('migration_provider_', id), 
--     oauth2_provider = 'keycloak'
-- WHERE oauth2_id IS NULL;

-- UPDATE employees
-- SET oauth2_id = CONCAT('migration_employee_', id),
--     oauth2_provider = 'keycloak'
-- WHERE oauth2_id IS NULL;
