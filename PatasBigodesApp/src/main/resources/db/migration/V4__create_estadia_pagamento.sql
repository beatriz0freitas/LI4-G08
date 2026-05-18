-- V4: Create tables for Estadia and Pagamento
CREATE TABLE IF NOT EXISTS estadia (
  id BIGINT AUTO_INCREMENT PRIMARY KEY,
  reserva_id BIGINT NOT NULL,
  data_inicio TIMESTAMP NOT NULL,
  data_fim TIMESTAMP,
  estado VARCHAR(30) NOT NULL,
  data_criacao TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
  CONSTRAINT fk_estadia_reserva FOREIGN KEY (reserva_id) REFERENCES reserva(id)
);

CREATE TABLE IF NOT EXISTS pagamento (
  id BIGINT AUTO_INCREMENT PRIMARY KEY,
  estadia_id BIGINT NOT NULL,
  valor DECIMAL(12,2) NOT NULL,
  metodo VARCHAR(50) NOT NULL,
  tipo VARCHAR(30) NOT NULL,
  estado VARCHAR(30) NOT NULL,
  data_criacao TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
  CONSTRAINT fk_pagamento_estadia FOREIGN KEY (estadia_id) REFERENCES estadia(id)
);
