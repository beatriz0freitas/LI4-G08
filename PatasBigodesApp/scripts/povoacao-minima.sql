-- =============================================================================
-- Povoacao em Grande Escala — Sistema Patas & Bigodes
-- =============================================================================
-- Pressupostos:
--   • A migration V2__seed_alojamentos.sql é a fonte canónica dos alojamentos.
--   • A plataforma deve manter estritamente 32 alojamentos:
--     16 CANINO (A01-A16) e 16 FELINO (B01-B16).
--   • Idempotente: usa ON DUPLICATE KEY UPDATE e WHERE NOT EXISTS.
-- =============================================================================

SET @senha_diretor123 = '$2a$10$tj7H0DQvRBXGOcbPuEJwoeAIBAsuqGpAKZvDxYx3g5rEj1Wf6IOcm';

-- =============================================================================
-- 0. COLABORADORES ADICIONAIS
-- =============================================================================

INSERT INTO colaborador (username, nome, email, password_hash, tipo_colaborador, ativo)
VALUES
  ('cuidador2',    'Cuidador Dois',        'cuidador2@hotel.local',    @senha_diretor123, 'CUIDADOR',               TRUE),
  ('cuidador3',    'Cuidador Tres',        'cuidador3@hotel.local',    @senha_diretor123, 'CUIDADOR',               TRUE),
  ('cuidador4',    'Cuidador Quatro',      'cuidador4@hotel.local',    @senha_diretor123, 'CUIDADOR',               TRUE),
  ('cuidador5',    'Cuidador Cinco',       'cuidador5@hotel.local',    @senha_diretor123, 'CUIDADOR',               TRUE),
  ('rececao2',     'Funcionario Rececao 2','rececao2@hotel.local',     @senha_diretor123, 'FUNCIONARIO_RECEPCAO',   TRUE),
  ('limpeza2',     'Responsavel Limpeza 2','limpeza2@hotel.local',     @senha_diretor123, 'RESPONSAVEL_LIMPEZA',    TRUE),
  ('veterinario2', 'Medico Veterinario 2', 'veterinario2@hotel.local', @senha_diretor123, 'MEDICO_VETERINARIO',     TRUE)
ON DUPLICATE KEY UPDATE
  nome             = VALUES(nome),
  email            = VALUES(email),
  password_hash    = VALUES(password_hash),
  tipo_colaborador = VALUES(tipo_colaborador),
  ativo            = VALUES(ativo);

SET @diretor_id     = (SELECT id FROM colaborador WHERE username = 'diretor');
SET @rececao_id     = (SELECT id FROM colaborador WHERE username = 'rececao');
SET @rececao2_id    = (SELECT id FROM colaborador WHERE username = 'rececao2');
SET @cuidador_id    = (SELECT id FROM colaborador WHERE username = 'cuidador');
SET @cuidador2_id   = (SELECT id FROM colaborador WHERE username = 'cuidador2');
SET @cuidador3_id   = (SELECT id FROM colaborador WHERE username = 'cuidador3');
SET @cuidador4_id   = (SELECT id FROM colaborador WHERE username = 'cuidador4');
SET @cuidador5_id   = (SELECT id FROM colaborador WHERE username = 'cuidador5');
SET @veterinario_id = (SELECT id FROM colaborador WHERE username = 'veterinario');
SET @vet2_id        = (SELECT id FROM colaborador WHERE username = 'veterinario2');
SET @limpeza_id     = (SELECT id FROM colaborador WHERE username = 'limpeza');
SET @limpeza2_id    = (SELECT id FROM colaborador WHERE username = 'limpeza2');

-- =============================================================================
-- 1. ALOJAMENTOS CANÓNICOS (32 total)
--    Ala Canina:  16 CANINO (A01-A16)
--    Ala Felina:  16 FELINO (B01-B16)
-- =============================================================================

INSERT INTO alojamento (identificacao, tipo, capacidade, estado_limpeza)
VALUES
  -- Ala Canina
  ('A01', 'CANINO', 1, 'CONCLUIDO'),
  ('A02', 'CANINO', 1, 'CONCLUIDO'),
  ('A03', 'CANINO', 1, 'CONCLUIDO'),
  ('A04', 'CANINO', 1, 'CONCLUIDO'),
  ('A05', 'CANINO', 1, 'CONCLUIDO'),
  ('A06', 'CANINO', 1, 'PENDENTE'),
  ('A07', 'CANINO', 1, 'CONCLUIDO'),
  ('A08', 'CANINO', 1, 'CONCLUIDO'),
  ('A09', 'CANINO', 1, 'PENDENTE'),
  ('A10', 'CANINO', 1, 'CONCLUIDO'),
  ('A11', 'CANINO', 1, 'CONCLUIDO'),
  ('A12', 'CANINO', 1, 'CONCLUIDO'),
  ('A13', 'CANINO', 1, 'PENDENTE'),
  ('A14', 'CANINO', 1, 'CONCLUIDO'),
  ('A15', 'CANINO', 1, 'CONCLUIDO'),
  ('A16', 'CANINO', 1, 'CONCLUIDO'),
  -- Ala Felina
  ('B01', 'FELINO', 1, 'CONCLUIDO'),
  ('B02', 'FELINO', 1, 'CONCLUIDO'),
  ('B03', 'FELINO', 1, 'CONCLUIDO'),
  ('B04', 'FELINO', 1, 'CONCLUIDO'),
  ('B05', 'FELINO', 1, 'CONCLUIDO'),
  ('B06', 'FELINO', 1, 'PENDENTE'),
  ('B07', 'FELINO', 1, 'CONCLUIDO'),
  ('B08', 'FELINO', 1, 'CONCLUIDO'),
  ('B09', 'FELINO', 1, 'CONCLUIDO'),
  ('B10', 'FELINO', 1, 'PENDENTE'),
  ('B11', 'FELINO', 1, 'CONCLUIDO'),
  ('B12', 'FELINO', 1, 'CONCLUIDO'),
  ('B13', 'FELINO', 1, 'CONCLUIDO'),
  ('B14', 'FELINO', 1, 'PENDENTE'),
  ('B15', 'FELINO', 1, 'CONCLUIDO'),
  ('B16', 'FELINO', 1, 'CONCLUIDO')
ON DUPLICATE KEY UPDATE
  tipo           = VALUES(tipo),
  capacidade     = VALUES(capacidade),
  estado_limpeza = VALUES(estado_limpeza);

-- =============================================================================
-- 2. SERVIÇOS EXTRA ADICIONAIS
-- =============================================================================

INSERT INTO tipo_servico_extra (nome, descricao, ativo)
VALUES
  ('Consulta Veterinaria', 'Consulta medica veterinaria presencial', TRUE),
  ('Corte de Unhas',       'Corte e limpeza de unhas',               TRUE),
  ('Treino Basico',        'Sessao de treino de obediencia basica',   TRUE)
ON DUPLICATE KEY UPDATE
  descricao = VALUES(descricao),
  ativo     = VALUES(ativo);

-- =============================================================================
-- 3. TUTORES (20 tutores)
-- =============================================================================

INSERT INTO tutor (nome, nif, contacto, email)
VALUES
  ('Diana Oliveira',    '910000001', '920000001', 'diana.oliveira@example.local'),
  ('Eduardo Santos',    '910000002', '920000002', 'eduardo.santos@example.local'),
  ('Filipa Rodrigues',  '910000003', '920000003', 'filipa.rodrigues@example.local'),
  ('Gustavo Lima',      '910000004', '920000004', 'gustavo.lima@example.local'),
  ('Helena Carvalho',   '910000005', '920000005', 'helena.carvalho@example.local'),
  ('Ines Pereira',      '910000006', '920000006', 'ines.pereira@example.local'),
  ('Jorge Fernandes',   '910000007', '920000007', 'jorge.fernandes@example.local'),
  ('Katia Moreira',     '910000008', '920000008', 'katia.moreira@example.local'),
  ('Luis Teixeira',     '910000009', '920000009', 'luis.teixeira@example.local'),
  ('Margarida Sousa',   '910000010', '920000010', 'margarida.sousa@example.local'),
  ('Nuno Azevedo',      '910000011', '920000011', 'nuno.azevedo@example.local'),
  ('Olivia Correia',    '910000012', '920000012', 'olivia.correia@example.local'),
  ('Paulo Mendes',      '910000013', '920000013', 'paulo.mendes@example.local'),
  ('Rita Gomes',        '910000014', '920000014', 'rita.gomes@example.local'),
  ('Sergio Lopes',      '910000015', '920000015', 'sergio.lopes@example.local'),
  ('Teresa Vieira',     '910000016', '920000016', 'teresa.vieira@example.local'),
  ('Ulisses Fonseca',   '910000017', '920000017', 'ulisses.fonseca@example.local'),
  ('Vera Cunha',        '910000018', '920000018', 'vera.cunha@example.local'),
  ('Xavier Pinto',      '910000019', '920000019', 'xavier.pinto@example.local'),
  ('Yolanda Batista',   '910000020', '920000020', 'yolanda.batista@example.local')
ON DUPLICATE KEY UPDATE
  nome      = VALUES(nome),
  contacto  = VALUES(contacto),
  email     = VALUES(email);

SET @t01 = (SELECT id FROM tutor WHERE nif = '910000001');
SET @t02 = (SELECT id FROM tutor WHERE nif = '910000002');
SET @t03 = (SELECT id FROM tutor WHERE nif = '910000003');
SET @t04 = (SELECT id FROM tutor WHERE nif = '910000004');
SET @t05 = (SELECT id FROM tutor WHERE nif = '910000005');
SET @t06 = (SELECT id FROM tutor WHERE nif = '910000006');
SET @t07 = (SELECT id FROM tutor WHERE nif = '910000007');
SET @t08 = (SELECT id FROM tutor WHERE nif = '910000008');
SET @t09 = (SELECT id FROM tutor WHERE nif = '910000009');
SET @t10 = (SELECT id FROM tutor WHERE nif = '910000010');
SET @t11 = (SELECT id FROM tutor WHERE nif = '910000011');
SET @t12 = (SELECT id FROM tutor WHERE nif = '910000012');
SET @t13 = (SELECT id FROM tutor WHERE nif = '910000013');
SET @t14 = (SELECT id FROM tutor WHERE nif = '910000014');
SET @t15 = (SELECT id FROM tutor WHERE nif = '910000015');
SET @t16 = (SELECT id FROM tutor WHERE nif = '910000016');
SET @t17 = (SELECT id FROM tutor WHERE nif = '910000017');
SET @t18 = (SELECT id FROM tutor WHERE nif = '910000018');
SET @t19 = (SELECT id FROM tutor WHERE nif = '910000019');
SET @t20 = (SELECT id FROM tutor WHERE nif = '910000020');

