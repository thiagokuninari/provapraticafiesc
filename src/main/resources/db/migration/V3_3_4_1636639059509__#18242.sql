DECLARE
    existe INT;
BEGIN
    -- Verifica de funcionalidade está atribuída ao cargo ASSISTENTE de nível OPERAÇÃO
    SELECT COUNT(*) INTO existe
        FROM CARGO_DEPART_FUNC
        WHERE FK_CARGO = 2 AND FK_DEPARTAMENTO = 3 AND FK_FUNCIONALIDADE = 2039;
    -- Caso não exista adiciona
    IF (existe = 0) THEN
        EXECUTE IMMEDIATE 'INSERT INTO CARGO_DEPART_FUNC (ID, FK_CARGO, FK_DEPARTAMENTO, FK_FUNCIONALIDADE) VALUES (SEQ_CARGO_DEPART_FUNC.nextval, 2, 3, 2039)';
    END IF;
END;