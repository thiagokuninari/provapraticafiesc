create table solicitacao_ramal_usuario (fk_usuario integer not null,
					                    fk_solicitacao_ramal integer not null);

alter table solicitacao_ramal_usuario add constraint FK_SOLIC_RM_USUARIO_USUARIO foreign key (fk_usuario) references usuario;
alter table solicitacao_ramal_usuario add constraint FK_SOLIC_RM_USUARIO_SOLIC_RM foreign key (fk_solicitacao_ramal) references solicitacao_ramal;