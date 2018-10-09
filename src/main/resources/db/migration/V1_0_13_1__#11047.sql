create table feriado (
  id integer not null,
  data_cadastro timestamp not null,
  data_feriado timestamp not null,
  feriado_nacional varchar(255) not null,
  nome varchar(255) not null,
  fk_cidade integer,
  primary key (id)
);

alter table feriado add constraint FK_FERIADO_CIDADE foreign key (fk_cidade) references cidade;

create sequence seq_feriado start with 1 increment by 1;
