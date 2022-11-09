create table if not exists users
(
    id    BIGINT generated always as identity primary key,
    name  VARCHAR not null,
    email VARCHAR not null unique
);

create table if not exists requests
(
    id           BIGINT generated always as identity primary key,
    description  VARCHAR(100),
    requestor_id BIGINT references users on delete cascade

);

create table if not exists items
(
    id           BIGINT generated always as identity primary key,
    name         VARCHAR      not null,
    description  VARCHAR(100) not null,
    owner_id     VARCHAR references users on delete cascade,
    is_available BOOLEAN
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
    id         BIGINT generated always as identity primary key,
    start_date TIMESTAMP WITHOUT TIME ZONE,
    end_date   TIMESTAMP WITHOUT TIME ZONE,
    item_id    BIGINT references items on delete cascade,
    booker_id  BIGINT references users on delete cascade,
    status     VARCHAR
);

create table if not exists requests
(
    id           BIGINT generated always as identity primary key,
    description  VARCHAR(100),
    requestor_id BIGINT references users on delete cascade

);

create table if not exists comments
(
    id        BIGINT generated always as identity primary key,
    text      VARCHAR(200),
    item_id   BIGINT references ITEMS on delete cascade,
    author_id BIGINT references USERS on delete cascade,
    created   TIMESTAMP WITHOUT TIME ZONE

);



