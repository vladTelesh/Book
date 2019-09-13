create table book
(
    id                        varchar(50) not null,
    title                     varchar(45) not null,
    author                    varchar(45) not null,
    additional_authors        varchar(45) null,
    isbn                      varchar(15) not null,
    isbn13                    varchar(20) not null,
    average_rating            double      null,
    publisher                 varchar(45) null,
    binding                   varchar(45) null,
    pages_number              int         not null,
    publication_year          int         null,
    original_publication_year int         null,
    primary key (id),
    constraint book_isbn13_uindex
        unique (isbn13),
    constraint book_isbn_uindex
        unique (isbn),
    constraint id_UNIQUE
        unique (id)
);

create table image
(
    id   varchar(50)  not null,
    link varchar(200) not null,
    user_id varchar(50)          not null,
    is_main tinyint(1) default 0 null,
    primary key (id),
    constraint photo_id_uindex
        unique (id),
    constraint photo_link_uindex
        unique (link)
);

create table user
(
    id            varchar(50) not null,
    username      varchar(50) not null,
    password_hash varchar(50) not null,
    first_name    varchar(50) not null,
    last_name     varchar(50) not null,
    user_status   varchar(30) not null,
    date_of_birth date,
    photo_link    varchar(50) null,
    primary key (id),
    constraint user_id_uindex
        unique (id),
    constraint user_username_uindex
        unique (username)
);

create table user_book
(
    id         varchar(50) not null,
    book_id    varchar(50) not null,
    user_id    varchar(50) not null,
    my_rating  double      null,
    date_read  varchar(10) null,
    date_added varchar(10) null,
    read_count int         null,
    primary key (id),
    constraint id_UNIQUE
        unique (id),
    constraint user_book_book_id_fk
        foreign key (book_id) references book (id)
            on update cascade on delete cascade
);
