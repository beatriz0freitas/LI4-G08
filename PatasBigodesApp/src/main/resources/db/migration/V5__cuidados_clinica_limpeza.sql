-- V5: Cuidados, Clinica e Limpeza - tabelas iniciais

-- Create RegistoCuidado
CREATE TABLE registo_cuidado (
  id BIGINT PRIMARY KEY AUTO_INCREMENT,
  estadia_id BIGINT NOT NULL,
  descricao VARCHAR(1000) NOT NULL,
  data_hora TIMESTAMP NOT NULL,
  autor_id BIGINT,
  created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  created_by BIGINT,
  CONSTRAINT fk_rc_estadia FOREIGN KEY (estadia_id) REFERENCES estadia(id)
);

-- Create ServicoExtra
CREATE TABLE servico_extra (
  id BIGINT PRIMARY KEY AUTO_INCREMENT,
  estadia_id BIGINT NOT NULL,
  tipo VARCHAR(50) NOT NULL,
  custo DECIMAL(10,2) NOT NULL,
  data_hora TIMESTAMP NOT NULL,
  autor_id BIGINT,
  created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  created_by BIGINT,
  CONSTRAINT fk_se_estadia FOREIGN KEY (estadia_id) REFERENCES estadia(id)
);

-- Create IntervencaoClinica
CREATE TABLE intervencao_clinica (
  id BIGINT PRIMARY KEY AUTO_INCREMENT,
  estadia_id BIGINT NOT NULL,
  descricao VARCHAR(1000) NOT NULL,
  custo DECIMAL(10,2) NOT NULL,
  data_hora TIMESTAMP NOT NULL,
  medico_id BIGINT,
  created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  created_by BIGINT,
  CONSTRAINT fk_ic_estadia FOREIGN KEY (estadia_id) REFERENCES estadia(id)
);

-- Create Nota
CREATE TABLE nota (
  id BIGINT PRIMARY KEY AUTO_INCREMENT,
  reserva_id BIGINT NOT NULL,
  descricao VARCHAR(1000) NOT NULL,
  autor_id BIGINT,
  data_hora TIMESTAMP NOT NULL,
  created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  created_by BIGINT,
  CONSTRAINT fk_n_reserva FOREIGN KEY (reserva_id) REFERENCES reserva(id)
);

-- Create AlteracaoEstadoSaude
CREATE TABLE alteracao_estado_saude (
  id BIGINT PRIMARY KEY AUTO_INCREMENT,
  estadia_id BIGINT NOT NULL,
  descricao VARCHAR(1000) NOT NULL,
  severidade VARCHAR(50) NOT NULL,
  data_hora TIMESTAMP NOT NULL,
  autor_id BIGINT,
  created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  created_by BIGINT,
  CONSTRAINT fk_aes_estadia FOREIGN KEY (estadia_id) REFERENCES estadia(id)
);
