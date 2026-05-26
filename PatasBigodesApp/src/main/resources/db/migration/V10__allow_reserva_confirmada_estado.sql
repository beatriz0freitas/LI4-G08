ALTER TABLE reserva DROP CHECK chk_reserva_estado;

ALTER TABLE reserva
    ADD CONSTRAINT chk_reserva_estado
    CHECK (estado IN ('ATIVA', 'CONFIRMADA', 'CANCELADA', 'CONCLUIDA'));
