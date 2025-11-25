/*
    VISTAS - CASA DE TÉ
    Inserción de registros de ejemplo para todas las tablas
*/

system echo =====================
system echo === JOINS SIMPLES ===
system echo =====================
system echo  

system echo =============================================
system echo === CONSULTA DE INVENTARIOS CON PRODUCTOS ===
system echo =============================================
system echo  

create view products_in_inventory as
select i.id,p.name product_name,i.current_quantity,i.minimum_quantity 
from inventory i 
inner join products p on i.id_product = p.id;

select * from products_in_inventory;

system echo ========================================================
system echo === CONSULTA DE PEDIDOS CON NOMBRES DE LOS EMPLEADOS ===
system echo ========================================================
system echo  

create view orders_by_employee as
select concat(em.first_name," ",em.last_name) full_name, oc.order_date, oc.order_state, oc.service_type
from orders_clients oc
inner join employees em on oc.id_employee = em.id;

select * from orders_by_employee;

system echo ==============================================================
system echo === CONSULTA DE INVENTARIOS CON PRODUCTOS EN POCA CANTIDAD ===
system echo ==============================================================
system echo 

create view products_on_low_stack as
select pd.name product_name,i.current_quantity,i.minimum_quantity 
from inventory i 
inner join products pd on i.id_product = p.id
where i.current_quantity < i.minimum_quantity;

select * from products_on_low_stack;

system echo =========================================================================
system echo === CONSULTA DE DETALLES PEDIDOS DE PROVEEDORES MANEJADO POR EMPLEADO ===
system echo =========================================================================
system echo  

create view detail_providers_by_employee as
select op.id,p.name nombre_producto,concat(e.first_name," ",e.last_name) nombre_empleado ,op.order_date,op.total,op.observations
from orders_providers op
inner join invoices_providers ip on ip.id_order_provider=op.id
inner join providers p on op.id_provider=p.id
inner join employees e on op.id_employee=e.id;

select * from detail_providers_by_employee;

system echo =====================================================
system echo === CONSULTA DE DETALLES DE PEDIDOS A PROVEEDORES ===
system echo =====================================================
system echo  

create view details_by_providers_orders as
select dop.id, pv.name, pd.name, dop.quantity, op.order_date, op.total
from details_order_provider dop
inner join products pd on dop.id_product=pd.id
inner join orders_providers op on dop.id_order_provider=op.id
inner join providers pv on op.id_provider=pv.id;

select * from details_by_providers_orders;

system echo ===========================================================
system echo === CONSULTA DE GASTOS TOTALES POR PROVEEDORES ORDENADO ===
system echo ===========================================================
system echo  

create view total_cost_from_providers as
select pv.name, sum(op.total) order_total
from details_order_provider dop
inner join orders_providers op on dop.id_order_provider=op.id
inner join providers pv on op.id_provider=pv.id
group by pv.name
order by order_total;

select * from total_cost_from_providers;

system echo ======================
system echo === GASTOS TOTALES ===
system echo ======================
system echo  

create view total_cost as
select sum(op.total) order_total
from details_order_provider dop
inner join orders_providers op on dop.id_order_provider=op.id
inner join providers pv on op.id_provider=pv.id;

select * from total_cost;

system echo =================================
system echo === GASTOS TOTALES EN OCTUBRE ===
system echo =================================
system echo  

create view total_cost_in_october as
select sum(op.total) order_total
from details_order_provider dop
inner join orders_providers op on dop.id_order_provider=op.id
inner join providers pv on op.id_provider=pv.id
where month(op.order_date) = 10;

select * from total_cost_in_october;

system echo ==============================================================
system echo === CONSULTA DE CANTIDADES DE PRODUCTOS COMPRADOS ORDENADO ===
system echo ==============================================================
system echo  

create view total_products_in_october as
select pd.name, sum(dop.quantity) total_quantity from details_order_provider dop
inner join products pd on dop.id_product=pd.id
inner join orders_providers op on dop.id_order_provider=op.id
inner join providers pv on op.id_provider=pv.id
group by pd.name
order by total_quantity desc;

select * from total_products_in_october;

system echo ===================================================================
system echo === CONSULTA DE GASTOS TOTALES POR PRODUCTO ORDENADO EN OCTUBRE ===
system echo ===================================================================
system echo  

create view total_cost_orders_in_october as
select pd.name, sum(op.total) order_total
from details_order_provider dop
inner join products pd on dop.id_product=pd.id
inner join orders_providers op on dop.id_order_provider=op.id
inner join providers pv on op.id_provider=pv.id
where month(op.order_date) = 10
group by pd.name
order by order_total desc;

select * from total_cost_orders_in_october;

system echo =========================================================================
system echo === CONSULTA DE CANTIDADES DE PRODUCTOS COMPRADOS ORDENADO EN OCTUBRE ===
system echo =========================================================================
system echo  

create view total_products_bought_in_october as
select pd.name, sum(dop.quantity) total_quantity from details_order_provider dop
inner join products pd on dop.id_product=pd.id
inner join orders_providers op on dop.id_order_provider=op.id
inner join providers pv on op.id_provider=pv.id
where month(op.order_date) = 10
group by pd.name
order by total_quantity desc;

select * from total_products_bought_in_october;

system echo =======================================
system echo === CONSULTA DE PEDIDOS DE CLIENTES ===
system echo =======================================
system echo  

create view client_orders_and_product as
select doc.id , pd.name, oc.service_type, doc.quantity from details_order_client doc
inner join orders_clients oc on doc.id_order_client=oc.id
inner join products pd on doc.id_product=pd.id;

