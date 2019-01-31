create table solicitacao_ramal (id integer not null,
                                agente_autorizado_cnpj varchar(255),
                                agente_autorizado_id integer not null,
                                agente_autorizado_nome varchar(255) not null,
                                data_cadastro timestamp not null,
                                email_ti varchar(255),
                                enviou_email_expiracao varchar(255) not null,
                                melhor_data_implantacao timestamp not null,
                                melhor_horario_implantacao timestamp not null,
                                quantidade_ramais integer not null,
                                situacao varchar(255),
                                telefone_ti varchar(255),
                                fk_usuario integer, primary key (id));

alter table solicitacao_ramal add constraint FK_SOLICITACAO_RAMAL_USUARIO foreign key (fk_usuario) references usuario;

create sequence seq_solicitacao_ramal start with 1 increment by 1;


create table solicitacao_ramal_historico (id integer not null,
                                          comentario varchar(255),
                                          data_cadastro timestamp not null,
                                          situacao varchar(255) not null,
                                          fk_solicitacao_ramal integer,
                                          fk_usuario integer, primary key (id));


alter table solicitacao_ramal_historico add constraint FK_SOLIC_RM_HIST_SOLIC_RAMAL foreign key (fk_solicitacao_ramal) references solicitacao_ramal;
alter table solicitacao_ramal_historico add constraint FK_SOLIC_RM_HIST_USUARIO foreign key (fk_usuario) references usuario;

create sequence seq_solic_rm_hist start with 1 increment by 1;

