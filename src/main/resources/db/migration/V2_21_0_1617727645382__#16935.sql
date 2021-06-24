-- Remove permissão Visualizar Carga de Leads para usuários importadores de carga
DELETE FROM CARGO_DEPART_FUNC WHERE FK_CARGO = 97 AND FK_DEPARTAMENTO = 68 AND FK_FUNCIONALIDADE= 15009;

--Adiciona permissão Gerenciar Lead para usuários importadores de carga
INSERT INTO CARGO_DEPART_FUNC (ID, FK_CARGO, FK_DEPARTAMENTO, FK_FUNCIONALIDADE) VALUES (SEQ_CARGO_DEPART_FUNC.nextval, 97, 68, 15000);

