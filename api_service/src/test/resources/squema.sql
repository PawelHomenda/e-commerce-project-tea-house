-- Schema compatible con H2 para tests
create table if not exists employees(
    id bigint not null auto_increment,
    first_name varchar(50) not null,
    last_name varchar(50) not null,
    salary decimal(8,2) not null,
    phone_number varchar(15) not null,
    email varchar(50) unique not null,
    oauth2_id varchar(255) unique not null,
    oauth2_provider varchar(50),
    primary key(id)
);

create index if not exists idx_oauth2_id on employees(oauth2_id);

create table if not exists clients(
    id bigint not null auto_increment,
    first_name varchar(50) not null,
    last_name varchar(50) not null,
    email varchar(50) unique not null,
    phone_number varchar(15) not null,
    address varchar(100),
    oauth2_id varchar(255) unique not null,
    oauth2_provider varchar(50),
    primary key(id)
);

create table if not exists providers(
    id bigint not null auto_increment,
    name varchar(100) not null,
    email varchar(50) unique not null,
    phone_number varchar(15) not null,
    address varchar(100),
    oauth2_id varchar(255) unique not null,
    oauth2_provider varchar(50),
    primary key(id)
);

create table if not exists products(
    id bigint not null auto_increment,
    name varchar(100) not null,
    description text,
    price decimal(10,2) not null,
    stock_quantity integer not null default 0,
    primary key(id)
);

create table if not exists inventory(
    id bigint not null auto_increment,
    id_product bigint not null,
    quantity integer not null default 0,
    last_updated timestamp default current_timestamp,
    primary key(id),
    foreign key(id_product) references products(id) on delete cascade
);

create table if not exists orders_clients(
    id bigint not null auto_increment,
    id_client bigint not null,
    id_employee bigint,
    order_date timestamp default current_timestamp,
    status varchar(50) default 'PENDING',
    total_amount decimal(10,2),
    primary key(id),
    foreign key(id_client) references clients(id),
    foreign key(id_employee) references employees(id)
);

create table if not exists details_order_client(
    id bigint not null auto_increment,
    id_order_client bigint not null,
    id_product bigint not null,
    quantity integer not null,
    unit_price decimal(10,2),
    primary key(id),
    foreign key(id_order_client) references orders_clients(id) on delete cascade,
    foreign key(id_product) references products(id)
);

create table if not exists orders_providers(
    id bigint not null auto_increment,
    id_provider bigint not null,
    id_employee bigint,
    order_date timestamp default current_timestamp,
    status varchar(50) default 'PENDING',
    total_amount decimal(10,2),
    primary key(id),
    foreign key(id_provider) references providers(id),
    foreign key(id_employee) references employees(id)
);

create table if not exists details_order_provider(
    id bigint not null auto_increment,
    id_order_provider bigint not null,
    id_product bigint not null,
    quantity integer not null,
    unit_price decimal(10,2),
    primary key(id),
    foreign key(id_order_provider) references orders_providers(id) on delete cascade,
    foreign key(id_product) references products(id)
);

create table if not exists invoices_clients(
    id bigint not null auto_increment,
    id_order_client bigint not null,
    invoice_date timestamp default current_timestamp,
    total_amount decimal(10,2) not null,
    status varchar(50) default 'PENDING',
    primary key(id),
    foreign key(id_order_client) references orders_clients(id) on delete cascade
);

create table if not exists invoices_providers(
    id bigint not null auto_increment,
    id_order_provider bigint not null,
    invoice_date timestamp default current_timestamp,
    total_amount decimal(10,2) not null,
    status varchar(50) default 'PENDING',
    primary key(id),
    foreign key(id_order_provider) references orders_providers(id) on delete cascade
);