-- =============================================================================
-- 4. ANIMAIS (2 por tutor, mix de cães e gatos, estados de saúde variados)
-- =============================================================================

INSERT INTO animal (tutor_id, nome, especie, raca, data_nascimento, peso, estado_saude, necessidades_alimentares, medicacao_curso)
SELECT @t01, 'Rex',    'CAO',  'Pastor Alemao',    '2019-05-12', 30.00, 'NORMAL',   'Racao adulto premium',           NULL
WHERE NOT EXISTS (SELECT 1 FROM animal WHERE tutor_id = @t01 AND nome = 'Rex');

INSERT INTO animal (tutor_id, nome, especie, raca, data_nascimento, peso, estado_saude, necessidades_alimentares, medicacao_curso)
SELECT @t01, 'Luna',   'GATO', 'Persa',            '2022-01-20', 4.50, 'NORMAL',   'Racao seca felino adulto',       NULL
WHERE NOT EXISTS (SELECT 1 FROM animal WHERE tutor_id = @t01 AND nome = 'Luna');

INSERT INTO animal (tutor_id, nome, especie, raca, data_nascimento, peso, estado_saude, necessidades_alimentares, medicacao_curso)
SELECT @t02, 'Bobi',   'CAO',  'Cocker Spaniel',   '2020-08-03', 14.00, 'NORMAL',  'Racao adulto medio porte',       NULL
WHERE NOT EXISTS (SELECT 1 FROM animal WHERE tutor_id = @t02 AND nome = 'Bobi');

INSERT INTO animal (tutor_id, nome, especie, raca, data_nascimento, peso, estado_saude, necessidades_alimentares, medicacao_curso)
SELECT @t02, 'Nala',   'GATO', 'Siames',           '2021-03-15', 3.80, 'ALTERADO', 'Dieta renal humida',             'Suplemento renal diario'
WHERE NOT EXISTS (SELECT 1 FROM animal WHERE tutor_id = @t02 AND nome = 'Nala');

INSERT INTO animal (tutor_id, nome, especie, raca, data_nascimento, peso, estado_saude, necessidades_alimentares, medicacao_curso)
SELECT @t03, 'Simba',  'CAO',  'Golden Retriever', '2018-11-28', 28.00, 'NORMAL',  'Racao senior sem gluten',        NULL
WHERE NOT EXISTS (SELECT 1 FROM animal WHERE tutor_id = @t03 AND nome = 'Simba');

INSERT INTO animal (tutor_id, nome, especie, raca, data_nascimento, peso, estado_saude, necessidades_alimentares, medicacao_curso)
SELECT @t03, 'Kika',   'GATO', 'Angorá',           '2023-02-10', 3.20, 'NORMAL',   'Racao junior felino',            NULL
WHERE NOT EXISTS (SELECT 1 FROM animal WHERE tutor_id = @t03 AND nome = 'Kika');

INSERT INTO animal (tutor_id, nome, especie, raca, data_nascimento, peso, estado_saude, necessidades_alimentares, medicacao_curso)
SELECT @t04, 'Bruno',  'CAO',  'Bulldog Frances',  '2021-06-19', 12.00, 'ALTERADO','Dieta hipocalorica',             'Anti-inflamatorio semanal'
WHERE NOT EXISTS (SELECT 1 FROM animal WHERE tutor_id = @t04 AND nome = 'Bruno');

INSERT INTO animal (tutor_id, nome, especie, raca, data_nascimento, peso, estado_saude, necessidades_alimentares, medicacao_curso)
SELECT @t04, 'Mel',    'GATO', 'Europeu Comum',    '2020-09-05', 4.10, 'NORMAL',   'Racao seca adulto',              NULL
WHERE NOT EXISTS (SELECT 1 FROM animal WHERE tutor_id = @t04 AND nome = 'Mel');

INSERT INTO animal (tutor_id, nome, especie, raca, data_nascimento, peso, estado_saude, necessidades_alimentares, medicacao_curso)
SELECT @t05, 'Lola',   'CAO',  'Shih Tzu',         '2022-04-07', 7.00, 'NORMAL',   'Racao pequeno porte adulto',     NULL
WHERE NOT EXISTS (SELECT 1 FROM animal WHERE tutor_id = @t05 AND nome = 'Lola');

INSERT INTO animal (tutor_id, nome, especie, raca, data_nascimento, peso, estado_saude, necessidades_alimentares, medicacao_curso)
SELECT @t05, 'Mimi',   'GATO', 'Maine Coon',       '2019-12-30', 6.80, 'NORMAL',   'Racao senior felino grande',     NULL
WHERE NOT EXISTS (SELECT 1 FROM animal WHERE tutor_id = @t05 AND nome = 'Mimi');

INSERT INTO animal (tutor_id, nome, especie, raca, data_nascimento, peso, estado_saude, necessidades_alimentares, medicacao_curso)
SELECT @t06, 'Zeus',   'CAO',  'Husky Siberiano',  '2020-02-14', 22.00, 'NORMAL',  'Racao alto desempenho',          NULL
WHERE NOT EXISTS (SELECT 1 FROM animal WHERE tutor_id = @t06 AND nome = 'Zeus');

INSERT INTO animal (tutor_id, nome, especie, raca, data_nascimento, peso, estado_saude, necessidades_alimentares, medicacao_curso)
SELECT @t06, 'Mochi',  'GATO', 'Ragdoll',          '2023-07-01', 5.10, 'NORMAL',   'Racao junior felino premium',    NULL
WHERE NOT EXISTS (SELECT 1 FROM animal WHERE tutor_id = @t06 AND nome = 'Mochi');

INSERT INTO animal (tutor_id, nome, especie, raca, data_nascimento, peso, estado_saude, necessidades_alimentares, medicacao_curso)
SELECT @t07, 'Toby',   'CAO',  'Beagle',           '2017-09-23', 11.00, 'CRITICO', 'Dieta prescrita veterinaria',    'Medicacao cardiaca diaria'
WHERE NOT EXISTS (SELECT 1 FROM animal WHERE tutor_id = @t07 AND nome = 'Toby');

INSERT INTO animal (tutor_id, nome, especie, raca, data_nascimento, peso, estado_saude, necessidades_alimentares, medicacao_curso)
SELECT @t07, 'Coco',   'GATO', 'Bengala',          '2021-05-11', 4.70, 'ALTERADO', 'Comida humida premium',          'Comprimido anti-ansiedade noturno'
WHERE NOT EXISTS (SELECT 1 FROM animal WHERE tutor_id = @t07 AND nome = 'Coco');

INSERT INTO animal (tutor_id, nome, especie, raca, data_nascimento, peso, estado_saude, necessidades_alimentares, medicacao_curso)
SELECT @t08, 'Niko',   'CAO',  'Dalmata',          '2020-10-17', 26.00, 'NORMAL',  'Racao adulto grande porte',      NULL
WHERE NOT EXISTS (SELECT 1 FROM animal WHERE tutor_id = @t08 AND nome = 'Niko');

INSERT INTO animal (tutor_id, nome, especie, raca, data_nascimento, peso, estado_saude, necessidades_alimentares, medicacao_curso)
SELECT @t08, 'Pipa',   'GATO', 'Azul Russo',       '2022-08-22', 3.90, 'NORMAL',   'Racao seca adulto premium',      NULL
WHERE NOT EXISTS (SELECT 1 FROM animal WHERE tutor_id = @t08 AND nome = 'Pipa');

INSERT INTO animal (tutor_id, nome, especie, raca, data_nascimento, peso, estado_saude, necessidades_alimentares, medicacao_curso)
SELECT @t09, 'Bolt',   'CAO',  'Border Collie',    '2021-01-08', 18.00, 'NORMAL',  'Racao alto desempenho',          NULL
WHERE NOT EXISTS (SELECT 1 FROM animal WHERE tutor_id = @t09 AND nome = 'Bolt');

INSERT INTO animal (tutor_id, nome, especie, raca, data_nascimento, peso, estado_saude, necessidades_alimentares, medicacao_curso)
SELECT @t09, 'Fifi',   'GATO', 'Abissinio',        '2020-06-03', 4.00, 'NORMAL',   'Racao adulto medio',             NULL
WHERE NOT EXISTS (SELECT 1 FROM animal WHERE tutor_id = @t09 AND nome = 'Fifi');

INSERT INTO animal (tutor_id, nome, especie, raca, data_nascimento, peso, estado_saude, necessidades_alimentares, medicacao_curso)
SELECT @t10, 'Duke',   'CAO',  'Labrador',         '2019-03-29', 29.00, 'NORMAL',  'Racao senior',                   NULL
WHERE NOT EXISTS (SELECT 1 FROM animal WHERE tutor_id = @t10 AND nome = 'Duke');

INSERT INTO animal (tutor_id, nome, especie, raca, data_nascimento, peso, estado_saude, necessidades_alimentares, medicacao_curso)
SELECT @t10, 'Zara',   'GATO', 'Persa',            '2021-11-14', 4.30, 'ALTERADO', 'Dieta baixo fosforo',            'Comprimido renal manha e tarde'
WHERE NOT EXISTS (SELECT 1 FROM animal WHERE tutor_id = @t10 AND nome = 'Zara');

INSERT INTO animal (tutor_id, nome, especie, raca, data_nascimento, peso, estado_saude, necessidades_alimentares, medicacao_curso)
SELECT @t11, 'Argo',   'CAO',  'Rottweiler',       '2020-07-15', 40.00, 'NORMAL',  'Racao adulto grande porte',      NULL
WHERE NOT EXISTS (SELECT 1 FROM animal WHERE tutor_id = @t11 AND nome = 'Argo');

INSERT INTO animal (tutor_id, nome, especie, raca, data_nascimento, peso, estado_saude, necessidades_alimentares, medicacao_curso)
SELECT @t11, 'Sushi',  'GATO', 'Europeu Comum',    '2022-02-28', 3.60, 'NORMAL',   'Racao seca adulto',              NULL
WHERE NOT EXISTS (SELECT 1 FROM animal WHERE tutor_id = @t11 AND nome = 'Sushi');

