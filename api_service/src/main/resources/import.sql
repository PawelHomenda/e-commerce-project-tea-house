-- =====================================================================
-- CASA DE TÉ - DATOS DE PRUEBA (AUTO-EJECUTADO POR HIBERNATE)
-- =====================================================================
-- Este archivo es ejecutado automáticamente por Hibernate después de crear el schema
-- Formato correcto: Cada statement SQL en UNA sola línea, terminado con ;
-- =====================================================================

SET FOREIGN_KEY_CHECKS = 0;

-- 1. EMPLEADOS (5 registros)
INSERT IGNORE INTO employees (first_name, last_name, email, phone_number, salary, oauth2_id, oauth2_provider) VALUES ('Juan', 'García Martínez', 'juan.garcia@teahouse.com', '915551234', 2500.00, 'employee1', 'keycloak');
INSERT IGNORE INTO employees (first_name, last_name, email, phone_number, salary, oauth2_id, oauth2_provider) VALUES ('María', 'López Sánchez', 'maria.lopez@teahouse.com', '915552345', 2000.00, 'employee2', 'keycloak');
INSERT IGNORE INTO employees (first_name, last_name, email, phone_number, salary, oauth2_id, oauth2_provider) VALUES ('Carlos', 'Rodríguez González', 'carlos.rodriguez@teahouse.com', '915553456', 1500.00, 'employee3', 'keycloak');
INSERT IGNORE INTO employees (first_name, last_name, email, phone_number, salary, oauth2_id, oauth2_provider) VALUES ('Ana', 'Martínez Jiménez', 'ana.martinez@teahouse.com', '915554567', 1500.00, 'employee4', 'keycloak');
INSERT IGNORE INTO employees (first_name, last_name, email, phone_number, salary, oauth2_id, oauth2_provider) VALUES ('David', 'Fernández Díaz', 'david.fernandez@teahouse.com', '915555678', 1300.00, 'employee5', 'keycloak');

-- 2. PROVEEDORES (5 registros)
INSERT IGNORE INTO providers (name, contact, phone_number, email, address, oauth2_id, oauth2_provider) VALUES ('TéDelMundo S.L.', 'Carlos Ruiz Hernández', '915551001', 'ventas@tedelmundo.com', 'Calle del Té 123, 28001 Madrid', 'provider1', 'keycloak');
INSERT IGNORE INTO providers (name, contact, phone_number, email, address, oauth2_id, oauth2_provider) VALUES ('Dulces Artesanales García', 'Ana María García López', '933334002', 'pedidos@dulcesartesanales.com', 'Avenida Gourmet 45, 08015 Barcelona', 'provider2', 'keycloak');
INSERT IGNORE INTO providers (name, contact, phone_number, email, address, oauth2_id, oauth2_provider) VALUES ('Distribuciones TeaTime', 'Pedro Sánchez Martín', '963337003', 'contacto@teatime.es', 'Polígono Industrial Las Flores 7, 46980 Valencia', 'provider3', 'keycloak');
INSERT IGNORE INTO providers (name, contact, phone_number, email, address, oauth2_id, oauth2_provider) VALUES ('Infusiones Naturales Del Sur', 'Carmen Jiménez Torres', '954441004', 'info@infusionesnaturales.com', 'Calle Andalucía 89, 41001 Sevilla', 'provider4', 'keycloak');
INSERT IGNORE INTO providers (name, contact, phone_number, email, address, oauth2_id, oauth2_provider) VALUES ('Pastelería Premium Imports', 'Miguel Ángel Rodríguez', '944445005', 'comercial@pasteleriapr.com', 'Barrio de Deusto 34, 48014 Bilbao', 'provider5', 'keycloak');

