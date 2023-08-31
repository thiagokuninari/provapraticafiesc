-- Inserir funcionalidade para consultar Indicações Inside Sales Pme
INSERT INTO FUNCIONALIDADE (ID, NOME, ROLE, FK_APLICACAO)
VALUES (3071, 'Consultar Indicações Inside Sales Pme', 'VDS_3071', 3);

-- Inserir funcionalidade Consultar Indicações Inside Sales Pme para: Cargo ADMINSTRADOR / Departamento ADMINISTRADOR
INSERT INTO CARGO_DEPART_FUNC (ID, FK_CARGO, FK_DEPARTAMENTO, FK_FUNCIONALIDADE)
VALUES (SEQ_CARGO_DEPART_FUNC.nextval, 50, 50, 3071);

-- Inserir funcionalidade Consultar Indicações Inside Sales Pme para: Cargo MSO_CONSULTOR / Departamento COMERCIAL
INSERT INTO CARGO_DEPART_FUNC (ID, FK_CARGO, FK_DEPARTAMENTO, FK_FUNCIONALIDADE)
VALUES (SEQ_CARGO_DEPART_FUNC.nextval, 22, 21, 3071);