INSERT INTO animal (tutor_id, nome, especie, raca, data_nascimento, peso, estado_saude, necessidades_alimentares, medicacao_curso)
SELECT @t12, 'Ace',    'CAO',  'Boxer',            '2021-09-04', 25.00, 'NORMAL',  'Racao adulto medio-grande',      NULL
WHERE NOT EXISTS (SELECT 1 FROM animal WHERE tutor_id = @t12 AND nome = 'Ace');

INSERT INTO animal (tutor_id, nome, especie, raca, data_nascimento, peso, estado_saude, necessidades_alimentares, medicacao_curso)
SELECT @t12, 'Bola',   'GATO', 'Siames',           '2020-04-17', 3.70, 'CRITICO',  'Dieta prescrita',                'Medicacao oncologica diaria'
WHERE NOT EXISTS (SELECT 1 FROM animal WHERE tutor_id = @t12 AND nome = 'Bola');

INSERT INTO animal (tutor_id, nome, especie, raca, data_nascimento, peso, estado_saude, necessidades_alimentares, medicacao_curso)
SELECT @t13, 'Rufi',   'CAO',  'Yorkshire',        '2023-03-10', 3.20, 'NORMAL',   'Racao mini adulto',              NULL
WHERE NOT EXISTS (SELECT 1 FROM animal WHERE tutor_id = @t13 AND nome = 'Rufi');

INSERT INTO animal (tutor_id, nome, especie, raca, data_nascimento, peso, estado_saude, necessidades_alimentares, medicacao_curso)
SELECT @t13, 'Nina',   'GATO', 'Maine Coon',       '2019-08-20', 7.20, 'NORMAL',   'Racao senior felino grande',     NULL
WHERE NOT EXISTS (SELECT 1 FROM animal WHERE tutor_id = @t13 AND nome = 'Nina');

INSERT INTO animal (tutor_id, nome, especie, raca, data_nascimento, peso, estado_saude, necessidades_alimentares, medicacao_curso)
SELECT @t14, 'Buster', 'CAO',  'Dachshund',        '2020-12-01', 9.00, 'ALTERADO', 'Dieta articular',                'Condroprotector diario'
WHERE NOT EXISTS (SELECT 1 FROM animal WHERE tutor_id = @t14 AND nome = 'Buster');

INSERT INTO animal (tutor_id, nome, especie, raca, data_nascimento, peso, estado_saude, necessidades_alimentares, medicacao_curso)
SELECT @t14, 'Tico',   'GATO', 'Ragdoll',          '2022-06-15', 5.50, 'NORMAL',   'Racao adulto premium',           NULL
WHERE NOT EXISTS (SELECT 1 FROM animal WHERE tutor_id = @t14 AND nome = 'Tico');

INSERT INTO animal (tutor_id, nome, especie, raca, data_nascimento, peso, estado_saude, necessidades_alimentares, medicacao_curso)
SELECT @t15, 'Gaia',   'CAO',  'Weimaraner',       '2019-10-07', 27.00, 'NORMAL',  'Racao adulto grande',            NULL
WHERE NOT EXISTS (SELECT 1 FROM animal WHERE tutor_id = @t15 AND nome = 'Gaia');

INSERT INTO animal (tutor_id, nome, especie, raca, data_nascimento, peso, estado_saude, necessidades_alimentares, medicacao_curso)
SELECT @t15, 'Puff',   'GATO', 'Angorá',           '2021-04-25', 4.60, 'NORMAL',   'Racao adulto pelo longo',        NULL
WHERE NOT EXISTS (SELECT 1 FROM animal WHERE tutor_id = @t15 AND nome = 'Puff');

INSERT INTO animal (tutor_id, nome, especie, raca, data_nascimento, peso, estado_saude, necessidades_alimentares, medicacao_curso)
SELECT @t16, 'Rocco',  'CAO',  'Dobermann',        '2020-01-21', 34.00, 'NORMAL',  'Racao adulto grande porte',      NULL
WHERE NOT EXISTS (SELECT 1 FROM animal WHERE tutor_id = @t16 AND nome = 'Rocco');

INSERT INTO animal (tutor_id, nome, especie, raca, data_nascimento, peso, estado_saude, necessidades_alimentares, medicacao_curso)
SELECT @t16, 'Diva',   'GATO', 'Bengala',          '2022-10-09', 4.20, 'NORMAL',   'Racao adulto medio',             NULL
WHERE NOT EXISTS (SELECT 1 FROM animal WHERE tutor_id = @t16 AND nome = 'Diva');

INSERT INTO animal (tutor_id, nome, especie, raca, data_nascimento, peso, estado_saude, necessidades_alimentares, medicacao_curso)
SELECT @t17, 'Paco',   'CAO',  'Chow Chow',        '2021-08-12', 20.00, 'ALTERADO','Racao adulto sem corantes',      'Anti-histaminico semanal'
WHERE NOT EXISTS (SELECT 1 FROM animal WHERE tutor_id = @t17 AND nome = 'Paco');

INSERT INTO animal (tutor_id, nome, especie, raca, data_nascimento, peso, estado_saude, necessidades_alimentares, medicacao_curso)
SELECT @t17, 'Yuki',   'GATO', 'Azul Russo',       '2020-03-18', 3.80, 'NORMAL',   'Racao seca adulto',              NULL
WHERE NOT EXISTS (SELECT 1 FROM animal WHERE tutor_id = @t17 AND nome = 'Yuki');

INSERT INTO animal (tutor_id, nome, especie, raca, data_nascimento, peso, estado_saude, necessidades_alimentares, medicacao_curso)
SELECT @t18, 'Woody',  'CAO',  'Setter Irlandes',  '2018-06-30', 25.00, 'CRITICO', 'Dieta prescrita cardiaca',       'Diuretico e beta-bloqueante diarios'
WHERE NOT EXISTS (SELECT 1 FROM animal WHERE tutor_id = @t18 AND nome = 'Woody');

INSERT INTO animal (tutor_id, nome, especie, raca, data_nascimento, peso, estado_saude, necessidades_alimentares, medicacao_curso)
SELECT @t18, 'Gizmo',  'GATO', 'Europeu Comum',    '2021-12-05', 4.40, 'NORMAL',   'Racao adulto standard',          NULL
WHERE NOT EXISTS (SELECT 1 FROM animal WHERE tutor_id = @t18 AND nome = 'Gizmo');

INSERT INTO animal (tutor_id, nome, especie, raca, data_nascimento, peso, estado_saude, necessidades_alimentares, medicacao_curso)
SELECT @t19, 'Indie',  'CAO',  'Vizsla',           '2022-05-16', 19.00, 'NORMAL',  'Racao adulto medio-grande',      NULL
WHERE NOT EXISTS (SELECT 1 FROM animal WHERE tutor_id = @t19 AND nome = 'Indie');

INSERT INTO animal (tutor_id, nome, especie, raca, data_nascimento, peso, estado_saude, necessidades_alimentares, medicacao_curso)
SELECT @t19, 'Leo',    'GATO', 'Persa',            '2020-11-01', 5.00, 'NORMAL',   'Racao adulto pelo longo',        NULL
WHERE NOT EXISTS (SELECT 1 FROM animal WHERE tutor_id = @t19 AND nome = 'Leo');

INSERT INTO animal (tutor_id, nome, especie, raca, data_nascimento, peso, estado_saude, necessidades_alimentares, medicacao_curso)
SELECT @t20, 'Maya',   'CAO',  'Poodle',           '2023-01-28', 6.00, 'NORMAL',   'Racao pequeno porte junior',     NULL
WHERE NOT EXISTS (SELECT 1 FROM animal WHERE tutor_id = @t20 AND nome = 'Maya');

INSERT INTO animal (tutor_id, nome, especie, raca, data_nascimento, peso, estado_saude, necessidades_alimentares, medicacao_curso)
SELECT @t20, 'Oreo',   'GATO', 'Europeu Comum',    '2022-07-14', 4.00, 'ALTERADO', 'Dieta digestiva',                'Probiotico diario'
WHERE NOT EXISTS (SELECT 1 FROM animal WHERE tutor_id = @t20 AND nome = 'Oreo');

-- Resolver IDs dos animais
SET @a_rex    = (SELECT id FROM animal WHERE tutor_id = @t01 AND nome = 'Rex');
SET @a_luna   = (SELECT id FROM animal WHERE tutor_id = @t01 AND nome = 'Luna');
SET @a_bobi   = (SELECT id FROM animal WHERE tutor_id = @t02 AND nome = 'Bobi');
SET @a_nala   = (SELECT id FROM animal WHERE tutor_id = @t02 AND nome = 'Nala');
SET @a_simba  = (SELECT id FROM animal WHERE tutor_id = @t03 AND nome = 'Simba');
SET @a_kika   = (SELECT id FROM animal WHERE tutor_id = @t03 AND nome = 'Kika');
SET @a_bruno  = (SELECT id FROM animal WHERE tutor_id = @t04 AND nome = 'Bruno');
SET @a_mel    = (SELECT id FROM animal WHERE tutor_id = @t04 AND nome = 'Mel');
SET @a_lola   = (SELECT id FROM animal WHERE tutor_id = @t05 AND nome = 'Lola');
SET @a_mimi   = (SELECT id FROM animal WHERE tutor_id = @t05 AND nome = 'Mimi');
SET @a_zeus   = (SELECT id FROM animal WHERE tutor_id = @t06 AND nome = 'Zeus');
SET @a_mochi  = (SELECT id FROM animal WHERE tutor_id = @t06 AND nome = 'Mochi');
SET @a_toby   = (SELECT id FROM animal WHERE tutor_id = @t07 AND nome = 'Toby');
SET @a_coco   = (SELECT id FROM animal WHERE tutor_id = @t07 AND nome = 'Coco');
SET @a_niko   = (SELECT id FROM animal WHERE tutor_id = @t08 AND nome = 'Niko');
SET @a_pipa   = (SELECT id FROM animal WHERE tutor_id = @t08 AND nome = 'Pipa');
SET @a_bolt   = (SELECT id FROM animal WHERE tutor_id = @t09 AND nome = 'Bolt');
SET @a_fifi   = (SELECT id FROM animal WHERE tutor_id = @t09 AND nome = 'Fifi');
SET @a_duke   = (SELECT id FROM animal WHERE tutor_id = @t10 AND nome = 'Duke');
SET @a_zara   = (SELECT id FROM animal WHERE tutor_id = @t10 AND nome = 'Zara');
SET @a_argo   = (SELECT id FROM animal WHERE tutor_id = @t11 AND nome = 'Argo');
SET @a_sushi  = (SELECT id FROM animal WHERE tutor_id = @t11 AND nome = 'Sushi');
SET @a_ace    = (SELECT id FROM animal WHERE tutor_id = @t12 AND nome = 'Ace');
SET @a_bola   = (SELECT id FROM animal WHERE tutor_id = @t12 AND nome = 'Bola');
SET @a_rufi   = (SELECT id FROM animal WHERE tutor_id = @t13 AND nome = 'Rufi');
SET @a_nina   = (SELECT id FROM animal WHERE tutor_id = @t13 AND nome = 'Nina');
SET @a_buster = (SELECT id FROM animal WHERE tutor_id = @t14 AND nome = 'Buster');
SET @a_tico   = (SELECT id FROM animal WHERE tutor_id = @t14 AND nome = 'Tico');
SET @a_gaia   = (SELECT id FROM animal WHERE tutor_id = @t15 AND nome = 'Gaia');
SET @a_puff   = (SELECT id FROM animal WHERE tutor_id = @t15 AND nome = 'Puff');
SET @a_rocco  = (SELECT id FROM animal WHERE tutor_id = @t16 AND nome = 'Rocco');
SET @a_diva   = (SELECT id FROM animal WHERE tutor_id = @t16 AND nome = 'Diva');
SET @a_paco   = (SELECT id FROM animal WHERE tutor_id = @t17 AND nome = 'Paco');
SET @a_yuki   = (SELECT id FROM animal WHERE tutor_id = @t17 AND nome = 'Yuki');
SET @a_woody  = (SELECT id FROM animal WHERE tutor_id = @t18 AND nome = 'Woody');
SET @a_gizmo  = (SELECT id FROM animal WHERE tutor_id = @t18 AND nome = 'Gizmo');
SET @a_indie  = (SELECT id FROM animal WHERE tutor_id = @t19 AND nome = 'Indie');
SET @a_leo    = (SELECT id FROM animal WHERE tutor_id = @t19 AND nome = 'Leo');
SET @a_maya   = (SELECT id FROM animal WHERE tutor_id = @t20 AND nome = 'Maya');
SET @a_oreo   = (SELECT id FROM animal WHERE tutor_id = @t20 AND nome = 'Oreo');