-- 3. CLIENTES (10 registros)
INSERT IGNORE INTO clients (first_name, last_name, email, phone_number, address, oauth2_id, oauth2_provider) VALUES ('Roberto', 'Sánchez López', 'roberto.sanchez@email.com', '670111222', 'Calle Principal 123, Madrid', 'client1', 'keycloak');
INSERT IGNORE INTO clients (first_name, last_name, email, phone_number, address, oauth2_id, oauth2_provider) VALUES ('Elena', 'García Fernández', 'elena.garcia@email.com', '671222333', 'Avenida Central 456, Barcelona', 'client2', 'keycloak');
INSERT IGNORE INTO clients (first_name, last_name, email, phone_number, address, oauth2_id, oauth2_provider) VALUES ('Miguel', 'Rodríguez Martín', 'miguel.rodriguez@email.com', '672333444', 'Plaza Mayor 789, Valencia', 'client3', 'keycloak');
INSERT IGNORE INTO clients (first_name, last_name, email, phone_number, address, oauth2_id, oauth2_provider) VALUES ('Sofía', 'Martínez Ruiz', 'sofia.martinez@email.com', '673444555', 'Calle Flores 321, Bilbao', 'client4', 'keycloak');
INSERT IGNORE INTO clients (first_name, last_name, email, phone_number, address, oauth2_id, oauth2_provider) VALUES ('David', 'López Jiménez', 'david.lopez@email.com', '674555666', 'Paseo del Parque 654, Sevilla', 'client5', 'keycloak');
INSERT IGNORE INTO clients (first_name, last_name, email, phone_number, address, oauth2_id, oauth2_provider) VALUES ('Isabel', 'Pérez Torres', 'isabel.perez@email.com', '675666777', 'Calle Real 987, Málaga', 'client6', 'keycloak');
INSERT IGNORE INTO clients (first_name, last_name, email, phone_number, address, oauth2_id, oauth2_provider) VALUES ('Francisco', 'Díaz Cabrera', 'francisco.diaz@email.com', '676777888', 'Avenida del Mar 246, Alicante', 'client7', 'keycloak');
INSERT IGNORE INTO clients (first_name, last_name, email, phone_number, address, oauth2_id, oauth2_provider) VALUES ('Teresa', 'Gómez Soto', 'teresa.gomez@email.com', '677888999', 'Calle Comercio 135, Zaragoza', 'client8', 'keycloak');
INSERT IGNORE INTO clients (first_name, last_name, email, phone_number, address, oauth2_id, oauth2_provider) VALUES ('Antonio', 'Moreno Ruiz', 'antonio.moreno@email.com', '678999000', 'Plaza Nueva 802, Granada', 'client9', 'keycloak');
INSERT IGNORE INTO clients (first_name, last_name, email, phone_number, address, oauth2_id, oauth2_provider) VALUES ('Marta', 'Jiménez Navarro', 'marta.jimenez@email.com', '679000111', 'Calle Paz 573, Córdoba', 'client10', 'keycloak');

