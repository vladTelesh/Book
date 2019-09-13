alter table book
    add column description varchar(2000),
    add column image_link varchar(200);

alter table user
    add column is_confirm tinyint(1) not null,
    add column confirmation_code varchar(50) not null,
    add constraint user_confirmation_code_uindex
        unique (confirmation_code);

alter table user_book
    add column user_comment varchar(200) not null,
    modify column date_read date,
    modify column date_added date;

create table comment_has_like
(
    id         int auto_increment,
    comment_id varchar(50) not null,
    user_id    varchar(50) not null,
    constraint comment_has_like_id_uindex
        unique (id)
);

alter table comment_has_like
    add primary key (id);

create table comment_like
(
    id         int auto_increment,
    comment_id varchar(50) not null,
    user_id    varchar(50) not null,
    constraint comment_like_id_uindex
        unique (id)
);

alter table comment_like
    add primary key (id);

create table comment
(
    id           varchar(50)   not null,
    user_id      varchar(50)   not null,
    date_added   datetime      not null,
    book_id      varchar(50)   not null,
    comment_text varchar(2000) not null,
    constraint comment_id_uindex
        unique (id),
    constraint comment_user_id_fk
        foreign key (user_id) references user (id)
);

alter table comment
    add primary key (id);