-- Resolver IDs dos alojamentos canónicos
SET @c03  = (SELECT id FROM alojamento WHERE identificacao = 'A03');
SET @c04  = (SELECT id FROM alojamento WHERE identificacao = 'A04');
SET @c05  = (SELECT id FROM alojamento WHERE identificacao = 'A05');
SET @c06  = (SELECT id FROM alojamento WHERE identificacao = 'A06');
SET @c07  = (SELECT id FROM alojamento WHERE identificacao = 'A07');
SET @c08  = (SELECT id FROM alojamento WHERE identificacao = 'A08');
SET @c09  = (SELECT id FROM alojamento WHERE identificacao = 'A09');
SET @c10  = (SELECT id FROM alojamento WHERE identificacao = 'A10');
SET @c11  = (SELECT id FROM alojamento WHERE identificacao = 'A11');
SET @c12  = (SELECT id FROM alojamento WHERE identificacao = 'A12');
SET @c13  = (SELECT id FROM alojamento WHERE identificacao = 'A13');
SET @f03  = (SELECT id FROM alojamento WHERE identificacao = 'B03');
SET @f04  = (SELECT id FROM alojamento WHERE identificacao = 'B04');
SET @f05  = (SELECT id FROM alojamento WHERE identificacao = 'B05');
SET @f06  = (SELECT id FROM alojamento WHERE identificacao = 'B06');
SET @f07  = (SELECT id FROM alojamento WHERE identificacao = 'B07');
SET @f08  = (SELECT id FROM alojamento WHERE identificacao = 'B08');
SET @f09  = (SELECT id FROM alojamento WHERE identificacao = 'B09');
SET @f10  = (SELECT id FROM alojamento WHERE identificacao = 'B10');
SET @f11  = (SELECT id FROM alojamento WHERE identificacao = 'B11');
SET @f12  = (SELECT id FROM alojamento WHERE identificacao = 'B12');

-- =============================================================================
-- 5. RESERVAS
--    Mistura de: passadas (CONCLUIDA), ativas (EM_CURSO), futuras (CONFIRMADA),
--    e canceladas (CANCELADA)
-- =============================================================================

-- PASSADAS (já concluídas — histórico)
INSERT INTO reserva (tutor_id, animal_id, alojamento_id, data_inicio, data_fim, estado)
SELECT @t01, @a_rex,   @c03, DATE_SUB(CURDATE(), INTERVAL 60 DAY), DATE_SUB(CURDATE(), INTERVAL 55 DAY), 'CONCLUIDA'
WHERE NOT EXISTS (SELECT 1 FROM reserva WHERE animal_id = @a_rex  AND data_inicio = DATE_SUB(CURDATE(), INTERVAL 60 DAY));

INSERT INTO reserva (tutor_id, animal_id, alojamento_id, data_inicio, data_fim, estado)
SELECT @t02, @a_bobi,  @c04, DATE_SUB(CURDATE(), INTERVAL 50 DAY), DATE_SUB(CURDATE(), INTERVAL 45 DAY), 'CONCLUIDA'
WHERE NOT EXISTS (SELECT 1 FROM reserva WHERE animal_id = @a_bobi AND data_inicio = DATE_SUB(CURDATE(), INTERVAL 50 DAY));

INSERT INTO reserva (tutor_id, animal_id, alojamento_id, data_inicio, data_fim, estado)
SELECT @t03, @a_simba, @c05, DATE_SUB(CURDATE(), INTERVAL 40 DAY), DATE_SUB(CURDATE(), INTERVAL 35 DAY), 'CONCLUIDA'
WHERE NOT EXISTS (SELECT 1 FROM reserva WHERE animal_id = @a_simba AND data_inicio = DATE_SUB(CURDATE(), INTERVAL 40 DAY));

INSERT INTO reserva (tutor_id, animal_id, alojamento_id, data_inicio, data_fim, estado)
SELECT @t01, @a_luna,  @f03, DATE_SUB(CURDATE(), INTERVAL 45 DAY), DATE_SUB(CURDATE(), INTERVAL 40 DAY), 'CONCLUIDA'
WHERE NOT EXISTS (SELECT 1 FROM reserva WHERE animal_id = @a_luna AND data_inicio = DATE_SUB(CURDATE(), INTERVAL 45 DAY));

INSERT INTO reserva (tutor_id, animal_id, alojamento_id, data_inicio, data_fim, estado)
SELECT @t04, @a_mel,   @f04, DATE_SUB(CURDATE(), INTERVAL 35 DAY), DATE_SUB(CURDATE(), INTERVAL 30 DAY), 'CONCLUIDA'
WHERE NOT EXISTS (SELECT 1 FROM reserva WHERE animal_id = @a_mel AND data_inicio = DATE_SUB(CURDATE(), INTERVAL 35 DAY));

INSERT INTO reserva (tutor_id, animal_id, alojamento_id, data_inicio, data_fim, estado)
SELECT @t05, @a_mimi,  @f05, DATE_SUB(CURDATE(), INTERVAL 30 DAY), DATE_SUB(CURDATE(), INTERVAL 25 DAY), 'CONCLUIDA'
WHERE NOT EXISTS (SELECT 1 FROM reserva WHERE animal_id = @a_mimi AND data_inicio = DATE_SUB(CURDATE(), INTERVAL 30 DAY));

INSERT INTO reserva (tutor_id, animal_id, alojamento_id, data_inicio, data_fim, estado)
SELECT @t06, @a_zeus,  @c06, DATE_SUB(CURDATE(), INTERVAL 25 DAY), DATE_SUB(CURDATE(), INTERVAL 20 DAY), 'CONCLUIDA'
WHERE NOT EXISTS (SELECT 1 FROM reserva WHERE animal_id = @a_zeus AND data_inicio = DATE_SUB(CURDATE(), INTERVAL 25 DAY));

INSERT INTO reserva (tutor_id, animal_id, alojamento_id, data_inicio, data_fim, estado)
SELECT @t08, @a_niko,  @c07, DATE_SUB(CURDATE(), INTERVAL 20 DAY), DATE_SUB(CURDATE(), INTERVAL 15 DAY), 'CONCLUIDA'
WHERE NOT EXISTS (SELECT 1 FROM reserva WHERE animal_id = @a_niko AND data_inicio = DATE_SUB(CURDATE(), INTERVAL 20 DAY));

INSERT INTO reserva (tutor_id, animal_id, alojamento_id, data_inicio, data_fim, estado)
SELECT @t09, @a_bolt,  @c08, DATE_SUB(CURDATE(), INTERVAL 15 DAY), DATE_SUB(CURDATE(), INTERVAL 10 DAY), 'CONCLUIDA'
WHERE NOT EXISTS (SELECT 1 FROM reserva WHERE animal_id = @a_bolt AND data_inicio = DATE_SUB(CURDATE(), INTERVAL 15 DAY));

INSERT INTO reserva (tutor_id, animal_id, alojamento_id, data_inicio, data_fim, estado)
SELECT @t10, @a_duke,  @c09, DATE_SUB(CURDATE(), INTERVAL 10 DAY), DATE_SUB(CURDATE(), INTERVAL 5 DAY), 'CONCLUIDA'
WHERE NOT EXISTS (SELECT 1 FROM reserva WHERE animal_id = @a_duke AND data_inicio = DATE_SUB(CURDATE(), INTERVAL 10 DAY));

-- CANCELADAS
INSERT INTO reserva (tutor_id, animal_id, alojamento_id, data_inicio, data_fim, estado)
SELECT @t11, @a_argo,  @c10, DATE_SUB(CURDATE(), INTERVAL 20 DAY), DATE_SUB(CURDATE(), INTERVAL 15 DAY), 'CANCELADA'
WHERE NOT EXISTS (SELECT 1 FROM reserva WHERE animal_id = @a_argo AND data_inicio = DATE_SUB(CURDATE(), INTERVAL 20 DAY));

INSERT INTO reserva (tutor_id, animal_id, alojamento_id, data_inicio, data_fim, estado)
SELECT @t12, @a_bola,  @f06, DATE_SUB(CURDATE(), INTERVAL 12 DAY), DATE_SUB(CURDATE(), INTERVAL 8 DAY), 'CANCELADA'
WHERE NOT EXISTS (SELECT 1 FROM reserva WHERE animal_id = @a_bola AND data_inicio = DATE_SUB(CURDATE(), INTERVAL 12 DAY));

