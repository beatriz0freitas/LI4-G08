INSERT INTO tipo_alojamento_tarifa (tipo_alojamento, tarifa_diaria, ativo)
VALUES
  ('CANINO', 15.00, TRUE),
  ('FELINO', 10.00, TRUE)
ON DUPLICATE KEY UPDATE
  tarifa_diaria = VALUES(tarifa_diaria),
  ativo = VALUES(ativo);

INSERT INTO tipo_servico_extra (nome, descricao, ativo)
VALUES
  ('Banho', 'Banho e secagem do animal', TRUE),
  ('Passeio', 'Passeio supervisionado durante a estadia', TRUE),
  ('Tosa', 'Serviço de tosquia e higiene', TRUE)
ON DUPLICATE KEY UPDATE
  descricao = VALUES(descricao),
  ativo = VALUES(ativo);

INSERT INTO tipo_servico_extra (nome, descricao, ativo)
SELECT DISTINCT se.tipo, NULL, TRUE
FROM servico_extra se
LEFT JOIN tipo_servico_extra tse ON tse.nome = se.tipo
WHERE se.tipo IS NOT NULL
  AND se.tipo_servico_extra_id IS NULL
  AND tse.id IS NULL;

UPDATE servico_extra se
JOIN tipo_servico_extra tse ON tse.nome = se.tipo
SET se.tipo_servico_extra_id = tse.id
WHERE se.tipo_servico_extra_id IS NULL;

ALTER TABLE servico_extra
  MODIFY tipo_servico_extra_id BIGINT NOT NULL;
