-- ============================================================
-- Schema SQL - Casa de Té E-Commerce
-- Se ejecuta automáticamente al iniciar la aplicación
-- ============================================================

-- 1. Tabla de empleados con OAuth2
create table if not exists employees(
    id bigint not null auto_increment,
    first_name varchar(50) not null,
    last_name varchar(50) not null,
    salary decimal(8,2) not null,
    phone_number varchar(15) not null,
    email varchar(50) unique not null,
    oauth2_id varchar(255) unique not null,
    oauth2_provider varchar(50),
    primary key(id),
    index idx_oauth2_id (oauth2_id)
);

-- 2. Tabla de productos
create table if not exists products(
    id bigint not null auto_increment,
    name varchar(50) not null,
    description text not null,
    category varchar(10) not null,
	price decimal(5,2) not null,
    measure_unit varchar(10),
    active boolean default 1,
	primary key(id)
);

-- 3. Tabla de proveedores con OAuth2
create table if not exists providers(
	id bigint not null auto_increment,
	name varchar(50) not null,
	contact varchar(50) not null,
	phone_number varchar(10) not null,
	email varchar(50) unique not null,
	address varchar(100) not null,
	oauth2_id varchar(255) unique not null,
	oauth2_provider varchar(50),
	primary key(id),
	index idx_oauth2_id (oauth2_id)
);

-- 4. Tabla de inventario
create table if not exists inventory(
    id bigint not null auto_increment,
    id_product bigint not null,
    current_quantity int not null,
    minimum_quantity int not null,
    primary key(id),
    foreign key(id_product) references products(id),
    unique key unique_product (id_product)
);

-- 5. Tabla de clientes con OAuth2
create table if not exists clients(
    id bigint not null auto_increment,
    first_name varchar(50) not null,
    last_name varchar(50) not null,
    email varchar(50) unique not null,
    phone_number varchar(15),
    address varchar(100),
    oauth2_id varchar(255) unique not null,
    oauth2_provider varchar(50),
    created_at timestamp default current_timestamp,
    updated_at timestamp default current_timestamp on update current_timestamp,
    primary key(id),
    index idx_oauth2_id (oauth2_id),
    index idx_email (email)
);

-- 6. Tabla de órdenes de proveedores
create table if not exists orders_providers(
    id bigint not null auto_increment,
    id_provider bigint not null,
    id_employee bigint not null,
    order_date DATE not null,
    total DECIMAL(8,2),
    observations TEXT,
    primary key(id),
    foreign key(id_provider) references providers(id),
    foreign key(id_employee) references employees(id),
    index idx_provider (id_provider),
    index idx_employee (id_employee)
);

-- 7. Tabla de facturas de proveedores
create table if not exists invoices_providers(
    id bigint not null auto_increment,
    id_order_provider bigint not null,
    invoice_number varchar(20) unique,
    invoice_date date,
    total decimal(6,2),
    payment_state varchar(20),
    payment_date date,
    primary key(id),
    foreign key(id_order_provider) references orders_providers(id),
    index idx_order (id_order_provider)
);

-- 8. Tabla de órdenes de clientes (modificada)
create table if not exists orders_clients(
    id bigint not null auto_increment,
    id_client bigint not null,
    id_employee bigint,
    order_date date not null,
    order_state varchar(20) default "PENDENT",
    service_type varchar(20) not null,
    primary key(id),
    foreign key(id_client) references clients(id),
    foreign key(id_employee) references employees(id),
    index idx_client (id_client),
    index idx_employee (id_employee)
);

-- 9. Tabla de facturas de clientes
create table if not exists invoices_clients(
    id bigint not null auto_increment,
    id_order_client bigint not null,
    invoice_number varchar(20) unique,
    invoice_date date,
    total decimal(6,2),
    payment_method varchar(20) default "METALIC",
    payment_date date,
    primary key(id),
    foreign key(id_order_client) references orders_clients(id),
    index idx_order (id_order_client)
);

-- 10. Tabla de detalles de órdenes de proveedores
create table if not exists detail_order_providers(
    id bigint not null auto_increment,
    id_order_provider bigint not null,
    id_product bigint not null,
    quantity int not null,
	unit_price decimal(5,2),
    primary key(id),
    foreign key(id_order_provider) references orders_providers(id),
    foreign key(id_product) references products(id),
    index idx_order (id_order_provider)
);

-- 11. Tabla de detalles de órdenes de clientes
create table if not exists detail_order_clients(
	id bigint not null auto_increment,
	id_order_client bigint not null,
	id_product bigint not null,
	quantity int not null,
	unit_price decimal(5,2),
	primary key(id),
	foreign key(id_order_client) references orders_clients(id),
    foreign key(id_product) references products(id),
    index idx_order (id_order_client)
);
