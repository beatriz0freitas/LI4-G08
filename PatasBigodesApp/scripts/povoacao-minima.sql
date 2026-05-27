-- Povoacao minima para o Sistema Patas & Bigodes.
-- Objetivo: disponibilizar dados coerentes para testar todos os modulos
-- funcionais da aplicacao local.
--
-- Credenciais dos colaboradores criados/atualizados:
--   diretor / diretor123
--   rececao / diretor123
--   cuidador / diretor123
--   veterinario / diretor123
--   limpeza / diretor123
--
-- O script e idempotente: pode ser executado varias vezes sem duplicar os
-- registos principais.

SET @senha_diretor123 = '$2a$10$tj7H0DQvRBXGOcbPuEJwoeAIBAsuqGpAKZvDxYx3g5rEj1Wf6IOcm';

INSERT INTO colaborador (username, nome, email, password_hash, tipo_colaborador, ativo)
VALUES
  ('diretor', 'Diretor', 'diretor@hotel.local', @senha_diretor123, 'DIRETOR', TRUE),
  ('rececao', 'Funcionario Rececao', 'rececao@hotel.local', @senha_diretor123, 'FUNCIONARIO_RECEPCAO', TRUE),
  ('cuidador', 'Cuidador', 'cuidador@hotel.local', @senha_diretor123, 'CUIDADOR', TRUE),
  ('veterinario', 'Medico Veterinario', 'veterinario@hotel.local', @senha_diretor123, 'MEDICO_VETERINARIO', TRUE),
  ('limpeza', 'Responsavel Limpeza', 'limpeza@hotel.local', @senha_diretor123, 'RESPONSAVEL_LIMPEZA', TRUE)
ON DUPLICATE KEY UPDATE
  nome = VALUES(nome),
  email = VALUES(email),
  password_hash = VALUES(password_hash),
  tipo_colaborador = VALUES(tipo_colaborador),
  ativo = VALUES(ativo);

SET @diretor_id = (SELECT id FROM colaborador WHERE username = 'diretor');
SET @rececao_id = (SELECT id FROM colaborador WHERE username = 'rececao');
SET @cuidador_id = (SELECT id FROM colaborador WHERE username = 'cuidador');
SET @veterinario_id = (SELECT id FROM colaborador WHERE username = 'veterinario');
SET @limpeza_id = (SELECT id FROM colaborador WHERE username = 'limpeza');

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
  ('Tosa', 'Tosquia e higiene do animal', TRUE),
  ('Medicacao Assistida', 'Administracao assistida de medicacao prescrita', TRUE)
ON DUPLICATE KEY UPDATE
  descricao = VALUES(descricao),
  ativo = VALUES(ativo);

INSERT INTO alojamento (identificacao, tipo, capacidade, estado_limpeza)
VALUES
  ('TEST-CANINO-01', 'CANINO', 1, 'CONCLUIDO'),
  ('TEST-CANINO-02', 'CANINO', 1, 'PENDENTE'),
  ('TEST-FELINO-01', 'FELINO', 1, 'CONCLUIDO'),
  ('TEST-FELINO-02', 'FELINO', 1, 'PENDENTE')
ON DUPLICATE KEY UPDATE
  tipo = VALUES(tipo),
  capacidade = VALUES(capacidade),
  estado_limpeza = VALUES(estado_limpeza);

INSERT INTO tutor (nome, nif, contacto, email)
VALUES
  ('Ana Martins', '900000001', '910000001', 'ana.martins@example.local'),
  ('Bruno Costa', '900000002', '910000002', 'bruno.costa@example.local'),
  ('Carla Ferreira', '900000003', '910000003', 'carla.ferreira@example.local')
ON DUPLICATE KEY UPDATE
  nome = VALUES(nome),
  contacto = VALUES(contacto),
  email = VALUES(email);

SET @tutor_ana = (SELECT id FROM tutor WHERE nif = '900000001');
SET @tutor_bruno = (SELECT id FROM tutor WHERE nif = '900000002');
SET @tutor_carla = (SELECT id FROM tutor WHERE nif = '900000003');

INSERT INTO animal (
  tutor_id, nome, especie, raca, data_nascimento, peso,
  estado_saude, necessidades_alimentares, medicacao_curso
)
SELECT @tutor_ana, 'Max', 'CAO', 'Labrador', '2020-03-10', 24.50,
       'NORMAL', 'Racao senior sem cereais', NULL
WHERE NOT EXISTS (
  SELECT 1 FROM animal WHERE tutor_id = @tutor_ana AND nome = 'Max'
);

INSERT INTO animal (
  tutor_id, nome, especie, raca, data_nascimento, peso,
  estado_saude, necessidades_alimentares, medicacao_curso
)
SELECT @tutor_bruno, 'Mia', 'GATO', 'Europeu Comum', '2021-07-02', 4.20,
       'ALTERADO', 'Comida humida renal', 'Comprimido de manha'
WHERE NOT EXISTS (
  SELECT 1 FROM animal WHERE tutor_id = @tutor_bruno AND nome = 'Mia'
);

