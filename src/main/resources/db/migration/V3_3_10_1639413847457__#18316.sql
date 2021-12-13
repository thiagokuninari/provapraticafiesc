BEGIN
    EXECUTE IMMEDIATE 'ALTER TABLE CIDADE_DBM MODIFY(CODIGO_CIDADE_DBM NULL)';
END;

/

-- Adiciona COARI/ES em CIDADE_DBM
DECLARE
    QTDE INT;

BEGIN
    SELECT COUNT(*) INTO QTDE
        FROM CIDADE_DBM cd
        INNER JOIN CIDADE c ON c.ID = cd.FK_CIDADE
        INNER JOIN UF u ON u.ID = c.FK_UF
        WHERE c.NOME = 'COARI'
            AND u.UF = 'AM';

    IF (QTDE = 0) THEN
        EXECUTE IMMEDIATE 'INSERT INTO CIDADE_DBM(ID, FK_CIDADE, CODIGO_CIDADE_DBM)
                           VALUES(SEQ_CIDADE_DBM.NEXTVAL,
                                  (SELECT c.ID
                                    FROM CIDADE c
                                    INNER JOIN UF u ON u.ID = c.FK_UF
                                    WHERE c.NOME = ''COARI''
                                        AND u.UF = ''AM''),
                                    NULL)';
    END IF;
END;

/

-- Adiciona PICOS/PI em CIDADE_DBM
DECLARE
    QTDE INT;

BEGIN
    SELECT COUNT(*) INTO QTDE
        FROM CIDADE_DBM cd
        INNER JOIN CIDADE c ON c.ID = cd.FK_CIDADE
        INNER JOIN UF u ON u.ID = c.FK_UF
        WHERE c.NOME = 'PICOS'
            AND u.UF = 'PI';

    IF (QTDE = 0) THEN
        EXECUTE IMMEDIATE 'INSERT INTO CIDADE_DBM(ID, FK_CIDADE, CODIGO_CIDADE_DBM)
                           VALUES(SEQ_CIDADE_DBM.NEXTVAL,
                                  (SELECT c.ID
                                    FROM CIDADE c
                                    INNER JOIN UF u ON u.ID = c.FK_UF
                                    WHERE c.NOME = ''PICOS''
                                        AND u.UF = ''PI''),
                                    NULL)';
    END IF;
END;

/

-- Adiciona SANTAREM/PA em CIDADE_DBM
DECLARE
    QTDE INT;

BEGIN
    SELECT COUNT(*) INTO QTDE
        FROM CIDADE_DBM cd
        INNER JOIN CIDADE c ON c.ID = cd.FK_CIDADE
        INNER JOIN UF u ON u.ID = c.FK_UF
        WHERE c.NOME = 'SANTAREM'
            AND u.UF = 'PA';

    IF (QTDE = 0) THEN
        EXECUTE IMMEDIATE 'INSERT INTO CIDADE_DBM(ID, FK_CIDADE, CODIGO_CIDADE_DBM)
                           VALUES(SEQ_CIDADE_DBM.NEXTVAL,
                                  (SELECT c.ID
                                    FROM CIDADE c
                                    INNER JOIN UF u ON u.ID = c.FK_UF
                                    WHERE c.NOME = ''SANTAREM''
                                        AND u.UF = ''PA''),
                                    NULL)';
    END IF;
END;

/

-- Adiciona DDD em CIDADE_DBM
DECLARE
    QTDE INT;

BEGIN
    SELECT COUNT(*) INTO QTDE
        FROM USER_TAB_COLS
        WHERE TABLE_NAME = 'CIDADE_DBM'
            AND COLUMN_NAME = 'DDD';

    IF (QTDE = 0) THEN
        EXECUTE IMMEDIATE 'ALTER TABLE CIDADE_DBM ADD DDD NUMBER(2,0)';
    END IF;
END;

/