-- ATIVAS (em curso hoje)
INSERT INTO reserva (tutor_id, animal_id, alojamento_id, data_inicio, data_fim, estado)
SELECT @t04, @a_bruno, @c11, DATE_SUB(CURDATE(), INTERVAL 2 DAY), DATE_ADD(CURDATE(), INTERVAL 3 DAY), 'ATIVA'
WHERE NOT EXISTS (SELECT 1 FROM reserva WHERE animal_id = @a_bruno AND data_inicio = DATE_SUB(CURDATE(), INTERVAL 2 DAY));

INSERT INTO reserva (tutor_id, animal_id, alojamento_id, data_inicio, data_fim, estado)
SELECT @t02, @a_nala,  @f07, DATE_SUB(CURDATE(), INTERVAL 1 DAY), DATE_ADD(CURDATE(), INTERVAL 4 DAY), 'ATIVA'
WHERE NOT EXISTS (SELECT 1 FROM reserva WHERE animal_id = @a_nala AND data_inicio = DATE_SUB(CURDATE(), INTERVAL 1 DAY));

INSERT INTO reserva (tutor_id, animal_id, alojamento_id, data_inicio, data_fim, estado)
SELECT @t07, @a_toby,  @c12, DATE_SUB(CURDATE(), INTERVAL 3 DAY), DATE_ADD(CURDATE(), INTERVAL 2 DAY), 'ATIVA'
WHERE NOT EXISTS (SELECT 1 FROM reserva WHERE animal_id = @a_toby AND data_inicio = DATE_SUB(CURDATE(), INTERVAL 3 DAY));

INSERT INTO reserva (tutor_id, animal_id, alojamento_id, data_inicio, data_fim, estado)
SELECT @t10, @a_zara,  @f08, DATE_SUB(CURDATE(), INTERVAL 1 DAY), DATE_ADD(CURDATE(), INTERVAL 5 DAY), 'ATIVA'
WHERE NOT EXISTS (SELECT 1 FROM reserva WHERE animal_id = @a_zara AND data_inicio = DATE_SUB(CURDATE(), INTERVAL 1 DAY));

INSERT INTO reserva (tutor_id, animal_id, alojamento_id, data_inicio, data_fim, estado)
SELECT @t12, @a_ace,   @c13, CURDATE(), DATE_ADD(CURDATE(), INTERVAL 7 DAY), 'ATIVA'
WHERE NOT EXISTS (SELECT 1 FROM reserva WHERE animal_id = @a_ace AND data_inicio = CURDATE());

INSERT INTO reserva (tutor_id, animal_id, alojamento_id, data_inicio, data_fim, estado)
SELECT @t07, @a_coco,  @f09, CURDATE(), DATE_ADD(CURDATE(), INTERVAL 3 DAY), 'ATIVA'
WHERE NOT EXISTS (SELECT 1 FROM reserva WHERE animal_id = @a_coco AND data_inicio = CURDATE());

-- FUTURAS (confirmadas)
INSERT INTO reserva (tutor_id, animal_id, alojamento_id, data_inicio, data_fim, estado)
SELECT @t13, @a_rufi,  @c03, DATE_ADD(CURDATE(), INTERVAL 5 DAY),  DATE_ADD(CURDATE(), INTERVAL 10 DAY), 'CONFIRMADA'
WHERE NOT EXISTS (SELECT 1 FROM reserva WHERE animal_id = @a_rufi AND data_inicio = DATE_ADD(CURDATE(), INTERVAL 5 DAY));

INSERT INTO reserva (tutor_id, animal_id, alojamento_id, data_inicio, data_fim, estado)
SELECT @t13, @a_nina,  @f03, DATE_ADD(CURDATE(), INTERVAL 5 DAY),  DATE_ADD(CURDATE(), INTERVAL 10 DAY), 'CONFIRMADA'
WHERE NOT EXISTS (SELECT 1 FROM reserva WHERE animal_id = @a_nina AND data_inicio = DATE_ADD(CURDATE(), INTERVAL 5 DAY));

INSERT INTO reserva (tutor_id, animal_id, alojamento_id, data_inicio, data_fim, estado)
SELECT @t14, @a_buster,@c04, DATE_ADD(CURDATE(), INTERVAL 7 DAY),  DATE_ADD(CURDATE(), INTERVAL 14 DAY), 'CONFIRMADA'
WHERE NOT EXISTS (SELECT 1 FROM reserva WHERE animal_id = @a_buster AND data_inicio = DATE_ADD(CURDATE(), INTERVAL 7 DAY));

INSERT INTO reserva (tutor_id, animal_id, alojamento_id, data_inicio, data_fim, estado)
SELECT @t14, @a_tico,  @f04, DATE_ADD(CURDATE(), INTERVAL 7 DAY),  DATE_ADD(CURDATE(), INTERVAL 14 DAY), 'CONFIRMADA'
WHERE NOT EXISTS (SELECT 1 FROM reserva WHERE animal_id = @a_tico AND data_inicio = DATE_ADD(CURDATE(), INTERVAL 7 DAY));

INSERT INTO reserva (tutor_id, animal_id, alojamento_id, data_inicio, data_fim, estado)
SELECT @t15, @a_gaia,  @c05, DATE_ADD(CURDATE(), INTERVAL 10 DAY), DATE_ADD(CURDATE(), INTERVAL 17 DAY), 'CONFIRMADA'
WHERE NOT EXISTS (SELECT 1 FROM reserva WHERE animal_id = @a_gaia AND data_inicio = DATE_ADD(CURDATE(), INTERVAL 10 DAY));

INSERT INTO reserva (tutor_id, animal_id, alojamento_id, data_inicio, data_fim, estado)
SELECT @t15, @a_puff,  @f05, DATE_ADD(CURDATE(), INTERVAL 10 DAY), DATE_ADD(CURDATE(), INTERVAL 17 DAY), 'CONFIRMADA'
WHERE NOT EXISTS (SELECT 1 FROM reserva WHERE animal_id = @a_puff AND data_inicio = DATE_ADD(CURDATE(), INTERVAL 10 DAY));

INSERT INTO reserva (tutor_id, animal_id, alojamento_id, data_inicio, data_fim, estado)
SELECT @t16, @a_rocco, @c06, DATE_ADD(CURDATE(), INTERVAL 14 DAY), DATE_ADD(CURDATE(), INTERVAL 21 DAY), 'CONFIRMADA'
WHERE NOT EXISTS (SELECT 1 FROM reserva WHERE animal_id = @a_rocco AND data_inicio = DATE_ADD(CURDATE(), INTERVAL 14 DAY));

INSERT INTO reserva (tutor_id, animal_id, alojamento_id, data_inicio, data_fim, estado)
SELECT @t17, @a_paco,  @c07, DATE_ADD(CURDATE(), INTERVAL 20 DAY), DATE_ADD(CURDATE(), INTERVAL 25 DAY), 'CONFIRMADA'
WHERE NOT EXISTS (SELECT 1 FROM reserva WHERE animal_id = @a_paco AND data_inicio = DATE_ADD(CURDATE(), INTERVAL 20 DAY));

INSERT INTO reserva (tutor_id, animal_id, alojamento_id, data_inicio, data_fim, estado)
SELECT @t18, @a_woody, @c08, DATE_ADD(CURDATE(), INTERVAL 3 DAY),  DATE_ADD(CURDATE(), INTERVAL 10 DAY), 'CONFIRMADA'
WHERE NOT EXISTS (SELECT 1 FROM reserva WHERE animal_id = @a_woody AND data_inicio = DATE_ADD(CURDATE(), INTERVAL 3 DAY));

INSERT INTO reserva (tutor_id, animal_id, alojamento_id, data_inicio, data_fim, estado)
SELECT @t19, @a_indie, @c09, DATE_ADD(CURDATE(), INTERVAL 2 DAY),  DATE_ADD(CURDATE(), INTERVAL 9 DAY),  'CONFIRMADA'
WHERE NOT EXISTS (SELECT 1 FROM reserva WHERE animal_id = @a_indie AND data_inicio = DATE_ADD(CURDATE(), INTERVAL 2 DAY));

INSERT INTO reserva (tutor_id, animal_id, alojamento_id, data_inicio, data_fim, estado)
SELECT @t20, @a_maya,  @c10, DATE_ADD(CURDATE(), INTERVAL 6 DAY),  DATE_ADD(CURDATE(), INTERVAL 11 DAY), 'CONFIRMADA'
WHERE NOT EXISTS (SELECT 1 FROM reserva WHERE animal_id = @a_maya AND data_inicio = DATE_ADD(CURDATE(), INTERVAL 6 DAY));

INSERT INTO reserva (tutor_id, animal_id, alojamento_id, data_inicio, data_fim, estado)
SELECT @t20, @a_oreo,  @f10, DATE_ADD(CURDATE(), INTERVAL 6 DAY),  DATE_ADD(CURDATE(), INTERVAL 11 DAY), 'CONFIRMADA'
WHERE NOT EXISTS (SELECT 1 FROM reserva WHERE animal_id = @a_oreo AND data_inicio = DATE_ADD(CURDATE(), INTERVAL 6 DAY));

-- =============================================================================
-- 6. ESTADIAS para reservas ATIVAS + notas, cuidados, pagamentos, serviços
-- =============================================================================

-- Resolver IDs das reservas ativas
SET @r_bruno = (SELECT id FROM reserva WHERE animal_id = @a_bruno AND data_inicio = DATE_SUB(CURDATE(), INTERVAL 2 DAY) LIMIT 1);
SET @r_nala  = (SELECT id FROM reserva WHERE animal_id = @a_nala  AND data_inicio = DATE_SUB(CURDATE(), INTERVAL 1 DAY) LIMIT 1);
SET @r_toby  = (SELECT id FROM reserva WHERE animal_id = @a_toby  AND data_inicio = DATE_SUB(CURDATE(), INTERVAL 3 DAY) LIMIT 1);
SET @r_zara  = (SELECT id FROM reserva WHERE animal_id = @a_zara  AND data_inicio = DATE_SUB(CURDATE(), INTERVAL 1 DAY) LIMIT 1);
SET @r_ace   = (SELECT id FROM reserva WHERE animal_id = @a_ace   AND data_inicio = CURDATE() LIMIT 1);
SET @r_coco  = (SELECT id FROM reserva WHERE animal_id = @a_coco  AND data_inicio = CURDATE() LIMIT 1);