-- 4. PRODUCTOS (23 registros)
INSERT IGNORE INTO products (name, description, category, price, measure_unit, active) VALUES ('Té Verde Sencha', 'Té verde japonés de alta calidad, suave y refrescante', 'DRINK', 3.50, 'taza', 1);
INSERT IGNORE INTO products (name, description, category, price, measure_unit, active) VALUES ('Té Negro Earl Grey', 'Té negro aromático con bergamota', 'DRINK', 3.00, 'taza', 1);
INSERT IGNORE INTO products (name, description, category, price, measure_unit, active) VALUES ('Té Oolong Premium', 'Té semifermentado con notas florales', 'DRINK', 4.50, 'taza', 1);
INSERT IGNORE INTO products (name, description, category, price, measure_unit, active) VALUES ('Té Blanco Silver Needle', 'Té blanco delicado y exclusivo', 'DRINK', 5.00, 'taza', 1);
INSERT IGNORE INTO products (name, description, category, price, measure_unit, active) VALUES ('Té Rooibos', 'Infusión sudafricana sin cafeína', 'DRINK', 3.20, 'taza', 1);
INSERT IGNORE INTO products (name, description, category, price, measure_unit, active) VALUES ('Té Chai Masala', 'Té negro especiado con leche', 'DRINK', 3.80, 'taza', 1);
INSERT IGNORE INTO products (name, description, category, price, measure_unit, active) VALUES ('Té Matcha Latte', 'Bebida cremosa de té verde en polvo', 'DRINK', 4.80, 'vaso', 1);
INSERT IGNORE INTO products (name, description, category, price, measure_unit, active) VALUES ('Té de Manzanilla', 'Infusión relajante de manzanilla', 'DRINK', 2.50, 'taza', 1);
INSERT IGNORE INTO products (name, description, category, price, measure_unit, active) VALUES ('Té de Jengibre', 'Bebida caliente estimulante', 'DRINK', 3.00, 'taza', 1);
INSERT IGNORE INTO products (name, description, category, price, measure_unit, active) VALUES ('Café Espresso', 'Café fuerte e intenso', 'DRINK', 2.00, 'taza', 1);
INSERT IGNORE INTO products (name, description, category, price, measure_unit, active) VALUES ('Brownie de Chocolate', 'Brownie casero con chocolate 70%', 'DESSERT', 4.50, 'pieza', 1);
INSERT IGNORE INTO products (name, description, category, price, measure_unit, active) VALUES ('Tarta de Manzana', 'Tarta casera de manzana con canela', 'DESSERT', 5.00, 'porción', 1);
INSERT IGNORE INTO products (name, description, category, price, measure_unit, active) VALUES ('Magdalena Clásica', 'Magdalena tradicional esponjosa', 'DESSERT', 2.50, 'pieza', 1);
INSERT IGNORE INTO products (name, description, category, price, measure_unit, active) VALUES ('Galletas de Té', 'Galletas artesanales de mantequilla', 'DESSERT', 3.20, 'paquete', 1);
INSERT IGNORE INTO products (name, description, category, price, measure_unit, active) VALUES ('Tiramisú Italiano', 'Tiramisú casero auténtico', 'DESSERT', 6.50, 'porción', 1);
INSERT IGNORE INTO products (name, description, category, price, measure_unit, active) VALUES ('Cheesecake', 'Cheesecake de queso Philadelphia', 'DESSERT', 6.00, 'porción', 1);
INSERT IGNORE INTO products (name, description, category, price, measure_unit, active) VALUES ('Éclair de Vainilla', 'Éclair relleno de crema de vainilla', 'DESSERT', 4.00, 'pieza', 1);
INSERT IGNORE INTO products (name, description, category, price, measure_unit, active) VALUES ('Profiteroles', 'Profiteroles rellenas de crema', 'DESSERT', 5.50, 'porción', 1);
INSERT IGNORE INTO products (name, description, category, price, measure_unit, active) VALUES ('Lemon Cake', 'Pastel de limón con glaseado', 'DESSERT', 4.80, 'porción', 1);
INSERT IGNORE INTO products (name, description, category, price, measure_unit, active) VALUES ('Donuts', 'Donuts glaseados variados', 'DESSERT', 3.00, 'pieza', 1);
INSERT IGNORE INTO products (name, description, category, price, measure_unit, active) VALUES ('Frutos Secos', 'Mezcla de almendras, avellanas y nueces', 'DESSERT', 8.00, 'ración', 1);
INSERT IGNORE INTO products (name, description, category, price, measure_unit, active) VALUES ('Tarta de Chocolate', 'Tarta de chocolate belga premium', 'DESSERT', 7.00, 'porción', 1);
INSERT IGNORE INTO products (name, description, category, price, measure_unit, active) VALUES ('Pavlova', 'Pavlova con frutos rojos', 'DESSERT', 5.50, 'pieza', 1);

-- 5. INVENTARIO (10 registros)
INSERT IGNORE INTO inventory (id_product, current_quantity, minimum_quantity) VALUES (1, 50, 5);
INSERT IGNORE INTO inventory (id_product, current_quantity, minimum_quantity) VALUES (2, 40, 5);
INSERT IGNORE INTO inventory (id_product, current_quantity, minimum_quantity) VALUES (3, 30, 3);
INSERT IGNORE INTO inventory (id_product, current_quantity, minimum_quantity) VALUES (4, 20, 2);
INSERT IGNORE INTO inventory (id_product, current_quantity, minimum_quantity) VALUES (5, 60, 5);
INSERT IGNORE INTO inventory (id_product, current_quantity, minimum_quantity) VALUES (6, 45, 5);
INSERT IGNORE INTO inventory (id_product, current_quantity, minimum_quantity) VALUES (7, 35, 3);
INSERT IGNORE INTO inventory (id_product, current_quantity, minimum_quantity) VALUES (8, 55, 5);
INSERT IGNORE INTO inventory (id_product, current_quantity, minimum_quantity) VALUES (9, 70, 10);
INSERT IGNORE INTO inventory (id_product, current_quantity, minimum_quantity) VALUES (10, 100, 15);

