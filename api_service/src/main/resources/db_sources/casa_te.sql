/*
	GENEREACIÓN DE BASES DE DATOS - CASA DE TÉ

    /puente/mis_cosas/programacion/casa_te.sql
*/

system cls

drop database if exists casa_te;

create database if not exists casa_te;

use casa_te;

create table employees(
    id bigint not null auto_increment,
    first_name varchar(50) not null,
    last_name varchar(50) not null,
    salary float(8,2) not null,
    phone_number varchar(15) not null,
    email varchar(50) unique not null,
    primary key(id)
);

create table products(
    id bigint not null auto_increment,
    name varchar(50) not null,
    description text not null,
    category enum("DRINK","DESSERT") not null,
	price float(5,2) not null,
    measure_unit varchar(10),
    active boolean default 1,
	primary key(id)
);

create table providers(
	id bigint not null auto_increment,
	name varchar(50) not null,
	contact varchar(50) not null,
	phone_number varchar(10) not null,
	email varchar(50) unique not null,
	address varchar(100) not null,
	primary key(id)
);

create table inventory(
    id bigint not null auto_increment,
    id_product bigint not null,
    current_quantity int not null,
    minimum_quantity int not null,
    primary key(id),
    foreign key(id_product) references products(id),
    unique key unique_product (id_product)
);

create table orders_providers(
    id bigint not null auto_increment,
    id_provider bigint not null,
    id_employee bigint not null,
    order_date DATE not null,
    total DECIMAL(8,2),
    observations TEXT,
    primary key(id),
    foreign key(id_provider) references providers(id),
    foreign key(id_employee) references employees(id)
);

create table invoices_providers(
    id bigint not null auto_increment,
    id_order_provider bigint not null,
    invoice_number varchar(20) unique,
    invoice_date date,
    total float(6,2),
    payment_state enum("PAID","PENDENT"),
    payment_date date,
    primary key(id),
    foreign key(id_order_provider) references orders_providers(id)
);

create table orders_clients(
    id bigint not null auto_increment,
    id_employee bigint not null,
    order_date date not null,
    order_state enum("PENDENT","PREPARING","DELIVERED","CANCELED"),
    service_type enum("TAKEAWAY","TABLE","DELIVERY"),
    primary key(id),
    foreign key(id_employee) references employees(id)
);

create table invoices_clients(
    id bigint not null auto_increment,
    id_order_client bigint not null,
    invoice_number varchar(20)unique,
    invoice_date date,
    total float(6,2),
    payment_method enum("METALIC","CREDIT") default "METALIC",
    payment_date date,
    primary key(id),
    foreign key(id_order_client) references orders_clients(id)
);

create table details_order_provider(
    id bigint not null auto_increment,
    id_order_provider bigint not null,
    id_product bigint not null,
    quantity int not null,
	unit_price float(5,2),
    primary key(id),
    foreign key(id_order_provider) references orders_providers(id),
    foreign key(id_product) references products(id)
);

create table details_order_client(
	id bigint not null auto_increment,
	id_order_client bigint not null,
	id_product bigint not null,
	quantity int not null,
	unit_price float(5,2),
	primary key(id),
	foreign key(id_order_client) references orders_clients(id),
    foreign key(id_product) references products(id)
);

describe employees;

describe products;

describe providers;

describe inventory;

describe orders_clients;

describe invoices_providers;

describe invoices_clients;

describe details_order_provider;

describe details_order_client;

source casa_te_datos.sql;

source casa_te_consultas.sql;

source casa_te_vistas.sql;

source casa_te_disparadores.sql;
