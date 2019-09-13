create table author
(
    id             varchar(50)   not null,
    first_name     varchar(20)   not null,
    last_name      varchar(30)   null,
    date_of_birth  date          null,
    date_of_death  date          null,
    place_of_birth varchar(70)   not null,
    genre          varchar(20)   not null,
    photo_link     varchar(200)  null,
    biography      varchar(2000) null,
    constraint author_id_uindex
        unique (id)
);

alter table author
    add primary key (id);

alter table book
    rename column author_id to author_id,
    add constraint comment_user_id_fk
        foreign key (author_id) references author (id);