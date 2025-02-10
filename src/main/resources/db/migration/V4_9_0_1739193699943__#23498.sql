--Remove dos cargos do Backoffice (Supervisor, Coordenador e Gerente) as permissões:
--Cadastrar Funcionalidade de Gerenciar Grupos
--Visualizar Funcionalidade de Gerenciar Grupos

DELETE FROM CARGO_DEPART_FUNC WHERE FK_FUNCIONALIDADE IN (16011, 16012) AND FK_CARGO <> 50;
