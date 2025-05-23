CREATE SEQUENCE SEQ_PROFESSOR START WITH 1 INCREMENT BY 1;

CREATE TABLE PROFESSOR (
    ID INTEGER NOT NULL,
    CPF VARCHAR(14) NOT NULL,
    NOME VARCHAR(255) NOT NULL,
    DATA_NASCIMENTO DATE NOT NULL,
    FK_ESPECIALIDADE INTEGER NOT NULL,
    STATUS VARCHAR(255) NOT NULL,
    PRIMARY KEY (ID),
    FOREIGN KEY (FK_ESPECIALIDADE) REFERENCES ESPECIALIDADE (ID)
)