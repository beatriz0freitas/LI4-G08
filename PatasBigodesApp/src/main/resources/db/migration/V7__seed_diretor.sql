INSERT INTO colaborador (
    username,
    nome,
    email,
    password_hash,
    tipo_colaborador,
    ativo
)
SELECT
    'diretor',
    'Diretor',
    'diretor@hotel.local',
    '$2a$10$tj7H0DQvRBXGOcbPuEJwoeAIBAsuqGpAKZvDxYx3g5rEj1Wf6IOcm',
    'DIRETOR',
    TRUE
WHERE NOT EXISTS (
    SELECT 1 FROM colaborador WHERE username = 'diretor'
);
