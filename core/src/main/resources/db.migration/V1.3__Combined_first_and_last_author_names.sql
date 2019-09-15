alter table author change first_name name varchar(50) not null;

alter table author drop column last_name;

alter table author
    add is_generated tinyint(1) default 0 null;