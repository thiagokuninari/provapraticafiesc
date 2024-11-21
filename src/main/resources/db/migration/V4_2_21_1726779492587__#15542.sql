--Remove permissão para cadastrar PGAA dos cargos OPERACAO_ANALISTA
--                                                ASSISTENTE_OPERACAO
--                                                OPERACAO_CONSULTOR
--                                                ASSISTENTE_HUNTER
--                                                DIRETOR_OPERACAO

DELETE FROM CARGO_DEPART_FUNC WHERE fk_funcionalidade = 239 AND FK_CARGO IN (1, 2, 3, 94, 6);