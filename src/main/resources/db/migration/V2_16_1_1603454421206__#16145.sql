---------------
-- APLICAÇÃO --
---------------

INSERT INTO APLICACAO (ID, NOME, CODIGO) VALUES (26, 'CHATBOT', 'CHB');

--------------------
-- FUNCIONALIDADE --
--------------------

INSERT INTO FUNCIONALIDADE (ID, NOME, ROLE, FK_APLICACAO) VALUES (18000, 'ChatBot - Relatório', 'CHB_18000', 26);

----------------
-- PERMISSÕES --
----------------

-- Nível MSO -- Cargo Consultor -- Departamento Comercial
INSERT INTO CARGO_DEPART_FUNC (ID, FK_CARGO, FK_DEPARTAMENTO, FK_FUNCIONALIDADE) VALUES (SEQ_CARGO_DEPART_FUNC.nextval, 22, 21, 18000);
-- Nível X-Brain -- Cargo Administrador -- Departamento Administrador
INSERT INTO CARGO_DEPART_FUNC (ID, FK_CARGO, FK_DEPARTAMENTO, FK_FUNCIONALIDADE) VALUES (SEQ_CARGO_DEPART_FUNC.nextval, 50, 50, 18000);
