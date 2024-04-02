CREATE SEQUENCE SEQ_CONFIGURACAO_AGENDA_HIST START WITH 1 INCREMENT BY 1;

CREATE TABLE CONFIGURACAO_AGENDA_HISTORICO
(
    id                     INTEGER       NOT NULL,
    configuracao_agenda_id INTEGER       NOT NULL,
    acao                   VARCHAR2(255) NOT NULL,
    data_acao              TIMESTAMP     NOT NULL,
    usuario_acao_id        INTEGER,
    usuario_acao_nome      VARCHAR2(255),
    CONSTRAINT pk_configuracao_agenda_hist PRIMARY KEY (id)
);

ALTER TABLE CONFIGURACAO_AGENDA_HISTORICO
    ADD CONSTRAINT fk_hist_configuracao_agenda
        FOREIGN KEY (configuracao_agenda_id)
        REFERENCES CONFIGURACAO_AGENDA_REAL (id);