-- Popular DDD - UF SP
BEGIN
    UPDATE CIDADE_DBM SET DDD = 11 WHERE FK_CIDADE = (
        SELECT c.ID
            FROM CIDADE c
            INNER JOIN UF u ON u.ID = c.FK_UF
            WHERE c.NOME = 'SAO PAULO'
                AND u.UF = 'SP'
    );

    UPDATE CIDADE_DBM SET DDD = 12 WHERE FK_CIDADE = (
        SELECT c.ID
            FROM CIDADE c
            INNER JOIN UF u ON u.ID = c.FK_UF
            WHERE c.NOME = 'SAO JOSE DOS CAMPOS'
                AND u.UF = 'SP'
    );

    UPDATE CIDADE_DBM SET DDD = 13 WHERE FK_CIDADE = (
        SELECT c.ID
            FROM CIDADE c
            INNER JOIN UF u ON u.ID = c.FK_UF
            WHERE c.NOME = 'SANTOS'
                AND u.UF = 'SP'
    ) AND CODIGO_CIDADE_DBM = 4;

    UPDATE CIDADE_DBM SET DDD = 14 WHERE FK_CIDADE = (
        SELECT c.ID
            FROM CIDADE c
            INNER JOIN UF u ON u.ID = c.FK_UF
            WHERE c.NOME = 'BAURU'
                AND u.UF = 'SP'
    );

    UPDATE CIDADE_DBM SET DDD = 15 WHERE FK_CIDADE = (
        SELECT c.ID
            FROM CIDADE c
            INNER JOIN UF u ON u.ID = c.FK_UF
            WHERE c.NOME = 'SOROCABA'
                AND u.UF = 'SP'
    );

    UPDATE CIDADE_DBM SET DDD = 16 WHERE FK_CIDADE = (
        SELECT c.ID
            FROM CIDADE c
            INNER JOIN UF u ON u.ID = c.FK_UF
            WHERE c.NOME = 'RIBEIRAO PRETO'
                AND u.UF = 'SP'
    );

    UPDATE CIDADE_DBM SET DDD = 17 WHERE FK_CIDADE = (
        SELECT c.ID
            FROM CIDADE c
            INNER JOIN UF u ON u.ID = c.FK_UF
            WHERE c.NOME = 'SAO JOSE DO RIO PRETO'
                AND u.UF = 'SP'
    );

    UPDATE CIDADE_DBM SET DDD = 18 WHERE FK_CIDADE = (
        SELECT c.ID
            FROM CIDADE c
            INNER JOIN UF u ON u.ID = c.FK_UF
            WHERE c.NOME = 'ARACATUBA'
                AND u.UF = 'SP'
    );

    UPDATE CIDADE_DBM SET DDD = 19 WHERE FK_CIDADE = (
        SELECT c.ID
            FROM CIDADE c
            INNER JOIN UF u ON u.ID = c.FK_UF
            WHERE c.NOME = 'CAMPINAS'
                AND u.UF = 'SP'
    );
END;

/

-- Popular DDD - UF RJ
BEGIN
    UPDATE CIDADE_DBM SET DDD = 21 WHERE FK_CIDADE = (
        SELECT c.ID
            FROM CIDADE c
            INNER JOIN UF u ON u.ID = c.FK_UF
            WHERE c.NOME = 'RIO DE JANEIRO'
                AND u.UF = 'RJ'
    );

    UPDATE CIDADE_DBM SET DDD = 22 WHERE FK_CIDADE = (
        SELECT c.ID
            FROM CIDADE c
            INNER JOIN UF u ON u.ID = c.FK_UF
            WHERE c.NOME = 'CAMPOS DOS GOYTACAZES'
                AND u.UF = 'RJ'
    );

    UPDATE CIDADE_DBM SET DDD = 24 WHERE FK_CIDADE = (
        SELECT c.ID
            FROM CIDADE c
            INNER JOIN UF u ON u.ID = c.FK_UF
            WHERE c.NOME = 'VOLTA REDONDA'
                AND u.UF = 'RJ'
    );
END;

/

-- Popular DDD - UF ES
BEGIN
    UPDATE CIDADE_DBM SET DDD = 27 WHERE FK_CIDADE = (
        SELECT c.ID
            FROM CIDADE c
            INNER JOIN UF u ON u.ID = c.FK_UF
            WHERE c.NOME = 'VITORIA'
                AND u.UF = 'ES'
    );

    UPDATE CIDADE_DBM SET DDD = 28 WHERE FK_CIDADE = (
        SELECT c.ID
            FROM CIDADE c
            INNER JOIN UF u ON u.ID = c.FK_UF
            WHERE c.NOME = 'CACHOEIRO DE ITAPEMIRIM'
                AND u.UF = 'ES'
    );
END;

/