-- 6. PEDIDOS DE CLIENTES (10 registros)
INSERT IGNORE INTO orders_clients (id_client, id_employee, order_date, order_state, service_type) VALUES (1, 1, '2025-09-01', 'DELIVERED', 'DELIVERY');
INSERT IGNORE INTO orders_clients (id_client, id_employee, order_date, order_state, service_type) VALUES (2, 2, '2025-09-05', 'DELIVERED', 'TABLE');
INSERT IGNORE INTO orders_clients (id_client, id_employee, order_date, order_state, service_type) VALUES (3, 1, '2025-09-10', 'DELIVERED', 'DELIVERY');
INSERT IGNORE INTO orders_clients (id_client, id_employee, order_date, order_state, service_type) VALUES (4, 3, '2025-09-15', 'DELIVERED', 'TABLE');
INSERT IGNORE INTO orders_clients (id_client, id_employee, order_date, order_state, service_type) VALUES (5, 2, '2025-09-20', 'DELIVERED', 'DELIVERY');
INSERT IGNORE INTO orders_clients (id_client, id_employee, order_date, order_state, service_type) VALUES (6, 1, '2025-09-25', 'DELIVERED', 'TAKEAWAY');
INSERT IGNORE INTO orders_clients (id_client, id_employee, order_date, order_state, service_type) VALUES (7, 3, '2025-10-01', 'DELIVERED', 'TABLE');
INSERT IGNORE INTO orders_clients (id_client, id_employee, order_date, order_state, service_type) VALUES (8, 2, '2025-10-05', 'PENDENT', 'DELIVERY');
INSERT IGNORE INTO orders_clients (id_client, id_employee, order_date, order_state, service_type) VALUES (9, 1, '2025-10-08', 'PENDENT', 'TAKEAWAY');
INSERT IGNORE INTO orders_clients (id_client, id_employee, order_date, order_state, service_type) VALUES (10, 3, '2025-10-12', 'PENDENT', 'DELIVERY');

-- 7. DETALLES DE PEDIDOS DE CLIENTES (25 registros)
INSERT IGNORE INTO details_order_client (id_order_client, id_product, quantity, unit_price) VALUES (1, 1, 5, 3.50);
INSERT IGNORE INTO details_order_client (id_order_client, id_product, quantity, unit_price) VALUES (1, 11, 2, 4.50);
INSERT IGNORE INTO details_order_client (id_order_client, id_product, quantity, unit_price) VALUES (2, 2, 3, 3.00);
INSERT IGNORE INTO details_order_client (id_order_client, id_product, quantity, unit_price) VALUES (2, 12, 1, 5.00);
INSERT IGNORE INTO details_order_client (id_order_client, id_product, quantity, unit_price) VALUES (3, 3, 4, 4.50);
INSERT IGNORE INTO details_order_client (id_order_client, id_product, quantity, unit_price) VALUES (3, 13, 3, 2.50);
INSERT IGNORE INTO details_order_client (id_order_client, id_product, quantity, unit_price) VALUES (4, 4, 2, 5.00);
INSERT IGNORE INTO details_order_client (id_order_client, id_product, quantity, unit_price) VALUES (4, 14, 5, 3.20);
INSERT IGNORE INTO details_order_client (id_order_client, id_product, quantity, unit_price) VALUES (5, 5, 6, 3.20);
INSERT IGNORE INTO details_order_client (id_order_client, id_product, quantity, unit_price) VALUES (5, 15, 1, 6.50);
INSERT IGNORE INTO details_order_client (id_order_client, id_product, quantity, unit_price) VALUES (6, 6, 4, 3.80);
INSERT IGNORE INTO details_order_client (id_order_client, id_product, quantity, unit_price) VALUES (6, 16, 2, 6.00);
INSERT IGNORE INTO details_order_client (id_order_client, id_product, quantity, unit_price) VALUES (7, 7, 3, 4.80);
INSERT IGNORE INTO details_order_client (id_order_client, id_product, quantity, unit_price) VALUES (7, 17, 2, 4.00);
INSERT IGNORE INTO details_order_client (id_order_client, id_product, quantity, unit_price) VALUES (8, 8, 5, 2.50);
INSERT IGNORE INTO details_order_client (id_order_client, id_product, quantity, unit_price) VALUES (8, 18, 1, 5.50);
INSERT IGNORE INTO details_order_client (id_order_client, id_product, quantity, unit_price) VALUES (9, 9, 7, 3.00);
INSERT IGNORE INTO details_order_client (id_order_client, id_product, quantity, unit_price) VALUES (9, 19, 3, 4.80);
INSERT IGNORE INTO details_order_client (id_order_client, id_product, quantity, unit_price) VALUES (10, 10, 2, 2.00);
INSERT IGNORE INTO details_order_client (id_order_client, id_product, quantity, unit_price) VALUES (10, 20, 4, 3.00);
INSERT IGNORE INTO details_order_client (id_order_client, id_product, quantity, unit_price) VALUES (8, 1, 3, 3.50);
INSERT IGNORE INTO details_order_client (id_order_client, id_product, quantity, unit_price) VALUES (9, 11, 2, 4.50);
INSERT IGNORE INTO details_order_client (id_order_client, id_product, quantity, unit_price) VALUES (10, 2, 4, 3.00);
INSERT IGNORE INTO details_order_client (id_order_client, id_product, quantity, unit_price) VALUES (8, 12, 2, 5.00);
INSERT IGNORE INTO details_order_client (id_order_client, id_product, quantity, unit_price) VALUES (9, 21, 1, 8.00);

