-- =====================================================================
-- CASA DE TÉ - DATOS DE PRUEBA (AUTO-EJECUTADO POR SPRING BOOT)
-- =====================================================================
-- Spring Boot ejecuta este archivo automáticamente después de schema.sql
-- cuando spring.jpa.hibernate.ddl-auto = create
-- =====================================================================

SET FOREIGN_KEY_CHECKS = 0;

-- =====================================================================
-- 1. EMPLEADOS (5 registros)
-- =====================================================================

INSERT IGNORE INTO employees (first_name, last_name, salary, phone_number, email, oauth2_id, oauth2_provider) VALUES
('Juan', 'Pérez García', 2500.00, '612345678', 'juan.perez@casate.com', 'user', 'keycloak'),
('María', 'López Martínez', 2800.00, '623456789', 'maria.lopez@casate.com', 'tanaka', 'keycloak'),
('Carlos', 'García Rodríguez', 2600.00, '634567890', 'carlos.garcia@casate.com', 'employee4', 'keycloak'),
('Ana', 'Martínez Sánchez', 2700.00, '645678901', 'ana.martinez@casate.com', 'employee5', 'keycloak'),
('Luis', 'Fernández Díaz', 2400.00, '656789012', 'luis.fernandez@casate.com', 'employee6', 'keycloak');

-- =====================================================================
-- 2. PROVEEDORES (5 registros)
-- =====================================================================

INSERT IGNORE INTO providers (name, contact, phone_number, email, address, oauth2_id, oauth2_provider) VALUES
('TéDelMundo S.L.', 'Carlos Ruiz Hernández', '915551001', 'ventas@tedelmundo.com', 'Calle del Té 123, 28001 Madrid', 'provider1', 'keycloak'),
('Dulces Artesanales García', 'Ana María García López', '933334002', 'pedidos@dulcesartesanales.com', 'Avenida Gourmet 45, 08015 Barcelona', 'provider2', 'keycloak'),
('Distribuciones TeaTime', 'Pedro Sánchez Martín', '963337003', 'contacto@teatime.es', 'Polígono Industrial Las Flores 7, 46980 Valencia', 'provider3', 'keycloak'),
('Infusiones Naturales Del Sur', 'Carmen Jiménez Torres', '954441004', 'info@infusionesnaturales.com', 'Calle Andalucía 89, 41001 Sevilla', 'provider4', 'keycloak'),
('Pastelería Premium Imports', 'Miguel Ángel Rodríguez', '944445005', 'comercial@pasteleriapr.com', 'Barrio de Deusto 34, 48014 Bilbao', 'provider5', 'keycloak');

-- =====================================================================
-- 3. CLIENTES (10 registros)
-- =====================================================================

INSERT IGNORE INTO clients (first_name, last_name, email, phone_number, address, oauth2_id, oauth2_provider) VALUES
('Roberto', 'Sánchez López', 'roberto.sanchez@email.com', '670111222', 'Calle Principal 123, Madrid', 'client1', 'keycloak'),
('Elena', 'García Fernández', 'elena.garcia@email.com', '671222333', 'Avenida Central 456, Barcelona', 'client2', 'keycloak'),
('Miguel', 'Rodríguez Martín', 'miguel.rodriguez@email.com', '672333444', 'Plaza Mayor 789, Valencia', 'client3', 'keycloak'),
('Sofía', 'Martínez Ruiz', 'sofia.martinez@email.com', '673444555', 'Calle Flores 321, Bilbao', 'client4', 'keycloak'),
('David', 'López Jiménez', 'david.lopez@email.com', '674555666', 'Paseo del Parque 654, Sevilla', 'client5', 'keycloak'),
('Isabel', 'Pérez Torres', 'isabel.perez@email.com', '675666777', 'Calle Real 987, Málaga', 'client6', 'keycloak'),
('Francisco', 'Díaz Cabrera', 'francisco.diaz@email.com', '676777888', 'Avenida del Mar 246, Alicante', 'client7', 'keycloak'),
('Teresa', 'Gómez Soto', 'teresa.gomez@email.com', '677888999', 'Calle Comercio 135, Zaragoza', 'client8', 'keycloak'),
('Antonio', 'Moreno Ruiz', 'antonio.moreno@email.com', '678999000', 'Plaza Nueva 802, Granada', 'client9', 'keycloak'),
('Marta', 'Jiménez Navarro', 'marta.jimenez@email.com', '679000111', 'Calle Paz 573, Córdoba', 'client10', 'keycloak');

-- =====================================================================
-- 4. PRODUCTOS (23 registros - 10 bebidas, 13 postres)
-- =====================================================================

