--------------------
-- FUNCIONALIDADE --
--------------------
INSERT INTO FUNCIONALIDADE (ID, NOME, ROLE, FK_APLICACAO)
VALUES (15015, 'Cadastrar motivo de descarte', 'FDR_15015', 23);

----------------
-- PERMISSÕES --
----------------
-- Administrador (X-Brain) --
INSERT INTO CARGO_DEPART_FUNC (ID, FK_CARGO, FK_DEPARTAMENTO, FK_FUNCIONALIDADE)
VALUES (SEQ_CARGO_DEPART_FUNC.nextval, 50, 50, 15015);