-- 8. FACTURAS DE CLIENTES (7 registros)
INSERT IGNORE INTO invoices_clients (id_order_client, invoice_number, invoice_date, total, payment_method, payment_date) VALUES (1, 'FC-001', '2025-09-02', 130.00, 'METALIC', '2025-09-02');
INSERT IGNORE INTO invoices_clients (id_order_client, invoice_number, invoice_date, total, payment_method, payment_date) VALUES (2, 'FC-002', '2025-09-06', 119.00, 'CREDIT', '2025-09-06');
INSERT IGNORE INTO invoices_clients (id_order_client, invoice_number, invoice_date, total, payment_method, payment_date) VALUES (3, 'FC-003', '2025-09-11', 107.50, 'METALIC', '2025-09-12');
INSERT IGNORE INTO invoices_clients (id_order_client, invoice_number, invoice_date, total, payment_method, payment_date) VALUES (4, 'FC-004', '2025-09-16', 126.00, 'CREDIT', '2025-09-17');
INSERT IGNORE INTO invoices_clients (id_order_client, invoice_number, invoice_date, total, payment_method, payment_date) VALUES (5, 'FC-005', '2025-09-21', 125.20, 'METALIC', '2025-09-22');
INSERT IGNORE INTO invoices_clients (id_order_client, invoice_number, invoice_date, total, payment_method, payment_date) VALUES (6, 'FC-006', '2025-09-26', 122.80, 'CREDIT', '2025-09-27');
INSERT IGNORE INTO invoices_clients (id_order_client, invoice_number, invoice_date, total, payment_method, payment_date) VALUES (7, 'FC-007', '2025-10-02', 115.30, 'METALIC', '2025-10-02');

-- 9. PEDIDOS DE PROVEEDORES (8 registros)
INSERT IGNORE INTO orders_providers (id_provider, id_employee, order_date, total, observations) VALUES (1, 1, '2025-08-01', 57.00, 'Pedido de té verde y té negro');
INSERT IGNORE INTO orders_providers (id_provider, id_employee, order_date, total, observations) VALUES (2, 2, '2025-08-10', 42.50, 'Brownies y tartas');
INSERT IGNORE INTO orders_providers (id_provider, id_employee, order_date, total, observations) VALUES (3, 1, '2025-08-20', 70.00, 'Variedad de infusiones');
INSERT IGNORE INTO orders_providers (id_provider, id_employee, order_date, total, observations) VALUES (4, 3, '2025-09-01', 55.00, 'Dulces y pasteles');
INSERT IGNORE INTO orders_providers (id_provider, id_employee, order_date, total, observations) VALUES (1, 2, '2025-09-10', 78.50, 'Reabastecimiento mensual');
INSERT IGNORE INTO orders_providers (id_provider, id_employee, order_date, total, observations) VALUES (5, 1, '2025-09-20', 65.00, 'Productos premium');
INSERT IGNORE INTO orders_providers (id_provider, id_employee, order_date, total, observations) VALUES (2, 3, '2025-09-28', 48.00, 'Dulces variados');
INSERT IGNORE INTO orders_providers (id_provider, id_employee, order_date, total, observations) VALUES (3, 2, '2025-10-05', 62.50, 'Pedido urgente');