INSERT INTO animal (
  tutor_id, nome, especie, raca, data_nascimento, peso,
  estado_saude, necessidades_alimentares, medicacao_curso
)
SELECT @tutor_carla, 'Thor', 'CAO', 'Beagle', '2019-11-20', 12.80,
       'CRITICO', 'Dieta hipocalorica', 'Vigilancia clinica reforcada'
WHERE NOT EXISTS (
  SELECT 1 FROM animal WHERE tutor_id = @tutor_carla AND nome = 'Thor'
);

SET @animal_max = (SELECT id FROM animal WHERE tutor_id = @tutor_ana AND nome = 'Max');
SET @animal_mia = (SELECT id FROM animal WHERE tutor_id = @tutor_bruno AND nome = 'Mia');
SET @animal_thor = (SELECT id FROM animal WHERE tutor_id = @tutor_carla AND nome = 'Thor');
SET @aloj_canino_livre = (SELECT id FROM alojamento WHERE identificacao = 'TEST-CANINO-01');
SET @aloj_canino_limpeza = (SELECT id FROM alojamento WHERE identificacao = 'TEST-CANINO-02');
SET @aloj_felino_livre = (SELECT id FROM alojamento WHERE identificacao = 'TEST-FELINO-01');

INSERT INTO reserva (tutor_id, animal_id, alojamento_id, data_inicio, data_fim, estado)
SELECT @tutor_ana, @animal_max, @aloj_canino_livre, CURDATE(), DATE_ADD(CURDATE(), INTERVAL 3 DAY), 'CONFIRMADA'
WHERE NOT EXISTS (
  SELECT 1 FROM reserva
  WHERE animal_id = @animal_max
    AND alojamento_id = @aloj_canino_livre
    AND data_inicio = CURDATE()
    AND data_fim = DATE_ADD(CURDATE(), INTERVAL 3 DAY)
);

INSERT INTO reserva (tutor_id, animal_id, alojamento_id, data_inicio, data_fim, estado)
SELECT @tutor_bruno, @animal_mia, @aloj_felino_livre, DATE_ADD(CURDATE(), INTERVAL 7 DAY), DATE_ADD(CURDATE(), INTERVAL 10 DAY), 'ATIVA'
WHERE NOT EXISTS (
  SELECT 1 FROM reserva
  WHERE animal_id = @animal_mia
    AND alojamento_id = @aloj_felino_livre
    AND data_inicio = DATE_ADD(CURDATE(), INTERVAL 7 DAY)
    AND data_fim = DATE_ADD(CURDATE(), INTERVAL 10 DAY)
);

INSERT INTO reserva (tutor_id, animal_id, alojamento_id, data_inicio, data_fim, estado)
SELECT @tutor_carla, @animal_thor, @aloj_canino_limpeza, DATE_SUB(CURDATE(), INTERVAL 8 DAY), DATE_SUB(CURDATE(), INTERVAL 5 DAY), 'CANCELADA'
WHERE NOT EXISTS (
  SELECT 1 FROM reserva
  WHERE animal_id = @animal_thor
    AND alojamento_id = @aloj_canino_limpeza
    AND data_inicio = DATE_SUB(CURDATE(), INTERVAL 8 DAY)
    AND data_fim = DATE_SUB(CURDATE(), INTERVAL 5 DAY)
);

SET @reserva_max = (
  SELECT id FROM reserva
  WHERE animal_id = @animal_max
    AND alojamento_id = @aloj_canino_livre
    AND data_inicio = CURDATE()
    AND data_fim = DATE_ADD(CURDATE(), INTERVAL 3 DAY)
  LIMIT 1
);

SET @reserva_mia = (
  SELECT id FROM reserva
  WHERE animal_id = @animal_mia
    AND alojamento_id = @aloj_felino_livre
    AND data_inicio = DATE_ADD(CURDATE(), INTERVAL 7 DAY)
    AND data_fim = DATE_ADD(CURDATE(), INTERVAL 10 DAY)
  LIMIT 1
);

INSERT INTO estadia (reserva_id, data_inicio, data_fim, estado)
SELECT @reserva_max, NOW(), NULL, 'EM_CURSO'
WHERE NOT EXISTS (
  SELECT 1 FROM estadia WHERE reserva_id = @reserva_max
);

SET @estadia_max = (SELECT id FROM estadia WHERE reserva_id = @reserva_max LIMIT 1);

INSERT INTO pagamento (estadia_id, valor, metodo, tipo, estado)
SELECT @estadia_max, 45.00, 'CARTAO_DEBITO', 'CHECK_IN', 'LIQUIDADO'
WHERE NOT EXISTS (
  SELECT 1 FROM pagamento
  WHERE estadia_id = @estadia_max AND tipo = 'CHECK_IN'
);

SET @tipo_banho = (SELECT id FROM tipo_servico_extra WHERE nome = 'Banho');
SET @tipo_passeio = (SELECT id FROM tipo_servico_extra WHERE nome = 'Passeio');