INSERT IGNORE INTO products (name, description, category, price, measure_unit, active) VALUES
-- BEBIDAS (Tés)
('Té Verde Sencha', 'Té verde japonés de alta calidad, suave y refrescante', 'DRINK', 3.50, 'taza', 1),
('Té Negro Earl Grey', 'Té negro aromático con bergamota', 'DRINK', 3.00, 'taza', 1),
('Té Oolong Premium', 'Té semifermentado con notas florales', 'DRINK', 4.50, 'taza', 1),
('Té Blanco Silver Needle', 'Té blanco delicado y exclusivo', 'DRINK', 5.00, 'taza', 1),
('Té Rooibos', 'Infusión sudafricana sin cafeína', 'DRINK', 3.20, 'taza', 1),
('Té Chai Masala', 'Té negro especiado con leche', 'DRINK', 3.80, 'taza', 1),
('Té Matcha Latte', 'Bebida cremosa de té verde en polvo', 'DRINK', 4.80, 'vaso', 1),
('Té Helado de Melocotón', 'Refrescante té frío con frutas', 'DRINK', 3.50, 'vaso', 1),
('Infusión de Menta', 'Menta fresca natural', 'DRINK', 2.80, 'taza', 1),
('Infusión de Manzanilla', 'Relajante infusión de flores', 'DRINK', 2.50, 'taza', 1),
-- POSTRES
('Pastel de Chocolate', 'Delicioso pastel de chocolate casero con ganache', 'DESSERT', 12.00, 'porción', 1),
('Cheesecake de Fresa', 'Tarta de queso con topping de fresas frescas', 'DESSERT', 14.00, 'porción', 1),
('Cheesecake de Matcha', 'Tarta de queso con sabor a te matcha', 'DESSERT', 14.00, 'porción', 1),
('Tarta de Zanahoria', 'Tarta especiada con frosting de queso crema', 'DESSERT', 11.50, 'porción', 1),
('Brownie con Nueces', 'Brownie intenso de chocolate con nueces', 'DESSERT', 8.00, 'porción', 1),
('Galletas de Mantequilla', 'Galletas artesanales crujientes', 'DESSERT', 2.00, 'unidad', 1),
('Scones con Mermelada', 'Bollos ingleses con mermelada y nata', 'DESSERT', 6.50, 'porción', 1),
('Macarons Surtidos', 'Delicados macarons franceses de varios sabores', 'DESSERT', 3.50, 'unidad', 1),
('Tarta de Limón', 'Tarta refrescante de limón con merengue', 'DESSERT', 13.00, 'porción', 1),
('Croissant de Almendra', 'Croissant relleno de crema de almendras', 'DESSERT', 4.50, 'unidad', 1),
('Dorayaki', 'Dulce japonés de 2 tortillas con pasta anko', 'DESSERT', 2.50, 'unidad', 1),
('Mochi', 'Pastel de arroz con relleno de frutas en su interior', 'DESSERT', 3.50, 'unidad', 1),
('Tiramisú', 'Postre italiano clásico con café y mascarpone', 'DESSERT', 10.00, 'porción', 1);

-- =====================================================================
-- 5. INVENTARIO (23 registros - 1 por cada producto)
-- =====================================================================

INSERT IGNORE INTO inventory (id_product, current_quantity, minimum_quantity) VALUES
(1, 150, 30), (2, 120, 25), (3, 80, 20), (4, 50, 15), (5, 100, 20),
(6, 90, 20), (7, 60, 15), (8, 70, 15), (9, 110, 25), (10, 95, 20),
(11, 20, 5), (12, 18, 5), (13, 25, 8), (14, 15, 4), (15, 25, 8),
(16, 80, 15), (17, 30, 10), (18, 50, 12), (19, 12, 4), (20, 35, 10),
(21, 30, 10), (22, 40, 12), (23, 16, 5);

-- =====================================================================
-- 6. PEDIDOS DE CLIENTES (10 registros)
-- =====================================================================

INSERT IGNORE INTO orders_clients (id_client, id_employee, order_date, order_state, service_type) VALUES
(1, 1, '2025-10-01', 'DELIVERED', 'TABLE'),
(2, 2, '2025-10-01', 'DELIVERED', 'TAKEAWAY'),
(3, 3, '2025-10-02', 'DELIVERED', 'TABLE'),
(4, 1, '2025-10-02', 'DELIVERED', 'DELIVERY'),
(5, 4, '2025-10-03', 'DELIVERED', 'TABLE'),
(6, 2, '2025-10-13', 'PREPARING', 'TAKEAWAY'),
(7, 5, '2025-10-13', 'PREPARING', 'TABLE'),
(8, 3, '2025-10-13', 'PENDENT', 'DELIVERY'),
(9, 1, '2025-10-10', 'CANCELED', 'TABLE'),
(10, 4, '2025-10-11', 'CANCELED', 'TAKEAWAY');

-- =====================================================================
-- 7. DETALLES DE PEDIDOS DE CLIENTES (29 registros)
-- =====================================================================

