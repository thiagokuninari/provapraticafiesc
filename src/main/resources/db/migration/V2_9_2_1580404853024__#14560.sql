-- Nível novo de Geradores de Leads --
INSERT INTO NIVEL(ID, CODIGO, EXIBIR_CAD_USUARIO, NOME, SITUACAO) VALUES(17, 'GERADOR_LEADS', 'F', 'Gerador de Leads', 'A');

-- Cargo novo de Geradores de Leads --
INSERT INTO CARGO (ID, CODIGO, NOME, SITUACAO, FK_NIVEL, QUANTIDADE_SUPERIOR) VALUES (96, 'GERADOR_LEADS', 'Gerador de Leads', 'A', 17, 50);

-- Departamento novo de Geradores de Leads --
INSERT INTO DEPARTAMENTO (ID, CODIGO, NOME, SITUACAO, FK_NIVEL) VALUES (68, 'GERADOR_LEADS', 'Gerador de Leads', 'A', 17);