CREATE TABLE colaborador (
    id BIGINT NOT NULL AUTO_INCREMENT,
    username VARCHAR(80) NOT NULL,
    nome VARCHAR(150) NOT NULL,
    email VARCHAR(150) NOT NULL,
    password_hash VARCHAR(100) NOT NULL,
    tipo_colaborador VARCHAR(40) NOT NULL,
    ativo BOOLEAN NOT NULL DEFAULT TRUE,
    data_criacao TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    ultimo_login TIMESTAMP NULL,
    CONSTRAINT pk_colaborador PRIMARY KEY (id),
    CONSTRAINT uk_colaborador_username UNIQUE (username),
    CONSTRAINT uk_colaborador_email UNIQUE (email),
    CONSTRAINT chk_colaborador_tipo CHECK (
        tipo_colaborador IN (
            'DIRETOR',
            'FUNCIONARIO_RECEPCAO',
            'CUIDADOR',
            'MEDICO_VETERINARIO',
            'RESPONSAVEL_LIMPEZA'
        )
    )
);
