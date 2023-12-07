DECLARE
    QTD INT;
BEGIN
    SELECT COUNT(*) INTO QTD FROM FUNCIONALIDADE WHERE ID IN (10011, 10013);

    IF QTD = 0 THEN

        -- Inserir nivel Briefing para Solicitação de Projetos
        INSERT INTO NIVEL (ID, CODIGO, EXIBIR_CAD_USUARIO, NOME, SITUACAO) VALUES (20, 'BRIEFING', 'V', 'Briefing', 'A');

        -- Inserir funcionalidade para Cadastrar Solicitação de Projetos
        INSERT INTO FUNCIONALIDADE (ID, NOME, ROLE, FK_APLICACAO) VALUES (10011, 'Cadastrar Solicitação de Projetos', 'BRF_10011', 19);
        -- Inserir funcionalidade para Tratar Solicitação de Projetos
        INSERT INTO FUNCIONALIDADE (ID, NOME, ROLE, FK_APLICACAO) VALUES (10013, 'Tratar Solicitação de Projetos', 'BRF_10013', 19);

        -- Inserir Cargos para Solicitação de Projetos
        INSERT INTO CARGO (ID, CODIGO, NOME, SITUACAO, FK_NIVEL) VALUES (505, 'APROVADOR_PROJETOS_1', 'Aprovador de Projetos 1', 'A', 20);
        INSERT INTO CARGO (ID, CODIGO, NOME, SITUACAO, FK_NIVEL) VALUES (506, 'APROVADOR_PROJETOS_2', 'Aprovador de Projetos 2', 'A', 20);
        INSERT INTO CARGO (ID, CODIGO, NOME, SITUACAO, FK_NIVEL) VALUES (507, 'APROVADOR_PROJETOS_3', 'Aprovador de Projetos 3', 'A', 20);
        INSERT INTO CARGO (ID, CODIGO, NOME, SITUACAO, FK_NIVEL) VALUES (508, 'APROVADOR_PROJETOS_4', 'Aprovador de Projetos 4', 'A', 20);

        -- Inserir Departamento Briefing para Solicitação de Projetos
        INSERT INTO DEPARTAMENTO (ID, CODIGO, NOME, SITUACAO, FK_NIVEL) VALUES (72, 'BRIEFING', 'Briefing', 'A', 20);

        -- Inserir funcionalidades Cadastrar/Tratar Solicitação de Projetos para Cargo/Departamento/Nivel: MSO
        FOR CD IN (SELECT C.ID AS CARGO_ID, D.ID AS DEPT_ID
                  FROM CARGO C
                  JOIN DEPARTAMENTO D ON C.FK_NIVEL = D.FK_NIVEL
                  WHERE C.FK_NIVEL = 2 OR C.FK_NIVEL = 20)
        LOOP
            INSERT INTO CARGO_DEPART_FUNC (ID, FK_CARGO, FK_DEPARTAMENTO, FK_FUNCIONALIDADE) VALUES (SEQ_CARGO_DEPART_FUNC.NEXTVAL, CD.CARGO_ID, CD.DEPT_ID, 10011);
            INSERT INTO CARGO_DEPART_FUNC (ID, FK_CARGO, FK_DEPARTAMENTO, FK_FUNCIONALIDADE) VALUES (SEQ_CARGO_DEPART_FUNC.NEXTVAL, CD.CARGO_ID, CD.DEPT_ID, 10013);
        END LOOP;
    END IF;
END;
