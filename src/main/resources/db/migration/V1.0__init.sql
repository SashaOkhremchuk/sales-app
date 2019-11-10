create table client
(
    id           bigint       not null primary key
        auto_increment,
    first_name   varchar(255) not null,
    last_name    varchar(255) not null,
    email        varchar(255) not null,
    phone_number varchar(15)  null,
    address      varchar(255) not null,
    constraint UK_email unique (email),
    constraint UK_phone_number unique (phone_number)
);

create table item
(
    id          bigint       not null primary key
        auto_increment,
    name        varchar(255) not null,
    price       integer      not null,
    description varchar(255) null,
    status      varchar(8)   not null,
    seller_id   bigint       not null,
    constraint client_id
        foreign key (seller_id) references client (id)
);

create table item_order
(
    id             bigint        not null primary key
        auto_increment,
    payment_method varchar(25)   not null,
    date           date          not null,
    client_id      bigint        not null,
    item_id        bigint unique not null,
    constraint UK_client_id
        foreign key (client_id) references client (id),
    constraint UK_item_id
        foreign key (item_id) references item (id)
);