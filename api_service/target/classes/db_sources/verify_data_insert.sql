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