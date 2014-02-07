# --- Created by Ebean DDL
# To stop Ebean DDL generation, remove this comment and start using Evolutions

# --- !Ups

create table chatroom (
  id                        bigint not null,
  chat_log                  TEXT,
  constraint pk_chatroom primary key (id))
;

create sequence chatroom_seq;




# --- !Downs

SET REFERENTIAL_INTEGRITY FALSE;

drop table if exists chatroom;

SET REFERENTIAL_INTEGRITY TRUE;

drop sequence if exists chatroom_seq;

