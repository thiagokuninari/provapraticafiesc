----------------------------------
-- ADICIONA NOVA FUNCIONALIDADE --
----------------------------------

INSERT INTO FUNCIONALIDADE (ID, NOME, ROLE, FK_APLICACAO)
VALUES (252, 'Gerenciar Aas que trabalham Hp Tecnico Indicador', 'POL_GERENCIAR_TECNICO_INDICADOR', 2);



----------------------------------
-- ADICIONA PERMISSOES --
----------------------------------

-- Administrador (X-Brain) --
INSERT INTO CARGO_DEPART_FUNC (ID, FK_CARGO, FK_DEPARTAMENTO, FK_FUNCIONALIDADE, DATA_CADASTRO)
VALUES (SEQ_CARGO_DEPART_FUNC.nextval, 50, 50, 252, CURRENT_DATE);



----------------------------------
-- ADICIONA PERMISSOES ESPECIAIS --
----------------------------------

-- CAIO VINICIO PEREIRA SOARES --
INSERT INTO PERMISSAO_ESPECIAL (ID,DATA_CADASTRO ,FK_FUNCIONALIDADE, FK_USUARIO)
VALUES (SEQ_PERMISSAO_ESPECIAL.nextval, CURRENT_DATE, 252, 15382);
