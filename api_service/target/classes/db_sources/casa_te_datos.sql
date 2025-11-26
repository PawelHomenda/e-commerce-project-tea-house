/*
    DATOS DE PRUEBA - CASA DE TÉ
    Inserción de registros de ejemplo para todas las tablas
*/

system cls;

USE casa_te;

-- ============================================
-- EMPLEADOS
-- ============================================
INSERT INTO employees (first_name, last_name, salary, phone_number, email) VALUES
('Juan', 'Pérez García', 2500.00, '612345678', 'juan.perez@casate.com'),
('María', 'López Martínez', 2800.00, '623456789', 'maria.lopez@casate.com'),
('Carlos', 'García Rodríguez', 2600.00, '634567890', 'carlos.garcia@casate.com'),
('Ana', 'Martínez Sánchez', 2700.00, '645678901', 'ana.martinez@casate.com'),
('Luis', 'Fernández Díaz', 2400.00, '656789012', 'luis.fernandez@casate.com');

-- ============================================
-- PRODUCTOS
-- ============================================
INSERT INTO products (name, description, category, price, measure_unit, active) VALUES
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

-- ============================================
-- PROVEEDORES
-- ============================================
INSERT INTO providers (name, contact, phone_number, email, address) VALUES
('TéDelMundo S.L.', 'Carlos Ruiz Hernández', '915551001', 'ventas@tedelmundo.com', 'Calle del Té 123, 28001 Madrid'),
('Dulces Artesanales García', 'Ana María García López', '933334002', 'pedidos@dulcesartesanales.com', 'Avenida Gourmet 45, 08015 Barcelona'),
('Distribuciones TeaTime', 'Pedro Sánchez Martín', '963337003', 'contacto@teatime.es', 'Polígono Industrial Las Flores 7, 46980 Valencia'),
('Infusiones Naturales Del Sur', 'Carmen Jiménez Torres', '954441004', 'info@infusionesnaturales.com', 'Calle Andalucía 89, 41001 Sevilla'),
('Pastelería Premium Imports', 'Miguel Ángel Rodríguez', '944445005', 'comercial@pasteleriapr.com', 'Barrio de Deusto 34, 48014 Bilbao');

-- ============================================
-- INVENTARIO
-- ============================================
INSERT INTO inventory (id_product, current_quantity, minimum_quantity) VALUES
-- Bebidas
(1, 150, 30),   -- Té Verde Sencha
(2, 120, 25),   -- Té Negro Earl Grey
(3, 80, 20),    -- Té Oolong Premium
(4, 50, 15),    -- Té Blanco Silver Needle
(5, 100, 20),   -- Té Rooibos
(6, 90, 20),    -- Té Chai Masala
(7, 60, 15),    -- Té Matcha Latte
(8, 70, 15),    -- Té Helado de Melocotón
(9, 110, 25),   -- Infusión de Menta
(10, 95, 20),   -- Infusión de Manzanilla

-- Postres
(11, 20, 5),    -- Pastel de Chocolate
(12, 18, 5),    -- Cheesecake de Fresa
(13, 15, 4),    -- Tarta de Zanahoria
(14, 25, 8),    -- Brownie con Nueces
(15, 80, 15),   -- Galletas de Mantequilla
(16, 30, 10),   -- Scones con Mermelada
(17, 50, 12),   -- Macarons Surtidos
(18, 12, 4),    -- Tarta de Limón
(19, 35, 10),   -- Croissant de Almendra
(20, 16, 5);    -- Tiramisú

-- ============================================
-- PEDIDOS DE CLIENTES
-- ============================================
INSERT INTO orders_clients (id_employee, order_date, order_state, service_type) VALUES
-- Pedidos completados
(1, '2025-10-01', 'DELIVERED', 'TABLE'),
(2, '2025-10-01', 'DELIVERED', 'TAKEAWAY'),
(3, '2025-10-02', 'DELIVERED', 'TABLE'),
(1, '2025-10-02', 'DELIVERED', 'DELIVERY'),
(4, '2025-10-03', 'DELIVERED', 'TABLE'),

-- Pedidos en proceso
(2, '2025-10-13', 'PREPARING', 'TAKEAWAY'),
(5, '2025-10-13', 'PREPARING', 'TABLE'),
(3, '2025-10-13', 'PENDENT', 'DELIVERY'),

-- Pedidos CANCELEDs
(1, '2025-10-10', 'CANCELED', 'TABLE'),
(4, '2025-10-11', 'CANCELED', 'TAKEAWAY');

-- ============================================
-- DETALLES DE PEDIDOS DE CLIENTES
-- ============================================
INSERT INTO details_order_client (id_order_client, id_product, quantity, unit_price) VALUES
-- Pedido 1: María González (TABLE)
(1, 1, 2, 3.50),    -- 2 Té Verde
(1, 11, 1, 12.00),  -- 1 Pastel Chocolate
(1, 15, 3, 2.00),   -- 3 Galletas

