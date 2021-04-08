------------------------
---- FUNCIONALIDADE ----
------------------------

INSERT INTO FUNCIONALIDADE (ID, NOME, ROLE, FK_APLICACAO) VALUES (2043, 'Visualizar histórico de alterações das discadoras', 'CRT_2043', 20);

--------------------
---- PERMISSÕES ----
--------------------

-- X-Brain Administrador
INSERT INTO CARGO_DEPART_FUNC (ID, FK_CARGO, FK_DEPARTAMENTO, FK_FUNCIONALIDADE) VALUES (SEQ_CARGO_DEPART_FUNC.nextval, 50, 50, 2043);
