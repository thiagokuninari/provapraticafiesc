--------------------------------------
-- REMOVENDO A PERMISSÃO DOS CARGOS --
--------------------------------------
-- Visualizar relatório de login / logout
-----------------------------------------

-- Vendedor Back Office D2D
-- Vendedor Back Office Televendas
-- Vendedor Back Office Televendas Receptivo

DELETE FROM CARGO_DEPART_FUNC WHERE FK_CARGO IN (80, 79, 89) AND FK_FUNCIONALIDADE = 2100;
