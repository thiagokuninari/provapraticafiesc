-- Remove permissão de Gerenciar Ata de Reunião para Consultor do departamento de Treinamento
DELETE
FROM CARGO_DEPART_FUNC
WHERE FK_FUNCIONALIDADE = 241
  AND FK_CARGO = 3
  AND FK_DEPARTAMENTO = 12;
