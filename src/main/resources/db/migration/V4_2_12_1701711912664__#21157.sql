-- Inserir funcionalidade para exportar Indicações Inside Sales Pme
INSERT INTO FUNCIONALIDADE (ID, NOME, ROLE, FK_APLICACAO)
VALUES (21017, 'Exportar Indicações Inside Sales Pme', 'VDS_21017', 3);

-- Inserir funcionalidade Exportar Indicações Inside Sales Pme para: Cargo ADMINSTRADOR / Departamento ADMINISTRADOR
INSERT INTO CARGO_DEPART_FUNC (ID, FK_CARGO, FK_DEPARTAMENTO, FK_FUNCIONALIDADE)
VALUES (SEQ_CARGO_DEPART_FUNC.nextval, 50, 50, 21017);

-- Inserir funcionalidade Exportar Indicações Inside Sales Pme para: Cargo MSO_CONSULTOR / Departamento COMERCIAL
INSERT INTO CARGO_DEPART_FUNC (ID, FK_CARGO, FK_DEPARTAMENTO, FK_FUNCIONALIDADE)
VALUES (SEQ_CARGO_DEPART_FUNC.nextval, 22, 21, 21017);

-- INSERIR PERMISSAO ESPECIAL PARA EXPORTAR INDICACAO INSIDE SALES PME PARA USUARIOS COM SUBCANAL INSIDE SALES PME
INSERT INTO permissao_especial (id, data_baixa, data_cadastro, fk_funcionalidade, fk_usuario, fk_usuario_baixa, fk_usuario_cadastro)
SELECT seq_permissao_especial.NEXTVAL, NULL, sysdate, 21017, fk_usuario, NULL, 1
FROM (
    SELECT FK_USUARIO
    FROM USUARIO_SUBCANAL US
    INNER JOIN USUARIO U ON US.FK_USUARIO = U.ID
    WHERE US.FK_SUBCANAL = 4 AND U.FK_CARGO <> 8
);