-- Popular DDD - UF MG
BEGIN
    UPDATE CIDADE_DBM SET DDD = 31 WHERE FK_CIDADE = (
        SELECT c.ID
            FROM CIDADE c
            INNER JOIN UF u ON u.ID = c.FK_UF
            WHERE c.NOME = 'BELO HORIZONTE'
                AND u.UF = 'MG'
    );

    UPDATE CIDADE_DBM SET DDD = 32 WHERE FK_CIDADE = (
        SELECT c.ID
            FROM CIDADE c
            INNER JOIN UF u ON u.ID = c.FK_UF
            WHERE c.NOME = 'JUIZ DE FORA'
                AND u.UF = 'MG'
    );

    UPDATE CIDADE_DBM SET DDD = 33 WHERE FK_CIDADE = (
        SELECT c.ID
            FROM CIDADE c
            INNER JOIN UF u ON u.ID = c.FK_UF
            WHERE c.NOME = 'GOVERNADOR VALADARES'
                AND u.UF = 'MG'
    );

    UPDATE CIDADE_DBM SET DDD = 34 WHERE FK_CIDADE = (
        SELECT c.ID
            FROM CIDADE c
            INNER JOIN UF u ON u.ID = c.FK_UF
            WHERE c.NOME = 'UBERLANDIA'
                AND u.UF = 'MG'
    );

    UPDATE CIDADE_DBM SET DDD = 35 WHERE FK_CIDADE = (
        SELECT c.ID
            FROM CIDADE c
            INNER JOIN UF u ON u.ID = c.FK_UF
            WHERE c.NOME = 'POCOS DE CALDAS'
                AND u.UF = 'MG'
    );

    UPDATE CIDADE_DBM SET DDD = 37 WHERE FK_CIDADE = (
        SELECT c.ID
            FROM CIDADE c
            INNER JOIN UF u ON u.ID = c.FK_UF
            WHERE c.NOME = 'DIVINOPOLIS'
                AND u.UF = 'MG'
    );

    UPDATE CIDADE_DBM SET DDD = 38 WHERE FK_CIDADE = (
        SELECT c.ID
            FROM CIDADE c
            INNER JOIN UF u ON u.ID = c.FK_UF
            WHERE c.NOME = 'MONTES CLAROS'
                AND u.UF = 'MG'
    );
END;

/

-- Popular DDD - UF PR
BEGIN
    UPDATE CIDADE_DBM SET DDD = 41 WHERE FK_CIDADE = (
        SELECT c.ID
            FROM CIDADE c
            INNER JOIN UF u ON u.ID = c.FK_UF
            WHERE c.NOME = 'CURITIBA'
                AND u.UF = 'PR'
    );

    UPDATE CIDADE_DBM SET DDD = 42 WHERE FK_CIDADE = (
        SELECT c.ID
            FROM CIDADE c
            INNER JOIN UF u ON u.ID = c.FK_UF
            WHERE c.NOME = 'PONTA GROSSA'
                AND u.UF = 'PR'
    );

    UPDATE CIDADE_DBM SET DDD = 43 WHERE FK_CIDADE = (
        SELECT c.ID
            FROM CIDADE c
            INNER JOIN UF u ON u.ID = c.FK_UF
            WHERE c.NOME = 'LONDRINA'
                AND u.UF = 'PR'
    );

    UPDATE CIDADE_DBM SET DDD = 44 WHERE FK_CIDADE = (
        SELECT c.ID
            FROM CIDADE c
            INNER JOIN UF u ON u.ID = c.FK_UF
            WHERE c.NOME = 'MARINGA'
                AND u.UF = 'PR'
    );

    UPDATE CIDADE_DBM SET DDD = 45 WHERE FK_CIDADE = (
        SELECT c.ID
            FROM CIDADE c
            INNER JOIN UF u ON u.ID = c.FK_UF
            WHERE c.NOME = 'CASCAVEL'
                AND u.UF = 'PR'
    );

    UPDATE CIDADE_DBM SET DDD = 46 WHERE FK_CIDADE = (
        SELECT c.ID
            FROM CIDADE c
            INNER JOIN UF u ON u.ID = c.FK_UF
            WHERE c.NOME = 'PATO BRANCO'
                AND u.UF = 'PR'
    );
END;

/

-- Popular DDD - UF SC
BEGIN
    UPDATE CIDADE_DBM SET DDD = 47 WHERE FK_CIDADE = (
        SELECT c.ID
            FROM CIDADE c
            INNER JOIN UF u ON u.ID = c.FK_UF
            WHERE c.NOME = 'JOINVILLE'
                AND u.UF = 'SC'
    );

    UPDATE CIDADE_DBM SET DDD = 48 WHERE FK_CIDADE = (
        SELECT c.ID
            FROM CIDADE c
            INNER JOIN UF u ON u.ID = c.FK_UF
            WHERE c.NOME = 'FLORIANOPOLIS'
                AND u.UF = 'SC'
    );

    UPDATE CIDADE_DBM SET DDD = 49 WHERE FK_CIDADE = (
        SELECT c.ID
            FROM CIDADE c
            INNER JOIN UF u ON u.ID = c.FK_UF
            WHERE c.NOME = 'CHAPECO'
                AND u.UF = 'SC'
    );
