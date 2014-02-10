# --- Created by Ebean DDL
# To stop Ebean DDL generation, remove this comment and start using Evolutions

# --- !Ups

create table shared_transcript (
  id                        bigint not null,
  constraint pk_shared_transcript primary key (id))
;

create table utterance (
  id                        bigint not null,
  text                      TEXT,
  constraint pk_utterance primary key (id))
;


create table shared_transcript_utterance (
  shared_transcript_id           bigint not null,
  utterance_id                   bigint not null,
  constraint pk_shared_transcript_utterance primary key (shared_transcript_id, utterance_id))
;
create sequence shared_transcript_seq;

create sequence utterance_seq;




alter table shared_transcript_utterance add constraint fk_shared_transcript_utteranc_01 foreign key (shared_transcript_id) references shared_transcript (id) on delete restrict on update restrict;

alter table shared_transcript_utterance add constraint fk_shared_transcript_utteranc_02 foreign key (utterance_id) references utterance (id) on delete restrict on update restrict;

# --- !Downs

SET REFERENTIAL_INTEGRITY FALSE;

drop table if exists shared_transcript;

drop table if exists shared_transcript_utterance;

drop table if exists utterance;

SET REFERENTIAL_INTEGRITY TRUE;

drop sequence if exists shared_transcript_seq;

drop sequence if exists utterance_seq;