-- 10. DETALLES DE PEDIDOS DE PROVEEDORES (15 registros)
INSERT IGNORE INTO details_order_provider (id_order_provider, id_product, quantity, unit_price) VALUES (1, 1, 20, 1.50);
INSERT IGNORE INTO details_order_provider (id_order_provider, id_product, quantity, unit_price) VALUES (1, 2, 25, 1.20);
INSERT IGNORE INTO details_order_provider (id_order_provider, id_product, quantity, unit_price) VALUES (2, 3, 15, 2.00);
INSERT IGNORE INTO details_order_provider (id_order_provider, id_product, quantity, unit_price) VALUES (2, 4, 10, 2.50);
INSERT IGNORE INTO details_order_provider (id_order_provider, id_product, quantity, unit_price) VALUES (3, 5, 30, 1.60);
INSERT IGNORE INTO details_order_provider (id_order_provider, id_product, quantity, unit_price) VALUES (3, 6, 20, 1.90);
INSERT IGNORE INTO details_order_provider (id_order_provider, id_product, quantity, unit_price) VALUES (4, 7, 18, 2.40);
INSERT IGNORE INTO details_order_provider (id_order_provider, id_product, quantity, unit_price) VALUES (4, 8, 25, 1.25);
INSERT IGNORE INTO details_order_provider (id_order_provider, id_product, quantity, unit_price) VALUES (5, 9, 22, 1.50);
INSERT IGNORE INTO details_order_provider (id_order_provider, id_product, quantity, unit_price) VALUES (5, 10, 15, 0.80);
INSERT IGNORE INTO details_order_provider (id_order_provider, id_product, quantity, unit_price) VALUES (6, 11, 8, 2.25);
INSERT IGNORE INTO details_order_provider (id_order_provider, id_product, quantity, unit_price) VALUES (7, 12, 12, 2.50);
INSERT IGNORE INTO details_order_provider (id_order_provider, id_product, quantity, unit_price) VALUES (7, 13, 5, 1.60);
INSERT IGNORE INTO details_order_provider (id_order_provider, id_product, quantity, unit_price) VALUES (8, 14, 7, 1.60);
INSERT IGNORE INTO details_order_provider (id_order_provider, id_product, quantity, unit_price) VALUES (8, 15, 30, 5.00);

-- 11. FACTURAS DE PROVEEDORES (7 registros)
INSERT IGNORE INTO invoices_providers (id_order_provider, invoice_number, invoice_date, total, payment_state, payment_date) VALUES (1, 'FP-TDM-089', '2025-08-15', 850.00, 'PAID', '2025-08-30');
INSERT IGNORE INTO invoices_providers (id_order_provider, invoice_number, invoice_date, total, payment_state, payment_date) VALUES (2, 'FP-DA-145', '2025-08-25', 420.00, 'PAID', '2025-09-02');
INSERT IGNORE INTO invoices_providers (id_order_provider, invoice_number, invoice_date, total, payment_state, payment_date) VALUES (3, 'FP-DTT-234', '2025-09-05', 650.00, 'PAID', '2025-09-15');
INSERT IGNORE INTO invoices_providers (id_order_provider, invoice_number, invoice_date, total, payment_state, payment_date) VALUES (4, 'FP-INS-067', '2025-09-15', 380.00, 'PAID', '2025-09-25');
INSERT IGNORE INTO invoices_providers (id_order_provider, invoice_number, invoice_date, total, payment_state, payment_date) VALUES (5, 'FP-TDM-098', '2025-09-25', 920.00, 'PAID', '2025-10-05');
INSERT IGNORE INTO invoices_providers (id_order_provider, invoice_number, invoice_date, total, payment_state, payment_date) VALUES (6, 'FP-PPI-023', '2025-10-05', 550.00, 'PAID', '2025-10-10');
INSERT IGNORE INTO invoices_providers (id_order_provider, invoice_number, invoice_date, total, payment_state, payment_date) VALUES (7, 'FP-DA-156', '2025-10-12', 480.00, 'PAID', '2025-10-15');

SET FOREIGN_KEY_CHECKS = 1;