INSERT INTO servico_extra (estadia_id, tipo_servico_extra_id, tipo, custo, data_hora, autor_id, created_by)
SELECT @estadia_max, @tipo_banho, 'Banho', 12.50, NOW(), @cuidador_id, @cuidador_id
WHERE NOT EXISTS (
  SELECT 1 FROM servico_extra
  WHERE estadia_id = @estadia_max AND tipo_servico_extra_id = @tipo_banho
);

INSERT INTO servico_extra (estadia_id, tipo_servico_extra_id, tipo, custo, data_hora, autor_id, created_by)
SELECT @estadia_max, @tipo_passeio, 'Passeio', 5.00, NOW(), @cuidador_id, @cuidador_id
WHERE NOT EXISTS (
  SELECT 1 FROM servico_extra
  WHERE estadia_id = @estadia_max AND tipo_servico_extra_id = @tipo_passeio
);

INSERT INTO nota (reserva_id, descricao, autor_id, data_hora, created_by)
SELECT @reserva_max, 'Tutor informou que Max estranha trovoada.', @rececao_id, NOW(), @rececao_id
WHERE NOT EXISTS (
  SELECT 1 FROM nota
  WHERE reserva_id = @reserva_max AND descricao = 'Tutor informou que Max estranha trovoada.'
);

INSERT INTO registo_cuidado (estadia_id, descricao, data_hora, autor_id, created_by)
SELECT @estadia_max, 'Alimentacao da manha realizada e agua renovada.', NOW(), @cuidador_id, @cuidador_id
WHERE NOT EXISTS (
  SELECT 1 FROM registo_cuidado
  WHERE estadia_id = @estadia_max AND descricao = 'Alimentacao da manha realizada e agua renovada.'
);

INSERT INTO intervencao_clinica (estadia_id, descricao, custo, data_hora, medico_id, created_by)
SELECT @estadia_max, 'Observacao clinica preventiva sem sinais de alarme.', 20.00, NOW(), @veterinario_id, @veterinario_id
WHERE NOT EXISTS (
  SELECT 1 FROM intervencao_clinica
  WHERE estadia_id = @estadia_max AND descricao = 'Observacao clinica preventiva sem sinais de alarme.'
);

INSERT INTO alteracao_estado_saude (estadia_id, descricao, severidade, data_hora, autor_id, created_by)
SELECT @estadia_max, 'Ligeira perda de apetite observada no fim da tarde.', 'ALTERADO', NOW(), @veterinario_id, @veterinario_id
WHERE NOT EXISTS (
  SELECT 1 FROM alteracao_estado_saude
  WHERE estadia_id = @estadia_max AND descricao = 'Ligeira perda de apetite observada no fim da tarde.'
);

INSERT INTO plano_cuidados (
  animal_id, estadia_id, data_inicio, data_fim, prioridade, ativo,
  instrucoes, created_by
)
SELECT @animal_max, @estadia_max, NOW(), NULL, 'ROTINA', TRUE,
       'Plano base: alimentacao, passeio e observacao diaria.', @cuidador_id
WHERE NOT EXISTS (
  SELECT 1 FROM plano_cuidados WHERE estadia_id = @estadia_max
);

SET @plano_max = (SELECT id FROM plano_cuidados WHERE estadia_id = @estadia_max LIMIT 1);

INSERT INTO tarefa_cuidado (
  plano_cuidados_id, tipo, descricao, periodicidade, data_hora,
  concluida, autor_conclusao_id, created_by
)
SELECT @plano_max, 'ALIMENTACAO_MANHA', 'Dar racao senior sem cereais.', 'DIARIA',
       TIMESTAMP(CURDATE(), '08:30:00'), TRUE, @cuidador_id, @cuidador_id
WHERE NOT EXISTS (
  SELECT 1 FROM tarefa_cuidado
  WHERE plano_cuidados_id = @plano_max AND tipo = 'ALIMENTACAO_MANHA'
);

INSERT INTO tarefa_cuidado (
  plano_cuidados_id, tipo, descricao, periodicidade, data_hora,
  concluida, autor_conclusao_id, created_by
)
SELECT @plano_max, 'PASSEIO', 'Passeio supervisionado de 20 minutos.', 'DIARIA',
       TIMESTAMP(CURDATE(), '17:00:00'), FALSE, NULL, @cuidador_id
WHERE NOT EXISTS (
  SELECT 1 FROM tarefa_cuidado
  WHERE plano_cuidados_id = @plano_max AND tipo = 'PASSEIO'
);

INSERT INTO auditoria_evento (
  utilizador_id, operacao, entidade, entity_id, acao, detalhes, resultado, motivo_falha
)
SELECT @diretor_id, 'POVOACAO_MINIMA', 'Sistema', 0, 'SEED',
       '{"origem":"scripts/povoacao-minima.sql","objetivo":"dados mínimos de teste"}',
       'SUCESSO', NULL
WHERE NOT EXISTS (
  SELECT 1 FROM auditoria_evento
  WHERE operacao = 'POVOACAO_MINIMA' AND entidade = 'Sistema'
);

SELECT 'Povoacao minima aplicada com sucesso.' AS resultado;
