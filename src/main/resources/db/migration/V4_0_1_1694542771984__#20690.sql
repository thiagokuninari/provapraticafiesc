-- Inserir funcionalidade Consultar direcionamento Inside Sales Pme por CEP para: Cargo MSO_CONSULTOR / Departamento COMERCIAL
INSERT INTO CARGO_DEPART_FUNC (ID, FK_CARGO, FK_DEPARTAMENTO, FK_FUNCIONALIDADE)
VALUES (SEQ_CARGO_DEPART_FUNC.nextval, 22, 21, 3074);

-- Inserir funcionalidade Consultar planilha erro direcionamento Inside Sales Pme por CEP
INSERT INTO FUNCIONALIDADE (ID, NOME, ROLE, FK_APLICACAO)
VALUES (3075, 'Consultar Planilha Erro Direcionamento Inside Sales Pme Por Cep', 'CTR_3075', 20);

-- Inserir funcionalidade Consultar planilha erro direcionamento Inside Sales Pme por CEP para: Cargo ADMINISTRADOR / Departamento ADMINISTRADOR
INSERT INTO CARGO_DEPART_FUNC (ID, FK_CARGO, FK_DEPARTAMENTO, FK_FUNCIONALIDADE)
VALUES (SEQ_CARGO_DEPART_FUNC.nextval, 50, 50, 3075);