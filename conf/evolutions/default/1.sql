# --- Created by Ebean DDL
# To stop Ebean DDL generation, remove this comment and start using Evolutions

# --- !Ups

create table shared_text (
  id                        bigint not null,
  constraint pk_shared_text primary key (id))
;

create table utterance (
  id                        bigint not null,
  text                      TEXT,
  constraint pk_utterance primary key (id))
;


create table shared_text_utterance (
  shared_text_id                 bigint not null,
  utterance_id                   bigint not null,
  constraint pk_shared_text_utterance primary key (shared_text_id, utterance_id))
;
create sequence shared_text_seq;

create sequence utterance_seq;




alter table shared_text_utterance add constraint fk_shared_text_utterance_shar_01 foreign key (shared_text_id) references shared_text (id) on delete restrict on update restrict;

alter table shared_text_utterance add constraint fk_shared_text_utterance_utte_02 foreign key (utterance_id) references utterance (id) on delete restrict on update restrict;

# --- !Downs

SET REFERENTIAL_INTEGRITY FALSE;

drop table if exists shared_text;

drop table if exists shared_text_utterance;

drop table if exists utterance;

SET REFERENTIAL_INTEGRITY TRUE;

drop sequence if exists shared_text_seq;

drop sequence if exists utterance_seq;

