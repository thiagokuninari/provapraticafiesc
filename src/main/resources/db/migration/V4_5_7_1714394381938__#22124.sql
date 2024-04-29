-- Adiciona Permissão Distribuição Manual Indicação Externa
INSERT INTO FUNCIONALIDADE (ID, NOME, ROLE, FK_APLICACAO)
VALUES (22124, 'Distribuição Manual Indicação Externa', 'IND_22124', 22);

-- Atribui Permissão para Usuários XBRAIN
INSERT INTO CARGO_DEPART_FUNC (ID, FK_CARGO, FK_DEPARTAMENTO, FK_FUNCIONALIDADE)
VALUES (SEQ_CARGO_DEPART_FUNC.nextval, 50, 50, 22124);
