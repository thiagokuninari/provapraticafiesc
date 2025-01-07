create sequence seq_sub_canal_hist start with 1 increment by 1;

create table sub_canal_historico (
    id integer not null,
    fk_sub_canal integer not null,
    codigo varchar(255) not null,
    nome varchar(255) not null,
    situacao varchar(1) not null,
    nova_checagem_cred_antiga varchar(1 char),
    nova_checagem_viab_antiga varchar(1 char),
    nova_checagem_cred_nova varchar(1 char),
    nova_checagem_viab_nova varchar(1 char),
    acao varchar(40) not null,
    data_acao timestamp not null,
    usuario_acao_id integer not null,
    usuario_acao_nome varchar(255) not null,
    primary key (id)
);