SET @tipo_banho    = (SELECT id FROM tipo_servico_extra WHERE nome = 'Banho');
SET @tipo_passeio  = (SELECT id FROM tipo_servico_extra WHERE nome = 'Passeio');
SET @tipo_tosa     = (SELECT id FROM tipo_servico_extra WHERE nome = 'Tosa');
SET @tipo_med      = (SELECT id FROM tipo_servico_extra WHERE nome = 'Medicacao Assistida');
SET @tipo_consulta = (SELECT id FROM tipo_servico_extra WHERE nome = 'Consulta Veterinaria');
SET @tipo_unhas    = (SELECT id FROM tipo_servico_extra WHERE nome = 'Corte de Unhas');

-- --- ESTADIA BRUNO ---
INSERT INTO estadia (reserva_id, data_inicio, data_fim, estado)
SELECT @r_bruno, DATE_SUB(NOW(), INTERVAL 2 DAY), NULL, 'EM_CURSO'
WHERE NOT EXISTS (SELECT 1 FROM estadia WHERE reserva_id = @r_bruno);

SET @e_bruno = (SELECT id FROM estadia WHERE reserva_id = @r_bruno LIMIT 1);

INSERT INTO pagamento (estadia_id, valor, metodo, tipo, estado)
SELECT @e_bruno, 36.00, 'CARTAO_DEBITO', 'CHECK_IN', 'LIQUIDADO'
WHERE NOT EXISTS (SELECT 1 FROM pagamento WHERE estadia_id = @e_bruno AND tipo = 'CHECK_IN');

INSERT INTO registo_cuidado (estadia_id, descricao, data_hora, autor_id, created_by)
SELECT @e_bruno, 'Alimentacao manha: dieta hipocalorica conforme indicado.', DATE_SUB(NOW(), INTERVAL 2 DAY), @cuidador_id, @cuidador_id
WHERE NOT EXISTS (SELECT 1 FROM registo_cuidado WHERE estadia_id = @e_bruno AND descricao LIKE 'Alimentacao manha%');

INSERT INTO registo_cuidado (estadia_id, descricao, data_hora, autor_id, created_by)
SELECT @e_bruno, 'Passeio vespertino realizado. Animal ativo e bem-disposto.', DATE_SUB(NOW(), INTERVAL 1 DAY), @cuidador2_id, @cuidador2_id
WHERE NOT EXISTS (SELECT 1 FROM registo_cuidado WHERE estadia_id = @e_bruno AND descricao LIKE 'Passeio vespertino%');

INSERT INTO servico_extra (estadia_id, tipo_servico_extra_id, tipo, custo, data_hora, autor_id, created_by)
SELECT @e_bruno, @tipo_banho, 'Banho', 12.50, DATE_SUB(NOW(), INTERVAL 1 DAY), @cuidador_id, @cuidador_id
WHERE NOT EXISTS (SELECT 1 FROM servico_extra WHERE estadia_id = @e_bruno AND tipo_servico_extra_id = @tipo_banho);

INSERT INTO nota (reserva_id, descricao, autor_id, data_hora, created_by)
SELECT @r_bruno, 'Tutor pediu para evitar passeios em piso molhado devido a problemas articulares.', @rececao_id, DATE_SUB(NOW(), INTERVAL 2 DAY), @rececao_id
WHERE NOT EXISTS (SELECT 1 FROM nota WHERE reserva_id = @r_bruno);

INSERT INTO plano_cuidados (animal_id, estadia_id, data_inicio, data_fim, prioridade, ativo, instrucoes, created_by)
SELECT @a_bruno, @e_bruno, DATE_SUB(NOW(), INTERVAL 2 DAY), NULL, 'URGENTE', TRUE,
       'Dieta hipocalorica estrita. Anti-inflamatorio semanal conforme prescricao. Passeios curtos.', @cuidador_id
WHERE NOT EXISTS (SELECT 1 FROM plano_cuidados WHERE estadia_id = @e_bruno);

-- --- ESTADIA NALA ---
INSERT INTO estadia (reserva_id, data_inicio, data_fim, estado)
SELECT @r_nala, DATE_SUB(NOW(), INTERVAL 1 DAY), NULL, 'EM_CURSO'
WHERE NOT EXISTS (SELECT 1 FROM estadia WHERE reserva_id = @r_nala);

SET @e_nala = (SELECT id FROM estadia WHERE reserva_id = @r_nala LIMIT 1);

INSERT INTO pagamento (estadia_id, valor, metodo, tipo, estado)
SELECT @e_nala, 30.00, 'CARTAO_CREDITO', 'CHECK_IN', 'LIQUIDADO'
WHERE NOT EXISTS (SELECT 1 FROM pagamento WHERE estadia_id = @e_nala AND tipo = 'CHECK_IN');

INSERT INTO registo_cuidado (estadia_id, descricao, data_hora, autor_id, created_by)
SELECT @e_nala, 'Comida humida renal administrada. Comprimido renal dado com alimento.', DATE_SUB(NOW(), INTERVAL 1 DAY), @cuidador3_id, @cuidador3_id
WHERE NOT EXISTS (SELECT 1 FROM registo_cuidado WHERE estadia_id = @e_nala AND descricao LIKE 'Comida humida%');

INSERT INTO servico_extra (estadia_id, tipo_servico_extra_id, tipo, custo, data_hora, autor_id, created_by)
SELECT @e_nala, @tipo_med, 'Medicacao Assistida', 8.00, DATE_SUB(NOW(), INTERVAL 1 DAY), @veterinario_id, @veterinario_id
WHERE NOT EXISTS (SELECT 1 FROM servico_extra WHERE estadia_id = @e_nala AND tipo_servico_extra_id = @tipo_med);

INSERT INTO intervencao_clinica (estadia_id, descricao, custo, data_hora, medico_id, created_by)
SELECT @e_nala, 'Avaliacao inicial. Estado renal estavél. Continua medicacao prescrita.', 25.00, DATE_SUB(NOW(), INTERVAL 1 DAY), @veterinario_id, @veterinario_id
WHERE NOT EXISTS (SELECT 1 FROM intervencao_clinica WHERE estadia_id = @e_nala);

INSERT INTO plano_cuidados (animal_id, estadia_id, data_inicio, data_fim, prioridade, ativo, instrucoes, created_by)
SELECT @a_nala, @e_nala, DATE_SUB(NOW(), INTERVAL 1 DAY), NULL, 'URGENTE', TRUE,
       'Administrar suplemento renal diario. Monitorizar ingestao de agua. Reportar qualquer alteracao.', @veterinario_id
WHERE NOT EXISTS (SELECT 1 FROM plano_cuidados WHERE estadia_id = @e_nala);

-- --- ESTADIA TOBY (CRITICO) ---
INSERT INTO estadia (reserva_id, data_inicio, data_fim, estado)
SELECT @r_toby, DATE_SUB(NOW(), INTERVAL 3 DAY), NULL, 'EM_CURSO'
WHERE NOT EXISTS (SELECT 1 FROM estadia WHERE reserva_id = @r_toby);

SET @e_toby = (SELECT id FROM estadia WHERE reserva_id = @r_toby LIMIT 1);

INSERT INTO pagamento (estadia_id, valor, metodo, tipo, estado)
SELECT @e_toby, 45.00, 'CARTAO_CREDITO', 'CHECK_IN', 'LIQUIDADO'
WHERE NOT EXISTS (SELECT 1 FROM pagamento WHERE estadia_id = @e_toby AND tipo = 'CHECK_IN');

INSERT INTO registo_cuidado (estadia_id, descricao, data_hora, autor_id, created_by)
SELECT @e_toby, 'Medicacao cardiaca administrada de manha com alimento.', DATE_SUB(NOW(), INTERVAL 3 DAY), @veterinario_id, @veterinario_id
WHERE NOT EXISTS (SELECT 1 FROM registo_cuidado WHERE estadia_id = @e_toby AND descricao LIKE 'Medicacao cardiaca%');

INSERT INTO registo_cuidado (estadia_id, descricao, data_hora, autor_id, created_by)
SELECT @e_toby, 'Animal apresentou ligeira letargia ao final da tarde. Monitorizado.', DATE_SUB(NOW(), INTERVAL 2 DAY), @vet2_id, @vet2_id
WHERE NOT EXISTS (SELECT 1 FROM registo_cuidado WHERE estadia_id = @e_toby AND descricao LIKE 'Animal apresentou%');

INSERT INTO alteracao_estado_saude (estadia_id, descricao, severidade, data_hora, autor_id, created_by)
SELECT @e_toby, 'Episodio de letargia registado. Sem outros sintomas. Tutor notificado.', 'ALTERADO', DATE_SUB(NOW(), INTERVAL 2 DAY), @vet2_id, @vet2_id
WHERE NOT EXISTS (SELECT 1 FROM alteracao_estado_saude WHERE estadia_id = @e_toby);

INSERT INTO intervencao_clinica (estadia_id, descricao, custo, data_hora, medico_id, created_by)
SELECT @e_toby, 'Auscultacao cardiaca: ritmo irregular ligeiro. Ajuste de dose discutido com tutor.', 40.00, DATE_SUB(NOW(), INTERVAL 2 DAY), @veterinario_id, @veterinario_id
WHERE NOT EXISTS (SELECT 1 FROM intervencao_clinica WHERE estadia_id = @e_toby);

INSERT INTO nota (reserva_id, descricao, autor_id, data_hora, created_by)
SELECT @r_toby, 'Animal cardiaco. Em caso de agravamento contactar Dr. Silva (915000000) antes de qualquer intervencao.', @rececao_id, DATE_SUB(NOW(), INTERVAL 3 DAY), @rececao_id
WHERE NOT EXISTS (SELECT 1 FROM nota WHERE reserva_id = @r_toby);

INSERT INTO plano_cuidados (animal_id, estadia_id, data_inicio, data_fim, prioridade, ativo, instrucoes, created_by)
SELECT @a_toby, @e_toby, DATE_SUB(NOW(), INTERVAL 3 DAY), NULL, 'URGENTE', TRUE,
       'Medicacao cardiaca diaria obrigatoria. Atividade fisica minima. Vigilancia continua. Notificar veterinario em caso de qualquer alteracao.', @veterinario_id
WHERE NOT EXISTS (SELECT 1 FROM plano_cuidados WHERE estadia_id = @e_toby);

-- --- ESTADIA ZARA ---
INSERT INTO estadia (reserva_id, data_inicio, data_fim, estado)
SELECT @r_zara, DATE_SUB(NOW(), INTERVAL 1 DAY), NULL, 'EM_CURSO'
WHERE NOT EXISTS (SELECT 1 FROM estadia WHERE reserva_id = @r_zara);

