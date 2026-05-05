CREATE TABLE tutor (
    id BIGINT NOT NULL AUTO_INCREMENT,
    nome VARCHAR(150) NOT NULL,
    nif VARCHAR(20) NOT NULL,
    contacto VARCHAR(30) NOT NULL,
    email VARCHAR(150) NOT NULL,
    data_registo TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT pk_tutor PRIMARY KEY (id),
    CONSTRAINT uk_tutor_nif UNIQUE (nif)
);

CREATE TABLE animal (
    id BIGINT NOT NULL AUTO_INCREMENT,
    tutor_id BIGINT NOT NULL,
    nome VARCHAR(150) NOT NULL,
    especie VARCHAR(20) NOT NULL,
    raca VARCHAR(100) NOT NULL,
    data_nascimento DATE NOT NULL,
    peso DECIMAL(6,2) NOT NULL,
    estado_saude VARCHAR(20) NOT NULL,
    necessidades_alimentares TEXT,
    medicacao_curso TEXT,
    data_registo TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT pk_animal PRIMARY KEY (id),
    CONSTRAINT fk_animal_tutor FOREIGN KEY (tutor_id) REFERENCES tutor (id),
    CONSTRAINT chk_animal_especie CHECK (especie IN ('CAO', 'GATO')),
    CONSTRAINT chk_animal_estado_saude CHECK (estado_saude IN ('NORMAL', 'ALTERADO', 'CRITICO')),
    CONSTRAINT chk_animal_peso CHECK (peso > 0)
);

CREATE INDEX idx_animal_tutor_id ON animal (tutor_id);

CREATE TABLE reserva (
    id BIGINT NOT NULL AUTO_INCREMENT,
    tutor_id BIGINT NOT NULL,
    animal_id BIGINT NOT NULL,
    alojamento_id BIGINT NOT NULL,
    data_inicio DATE NOT NULL,
    data_fim DATE NOT NULL,
    estado VARCHAR(20) NOT NULL DEFAULT 'ATIVA',
    data_criacao TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT pk_reserva PRIMARY KEY (id),
    CONSTRAINT fk_reserva_tutor FOREIGN KEY (tutor_id) REFERENCES tutor (id),
    CONSTRAINT fk_reserva_animal FOREIGN KEY (animal_id) REFERENCES animal (id),
    CONSTRAINT fk_reserva_alojamento FOREIGN KEY (alojamento_id) REFERENCES alojamento (id),
    CONSTRAINT chk_reserva_estado CHECK (estado IN ('ATIVA', 'CANCELADA', 'CONCLUIDA')),
    CONSTRAINT chk_reserva_periodo CHECK (data_inicio < data_fim)
);

CREATE INDEX idx_reserva_tutor_id ON reserva (tutor_id);
CREATE INDEX idx_reserva_animal_id ON reserva (animal_id);
CREATE INDEX idx_reserva_alojamento_id ON reserva (alojamento_id);
CREATE INDEX idx_reserva_periodo ON reserva (data_inicio, data_fim);