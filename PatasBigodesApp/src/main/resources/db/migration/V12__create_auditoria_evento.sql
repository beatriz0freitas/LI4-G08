-- V12__create_auditoria_evento.sql
-- Criada: 2026-05-26
-- Descrição: Tabela centralizada de auditoria para rastreamento de operações críticas

CREATE TABLE auditoria_evento (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    timestamp TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    utilizador_id BIGINT NOT NULL,
    operacao VARCHAR(100) NOT NULL,
    entidade VARCHAR(100) NOT NULL,
    entity_id BIGINT NOT NULL,
    acao VARCHAR(50) NOT NULL,
    detalhes LONGTEXT,
    resultado VARCHAR(20) NOT NULL,
    motivo_falha VARCHAR(500),
    
    FOREIGN KEY (utilizador_id) REFERENCES colaborador(id),
    
    -- Índices para otimização de queries
    INDEX idx_timestamp (timestamp),
    INDEX idx_utilizador_timestamp (utilizador_id, timestamp),
    INDEX idx_operacao_timestamp (operacao, timestamp),
    INDEX idx_entidade_id_timestamp (entidade, entity_id, timestamp)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- Comentário para documentação
ALTER TABLE auditoria_evento COMMENT='Tabela de auditoria centralizada para rastreamento de operações críticas. Retenção: 12 meses. Soft delete: não (apagamento físico). Schema de detalhes: JSON livre com documentação em código.';
