CREATE TABLE alojamento (
    id BIGINT NOT NULL AUTO_INCREMENT,
    identificacao VARCHAR(100) NOT NULL,
    tipo VARCHAR(20) NOT NULL,
    capacidade INT NOT NULL,
    estado_limpeza VARCHAR(20) NOT NULL,
    CONSTRAINT pk_alojamento PRIMARY KEY (id),
    CONSTRAINT uk_alojamento_identificacao UNIQUE (identificacao),
    CONSTRAINT chk_alojamento_tipo CHECK (tipo IN ('CANINO', 'FELINO')),
    CONSTRAINT chk_alojamento_capacidade CHECK (capacidade >= 1),
    CONSTRAINT chk_alojamento_estado_limpeza CHECK (estado_limpeza IN ('PENDENTE', 'CONCLUIDO'))
);
