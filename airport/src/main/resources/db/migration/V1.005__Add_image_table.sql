create table image (id int8 not null, data bytea, file_name varchar(255), airport_id int8, primary key (id));
create table image_aud (id int8 not null, rev int4 not null, revtype int2, data bytea, file_name varchar(255), primary key (id, rev));
alter table if exists airport_image_aud add constraint FK57vbhpo86af1sgnb6wifqrsc foreign key (rev) references revinfo;
alter table if exists image add constraint FKr3v11sq0fbqjqhb5xenrcob3c foreign key (airport_id) references airport;
alter table if exists image_aud add constraint FKetc5y2t13bkdk5yuj4eswagd4 foreign key (rev) references revinfo;