-- Pedido 2: Luis Fernández (TAKEAWAY)
(2, 2, 1, 3.00),    -- 1 Té Negro
(2, 15, 2, 2.00),   -- 2 Galletas
(2, 14, 1, 8.00),   -- 1 Brownie

-- Pedido 3: Ana Rodríguez (TABLE)
(3, 3, 2, 4.50),    -- 2 Té Oolong
(3, 12, 1, 14.00),  -- 1 Cheesecake
(3, 17, 3, 3.00),   -- 3 Macarons

-- Pedido 4: Pedro Martínez (DELIVERY)
(4, 6, 2, 3.80),    -- 2 Chai Masala
(4, 13, 1, 11.50),  -- 1 Tarta Zanahoria
(4, 15, 5, 2.00),   -- 5 Galletas

-- Pedido 5: Carmen Sánchez (TABLE)
(5, 4, 2, 5.00),    -- 2 Té Blanco
(5, 18, 1, 13.00),  -- 1 Tarta Limón
(5, 20, 1, 10.00),  -- 1 Tiramisú
(5, 17, 4, 3.00),   -- 4 Macarons

-- Pedido 6: José García (EN PREPARACION - TAKEAWAY)
(6, 7, 2, 4.80),    -- 2 Matcha Latte
(6, 19, 2, 4.50),   -- 2 Croissant

-- Pedido 7: Laura Jiménez (EN PREPARACION - TABLE)
(7, 5, 2, 3.20),    -- 2 Té Rooibos
(7, 11, 1, 12.00),  -- 1 Pastel Chocolate
(7, 12, 1, 14.00),  -- 1 Cheesecake
(7, 15, 3, 2.00),   -- 3 Galletas

-- Pedido 8: Roberto Torres (PENDENT - DELIVERY)
(8, 8, 2, 3.50),    -- 2 Té Helado
(8, 14, 1, 8.00),   -- 1 Brownie
(8, 15, 4, 2.00),   -- 4 Galletas

-- Pedido 9: Isabel Romero (CANCELED)
(9, 1, 1, 3.50),    -- 1 Té Verde

-- Pedido 10: Francisco Navarro (CANCELED)
(10, 2, 1, 3.00);   -- 1 Té Negro

-- ============================================
-- FACTURAS DE CLIENTES
-- ============================================
INSERT INTO invoices_clients (id_order_client, invoice_number, invoice_date, total, payment_method, payment_date) VALUES
(1, 'FC-0001', '2025-10-01', 25.50, 'CREDIT', '2025-10-01'),
(2, 'FC-0002', '2025-10-01', 15.50, 'METALIC', '2025-10-01'),
(3, 'FC-0003', '2025-10-02', 32.00, 'CREDIT', '2025-10-02'),
(4, 'FC-0004', '2025-10-02', 28.50, 'METALIC', '2025-10-03'),
(5, 'FC-0005', '2025-10-03', 41.00, 'METALIC', '2025-10-03'),
(6, 'FC-0006', '2025-10-13', 18.80, 'CREDIT', NULL),          -- PENDENT de pago
(7, 'FC-0007', '2025-10-13', 35.50, 'CREDIT', NULL),           -- PENDENT de pago
(8, 'FC-0008', '2025-10-13', 22.00, 'METALIC', NULL);     -- PENDENT de pago

-- ============================================
-- PEDIDOS A PROVEEDORES
-- ============================================
INSERT INTO orders_providers (id_provider, id_employee, order_date, total, observations) VALUES
(1, 3, '2025-09-15', 850.00, 'Pedido mensual de tés variados'),
(2, 3, '2025-09-18', 420.00, 'Reposición de postres frescos'),
(3, 3, '2025-09-22', 650.00, 'Pedido urgente - stock bajo'),
(4, 3, '2025-09-28', 380.00, 'Infusiones naturales para temporada otoño'),
(1, 3, '2025-10-05', 920.00, 'Pedido especial tés premium'),
(5, 3, '2025-10-08', 550.00, 'Pastelería importada para eventos'),
(2, 3, '2025-10-12', 480.00, 'Reposición semanal postres');

-- ============================================
-- DETALLES DE PEDIDOS A PROVEEDORES
-- ============================================
INSERT INTO details_order_provider (id_order_provider, id_product, quantity, unit_price) VALUES
-- Pedido 1: TéDelMundo (15-Sep)
(1, 1, 100, 2.00),   -- 100 Té Verde @ 2.00
(1, 2, 80, 1.80),    -- 80 Té Negro @ 1.80
(1, 3, 50, 3.00),    -- 50 Té Oolong @ 3.00
(1, 4, 30, 3.50),    -- 30 Té Blanco @ 3.50

