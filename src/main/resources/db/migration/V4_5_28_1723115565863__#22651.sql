-- Cria aplicação
INSERT INTO APLICACAO (ID, CODIGO, NOME)
VALUES (28, 'AGD', 'AGENDADORES');

-- Cria funcionalidade Gerenciar agendadores
INSERT INTO FUNCIONALIDADE (ID, NOME, ROLE, FK_APLICACAO)
VALUES (30004, 'Gerenciar Agendadores', 'AGD_30004', 28);

-- Atribui funcionalidade Gerenciar agendadores para usuários X-Brain
INSERT INTO CARGO_DEPART_FUNC (ID, FK_CARGO, FK_DEPARTAMENTO, FK_FUNCIONALIDADE)
VALUES (SEQ_CARGO_DEPART_FUNC.nextval, 50, 50, 30004);

-- Cria funcionalidade Chave Geral Agendadores
INSERT INTO FUNCIONALIDADE (ID, NOME, ROLE, FK_APLICACAO)
VALUES (30005, 'Chave Geral Agendadores', 'AGD_30005', 28);