END;

/

-- Popular DDD - UF RS
BEGIN
    UPDATE CIDADE_DBM SET DDD = 51 WHERE FK_CIDADE = (
        SELECT c.ID
            FROM CIDADE c
            INNER JOIN UF u ON u.ID = c.FK_UF
            WHERE c.NOME = 'PORTO ALEGRE'
                AND u.UF = 'RS'
    ) AND CODIGO_CIDADE_DBM = 880;

    UPDATE CIDADE_DBM SET DDD = 53 WHERE FK_CIDADE = (
        SELECT c.ID
            FROM CIDADE c
            INNER JOIN UF u ON u.ID = c.FK_UF
            WHERE c.NOME = 'PELOTAS'
                AND u.UF = 'RS'
    );

    UPDATE CIDADE_DBM SET DDD = 54 WHERE FK_CIDADE = (
        SELECT c.ID
            FROM CIDADE c
            INNER JOIN UF u ON u.ID = c.FK_UF
            WHERE c.NOME = 'CAXIAS DO SUL'
                AND u.UF = 'RS'
    );

    UPDATE CIDADE_DBM SET DDD = 55 WHERE FK_CIDADE = (
        SELECT c.ID
            FROM CIDADE c
            INNER JOIN UF u ON u.ID = c.FK_UF
            WHERE c.NOME = 'SANTA MARIA'
                AND u.UF = 'RS'
    );
END;

/

-- Popular DDD - UF DF
BEGIN
    UPDATE CIDADE_DBM SET DDD = 61 WHERE FK_CIDADE = (
        SELECT c.ID
            FROM CIDADE c
            INNER JOIN UF u ON u.ID = c.FK_UF
            WHERE c.NOME = 'BRASILIA'
                AND u.UF = 'DF'
    );
END;

/

-- Popular DDD - UF GO
BEGIN
    UPDATE CIDADE_DBM SET DDD = 62 WHERE FK_CIDADE = (
        SELECT c.ID
            FROM CIDADE c
            INNER JOIN UF u ON u.ID = c.FK_UF
            WHERE c.NOME = 'GOIANIA'
                AND u.UF = 'GO'
    );

    UPDATE CIDADE_DBM SET DDD = 64 WHERE FK_CIDADE = (
        SELECT c.ID
            FROM CIDADE c
            INNER JOIN UF u ON u.ID = c.FK_UF
            WHERE c.NOME = 'RIO VERDE'
                AND u.UF = 'GO'
    );
END;

/

-- Popular DDD - UF TO
BEGIN
    UPDATE CIDADE_DBM SET DDD = 63 WHERE FK_CIDADE = (
        SELECT c.ID
            FROM CIDADE c
            INNER JOIN UF u ON u.ID = c.FK_UF
            WHERE c.NOME = 'PALMAS'
                AND u.UF = 'TO'
    );
END;

/

-- Popular DDD - UF MT
BEGIN
    UPDATE CIDADE_DBM SET DDD = 65 WHERE FK_CIDADE = (
        SELECT c.ID
            FROM CIDADE c
            INNER JOIN UF u ON u.ID = c.FK_UF
            WHERE c.NOME = 'CUIABA'
                AND u.UF = 'MT'
    );

    UPDATE CIDADE_DBM SET DDD = 66 WHERE FK_CIDADE = (
        SELECT c.ID
            FROM CIDADE c
            INNER JOIN UF u ON u.ID = c.FK_UF
            WHERE c.NOME = 'RONDONOPOLIS'
                AND u.UF = 'MT'
    );
END;

/

-- Popular DDD - UF MS
BEGIN
    UPDATE CIDADE_DBM SET DDD = 67 WHERE FK_CIDADE = (
        SELECT c.ID
            FROM CIDADE c
            INNER JOIN UF u ON u.ID = c.FK_UF
            WHERE c.NOME = 'CAMPO GRANDE'
                AND u.UF = 'MS'
    );
END;

/

