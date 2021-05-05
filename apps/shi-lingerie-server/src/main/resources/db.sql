create sequence users_id_seq;

alter sequence users_id_seq owner to postgres;

create table roles
(
    id bigserial not null
        constraint roles_pk
            primary key,
    role_name varchar
);

alter table roles owner to postgres;

create table users
(
    username varchar(100) not null,
    password varchar(100) not null,
    fullname varchar(100) not null,
    id bigserial not null
        constraint users_pk
            primary key,
    is_activated boolean,
    role_id bigint
        constraint users_role_id_fk
            references roles,
    is_signed_out boolean
);

alter table users owner to postgres;

create unique index users_username_uindex
	on users (username);

create unique index roles_role_name_uindex
	on roles (role_name);

