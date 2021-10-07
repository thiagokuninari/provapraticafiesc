--------------------
-- FUNCIONALIDADE --
--------------------
INSERT INTO FUNCIONALIDADE (ID, NOME, ROLE, FK_APLICACAO) VALUES (19001, 'Gerenciar Formulário de Solicitação Cliente PAP', 'PAP_19001', 27);

----------------
-- PERMISSÕES --
----------------
-- Administrador (X-Brain) --
INSERT INTO CARGO_DEPART_FUNC (ID, FK_CARGO, FK_DEPARTAMENTO, FK_FUNCIONALIDADE) VALUES (SEQ_CARGO_DEPART_FUNC.nextval, 50, 50, 19001);
-- Nível: MSO -- Cargo: Consultor -- Departamento: Comercial --
INSERT INTO CARGO_DEPART_FUNC (ID, FK_CARGO, FK_DEPARTAMENTO, FK_FUNCIONALIDADE) VALUES (SEQ_CARGO_DEPART_FUNC.nextval, 22, 21, 19001);
