-- V5: Cuidados, Clinica e Limpeza - tabelas iniciais (adicionadas PlanoCuidados e TarefaCuidado)

-- Create PlanoCuidados ( núcleo do plano dinâmico de cuidados)
CREATE TABLE plano_cuidados (
  id BIGINT PRIMARY KEY AUTO_INCREMENT,
  animal_id BIGINT NOT NULL,
  estadia_id BIGINT NOT NULL,
  data_inicio TIMESTAMP NOT NULL,
  data_fim TIMESTAMP,
  prioridade VARCHAR(50) NOT NULL DEFAULT 'ROTINA',
  ativo BOOLEAN NOT NULL DEFAULT TRUE,
  instrucoes VARCHAR(2000),
  created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  created_by BIGINT,
  updated_at TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  updated_by BIGINT,
  CONSTRAINT fk_pc_animal FOREIGN KEY (animal_id) REFERENCES animal(id),
  CONSTRAINT fk_pc_estadia FOREIGN KEY (estadia_id) REFERENCES estadia(id),
  CONSTRAINT uk_pc_estadia UNIQUE (estadia_id)
);

-- Create TarefaCuidado ( tarefas recorrentes estruturadas no plano)
CREATE TABLE tarefa_cuidado (
  id BIGINT PRIMARY KEY AUTO_INCREMENT,
  plano_cuidados_id BIGINT NOT NULL,
  tipo VARCHAR(100) NOT NULL,
  descricao VARCHAR(500),
  periodicidade VARCHAR(50) NOT NULL,
  data_hora TIMESTAMP NOT NULL,
  concluida BOOLEAN DEFAULT FALSE,
  autor_conclusao_id BIGINT,
  created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  created_by BIGINT,
  CONSTRAINT fk_tc_plano FOREIGN KEY (plano_cuidados_id) REFERENCES plano_cuidados(id)
);

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
-- Create Indexes for performance (LAC-02 & RNF-01)
CREATE INDEX idx_pc_animal ON plano_cuidados(animal_id);
CREATE INDEX idx_pc_estadia ON plano_cuidados(estadia_id);
CREATE INDEX idx_tc_plano ON tarefa_cuidado(plano_cuidados_id);
CREATE INDEX idx_tc_plano_concluida ON tarefa_cuidado(plano_cuidados_id, concluida);
CREATE INDEX idx_rc_estadia ON registo_cuidado(estadia_id);
CREATE INDEX idx_rc_estadia_datahora ON registo_cuidado(estadia_id, data_hora DESC);
CREATE INDEX idx_se_estadia ON servico_extra(estadia_id);
CREATE INDEX idx_ic_estadia ON intervencao_clinica(estadia_id);
CREATE INDEX idx_n_reserva ON nota(reserva_id);
CREATE INDEX idx_aes_estadia ON alteracao_estado_saude(estadia_id);