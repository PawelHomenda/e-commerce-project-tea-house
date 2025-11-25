/*
    CONSULTA DE DADOS - CASA DE TÉ
    Inserción de registros de ejemplo para todas las tablas
*/

system cls;

system echo =========================
system echo === CONSULTAS BASICAS ===
system echo =========================

system echo ===================================
system echo === REGISTRO TOTAL DE EMPLEADOS ===
system echo ===================================
system echo  

select * from employees;

system echo =====================================
system echo === REGISTRO TOTAL DE PROVEEDORES ===
system echo =====================================
system echo  

select * from providers;

system echo =============================================
system echo === REGISTRO TOTAL DE PRODUCTOS -BEBIDAS- ===
system echo =============================================
system echo  

select * from products where category="DRINK";

system echo =============================================
system echo === REGISTRO TOTAL DE PRODUCTOS -POSTRES- ===
system echo =============================================
system echo  

select * from products where category="DESSERT";

system echo =====================================
system echo === REGISTRO DE PRODUCTOS ACTIVOS ===
system echo =====================================
system echo  

select * from products where active = 1;

system echo ========================================
system echo === REGISTRO DE PRODUCTOS NO-ACTIVOS ===
system echo ========================================
system echo  

select * from products where active = 0;

system echo ========================================
system echo === REGISTRO DE PRODUCTOS MAYOR A 10 ===
system echo ========================================
system echo  

select * from products where price > 10;

system echo ========================================
system echo === REGISTRO DE PRODUCTOS MENOR A 10 ===
system echo ========================================
system echo  

select * from products where price < 10;

system echo ========================================================
system echo === REGISTRO DE EMPLEADOS CON NOMBRE PARECIDO A JUAN ===
system echo ========================================================
system echo  

select * from employees where email like "juan%";

system echo ==================================================
system echo === REGISTRO DE PRODUCTOS ORDENADOS ACENDIENTE ===
system echo ==================================================
system echo  

select * from products order by products.price asc;

system echo ====================================================
system echo === REGISTRO DE PRODUCTOS ORDENADOS DESCENDIENTE ===
system echo ====================================================
system echo  

select * from products order by products.price desc;

source casa_te_vistas.sql