-- Adiciona funcionalidade para gerenciar agenda real no backoffice
INSERT INTO FUNCIONALIDADE (ID, NOME, ROLE, FK_APLICACAO)
VALUES (21619, 'Gerenciar Agenda Real', 'BKO_21619', 24);

-- Atribui funcionalidade para usuários administradores
INSERT INTO CARGO_DEPART_FUNC (ID, FK_CARGO, FK_DEPARTAMENTO, FK_FUNCIONALIDADE)
VALUES (SEQ_CARGO_DEPART_FUNC.nextval, 50, 50, 21619);