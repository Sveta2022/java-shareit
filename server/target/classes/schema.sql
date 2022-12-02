drop table if exists users cascade;
drop table if exists requests cascade;
drop table if exists items cascade;
drop table if exists items_requests cascade;
drop table if exists statuses cascade;
drop table if exists bookings cascade;
drop table if exists comments cascade;


create table if not exists users
(
    id    BIGINT generated by default as identity primary key,
    name  VARCHAR not null,
    email VARCHAR not null unique
);

create table if not exists requests
(
    id           BIGINT generated by default as identity primary key,
    description  VARCHAR(200),
    requestor_id BIGINT references users,
    created      TIMESTAMP WITHOUT TIME ZONE

);

create table if not exists items
(
    id           BIGINT generated by default as identity primary key,
    name         VARCHAR      not null,
    description  VARCHAR(100) not null,
    owner_id     BIGINT references users,
    is_available BOOLEAN,
    request_id   BIGINT references requests
);

create table if not exists items_requests
(
    item_id    BIGINT references items,
    request_id BIGINT references requests
);

create table if not exists statuses
(
    id   INTEGER primary key,
    name VARCHAR(20)
);

create table if not exists bookings
(
    id         BIGINT generated by default as identity primary key,
    start_date TIMESTAMP WITHOUT TIME ZONE,
    end_date   TIMESTAMP WITHOUT TIME ZONE,
    item_id    BIGINT references items,
    booker_id  BIGINT references users,
    status     VARCHAR
);

create table if not exists comments
(
    id        BIGINT generated by default as identity primary key,
    text      VARCHAR(200),
    item_id   BIGINT references ITEMS,
    author_id BIGINT references USERS,
    created   TIMESTAMP WITHOUT TIME ZONE

);



