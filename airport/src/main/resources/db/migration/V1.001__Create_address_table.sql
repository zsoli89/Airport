create sequence if not exists address_seq start with 1 increment by 50;
create sequence if not exists airport_seq start with 1 increment by 50;
create sequence if not exists flight_seq start with 1 increment by 50;
create table address (id int8 not null, city varchar(255), country varchar(255), street varchar(255), zip varchar(255), primary key (id));
create table airport (address_id bigint, id bigint not null, name varchar(20), iata varchar(255), primary key (id));
create table flight (delay integer, id bigint not null, landing_id bigint, takeoff_id bigint, takeoff_time timestamp(6), flight_number varchar(255), primary key (id));

alter table if exists airport add column if not exists address_id int8;
alter table if exists airport add constraint FK_airport_to_address foreign key (address_id) references address;

alter table if exists flight add constraint FKlo8um1qhyvn4ec5tt90u49g9g foreign key (landing_id) references airport;
alter table if exists flight add constraint FKnvldg0a2nu9ivpbmebljf4g1i foreign key (takeoff_id) references airport;
