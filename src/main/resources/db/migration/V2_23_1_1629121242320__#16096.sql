-- REMOVE PERMISSÃO DA TELA DE FUNIS PARA EXECUTIVO HUNTER --

-- Funcionalidade: Gerenciar Funil de Prospecções e Lista de Ocorrências
-- Cargo: Executivo Hunter
DELETE FROM CARGO_DEPART_FUNC WHERE FK_FUNCIONALIDADE = 500 AND FK_CARGO = 95;
