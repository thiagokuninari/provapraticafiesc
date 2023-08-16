-- Inserir funcionalidade para Inserir planilha para direcionamento Inside Sales Pme por CEP
INSERT INTO FUNCIONALIDADE (ID, NOME, ROLE, FK_APLICACAO)
VALUES (3073, 'Importar Planilha Direcionamento Inside Sales Pme Por Cep', 'CTR_3073', 20);

-- Inserir funcionalidade Inserir planilha para direcionamento Inside Sales Pme por CEP para: Cargo ADMINSTRADOR / Departamento ADMINISTRADOR
INSERT INTO CARGO_DEPART_FUNC (ID, FK_CARGO, FK_DEPARTAMENTO, FK_FUNCIONALIDADE)
VALUES (SEQ_CARGO_DEPART_FUNC.nextval, 50, 50, 3073);

-- Inserir funcionalidade para consultar direcionamento Inside Sales Pme por CEP
INSERT INTO FUNCIONALIDADE (ID, NOME, ROLE, FK_APLICACAO)
VALUES (3074, 'Consultar Direcionamento Inside Sales Pme Por Cep', 'CTR_3074', 20);

-- Inserir funcionalidade consultar direcionamento Inside Sales Pme por CEP para: Cargo ADMINSTRADOR / Departamento ADMINISTRADOR
INSERT INTO CARGO_DEPART_FUNC (ID, FK_CARGO, FK_DEPARTAMENTO, FK_FUNCIONALIDADE)
VALUES (SEQ_CARGO_DEPART_FUNC.nextval, 50, 50, 3074);