select * from client_orders_and_product;

system echo ==============================================================
system echo === CONSULTA DE NUMEROS DE PRODUCTOS VENDIDOS POR SERVICIO ===
system echo ==============================================================
system echo  

create view number_products_by_service as
select oc.service_type, sum(doc.quantity) n_products_sold from details_order_client doc
inner join orders_clients oc on doc.id_order_client=oc.id
inner join products pd on doc.id_product=pd.id
group by oc.service_type;

select * from number_products_by_service;


system echo =============================================
system echo === TOP 5 PRODUCTOS VENDIDOS POR CANTIDAD ===
system echo =============================================
system echo  


create view top_5_products_by_quantity as
select pd.name, sum(doc.quantity * doc.unit_price) total_income_sold from details_order_client doc
inner join orders_clients oc on doc.id_order_client=oc.id
inner join products pd on doc.id_product=pd.id
group by pd.name
order by total_income_sold desc
limit 5;

select * from top_5_products_by_quantity;

system echo ===========================================
system echo === TOP 5 PRODUCTOS VENDIDOS POR PRECIO ===
system echo ===========================================
system echo  



create view top_5_products_by_price as
select pd.name, sum(doc.quantity) quantity_sold from details_order_client doc
inner join orders_clients oc on doc.id_order_client=oc.id
inner join products pd on doc.id_product=pd.id
group by pd.name
order by quantity_sold desc
limit 5;

select * from top_5_products_by_price;

system echo ======================
system echo === GANANCIA TOTAL ===
system echo ======================
system echo  



create view total_income as
select sum(doc.quantity * doc.unit_price) total_income_sold from details_order_client doc
inner join orders_clients oc on doc.id_order_client=oc.id
inner join products pd on doc.id_product=pd.id;

system echo =======================================
system echo === MARGEN DE GANANCIA POR PRODUCTO ===
system echo =======================================
system echo  

select * from orders_by_employee;

create view income_cost_by_product as
select distinct pd.name, doc.unit_price sale_price, dop.unit_price factory_price, (doc.unit_price - dop.unit_price) income_gained
from products pd
inner join details_order_provider dop on dop.id_product=pd.id
inner join details_order_client doc on doc.id_product=pd.id;

select * from income_cost_by_product;

system echo ================================
system echo === TOP 5 MARGEN DE GANANCIA ===
system echo ================================
system echo  

create view top_5_income_cost as
select distinct pd.name, doc.unit_price sale_price, dop.unit_price factory_price, (doc.unit_price - dop.unit_price) income_gained
from products pd
inner join details_order_provider dop on dop.id_product=pd.id
inner join details_order_client doc on doc.id_product=pd.id
order by income_gained desc
limit 5;

select * from top_5_income_cost;

system echo ===============================
system echo === EMPLEADO CON MÁS VENTAS ===
system echo ===============================
system echo  



create view employee_with_most_sell as
select concat(em.first_name," ",em.last_name) full_name, sum(ic.total) total_income
from employees em
inner join orders_clients oc on oc.id_employee=em.id
inner join invoices_clients ic on oc.id=ic.id_order_client
group by full_name
order by total_income desc;

select * from employee_with_most_sell;

system echo ============================
system echo === PRODUCTOS SIN VENTAS ===
system echo ============================
system echo  

create view products_without_sells as
select pd.name from products pd 
left join details_order_client doc on doc.id_product = pd.id
where doc.id IS NULL;

select * from products_without_sells;

system echo =================================================
system echo === FACTURAS PENDIENTES DE PAGO (PROVEEDORES) ===
system echo =================================================
system echo  


create view pendent_invoces as
select pv.name from providers pv
inner join orders_providers op on pv.id = op.id_provider
inner join invoices_providers ip on op.id = ip.id_order_provider
where ip.payment_state = "PENDIENTE";

select * from pendent_invoces;

system echo ========================
system echo === INGRESOS TOTALES ===
system echo ========================
system echo  


create view total_invoice as
select sum(ic.total) total_ingreso
from details_order_client doc
inner join orders_clients oc on doc.id_order_client=oc.id
inner join invoices_clients ic on ic.id_order_client=oc.id;

select * from total_invoice;

system echo ===================================
system echo === INGRESOS TOTALES EN OCTUBRE ===
system echo ===================================
system echo  

create view total_invoice_in_october as
select sum(ic.total) total_ingreso
from details_order_client doc
inner join orders_clients oc on doc.id_order_client=oc.id
inner join invoices_clients ic on doc.id_order_client=oc.id
where month(ic.invoice_date) = 10;

select * from total_invoice_in_october;

system echo =================================================
system echo === HISTORIAL COMPLETO DE TODOS LOS PRODUCTOS ===
system echo =================================================
system echo  


create view product_historial as
SELECT
pd.name nombre_producto,
( SELECT COALESCE(SUM(dop.quantity),0)
    FROM details_order_provider dop
    WHERE dop.id_product = pd.id ) total_cantidad_comprada,
( SELECT COALESCE(SUM(dop.quantity * dop.unit_price),0)
    FROM details_order_provider dop
    WHERE dop.id_product = pd.id ) total_gastos_comprado,
( SELECT COALESCE(SUM(doc.quantity),0)
    FROM details_order_client doc
    WHERE doc.id_product = pd.id ) total_cantidad_vendida,
( SELECT COALESCE(SUM(doc.quantity * doc.unit_price),0)
    FROM details_order_client doc
    WHERE doc.id_product = pd.id ) total_ingreso_ventas
FROM products pd
ORDER BY pd.id;

select * from product_historial;
