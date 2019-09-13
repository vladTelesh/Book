alter table author
    drop column last_name,
    rename column name to name,
    modify name varchar(40),
    add column is_generated tinyint(1) default 0;