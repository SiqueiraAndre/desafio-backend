-- Migration para criar tabela de contas a pagar
-- Versão 1.0

CREATE TABLE contas (
    id BIGSERIAL PRIMARY KEY,
    data_vencimento DATE NOT NULL,
    data_pagamento DATE,
    valor DECIMAL(15,2) NOT NULL CHECK (valor > 0),
    descricao VARCHAR(500) NOT NULL,
    situacao VARCHAR(20) NOT NULL DEFAULT 'PENDENTE',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Índices para otimizar consultas
CREATE INDEX idx_contas_data_vencimento ON contas(data_vencimento);
CREATE INDEX idx_contas_situacao ON contas(situacao);
CREATE INDEX idx_contas_descricao ON contas(descricao);
CREATE INDEX idx_contas_data_pagamento ON contas(data_pagamento);

-- Comentários para documentação
COMMENT ON TABLE contas IS 'Tabela para armazenar contas a pagar';
COMMENT ON COLUMN contas.id IS 'Identificador único da conta';
COMMENT ON COLUMN contas.data_vencimento IS 'Data de vencimento da conta';
COMMENT ON COLUMN contas.data_pagamento IS 'Data em que a conta foi paga (null se não pago)';
COMMENT ON COLUMN contas.valor IS 'Valor da conta em reais';
COMMENT ON COLUMN contas.descricao IS 'Descrição da conta';
COMMENT ON COLUMN contas.situacao IS 'Situação da conta: PENDENTE, PAGO, VENCIDO';
COMMENT ON COLUMN contas.created_at IS 'Data de criação do registro';
COMMENT ON COLUMN contas.updated_at IS 'Data da última atualização';
