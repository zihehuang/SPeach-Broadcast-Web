# --- Created by Ebean DDL
# To stop Ebean DDL generation, remove this comment and start using Evolutions

# --- !Ups

create table shared_text (
  id                        bigint not null,
  constraint pk_shared_text primary key (id))
;

create sequence shared_text_seq;




# --- !Downs

SET REFERENTIAL_INTEGRITY FALSE;

drop table if exists shared_text;

SET REFERENTIAL_INTEGRITY TRUE;

drop sequence if exists shared_text_seq;