-- Popular DDD - UF AC
BEGIN
    UPDATE CIDADE_DBM SET DDD = 68 WHERE FK_CIDADE = (
        SELECT c.ID
            FROM CIDADE c
            INNER JOIN UF u ON u.ID = c.FK_UF
            WHERE c.NOME = 'RIO BRANCO'
                AND u.UF = 'AC'
    );
END;

/

-- Popular DDD - UF RO
BEGIN
    UPDATE CIDADE_DBM SET DDD = 69 WHERE FK_CIDADE = (
        SELECT c.ID
            FROM CIDADE c
            INNER JOIN UF u ON u.ID = c.FK_UF
            WHERE c.NOME = 'PORTO VELHO'
                AND u.UF = 'RO'
    );
END;

/

-- Popular DDD - UF BA
BEGIN
    UPDATE CIDADE_DBM SET DDD = 71 WHERE FK_CIDADE = (
        SELECT c.ID
            FROM CIDADE c
            INNER JOIN UF u ON u.ID = c.FK_UF
            WHERE c.NOME = 'SALVADOR'
                AND u.UF = 'BA'
    );

    UPDATE CIDADE_DBM SET DDD = 73 WHERE FK_CIDADE = (
        SELECT c.ID
            FROM CIDADE c
            INNER JOIN UF u ON u.ID = c.FK_UF
            WHERE c.NOME = 'ILHEUS'
                AND u.UF = 'BA'
    );

    UPDATE CIDADE_DBM SET DDD = 74 WHERE FK_CIDADE = (
        SELECT c.ID
            FROM CIDADE c
            INNER JOIN UF u ON u.ID = c.FK_UF
            WHERE c.NOME = 'JUAZEIRO'
                AND u.UF = 'BA'
    );

    UPDATE CIDADE_DBM SET DDD = 75 WHERE FK_CIDADE = (
        SELECT c.ID
            FROM CIDADE c
            INNER JOIN UF u ON u.ID = c.FK_UF
            WHERE c.NOME = 'FEIRA DE SANTANA'
                AND u.UF = 'BA'
    );

    UPDATE CIDADE_DBM SET DDD = 77 WHERE FK_CIDADE = (
        SELECT c.ID
            FROM CIDADE c
            INNER JOIN UF u ON u.ID = c.FK_UF
            WHERE c.NOME = 'VITORIA DA CONQUISTA'
                AND u.UF = 'BA'
    );
END;

/

-- Popular DDD - UF SE
BEGIN
    UPDATE CIDADE_DBM SET DDD = 79 WHERE FK_CIDADE = (
        SELECT c.ID
            FROM CIDADE c
            INNER JOIN UF u ON u.ID = c.FK_UF
            WHERE c.NOME = 'ARACAJU'
                AND u.UF = 'SE'
    );
END;

/

-- Popular DDD - UF PE
BEGIN
    UPDATE CIDADE_DBM SET DDD = 81 WHERE FK_CIDADE = (
        SELECT c.ID
            FROM CIDADE c
            INNER JOIN UF u ON u.ID = c.FK_UF
            WHERE c.NOME = 'RECIFE'
                AND u.UF = 'PE'
    );

    UPDATE CIDADE_DBM SET DDD = 87 WHERE FK_CIDADE = (
        SELECT c.ID
            FROM CIDADE c
            INNER JOIN UF u ON u.ID = c.FK_UF
            WHERE c.NOME = 'PETROLINA'
                AND u.UF = 'PE'
    );
END;

/

-- Popular DDD - UF AL
BEGIN
    UPDATE CIDADE_DBM SET DDD = 82 WHERE FK_CIDADE = (
        SELECT c.ID
            FROM CIDADE c
            INNER JOIN UF u ON u.ID = c.FK_UF
            WHERE c.NOME = 'MACEIO'
                AND u.UF = 'AL'
    );
END;

/

-- Popular DDD - UF PB
BEGIN
    UPDATE CIDADE_DBM SET DDD = 83 WHERE FK_CIDADE = (
        SELECT c.ID
            FROM CIDADE c
            INNER JOIN UF u ON u.ID = c.FK_UF
            WHERE c.NOME = 'JOAO PESSOA'
                AND u.UF = 'PB'
    );
END;

/

-- Popular DDD - UF RN
BEGIN
    UPDATE CIDADE_DBM SET DDD = 84 WHERE FK_CIDADE = (
        SELECT c.ID
            FROM CIDADE c
            INNER JOIN UF u ON u.ID = c.FK_UF
            WHERE c.NOME = 'NATAL'
                AND u.UF = 'RN'
    );
END;

/

