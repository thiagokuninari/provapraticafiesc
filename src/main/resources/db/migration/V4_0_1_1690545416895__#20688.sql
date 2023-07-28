-- INSERIR PERMISSAO ESPECIAL PARA CONSULTAR INDICACAO INSIDE SALES PME PARA USUARIOS COM SUBCANAL INSIDE SALES PME
INSERT INTO permissao_especial (id, data_baixa, data_cadastro, fk_funcionalidade, fk_usuario, fk_usuario_baixa, fk_usuario_cadastro)
SELECT seq_permissao_especial.NEXTVAL, NULL, sysdate, 3071, fk_usuario, NULL, 1
FROM (
    SELECT fk_usuario
    FROM usuario_subcanal
    WHERE fk_subcanal = 4
);