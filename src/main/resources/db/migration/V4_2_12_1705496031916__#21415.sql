DECLARE
    QTD INT;
BEGIN
    SELECT COUNT(*) INTO QTD FROM FUNCIONALIDADE WHERE ID = 10021;

    IF QTD = 0 THEN

        -- Inserir funcionalidade para Visualizar Solicitação de Projetos
        INSERT INTO FUNCIONALIDADE (ID, NOME, ROLE, FK_APLICACAO) VALUES (10021, 'Visualizar Solicitação de Projetos', 'BRF_10021', 19);

        -- Inserir funcionalidade Visualizar Solicitação de Projetos para Cargo/Departamento/Nivel: MSO e XBRAIN
        FOR CD IN (SELECT C.ID AS CARGO_ID, D.ID AS DEPT_ID
                  FROM CARGO C
                  JOIN DEPARTAMENTO D ON C.FK_NIVEL = D.FK_NIVEL
                  WHERE C.FK_NIVEL = 2 OR C.FK_NIVEL = 4 OR C.FK_NIVEL = 20)
        LOOP
            INSERT INTO CARGO_DEPART_FUNC (ID, FK_CARGO, FK_DEPARTAMENTO, FK_FUNCIONALIDADE) VALUES (SEQ_CARGO_DEPART_FUNC.NEXTVAL, CD.CARGO_ID, CD.DEPT_ID, 10021);
        END LOOP;
    END IF;
END;