-- Pedido 2: Dulces Artesanales (18-Sep)
(2, 11, 15, 7.00),   -- 15 Pastel Chocolate @ 7.00
(2, 12, 12, 9.00),   -- 12 Cheesecake @ 9.00
(2, 13, 10, 7.50),   -- 10 Tarta Zanahoria @ 7.50
(2, 18, 8, 8.50),    -- 8 Tarta Limón @ 8.50

-- Pedido 3: Distribuciones TeaTime (22-Sep - URGENTE)
(3, 6, 60, 2.50),    -- 60 Chai Masala @ 2.50
(3, 7, 40, 3.20),    -- 40 Matcha Latte @ 3.20
(3, 8, 50, 2.00),    -- 50 Té Helado @ 2.00
(3, 15, 100, 0.80),  -- 100 Galletas @ 0.80

-- Pedido 4: Infusiones Naturales Del Sur (28-Sep)
(4, 5, 80, 1.90),    -- 80 Rooibos @ 1.90
(4, 9, 70, 1.50),    -- 70 Menta @ 1.50
(4, 10, 60, 1.30),   -- 60 Manzanilla @ 1.30

-- Pedido 5: TéDelMundo (05-Oct - PREMIUM)
(5, 3, 60, 3.00),    -- 60 Té Oolong @ 3.00
(5, 4, 50, 3.50),    -- 50 Té Blanco @ 3.50
(5, 1, 120, 2.00),   -- 120 Té Verde @ 2.00
(5, 7, 50, 3.20),    -- 50 Matcha Latte @ 3.20

-- Pedido 6: Pastelería Premium Imports (08-Oct)
(6, 17, 60, 2.00),   -- 60 Macarons @ 2.00
(6, 20, 20, 6.50),   -- 20 Tiramisú @ 6.50
(6, 19, 40, 2.80),   -- 40 Croissant @ 2.80
(6, 16, 35, 4.00),   -- 35 Scones @ 4.00

-- Pedido 7: Dulces Artesanales (12-Oct - SEMANAL)
(7, 11, 18, 7.00),   -- 18 Pastel Chocolate @ 7.00
(7, 14, 30, 5.00),   -- 30 Brownie @ 5.00
(7, 12, 15, 9.00),   -- 15 Cheesecake @ 9.00
(7, 15, 80, 0.80);   -- 80 Galletas @ 0.80

-- ============================================
-- FACTURAS DE PROVEEDORES
-- ============================================
INSERT INTO invoices_providers (id_order_provider, invoice_number, invoice_date, total, payment_state, payment_date) VALUES
(1, 'FP-TDM-089', '2025-09-15', 850.00, 'PAID', '2025-09-30'),
(2, 'FP-DA-145', '2025-09-18', 420.00, 'PAID', '2025-10-02'),
(3, 'FP-DTT-234', '2025-09-22', 650.00, 'PAID', '2025-10-05'),
(4, 'FP-INS-067', '2025-09-28', 380.00, 'PAID', '2025-10-10'),
(5, 'FP-TDM-098', '2025-10-05', 920.00, 'PENDENT', NULL),
(6, 'FP-PPI-023', '2025-10-08', 550.00, 'PENDENT', NULL),
(7, 'FP-DA-156', '2025-10-12', 480.00, 'PENDENT', NULL);

-- ============================================
-- VERIFICACIÓN DE DATOS INSERTADOS
-- ============================================
SELECT '=== RESUMEN DE DATOS INSERTADOS ===' AS '';
SELECT CONCAT('Empleados: ', COUNT(*)) AS Total FROM employees;
SELECT CONCAT('Productos: ', COUNT(*)) AS Total FROM products;
SELECT CONCAT('Proveedores: ', COUNT(*)) AS Total FROM providers;
SELECT CONCAT('Registros de Inventario: ', COUNT(*)) AS Total FROM inventory;
SELECT CONCAT('Pedidos de Clientes: ', COUNT(*)) AS Total FROM orders_clients;
SELECT CONCAT('Detalles Pedidos Clientes: ', COUNT(*)) AS Total FROM details_order_client;
SELECT CONCAT('Facturas de Clientes: ', COUNT(*)) AS Total FROM invoices_clients;
SELECT CONCAT('Pedidos a Proveedores: ', COUNT(*)) AS Total FROM orders_providers;
SELECT CONCAT('Detalles Pedidos Proveedores: ', COUNT(*)) AS Total FROM details_order_provider;
SELECT CONCAT('Facturas de Proveedores: ', COUNT(*)) AS Total FROM invoices_providers;
SELECT 'Datos de prueba insertados correctamente' AS Resultado;