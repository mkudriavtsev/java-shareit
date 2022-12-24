create table if not exists users
(
    id    BIGINT auto_increment,
    name  CHARACTER VARYING(255) not null,
    email CHARACTER VARYING(512) not null
        constraint UQ_USER_EMAIL unique,
    constraint PK_USERS primary key (id)
);

create table if not exists items
(
    id          BIGINT auto_increment,
    name        CHARACTER VARYING(255) not null,
    description CHARACTER LARGE OBJECT,
    available   BOOLEAN                not null,
    owner_id    BIGINT references USERS on delete cascade,
    constraint ITEMS_PK primary key (id)
);

create table if not exists bookings
(
    id         BIGINT auto_increment,
    start_date TIMESTAMP,
    end_date   TIMESTAMP,
    item_id    BIGINT references ITEMS on delete cascade,
    booker_id  BIGINT references USERS on delete cascade,
    status     CHARACTER VARYING(32),
    constraint PK_BOOKINGS primary key (id)
);

create table if not exists comments
(
    id        BIGINT auto_increment,
    text      CHARACTER LARGE OBJECT,
    item_id   BIGINT references ITEMS on delete cascade,
    author_id BIGINT references USERS on delete cascade,
    created   TIMESTAMP,
    constraint PK_COMMENTS primary key (id)
);

create table if not exists requests
(
    id           BIGINT auto_increment,
    description  CHARACTER LARGE OBJECT,
    requester_id BIGINT references USERS on delete cascade,
    created      TIMESTAMP,
    constraint PK_REQUESTS primary key (id)
);
