--liquibase formatted sql

--changeset Sauchanka:1
create table orders
(
    id          BIGSERIAL primary key,
    user_id     bigint         not null,
    status      varchar(255)   not null,
    total_price numeric(10, 2) not null,
    deleted     boolean        not null,
    created_at  timestamp,
    updated_at  timestamp

);
create table items
(
    id         BIGSERIAL primary key,
    name       varchar(255)   not null,
    price      numeric(10, 2) not null,
    created_at timestamp,
    updated_at timestamp
);
create table order_items
(
    id         BIGSERIAL primary key,
    order_id   bigint not null,
    item_id    bigint not null,
    quantity   int    not null,
    created_at timestamp,
    updated_at timestamp,
    constraint order_items_order_id_fk foreign key(order_id) references orders (id) on DELETE cascade,
    constraint order_items_item_id_fk foreign key (item_id) references items (id) on DELETE cascade
);
create index order_items_order_id_index
    on order_items (order_id);
create index order_items_items_id_index
    on order_items (item_id);
