/*
	DISPARADORES - CASA DEL TÉ
*/

CREATE TABLE auditory_employee (
    id INT PRIMARY KEY AUTO_INCREMENT,
    employee_id INT,
    accion VARCHAR(10),
    old_salary DECIMAL(10,2),
    new_salary DECIMAL(10,2),
    date TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Trigger para actualización
DELIMITER //
CREATE TRIGGER update_employee
AFTER UPDATE ON employee
FOR EACH ROW
BEGIN
    IF OLD.salary != NEW.salary THEN
        INSERT INTO auditoria_empleados (employee_id, accion, old_salary, new_salary)
        VALUES (NEW.id, 'UPDATE', OLD.salary, NEW.salary);
    END IF;
END//
DELIMITER ;

/*
    DISPARADORES (TRIGGERS) - CASA DE TÉ
    Automatización de procesos y auditoría
*/

-- ============================================
-- TABLA DE AUDITORÍA
-- ============================================
CREATE TABLE IF NOT EXISTS audit_log (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    table_name VARCHAR(50) NOT NULL,
    operation VARCHAR(10) NOT NULL,
    record_id BIGINT NOT NULL,
    old_value TEXT,
    new_value TEXT,
    changed_by VARCHAR(100),
    changed_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    INDEX idx_table_operation (table_name, operation),
    INDEX idx_changed_at (changed_at)
);

-- ============================================
-- 1. DESCONTAR INVENTARIO AL VENDER PRODUCTO
-- ============================================
DELIMITER $$

CREATE TRIGGER after_detail_order_client_insert
AFTER INSERT ON details_order_client
FOR EACH ROW
BEGIN
    -- Descontar del inventario la cantidad vendida
    UPDATE inventory 
    SET current_quantity = current_quantity - NEW.quantity
    WHERE id_product = NEW.id_product;
    
    -- Registrar en auditoría
    INSERT INTO audit_log (table_name, operation, record_id, new_value)
    VALUES ('inventory', 'SALE', NEW.id_product, 
            CONCAT('Vendido: ', NEW.quantity, ' unidades del producto ID: ', NEW.id_product));
END$$

DELIMITER ;

-- ============================================
-- 2. DESACTIVAR PRODUCTO SI INVENTARIO BAJO
-- ============================================
DELIMITER $$

CREATE TRIGGER after_inventory_update_check_stock
AFTER UPDATE ON inventory
FOR EACH ROW
BEGIN
    -- Si el stock actual es menor que el mínimo, desactivar el producto
    IF NEW.current_quantity < NEW.minimum_quantity THEN
        UPDATE products 
        SET active = 0 
        WHERE id = NEW.id_product AND active = 1;
        
        -- Registrar en auditoría
        INSERT INTO audit_log (table_name, operation, record_id, new_value)
        VALUES ('products', 'DEACTIVATE', NEW.id_product, 
                CONCAT('Producto desactivado por stock bajo. Stock actual: ', 
                       NEW.current_quantity, ', Mínimo: ', NEW.minimum_quantity));
    END IF;
    
    -- Si el stock se recupera por encima del mínimo, reactivar el producto
    IF NEW.current_quantity >= NEW.minimum_quantity AND OLD.current_quantity < OLD.minimum_quantity THEN
        UPDATE products 
        SET active = 1 
        WHERE id = NEW.id_product AND active = 0;
        
        -- Registrar en auditoría
        INSERT INTO audit_log (table_name, operation, record_id, new_value)
        VALUES ('products', 'ACTIVATE', NEW.id_product, 
                CONCAT('Producto reactivado. Stock actual: ', 
                       NEW.current_quantity, ', Mínimo: ', NEW.minimum_quantity));
    END IF;
END$$

DELIMITER ;

-- ============================================
-- 3. INCREMENTAR INVENTARIO AL RECIBIR PEDIDO DE PROVEEDOR
-- ============================================
DELIMITER $$

CREATE TRIGGER after_detail_order_provider_insert
AFTER INSERT ON details_order_provider
FOR EACH ROW
BEGIN
    -- Incrementar el inventario con la cantidad recibida
    UPDATE inventory 
    SET current_quantity = current_quantity + NEW.quantity
    WHERE id_product = NEW.id_product;
    
    -- Registrar en auditoría
    INSERT INTO audit_log (table_name, operation, record_id, new_value)
    VALUES ('inventory', 'RESTOCK', NEW.id_product, 
            CONCAT('Recibido: ', NEW.quantity, ' unidades del producto ID: ', NEW.id_product));
END$$

DELIMITER ;

-- ============================================
-- 4. CALCULAR TOTAL DEL PEDIDO DE CLIENTE AUTOMÁTICAMENTE
-- ============================================
DELIMITER $$

CREATE TRIGGER after_detail_order_client_insert_calc_total
AFTER INSERT ON details_order_client
FOR EACH ROW
BEGIN
    DECLARE order_total DECIMAL(10,2);
    
    -- Calcular el total del pedido
    SELECT SUM(quantity * unit_price) INTO order_total
    FROM details_order_client
    WHERE id_order_client = NEW.id_order_client;
    
    -- Actualizar la factura si existe
    UPDATE invoices_clients
    SET total = order_total
    WHERE id_order_client = NEW.id_order_client;
END$$

DELIMITER ;

-- ============================================
-- 5. CALCULAR TOTAL DEL PEDIDO A PROVEEDOR AUTOMÁTICAMENTE
-- ============================================
DELIMITER $$

CREATE TRIGGER after_detail_order_provider_insert_calc_total
AFTER INSERT ON details_order_provider
FOR EACH ROW
BEGIN
    DECLARE order_total DECIMAL(10,2);
    
    -- Calcular el total del pedido
    SELECT SUM(quantity * unit_price) INTO order_total
    FROM details_order_provider
    WHERE id_order_provider = NEW.id_order_provider;
    
    -- Actualizar el pedido
    UPDATE orders_providers
    SET total = order_total
    WHERE id = NEW.id_order_provider;
    
    -- Actualizar la factura si existe
    UPDATE invoices_providers
    SET total = order_total
    WHERE id_order_provider = NEW.id_order_provider;
END$$

DELIMITER ;

-- ============================================
-- 6. AUDITORÍA DE CAMBIOS EN PRODUCTOS
-- ============================================
DELIMITER $$

CREATE TRIGGER after_product_update_audit
AFTER UPDATE ON products
FOR EACH ROW
BEGIN
    -- Registrar cambios importantes
    IF OLD.price != NEW.price THEN
        INSERT INTO audit_log (table_name, operation, record_id, old_value, new_value)
        VALUES ('products', 'UPDATE', NEW.id, 
                CONCAT('Precio anterior: ', OLD.price),
                CONCAT('Precio nuevo: ', NEW.price));
    END IF;
    
    IF OLD.active != NEW.active THEN
        INSERT INTO audit_log (table_name, operation, record_id, old_value, new_value)
        VALUES ('products', 'UPDATE', NEW.id, 
                CONCAT('Estado anterior: ', IF(OLD.active = 1, 'ACTIVO', 'INACTIVO')),
                CONCAT('Estado nuevo: ', IF(NEW.active = 1, 'ACTIVO', 'INACTIVO')));
    END IF;
END$$

DELIMITER ;

-- ============================================
-- 7. VALIDAR STOCK ANTES DE VENDER
-- ============================================
DELIMITER $$

CREATE TRIGGER before_detail_order_client_insert_validate_stock
BEFORE INSERT ON details_order_client
FOR EACH ROW
BEGIN
    DECLARE available_stock INT;
    DECLARE product_name VARCHAR(50);
    
    -- Obtener stock disponible
    SELECT current_quantity INTO available_stock
    FROM inventory
    WHERE id_product = NEW.id_product;
    
    -- Si no hay suficiente stock, lanzar error
    IF available_stock < NEW.quantity THEN
        SELECT name INTO product_name FROM products WHERE id = NEW.id_product;
        SIGNAL SQLSTATE '45000'
        SET MESSAGE_TEXT = CONCAT('Stock insuficiente para el producto: ', 
                                   product_name, 
                                   '. Disponible: ', available_stock, 
                                   ', Solicitado: ', NEW.quantity);
    END IF;
END$$

DELIMITER ;

-- ============================================
-- 8. AUDITORÍA DE CAMBIOS EN PEDIDOS
-- ============================================
DELIMITER $$

CREATE TRIGGER after_order_client_update_audit
AFTER UPDATE ON orders_clients
FOR EACH ROW
BEGIN
    -- Registrar cambio de estado
    IF OLD.order_state != NEW.order_state THEN
        INSERT INTO audit_log (table_name, operation, record_id, old_value, new_value)
        VALUES ('orders_clients', 'UPDATE', NEW.id, 
                CONCAT('Estado anterior: ', OLD.order_state),
                CONCAT('Estado nuevo: ', NEW.order_state));
    END IF;
END$$

DELIMITER ;

-- ============================================
-- 9. PREVENIR ELIMINACIÓN DE PRODUCTOS CON INVENTARIO
-- ============================================
DELIMITER $$

CREATE TRIGGER before_product_delete_check_inventory
BEFORE DELETE ON products
FOR EACH ROW
BEGIN
    DECLARE stock INT;
    
    -- Verificar si tiene stock
    SELECT current_quantity INTO stock
    FROM inventory
    WHERE id_product = OLD.id;
    
    -- Si tiene stock, no permitir eliminación
    IF stock > 0 THEN
        SIGNAL SQLSTATE '45000'
        SET MESSAGE_TEXT = 'Unable to delete product with stock avaiable. Disable it first.';
    END IF;
END$$

DELIMITER ;

-- ============================================
-- 10. REGISTRAR CREACIÓN DE FACTURAS
-- ============================================
DELIMITER $$

CREATE TRIGGER after_invoice_client_insert_audit
AFTER INSERT ON invoices_clients
FOR EACH ROW
BEGIN
    INSERT INTO audit_log (table_name, operation, record_id, new_value)
    VALUES ('invoices_clients', 'INSERT', NEW.id, 
            CONCAT('Factura creada: ', NEW.invoice_number, 
                   ', Total: ', NEW.total, 
                   ', Método: ', NEW.payment_method));
END$$

DELIMITER ;

-- ============================================
-- 11. ALERTAS DE STOCK BAJO
-- ============================================
CREATE TABLE IF NOT EXISTS stock_alerts (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    id_product BIGINT NOT NULL,
    alert_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    current_quantity INT NOT NULL,
    minimum_quantity INT NOT NULL,
    resolved BOOLEAN DEFAULT FALSE,
    FOREIGN KEY (id_product) REFERENCES products(id),
    INDEX idx_resolved (resolved),
    INDEX idx_alert_date (alert_date)
);

DELIMITER $$

CREATE TRIGGER after_inventory_update_create_alert
AFTER UPDATE ON inventory
FOR EACH ROW
BEGIN
    -- Si el stock cae por debajo del mínimo, crear alerta
    IF NEW.current_quantity < NEW.minimum_quantity AND 
       OLD.current_quantity >= OLD.minimum_quantity THEN
        
        INSERT INTO stock_alerts (id_product, current_quantity, minimum_quantity)
        VALUES (NEW.id_product, NEW.current_quantity, NEW.minimum_quantity);
        
    END IF;
    
    -- Si el stock se recupera, marcar alertas como resueltas
    IF NEW.current_quantity >= NEW.minimum_quantity AND 
       OLD.current_quantity < OLD.minimum_quantity THEN
        
        UPDATE stock_alerts
        SET resolved = TRUE
        WHERE id_product = NEW.id_product AND resolved = FALSE;
        
    END IF;
END$$

DELIMITER ;

-- ============================================
-- 12. PREVENIR PRECIOS NEGATIVOS
-- ============================================
DELIMITER $$

CREATE TRIGGER before_product_insert_validate_price
BEFORE INSERT ON products
FOR EACH ROW
BEGIN
    IF NEW.price <= 0 THEN
        SIGNAL SQLSTATE '45000'
        SET MESSAGE_TEXT = 'El precio del producto debe ser mayor a 0';
    END IF;
END$$

CREATE TRIGGER before_product_update_validate_price
BEFORE UPDATE ON products
FOR EACH ROW
BEGIN
    IF NEW.price <= 0 THEN
        SIGNAL SQLSTATE '45000'
        SET MESSAGE_TEXT = 'El precio del producto debe ser mayor a 0';
    END IF;
END$$

DELIMITER ;

-- ============================================
-- CONSULTAS ÚTILES PARA MONITOREO
-- ============================================

-- Ver últimas acciones en auditoría
CREATE VIEW recent_audit_log AS
SELECT 
    al.id,
    al.table_name,
    al.operation,
    al.record_id,
    al.new_value,
    al.changed_at
FROM audit_log al
ORDER BY al.changed_at DESC
LIMIT 50;

-- Ver alertas de stock activas
CREATE VIEW active_stock_alerts AS
SELECT 
    sa.id,
    p.name AS product_name,
    sa.current_quantity,
    sa.minimum_quantity,
    sa.alert_date,
    TIMESTAMPDIFF(HOUR, sa.alert_date, NOW()) AS hours_since_alert
FROM stock_alerts sa
JOIN products p ON sa.id_product = p.id
WHERE sa.resolved = FALSE
ORDER BY sa.alert_date DESC;

-- Ver productos con stock bajo
CREATE VIEW low_stock_products AS
SELECT 
    p.id,
    p.name,
    p.category,
    i.current_quantity,
    i.minimum_quantity,
    (i.minimum_quantity - i.current_quantity) AS units_needed,
    p.active
FROM products p
JOIN inventory i ON p.id = i.id_product
WHERE i.current_quantity < i.minimum_quantity
ORDER BY (i.minimum_quantity - i.current_quantity) DESC;

-- ============================================
-- VERIFICACIÓN DE TRIGGERS
-- ============================================
SELECT 
    TRIGGER_NAME,
    EVENT_MANIPULATION,
    EVENT_OBJECT_TABLE,
    ACTION_TIMING
FROM information_schema.TRIGGERS
WHERE TRIGGER_SCHEMA = 'casa_te'
ORDER BY EVENT_OBJECT_TABLE, ACTION_TIMING, EVENT_MANIPULATION;

SELECT 'Triggers creados exitosamente' AS resultado;