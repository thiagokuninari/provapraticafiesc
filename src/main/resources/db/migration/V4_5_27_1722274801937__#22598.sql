-- CRIAR USUARIO ANTI-FRAUDE --
INSERT INTO usuario (
    id,
    alterar_senha,
    data_cadastro,
    email_01,
    nascimento,
    nome,
    senha,
    situacao,
    fk_cargo,
    fk_departamento,
    recuperar_senha_tentativa
) VALUES (
    seq_usuario.NEXTVAL,
    'F',
    current_date,
    'INTEGRACAOANTIFRAUDE@XBRAIN.COM.BR',
    current_date,
    'INTEGRACAO ANTI-FRAUDE',
    '$2a$10$Yv5L8D7ZpGbSgvqTcK20hOioi5Q.R8fdreXLfeDWQxP5HWtUXdECq',
    'A',
    ( SELECT id FROM cargo WHERE codigo = 'INTEGRACAO' ),
    ( SELECT id FROM departamento WHERE codigo = 'INTEGRACAO' ),
    0
);


-- VINCULAR UNIDADE NEGOCIO X-BRAIN A USUARIO ANTI-FRAUDE --
INSERT INTO usuario_unidade_negocio (
    fk_usuario,
    fk_unidade_negocio
) VALUES (
    ( SELECT id FROM usuario WHERE email_01 = 'INTEGRACAOANTIFRAUDE@XBRAIN.COM.BR' ),
    ( SELECT id FROM unidade_negocio WHERE codigo = 'XBRAIN' )
);


-- VINCULAR EMPRESA X-BRAIN A USUARIO ANTI-FRAUDE --
INSERT INTO usuario_empresa (
    fk_usuario,
    fk_empresa
) VALUES (
    ( SELECT id FROM usuario WHERE email_01 = 'INTEGRACAOANTIFRAUDE@XBRAIN.COM.BR' ),
    ( SELECT id FROM empresa WHERE codigo = 'XBRAIN' )
);


-- CRIAR FUNCIONALIDADE RESGATAR TRATATIVAS ANTI-FRAUDE --
INSERT INTO funcionalidade (
    id,
    nome,
    role,
    fk_aplicacao
) VALUES (
    7019,
    'Resgatar tratativas para Anti-Fraude',
    'INT_7019',
    ( SELECT id FROM aplicacao WHERE codigo = 'INT' )
);


-- VINCULAR FUNCIONALIDADE RESGATAR TRATATIVAS ANTI-FRAUDE PARA USUARIOS ADM (X-BRAIN) --
INSERT INTO cargo_depart_func (
    id,
    fk_cargo,
    fk_departamento,
    fk_funcionalidade
) VALUES (
    seq_cargo_depart_func.NEXTVAL,
    ( SELECT id FROM cargo WHERE codigo = 'ADMINISTRADOR' ),
    ( SELECT id FROM departamento WHERE codigo = 'ADMINISTRADOR' ),
    7019
);


-- ADICIONAR PERMISSAO ESPECIAL DA FUNCIONALIDADE RESGATAR TRATATIVAS ANTI-FRAUDE PARA USUARIO ANTI-FRAUDE --
INSERT INTO permissao_especial (
    id,
    data_cadastro,
    fk_funcionalidade,
    fk_usuario,
    fk_usuario_cadastro
) VALUES (
    seq_permissao_especial.NEXTVAL,
    current_date,
    7019,
    ( SELECT id FROM usuario WHERE email_01 = 'INTEGRACAOANTIFRAUDE@XBRAIN.COM.BR' ),
    ( SELECT id FROM usuario WHERE email_01 = 'ALISON@XBRAIN.COM.BR' )
);