INSERT IGNORE INTO details_order_client (id_order_client, id_product, quantity, unit_price) VALUES
(1, 1, 2, 3.50), (1, 11, 1, 12.00), (1, 15, 3, 2.00),
(2, 2, 1, 3.00), (2, 15, 2, 2.00), (2, 14, 1, 8.00),
(3, 3, 2, 4.50), (3, 12, 1, 14.00), (3, 17, 3, 3.00),
(4, 6, 2, 3.80), (4, 13, 1, 11.50), (4, 15, 5, 2.00),
(5, 4, 2, 5.00), (5, 18, 1, 13.00), (5, 20, 1, 10.00), (5, 17, 4, 3.00),
(6, 7, 2, 4.80), (6, 19, 2, 4.50),
(7, 5, 2, 3.20), (7, 11, 1, 12.00), (7, 12, 1, 14.00), (7, 15, 3, 2.00),
(8, 8, 2, 3.50), (8, 14, 1, 8.00), (8, 15, 4, 2.00),
(9, 1, 1, 3.50),
(10, 2, 1, 3.00);

-- =====================================================================
-- 8. FACTURAS DE CLIENTES (8 registros)
-- =====================================================================

INSERT IGNORE INTO invoices_clients (id_order_client, invoice_number, invoice_date, total, payment_method, payment_date) VALUES
(1, 'FC-0001', '2025-10-01', 25.50, 'CREDIT', '2025-10-01'),
(2, 'FC-0002', '2025-10-01', 15.50, 'METALIC', '2025-10-01'),
(3, 'FC-0003', '2025-10-02', 32.00, 'CREDIT', '2025-10-02'),
(4, 'FC-0004', '2025-10-02', 28.50, 'METALIC', '2025-10-03'),
(5, 'FC-0005', '2025-10-03', 41.00, 'METALIC', '2025-10-03'),
(6, 'FC-0006', '2025-10-13', 18.80, 'CREDIT', NULL),
(7, 'FC-0007', '2025-10-13', 35.50, 'CREDIT', NULL),
(8, 'FC-0008', '2025-10-13', 22.00, 'METALIC', NULL);

-- =====================================================================
-- 9. PEDIDOS A PROVEEDORES (7 registros)
-- =====================================================================

INSERT IGNORE INTO orders_providers (id_provider, id_employee, order_date, total, observations) VALUES
(1, 3, '2025-09-15', 850.00, 'Pedido mensual de tés variados'),
(2, 3, '2025-09-18', 420.00, 'Reposición de postres frescos'),
(3, 3, '2025-09-22', 650.00, 'Pedido urgente - stock bajo'),
(4, 3, '2025-09-28', 380.00, 'Infusiones naturales para temporada otoño'),
(1, 3, '2025-10-05', 920.00, 'Pedido especial tés premium'),
(5, 3, '2025-10-08', 550.00, 'Pastelería importada para eventos'),
(2, 3, '2025-10-12', 480.00, 'Reposición semanal postres');

-- =====================================================================
-- 10. DETALLES DE PEDIDOS A PROVEEDORES (30 registros)
-- =====================================================================

INSERT IGNORE INTO details_order_provider (id_order_provider, id_product, quantity, unit_price) VALUES
(1, 1, 100, 2.00), (1, 2, 80, 1.80), (1, 3, 50, 3.00), (1, 4, 30, 3.50),
(2, 11, 15, 7.00), (2, 12, 12, 9.00), (2, 13, 10, 7.50), (2, 18, 8, 8.50),
(3, 6, 60, 2.50), (3, 7, 40, 3.20), (3, 8, 50, 2.00), (3, 15, 100, 0.80),
(4, 5, 80, 1.90), (4, 9, 70, 1.50), (4, 10, 60, 1.30),
(5, 3, 60, 3.00), (5, 4, 50, 3.50), (5, 1, 120, 2.00), (5, 7, 50, 3.20),
(6, 17, 60, 2.00), (6, 20, 20, 6.50), (6, 19, 40, 2.80), (6, 16, 35, 4.00),
(7, 11, 18, 7.00), (7, 14, 30, 5.00), (7, 12, 15, 9.00), (7, 15, 80, 0.80);

-- =====================================================================
-- 11. FACTURAS DE PROVEEDORES (7 registros)
-- =====================================================================

INSERT IGNORE INTO invoices_providers (id_order_provider, invoice_number, invoice_date, total, payment_state, payment_date) VALUES
(1, 'FP-TDM-089', '2025-09-15', 850.00, 'PAID', '2025-09-30'),
(2, 'FP-DA-145', '2025-09-18', 420.00, 'PAID', '2025-10-02'),
(3, 'FP-DTT-234', '2025-09-22', 650.00, 'PAID', '2025-10-05'),
(4, 'FP-INS-067', '2025-09-28', 380.00, 'PAID', '2025-10-10'),
(5, 'FP-TDM-098', '2025-10-05', 920.00, 'PENDENT', NULL),
(6, 'FP-PPI-023', '2025-10-08', 550.00, 'PENDENT', NULL),
(7, 'FP-DA-156', '2025-10-12', 480.00, 'PENDENT', NULL);

SET FOREIGN_KEY_CHECKS = 1;
