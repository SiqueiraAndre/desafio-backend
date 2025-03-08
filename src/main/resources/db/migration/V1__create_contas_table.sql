CREATE SEQUENCE contas_id_seq START 1;

CREATE TABLE contas (
    id BIGINT PRIMARY KEY DEFAULT nextval('contas_id_seq'),
    data_vencimento DATE NOT NULL,
    data_pagamento DATE,
    valor NUMERIC(15,2) NOT NULL,
    descricao VARCHAR(255) NOT NULL,
    situacao VARCHAR(20) NOT NULL
);