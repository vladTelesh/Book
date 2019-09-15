create table if not exists user_goal
(
    id int auto_increment,
    user_id varchar(50) not null,
    book_count int not null,
    year int not null,
    constraint user_goal_id_uindex
        unique (id),
    constraint user_goal_user_id_fk
        foreign key (user_id) references user (id)
            on update cascade on delete cascade
);

alter table user_goal
    add primary key (id);

alter table user
    drop index user_confirmation_code_uindex,
    modify confirmation_code varchar(50) null,
    modify first_name varchar(50),
    modify last_name varchar(50),
    modify date_of_birth date,
    modify password_hash varchar(70),
    modify photo_link varchar(200) null,
    add column is_google tinyint(1) not null;
