-- Eventos como RELATORIO_GERADO representam uma operação consultiva sem entidade persistida.
ALTER TABLE auditoria_evento MODIFY COLUMN entity_id BIGINT NULL;
