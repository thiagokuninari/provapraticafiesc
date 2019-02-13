CREATE TABLE usuario_canal (fk_usuario integer NOT NULL,
                            canal varchar(20) NOT NULL);

ALTER TABLE usuario_canal ADD CONSTRAINT FK_USU_CANAL_USU
FOREIGN KEY (fk_usuario) REFERENCES usuario;

UPDATE CARGO SET CODIGO = 'DIRETOR_OPERACAO' WHERE ID = 6;