-- Popular DDD - UF CE
BEGIN
    UPDATE CIDADE_DBM SET DDD = 85 WHERE FK_CIDADE = (
        SELECT c.ID
            FROM CIDADE c
            INNER JOIN UF u ON u.ID = c.FK_UF
            WHERE c.NOME = 'FORTALEZA'
                AND u.UF = 'CE'
    );

    UPDATE CIDADE_DBM SET DDD = 88 WHERE FK_CIDADE = (
        SELECT c.ID
            FROM CIDADE c
            INNER JOIN UF u ON u.ID = c.FK_UF
            WHERE c.NOME = 'JUAZEIRO DO NORTE'
                AND u.UF = 'CE'
    );
END;

/

-- Popular DDD - UF PI
BEGIN
    UPDATE CIDADE_DBM SET DDD = 86 WHERE FK_CIDADE = (
        SELECT c.ID
            FROM CIDADE c
            INNER JOIN UF u ON u.ID = c.FK_UF
            WHERE c.NOME = 'TERESINA'
                AND u.UF = 'PI'
    );

    UPDATE CIDADE_DBM SET DDD = 89 WHERE FK_CIDADE = (
        SELECT c.ID
            FROM CIDADE c
            INNER JOIN UF u ON u.ID = c.FK_UF
            WHERE c.NOME = 'PICOS'
                AND u.UF = 'PI'
    );
END;

/

-- Popular DDD - UF PA
BEGIN
    UPDATE CIDADE_DBM SET DDD = 91 WHERE FK_CIDADE = (
        SELECT c.ID
            FROM CIDADE c
            INNER JOIN UF u ON u.ID = c.FK_UF
            WHERE c.NOME = 'BELEM'
                AND u.UF = 'PA'
    );

    UPDATE CIDADE_DBM SET DDD = 93 WHERE FK_CIDADE = (
        SELECT c.ID
            FROM CIDADE c
            INNER JOIN UF u ON u.ID = c.FK_UF
            WHERE c.NOME = 'SANTAREM'
                AND u.UF = 'PA'
    );

    UPDATE CIDADE_DBM SET DDD = 94 WHERE FK_CIDADE = (
        SELECT c.ID
            FROM CIDADE c
            INNER JOIN UF u ON u.ID = c.FK_UF
            WHERE c.NOME = 'MARABA'
                AND u.UF = 'PA'
    );
END;

/

-- Popular DDD - UF AM
BEGIN
    UPDATE CIDADE_DBM SET DDD = 92 WHERE FK_CIDADE = (
        SELECT c.ID
            FROM CIDADE c
            INNER JOIN UF u ON u.ID = c.FK_UF
            WHERE c.NOME = 'MANAUS'
                AND u.UF = 'AM'
    );

    UPDATE CIDADE_DBM SET DDD = 97 WHERE FK_CIDADE = (
        SELECT c.ID
            FROM CIDADE c
            INNER JOIN UF u ON u.ID = c.FK_UF
            WHERE c.NOME = 'COARI'
                AND u.UF = 'AM'
    );
END;

/

-- Popular DDD - UF RR
BEGIN
    UPDATE CIDADE_DBM SET DDD = 95 WHERE FK_CIDADE = (
        SELECT c.ID
            FROM CIDADE c
            INNER JOIN UF u ON u.ID = c.FK_UF
            WHERE c.NOME = 'BOA VISTA'
                AND u.UF = 'RR'
    );
END;

/

-- Popular DDD - UF AP
BEGIN
    UPDATE CIDADE_DBM SET DDD = 96 WHERE FK_CIDADE = (
        SELECT c.ID
            FROM CIDADE c
            INNER JOIN UF u ON u.ID = c.FK_UF
            WHERE c.NOME = 'MACAPA'
                AND u.UF = 'AP'
    );
END;

/

-- Popular DDD - UF MA
BEGIN
    UPDATE CIDADE_DBM SET DDD = 98 WHERE FK_CIDADE = (
        SELECT c.ID
            FROM CIDADE c
            INNER JOIN UF u ON u.ID = c.FK_UF
            WHERE c.NOME = 'SAO LUIS'
                AND u.UF = 'MA'
    );

    UPDATE CIDADE_DBM SET DDD = 99 WHERE FK_CIDADE = (
        SELECT c.ID
            FROM CIDADE c
            INNER JOIN UF u ON u.ID = c.FK_UF
            WHERE c.NOME = 'IMPERATRIZ'
                AND u.UF = 'MA'
    );
END;