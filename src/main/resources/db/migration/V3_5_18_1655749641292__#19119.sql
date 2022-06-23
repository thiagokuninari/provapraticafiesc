CREATE TABLE USUARIO_TIPO_FEEDER (
fk_usuario NUMBER(38, 0) NOT NULL, tipo_feeder_mso VARCHAR2(20 CHAR),
constraint fk_usuario_tipo_feeder FOREIGN KEY(fk_usuario)
    REFERENCES usuario(id));