SET @e_zara = (SELECT id FROM estadia WHERE reserva_id = @r_zara LIMIT 1);

INSERT INTO pagamento (estadia_id, valor, metodo, tipo, estado)
SELECT @e_zara, 50.00, 'CARTAO_DEBITO', 'CHECK_IN', 'LIQUIDADO'
WHERE NOT EXISTS (SELECT 1 FROM pagamento WHERE estadia_id = @e_zara AND tipo = 'CHECK_IN');

INSERT INTO registo_cuidado (estadia_id, descricao, data_hora, autor_id, created_by)
SELECT @e_zara, 'Comprimido renal administrado de manha e tarde. Ingestao de agua normal.', DATE_SUB(NOW(), INTERVAL 1 DAY), @cuidador4_id, @cuidador4_id
WHERE NOT EXISTS (SELECT 1 FROM registo_cuidado WHERE estadia_id = @e_zara AND descricao LIKE 'Comprimido renal%');

INSERT INTO servico_extra (estadia_id, tipo_servico_extra_id, tipo, custo, data_hora, autor_id, created_by)
SELECT @e_zara, @tipo_unhas, 'Corte de Unhas', 6.00, NOW(), @cuidador4_id, @cuidador4_id
WHERE NOT EXISTS (SELECT 1 FROM servico_extra WHERE estadia_id = @e_zara AND tipo_servico_extra_id = @tipo_unhas);

INSERT INTO plano_cuidados (animal_id, estadia_id, data_inicio, data_fim, prioridade, ativo, instrucoes, created_by)
SELECT @a_zara, @e_zara, DATE_SUB(NOW(), INTERVAL 1 DAY), NULL, 'URGENTE', TRUE,
       'Dieta baixo fosforo. Comprimido renal 2x por dia com refeicao. Registar ingestao diaria de agua.', @veterinario_id
WHERE NOT EXISTS (SELECT 1 FROM plano_cuidados WHERE estadia_id = @e_zara);

-- --- ESTADIA ACE ---
INSERT INTO estadia (reserva_id, data_inicio, data_fim, estado)
SELECT @r_ace, NOW(), NULL, 'EM_CURSO'
WHERE NOT EXISTS (SELECT 1 FROM estadia WHERE reserva_id = @r_ace);

SET @e_ace = (SELECT id FROM estadia WHERE reserva_id = @r_ace LIMIT 1);

INSERT INTO pagamento (estadia_id, valor, metodo, tipo, estado)
SELECT @e_ace, 75.00, 'CARTAO_CREDITO', 'CHECK_IN', 'LIQUIDADO'
WHERE NOT EXISTS (SELECT 1 FROM pagamento WHERE estadia_id = @e_ace AND tipo = 'CHECK_IN');

INSERT INTO nota (reserva_id, descricao, autor_id, data_hora, created_by)
SELECT @r_ace, 'Animal muito energetico. Tutor pediu passeio diario obrigatorio.', @rececao2_id, NOW(), @rececao2_id
WHERE NOT EXISTS (SELECT 1 FROM nota WHERE reserva_id = @r_ace);

INSERT INTO plano_cuidados (animal_id, estadia_id, data_inicio, data_fim, prioridade, ativo, instrucoes, created_by)
SELECT @a_ace, @e_ace, NOW(), NULL, 'ROTINA', TRUE,
       'Racao adulto medio-grande 2x dia. Passeio diario 30 minutos. Animal sociavel com cuidadores.', @cuidador_id
WHERE NOT EXISTS (SELECT 1 FROM plano_cuidados WHERE estadia_id = @e_ace);

-- --- ESTADIA COCO ---
INSERT INTO estadia (reserva_id, data_inicio, data_fim, estado)
SELECT @r_coco, NOW(), NULL, 'EM_CURSO'
WHERE NOT EXISTS (SELECT 1 FROM estadia WHERE reserva_id = @r_coco);

SET @e_coco = (SELECT id FROM estadia WHERE reserva_id = @r_coco LIMIT 1);

INSERT INTO pagamento (estadia_id, valor, metodo, tipo, estado)
SELECT @e_coco, 30.00, 'NUMERARIO', 'CHECK_IN', 'LIQUIDADO'
WHERE NOT EXISTS (SELECT 1 FROM pagamento WHERE estadia_id = @e_coco AND tipo = 'CHECK_IN');

INSERT INTO registo_cuidado (estadia_id, descricao, data_hora, autor_id, created_by)
SELECT @e_coco, 'Comprimido anti-ansiedade administrado apos jantar. Animal calmo durante a noite.', NOW(), @cuidador5_id, @cuidador5_id
WHERE NOT EXISTS (SELECT 1 FROM registo_cuidado WHERE estadia_id = @e_coco AND descricao LIKE 'Comprimido anti%');

INSERT INTO plano_cuidados (animal_id, estadia_id, data_inicio, data_fim, prioridade, ativo, instrucoes, created_by)
SELECT @a_coco, @e_coco, NOW(), NULL, 'URGENTE', TRUE,
       'Comprimido anti-ansiedade noturno obrigatorio. Ambiente calmo. Evitar barulho forte proximo ao alojamento.', @veterinario_id
WHERE NOT EXISTS (SELECT 1 FROM plano_cuidados WHERE estadia_id = @e_coco);

-- =============================================================================
-- 7. ESTADIAS TERMINADAS (histórico com check-out e pagamento final)
-- =============================================================================

SET @r_rex    = (SELECT id FROM reserva WHERE animal_id = @a_rex   AND data_inicio = DATE_SUB(CURDATE(), INTERVAL 60 DAY) LIMIT 1);
SET @r_bobi   = (SELECT id FROM reserva WHERE animal_id = @a_bobi  AND data_inicio = DATE_SUB(CURDATE(), INTERVAL 50 DAY) LIMIT 1);
SET @r_simba  = (SELECT id FROM reserva WHERE animal_id = @a_simba AND data_inicio = DATE_SUB(CURDATE(), INTERVAL 40 DAY) LIMIT 1);
SET @r_luna   = (SELECT id FROM reserva WHERE animal_id = @a_luna  AND data_inicio = DATE_SUB(CURDATE(), INTERVAL 45 DAY) LIMIT 1);
SET @r_mel    = (SELECT id FROM reserva WHERE animal_id = @a_mel   AND data_inicio = DATE_SUB(CURDATE(), INTERVAL 35 DAY) LIMIT 1);
SET @r_mimi   = (SELECT id FROM reserva WHERE animal_id = @a_mimi  AND data_inicio = DATE_SUB(CURDATE(), INTERVAL 30 DAY) LIMIT 1);
SET @r_zeus   = (SELECT id FROM reserva WHERE animal_id = @a_zeus  AND data_inicio = DATE_SUB(CURDATE(), INTERVAL 25 DAY) LIMIT 1);
SET @r_niko   = (SELECT id FROM reserva WHERE animal_id = @a_niko  AND data_inicio = DATE_SUB(CURDATE(), INTERVAL 20 DAY) LIMIT 1);
SET @r_bolt   = (SELECT id FROM reserva WHERE animal_id = @a_bolt  AND data_inicio = DATE_SUB(CURDATE(), INTERVAL 15 DAY) LIMIT 1);
SET @r_duke   = (SELECT id FROM reserva WHERE animal_id = @a_duke  AND data_inicio = DATE_SUB(CURDATE(), INTERVAL 10 DAY) LIMIT 1);

INSERT INTO estadia (reserva_id, data_inicio, data_fim, estado)
SELECT @r_rex, DATE_SUB(NOW(), INTERVAL 60 DAY), DATE_SUB(NOW(), INTERVAL 55 DAY), 'TERMINADA'
WHERE NOT EXISTS (SELECT 1 FROM estadia WHERE reserva_id = @r_rex);

INSERT INTO estadia (reserva_id, data_inicio, data_fim, estado)
SELECT @r_bobi, DATE_SUB(NOW(), INTERVAL 50 DAY), DATE_SUB(NOW(), INTERVAL 45 DAY), 'TERMINADA'
WHERE NOT EXISTS (SELECT 1 FROM estadia WHERE reserva_id = @r_bobi);

INSERT INTO estadia (reserva_id, data_inicio, data_fim, estado)
SELECT @r_simba, DATE_SUB(NOW(), INTERVAL 40 DAY), DATE_SUB(NOW(), INTERVAL 35 DAY), 'TERMINADA'
WHERE NOT EXISTS (SELECT 1 FROM estadia WHERE reserva_id = @r_simba);

INSERT INTO estadia (reserva_id, data_inicio, data_fim, estado)
SELECT @r_luna, DATE_SUB(NOW(), INTERVAL 45 DAY), DATE_SUB(NOW(), INTERVAL 40 DAY), 'TERMINADA'
WHERE NOT EXISTS (SELECT 1 FROM estadia WHERE reserva_id = @r_luna);

INSERT INTO estadia (reserva_id, data_inicio, data_fim, estado)
SELECT @r_mel, DATE_SUB(NOW(), INTERVAL 35 DAY), DATE_SUB(NOW(), INTERVAL 30 DAY), 'TERMINADA'
WHERE NOT EXISTS (SELECT 1 FROM estadia WHERE reserva_id = @r_mel);

INSERT INTO estadia (reserva_id, data_inicio, data_fim, estado)
SELECT @r_mimi, DATE_SUB(NOW(), INTERVAL 30 DAY), DATE_SUB(NOW(), INTERVAL 25 DAY), 'TERMINADA'
WHERE NOT EXISTS (SELECT 1 FROM estadia WHERE reserva_id = @r_mimi);

INSERT INTO estadia (reserva_id, data_inicio, data_fim, estado)
SELECT @r_zeus, DATE_SUB(NOW(), INTERVAL 25 DAY), DATE_SUB(NOW(), INTERVAL 20 DAY), 'TERMINADA'
WHERE NOT EXISTS (SELECT 1 FROM estadia WHERE reserva_id = @r_zeus);

INSERT INTO estadia (reserva_id, data_inicio, data_fim, estado)
SELECT @r_niko, DATE_SUB(NOW(), INTERVAL 20 DAY), DATE_SUB(NOW(), INTERVAL 15 DAY), 'TERMINADA'
WHERE NOT EXISTS (SELECT 1 FROM estadia WHERE reserva_id = @r_niko);

INSERT INTO estadia (reserva_id, data_inicio, data_fim, estado)
SELECT @r_bolt, DATE_SUB(NOW(), INTERVAL 15 DAY), DATE_SUB(NOW(), INTERVAL 10 DAY), 'TERMINADA'
WHERE NOT EXISTS (SELECT 1 FROM estadia WHERE reserva_id = @r_bolt);

