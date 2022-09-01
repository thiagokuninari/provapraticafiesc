-- INSERT DOS USUARIOS MSO NA TABELA USUARIO_TIPO_FEEDER

INSERT INTO usuario_tipo_feeder (fk_usuario, tipo_feeder_mso)
SELECT usuario.id, 'RESIDENCIAL'
FROM usuario
WHERE fk_cargo IN (20, 21, 22, 23, 24, 25, 52);

INSERT INTO usuario_tipo_feeder (fk_usuario, tipo_feeder_mso)
SELECT usuario.id, 'EMPRESARIAL'
FROM usuario
WHERE fk_cargo IN (20, 21, 22, 23, 24, 25, 52);

-- INSERT DAS PERMISSOES ESPECIAIS TIPO FEEDER RESIDENCIAL E EMPRESARIAL PARA OS USUARIOS MSO

INSERT INTO permissao_especial (id, data_cadastro, fk_funcionalidade, fk_usuario, fk_usuario_cadastro)
SELECT seq_permissao_especial.nextval, sysdate, 15000, usuario.id, 1
FROM usuario
WHERE fk_cargo IN (20, 21, 22, 23, 24, 25, 52);

INSERT INTO permissao_especial (id, data_cadastro, fk_funcionalidade, fk_usuario, fk_usuario_cadastro)
SELECT seq_permissao_especial.nextval, sysdate, 15001, usuario.id, 1
FROM usuario
WHERE fk_cargo IN (20, 21, 22, 23, 24, 25, 52);

INSERT INTO permissao_especial (id, data_cadastro, fk_funcionalidade, fk_usuario, fk_usuario_cadastro)
SELECT seq_permissao_especial.nextval, sysdate, 15002, usuario.id, 1
FROM usuario
WHERE fk_cargo IN (20, 21, 22, 23, 24, 25, 52);

INSERT INTO permissao_especial (id, data_cadastro, fk_funcionalidade, fk_usuario, fk_usuario_cadastro)
SELECT seq_permissao_especial.nextval, sysdate, 15003, usuario.id, 1
FROM usuario
WHERE fk_cargo IN (20, 21, 22, 23, 24, 25, 52);

INSERT INTO permissao_especial (id, data_cadastro, fk_funcionalidade, fk_usuario, fk_usuario_cadastro)
SELECT seq_permissao_especial.nextval, sysdate, 15006, usuario.id, 1
FROM usuario
WHERE fk_cargo IN (20, 21, 22, 23, 24, 25, 52);

INSERT INTO permissao_especial (id, data_cadastro, fk_funcionalidade, fk_usuario, fk_usuario_cadastro)
SELECT seq_permissao_especial.nextval, sysdate, 15007, usuario.id, 1
FROM usuario
WHERE fk_cargo IN (20, 21, 22, 23, 24, 25, 52);

INSERT INTO permissao_especial (id, data_cadastro, fk_funcionalidade, fk_usuario, fk_usuario_cadastro)
SELECT seq_permissao_especial.nextval, sysdate, 15008, usuario.id, 1
FROM usuario
WHERE fk_cargo IN (20, 21, 22, 23, 24, 25, 52);

INSERT INTO permissao_especial (id, data_cadastro, fk_funcionalidade, fk_usuario, fk_usuario_cadastro)
SELECT seq_permissao_especial.nextval, sysdate, 15009, usuario.id, 1
FROM usuario
WHERE fk_cargo IN (20, 21, 22, 23, 24, 25, 52);

INSERT INTO permissao_especial (id, data_cadastro, fk_funcionalidade, fk_usuario, fk_usuario_cadastro)
SELECT seq_permissao_especial.nextval, sysdate, 15010, usuario.id, 1
FROM usuario
WHERE fk_cargo IN (20, 21, 22, 23, 24, 25, 52);

INSERT INTO permissao_especial (id, data_cadastro, fk_funcionalidade, fk_usuario, fk_usuario_cadastro)
SELECT seq_permissao_especial.nextval, sysdate, 15011, usuario.id, 1
FROM usuario
WHERE fk_cargo IN (20, 21, 22, 23, 24, 25, 52);

INSERT INTO permissao_especial (id, data_cadastro, fk_funcionalidade, fk_usuario, fk_usuario_cadastro)
SELECT seq_permissao_especial.nextval, sysdate, 15012, usuario.id, 1
FROM usuario
WHERE fk_cargo IN (20, 21, 22, 23, 24, 25, 52);

INSERT INTO permissao_especial (id, data_cadastro, fk_funcionalidade, fk_usuario, fk_usuario_cadastro)
SELECT seq_permissao_especial.nextval, sysdate, 15013, usuario.id, 1
FROM usuario
WHERE fk_cargo IN (20, 21, 22, 23, 24, 25, 52);

INSERT INTO permissao_especial (id, data_cadastro, fk_funcionalidade, fk_usuario, fk_usuario_cadastro)
SELECT seq_permissao_especial.nextval, sysdate, 15014, usuario.id, 1
FROM usuario
WHERE fk_cargo IN (20, 21, 22, 23, 24, 25, 52);

INSERT INTO permissao_especial (id, data_cadastro, fk_funcionalidade, fk_usuario, fk_usuario_cadastro)
SELECT seq_permissao_especial.nextval, sysdate, 20007, usuario.id, 1
FROM usuario
WHERE fk_cargo IN (20, 21, 22, 23, 24, 25, 52);
