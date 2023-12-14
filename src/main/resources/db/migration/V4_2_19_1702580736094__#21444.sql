-- CRIA APLICACAO REDE SOCIAL CONEXAO --
INSERT INTO APLICACAO (ID, NOME, CODIGO) VALUES (SEQ_APLICACAO.nextval, 'REDE SOCIAL CONEXAO', 'SHB');

-- CRIA PERMISSAO PARA ACESSAR REDE SOCIAL CONEXAO --
INSERT INTO FUNCIONALIDADE (ID, NOME, ROLE, FK_APLICACAO) VALUES (30000, 'Acessar Rede Social Conexão', 'SHB_30000', SEQ_APLICACAO.CURRVAL);

-- ADICIONA PERMISSAO DE ACESSAR REDE SOCIAL PARA TODOS USUARIOS COM PREFIXOS
DECLARE
u_id INTEGER;
    usuario_cadastro INTEGER;

BEGIN
SELECT u.id INTO usuario_cadastro FROM autenticacao.usuario u WHERE u.email_01 = 'FERNANDOCANDIOTTI@XBRAIN.COM.BR';

FOR r IN (SELECT id FROM usuario
              WHERE EMAIL_01 LIKE '%@XBRAIN.COM.BR'
                 OR EMAIL_01 LIKE '%@CLARO.COM.BR'
                 OR EMAIL_01 LIKE '%@NET.COM.BR'
                 OR EMAIL_01 LIKE '%@EMBRATEL.COM.BR'
                 OR EMAIL_01 LIKE '%@CLARONEXT.COM.BR'
                 OR EMAIL_01 LIKE '%@GLOBALHITSS.COM.BR'
                 OR EMAIL_01 LIKE '%@USTORE.COM.BR')
    LOOP
        INSERT INTO autenticacao.permissao_especial
                    (id, data_cadastro, fk_funcionalidade, fk_usuario, fk_usuario_cadastro)
        VALUES      (autenticacao.seq_permissao_especial.nextval,
                     sysdate,
                     30000,
                     r.id,
                     usuario_cadastro);
END LOOP;
END;