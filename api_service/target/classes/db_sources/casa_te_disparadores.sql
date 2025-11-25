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