--------------------
-- FUNCIONALIDADE --
--------------------
INSERT INTO FUNCIONALIDADE (ID, NOME, ROLE, FK_APLICACAO)
VALUES (21011, 'Ajuste de leads do colaborador', 'POL_21011', 2);

----------------
-- PERMISSÕES --
----------------
-- Administrador (X-Brain) --
INSERT INTO CARGO_DEPART_FUNC (ID, FK_CARGO, FK_DEPARTAMENTO, FK_FUNCIONALIDADE)
VALUES (SEQ_CARGO_DEPART_FUNC.nextval, 50, 50, 21011);
