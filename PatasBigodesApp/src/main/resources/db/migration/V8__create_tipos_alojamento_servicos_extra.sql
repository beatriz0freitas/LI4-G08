CREATE TABLE IF NOT EXISTS tipo_alojamento_tarifa (
  id BIGINT PRIMARY KEY AUTO_INCREMENT,
  tipo_alojamento VARCHAR(50) NOT NULL,
  tarifa_diaria DECIMAL(10,2) NOT NULL,
  ativo BOOLEAN NOT NULL DEFAULT TRUE,
  data_criacao TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
  CONSTRAINT uk_tipo_alojamento UNIQUE (tipo_alojamento),
  CONSTRAINT chk_tipo_alojamento_tarifa CHECK (tarifa_diaria >= 0)
);

CREATE TABLE IF NOT EXISTS tipo_servico_extra (
  id BIGINT PRIMARY KEY AUTO_INCREMENT,
  nome VARCHAR(100) NOT NULL,
  descricao VARCHAR(500),
  ativo BOOLEAN NOT NULL DEFAULT TRUE,
  data_criacao TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
  CONSTRAINT uk_nome_tipo_servico UNIQUE (nome)
);

ALTER TABLE alojamento
  DROP CHECK chk_alojamento_tipo;

ALTER TABLE alojamento
  MODIFY tipo VARCHAR(50) NOT NULL;

ALTER TABLE servico_extra
  ADD COLUMN tipo_servico_extra_id BIGINT NULL AFTER estadia_id;

ALTER TABLE servico_extra
  ADD CONSTRAINT fk_se_tipo_servico_extra FOREIGN KEY (tipo_servico_extra_id) REFERENCES tipo_servico_extra(id);