INSERT INTO estadia (reserva_id, data_inicio, data_fim, estado)
SELECT @r_duke, DATE_SUB(NOW(), INTERVAL 10 DAY), DATE_SUB(NOW(), INTERVAL 5 DAY), 'TERMINADA'
WHERE NOT EXISTS (SELECT 1 FROM estadia WHERE reserva_id = @r_duke);

SET @e_rex   = (SELECT id FROM estadia WHERE reserva_id = @r_rex   LIMIT 1);
SET @e_bobi  = (SELECT id FROM estadia WHERE reserva_id = @r_bobi  LIMIT 1);
SET @e_simba = (SELECT id FROM estadia WHERE reserva_id = @r_simba LIMIT 1);
SET @e_luna  = (SELECT id FROM estadia WHERE reserva_id = @r_luna  LIMIT 1);
SET @e_mel   = (SELECT id FROM estadia WHERE reserva_id = @r_mel   LIMIT 1);
SET @e_mimi  = (SELECT id FROM estadia WHERE reserva_id = @r_mimi  LIMIT 1);
SET @e_zeus  = (SELECT id FROM estadia WHERE reserva_id = @r_zeus  LIMIT 1);
SET @e_niko  = (SELECT id FROM estadia WHERE reserva_id = @r_niko  LIMIT 1);
SET @e_bolt  = (SELECT id FROM estadia WHERE reserva_id = @r_bolt  LIMIT 1);
SET @e_duke  = (SELECT id FROM estadia WHERE reserva_id = @r_duke  LIMIT 1);

-- Pagamentos liquidados das estadias concluídas
INSERT INTO pagamento (estadia_id, valor, metodo, tipo, estado)
SELECT @e_rex,   75.00, 'CARTAO_DEBITO',  'CHECK_OUT', 'LIQUIDADO' WHERE NOT EXISTS (SELECT 1 FROM pagamento WHERE estadia_id = @e_rex   AND tipo = 'CHECK_OUT');
INSERT INTO pagamento (estadia_id, valor, metodo, tipo, estado)
SELECT @e_bobi,  50.00, 'CARTAO_DEBITO',     'CHECK_OUT', 'LIQUIDADO' WHERE NOT EXISTS (SELECT 1 FROM pagamento WHERE estadia_id = @e_bobi  AND tipo = 'CHECK_OUT');
INSERT INTO pagamento (estadia_id, valor, metodo, tipo, estado)
SELECT @e_simba, 50.00, 'CARTAO_CREDITO', 'CHECK_OUT', 'LIQUIDADO' WHERE NOT EXISTS (SELECT 1 FROM pagamento WHERE estadia_id = @e_simba AND tipo = 'CHECK_OUT');
INSERT INTO pagamento (estadia_id, valor, metodo, tipo, estado)
SELECT @e_luna,  40.00, 'NUMERARIO',      'CHECK_OUT', 'LIQUIDADO' WHERE NOT EXISTS (SELECT 1 FROM pagamento WHERE estadia_id = @e_luna  AND tipo = 'CHECK_OUT');
INSERT INTO pagamento (estadia_id, valor, metodo, tipo, estado)
SELECT @e_mel,   40.00, 'CARTAO_DEBITO',  'CHECK_OUT', 'LIQUIDADO' WHERE NOT EXISTS (SELECT 1 FROM pagamento WHERE estadia_id = @e_mel   AND tipo = 'CHECK_OUT');
INSERT INTO pagamento (estadia_id, valor, metodo, tipo, estado)
SELECT @e_mimi,  40.00, 'CARTAO_CREDITO',  'CHECK_OUT', 'LIQUIDADO' WHERE NOT EXISTS (SELECT 1 FROM pagamento WHERE estadia_id = @e_mimi  AND tipo = 'CHECK_OUT');
INSERT INTO pagamento (estadia_id, valor, metodo, tipo, estado)
SELECT @e_zeus,  55.00, 'CARTAO_CREDITO', 'CHECK_OUT', 'LIQUIDADO' WHERE NOT EXISTS (SELECT 1 FROM pagamento WHERE estadia_id = @e_zeus  AND tipo = 'CHECK_OUT');
INSERT INTO pagamento (estadia_id, valor, metodo, tipo, estado)
SELECT @e_niko,  65.00, 'CARTAO_DEBITO',     'CHECK_OUT', 'LIQUIDADO' WHERE NOT EXISTS (SELECT 1 FROM pagamento WHERE estadia_id = @e_niko  AND tipo = 'CHECK_OUT');
INSERT INTO pagamento (estadia_id, valor, metodo, tipo, estado)
SELECT @e_bolt,  60.00, 'CARTAO_DEBITO',  'CHECK_OUT', 'LIQUIDADO' WHERE NOT EXISTS (SELECT 1 FROM pagamento WHERE estadia_id = @e_bolt  AND tipo = 'CHECK_OUT');
INSERT INTO pagamento (estadia_id, valor, metodo, tipo, estado)
SELECT @e_duke,  75.00, 'CARTAO_CREDITO',  'CHECK_OUT', 'LIQUIDADO' WHERE NOT EXISTS (SELECT 1 FROM pagamento WHERE estadia_id = @e_duke  AND tipo = 'CHECK_OUT');

-- Serviços extra em estadias concluídas
INSERT INTO servico_extra (estadia_id, tipo_servico_extra_id, tipo, custo, data_hora, autor_id, created_by)
SELECT @e_rex,   @tipo_banho,    'Banho',             12.50, DATE_SUB(NOW(), INTERVAL 58 DAY), @cuidador_id,  @cuidador_id  WHERE NOT EXISTS (SELECT 1 FROM servico_extra WHERE estadia_id = @e_rex   AND tipo_servico_extra_id = @tipo_banho);
INSERT INTO servico_extra (estadia_id, tipo_servico_extra_id, tipo, custo, data_hora, autor_id, created_by)
SELECT @e_rex,   @tipo_passeio,  'Passeio',            5.00, DATE_SUB(NOW(), INTERVAL 57 DAY), @cuidador2_id, @cuidador2_id WHERE NOT EXISTS (SELECT 1 FROM servico_extra WHERE estadia_id = @e_rex   AND tipo_servico_extra_id = @tipo_passeio);
INSERT INTO servico_extra (estadia_id, tipo_servico_extra_id, tipo, custo, data_hora, autor_id, created_by)
SELECT @e_bobi,  @tipo_tosa,     'Tosa',              18.00, DATE_SUB(NOW(), INTERVAL 48 DAY), @cuidador_id,  @cuidador_id  WHERE NOT EXISTS (SELECT 1 FROM servico_extra WHERE estadia_id = @e_bobi  AND tipo_servico_extra_id = @tipo_tosa);
INSERT INTO servico_extra (estadia_id, tipo_servico_extra_id, tipo, custo, data_hora, autor_id, created_by)
SELECT @e_simba, @tipo_banho,    'Banho',             12.50, DATE_SUB(NOW(), INTERVAL 38 DAY), @cuidador3_id, @cuidador3_id WHERE NOT EXISTS (SELECT 1 FROM servico_extra WHERE estadia_id = @e_simba AND tipo_servico_extra_id = @tipo_banho);
INSERT INTO servico_extra (estadia_id, tipo_servico_extra_id, tipo, custo, data_hora, autor_id, created_by)
SELECT @e_luna,  @tipo_unhas,    'Corte de Unhas',     6.00, DATE_SUB(NOW(), INTERVAL 43 DAY), @cuidador4_id, @cuidador4_id WHERE NOT EXISTS (SELECT 1 FROM servico_extra WHERE estadia_id = @e_luna  AND tipo_servico_extra_id = @tipo_unhas);
INSERT INTO servico_extra (estadia_id, tipo_servico_extra_id, tipo, custo, data_hora, autor_id, created_by)
SELECT @e_zeus,  @tipo_consulta, 'Consulta Veterinaria',25.00,DATE_SUB(NOW(), INTERVAL 23 DAY), @veterinario_id,@veterinario_id WHERE NOT EXISTS (SELECT 1 FROM servico_extra WHERE estadia_id = @e_zeus  AND tipo_servico_extra_id = @tipo_consulta);
INSERT INTO servico_extra (estadia_id, tipo_servico_extra_id, tipo, custo, data_hora, autor_id, created_by)
SELECT @e_niko,  @tipo_banho,    'Banho',             12.50, DATE_SUB(NOW(), INTERVAL 18 DAY), @cuidador5_id, @cuidador5_id WHERE NOT EXISTS (SELECT 1 FROM servico_extra WHERE estadia_id = @e_niko  AND tipo_servico_extra_id = @tipo_banho);
INSERT INTO servico_extra (estadia_id, tipo_servico_extra_id, tipo, custo, data_hora, autor_id, created_by)
SELECT @e_bolt,  @tipo_passeio,  'Passeio',            5.00, DATE_SUB(NOW(), INTERVAL 13 DAY), @cuidador2_id, @cuidador2_id WHERE NOT EXISTS (SELECT 1 FROM servico_extra WHERE estadia_id = @e_bolt  AND tipo_servico_extra_id = @tipo_passeio);
INSERT INTO servico_extra (estadia_id, tipo_servico_extra_id, tipo, custo, data_hora, autor_id, created_by)
SELECT @e_duke,  @tipo_tosa,     'Tosa',              18.00, DATE_SUB(NOW(), INTERVAL 8 DAY),  @cuidador3_id, @cuidador3_id WHERE NOT EXISTS (SELECT 1 FROM servico_extra WHERE estadia_id = @e_duke  AND tipo_servico_extra_id = @tipo_tosa);

-- =============================================================================
-- 8. AUDITORIA
-- =============================================================================

INSERT INTO auditoria_evento (utilizador_id, operacao, entidade, entity_id, acao, detalhes, resultado, motivo_falha)
SELECT @diretor_id, 'POVOACAO_GRANDE_ESCALA', 'Sistema', 0, 'SEED',
       '{"origem":"scripts/povoacao-grande-escala.sql","tutores":20,"animais":40,"alojamentos":32,"reservas":32}',
       'SUCESSO', NULL
WHERE NOT EXISTS (
  SELECT 1 FROM auditoria_evento WHERE operacao = 'POVOACAO_GRANDE_ESCALA' AND entidade = 'Sistema'
);

SELECT 'Povoacao em grande escala aplicada com sucesso.' AS resultado;
