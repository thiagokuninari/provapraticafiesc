--- FUNCIONALIDADE ---
INSERT INTO FUNCIONALIDADE (ID, NOME, ROLE, FK_APLICACAO)
VALUES (247, 'Download e listagem de contratos do Portal Antigo', 'POL_247', 2);

--- PERMISSÕES ---

-- Nível: X-Brain || Cargo: Administrador || Departamento: Administrador
INSERT INTO CARGO_DEPART_FUNC (ID, FK_CARGO, FK_DEPARTAMENTO, FK_FUNCIONALIDADE)
VALUES (SEQ_CARGO_DEPART_FUNC.nextval, 50, 50, 247);

-- Nível: MSO || Cargo: Consultor || Departamento: Comercial
INSERT INTO CARGO_DEPART_FUNC (ID, FK_CARGO, FK_DEPARTAMENTO, FK_FUNCIONALIDADE)
VALUES (SEQ_CARGO_DEPART_FUNC.nextval, 22, 21, 247);

-- Nível: Operação || Departamento: Comercial || Canal: Agente Autorizado || Cargo: Gerente
INSERT INTO CARGO_DEPART_FUNC (ID, FK_CARGO, FK_DEPARTAMENTO, FK_FUNCIONALIDADE)
VALUES (SEQ_CARGO_DEPART_FUNC.nextval, 7, 3, 247);

-- Nível: Operação || Departamento: Comercial || Canal: Agente Autorizado || Cargo: Coordenador
INSERT INTO CARGO_DEPART_FUNC (ID, FK_CARGO, FK_DEPARTAMENTO, FK_FUNCIONALIDADE)
VALUES (SEQ_CARGO_DEPART_FUNC.nextval, 4, 3, 247);

-- Nível: Operação || Departamento: Comercial || Canal: Agente Autorizado || Cargo: Executivo
INSERT INTO CARGO_DEPART_FUNC (ID, FK_CARGO, FK_DEPARTAMENTO, FK_FUNCIONALIDADE)
VALUES (SEQ_CARGO_DEPART_FUNC.nextval, 5, 3, 247);

-- Nível: Operação || Departamento: Comercial || Canal: Agente Autorizado || Cargo: Assistente
INSERT INTO CARGO_DEPART_FUNC (ID, FK_CARGO, FK_DEPARTAMENTO, FK_FUNCIONALIDADE)
VALUES (SEQ_CARGO_DEPART_FUNC.nextval, 2, 3, 247);
