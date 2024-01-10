-- Remover funcionalidades 'Consultar telefones na Lei Não Perturbe'
DELETE FROM CARGO_DEPART_FUNC
WHERE FK_FUNCIONALIDADE = 2039;

-- Inserir funcionalidade 'Consultar telefones na Lei Não Perturbe' para todos cargos e níveis comerciais e agentes autorizados
INSERT INTO CARGO_DEPART_FUNC (ID, FK_CARGO, FK_DEPARTAMENTO, FK_FUNCIONALIDADE)
SELECT
    SEQ_CARGO_DEPART_FUNC.nextval,
    C.ID AS FK_CARGO,
    D.ID AS FK_DEPARTAMENTO,
    2039
FROM
    CARGO C
INNER JOIN
    DEPARTAMENTO D ON C.FK_NIVEL = D.FK_NIVEL
WHERE (
    D.CODIGO = 'COMERCIAL'
    OR D.CODIGO = 'AGENTE_AUTORIZADO'
    AND D.SITUACAO = 'A')
    AND C.SITUACAO = 'A';

-- Inserir funcionalidade 'Consultar telefones na Lei Não Perturbe' para cargo ADMINISTRADOR
INSERT INTO CARGO_DEPART_FUNC (ID, FK_CARGO, FK_DEPARTAMENTO, FK_FUNCIONALIDADE)
VALUES (SEQ_CARGO_DEPART_FUNC.nextval, 50, 50, 2039);