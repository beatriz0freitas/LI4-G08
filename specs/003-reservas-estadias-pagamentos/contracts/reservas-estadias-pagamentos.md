# Contract: Reservas, Estadias e Pagamentos

## Purpose

Definir os contratos de interação da receção para disponibilidade, reservas, check-in/check-out e pagamentos.

## Interactions

### Consultar disponibilidade

- **Method**: `GET`
- **Path**: `/reservas/disponibilidade`
- **Query**:
  - `dataInicio`
  - `dataFim`
  - `animalId` opcional
- **Result**: lista de alojamentos elegíveis para o período
- **Validation**:
  - período válido (`dataInicio < dataFim`)
  - filtragem por regras de disponibilidade (RF-06, RD-01)

### Criar reserva

- **Method**: `POST`
- **Path**: `/reservas`
- **Payload**:
  - `tutorId`
  - `animalId`
  - `alojamentoId`
  - `dataInicio`
  - `dataFim`
- **Success**: reserva criada com estado `ATIVA`
- **Validation**:
  - tutor/animal/alojamento existentes
  - sem sobreposição temporal
  - limpeza concluída

### Confirmar reserva

- **Method**: `POST`
- **Path**: `/reservas/{id}/confirmar`
- **Validation**:
  - reserva existente e elegível para confirmação
  - utilizador com permissão de receção
- **Success**: confirmação operacional registada na reserva (timestamp/utilizador) e evento de auditoria persistido

### Cancelar reserva

- **Method**: `POST`
- **Path**: `/reservas/{id}/cancelar`
- **Validation**:
  - reserva em estado `ATIVA`
- **Success**: estado `CANCELADA`
- **Rule**: reserva cancelada não pode ser reativada (RD-06)

### Registar check-in

- **Method**: `POST`
- **Path**: `/estadias/check-in`
- **Payload**:
  - `reservaId`
  - `metodoPagamento` obrigatório (`NUMERARIO`, `CARTAO_DEBITO` ou `CARTAO_CREDITO`)
- **Success**:
  - estadia criada em `EM_CURSO`
  - alojamento marcado `OCUPADO`
  - pagamento de `CHECK_IN` registado como `LIQUIDADO`
- **Validation**:
  - reserva válida para check-in (RD-02)
  - método de pagamento explícito obrigatório; não existe método por defeito

### Registar check-out

- **Method**: `POST`
- **Path**: `/estadias/{id}/check-out`
- **Payload**:
  - `metodoPagamento` obrigatório (`NUMERARIO`, `CARTAO_DEBITO` ou `CARTAO_CREDITO`)
- **Success**:
  - estadia marcada `TERMINADA`
  - alojamento marcado `PENDENTE_LIMPEZA`
  - pagamento complementar de `CHECK_OUT` registado quando aplicável
- **Validation**:
  - check-in prévio obrigatório (RD-03)
  - método de pagamento explícito obrigatório; não existe método por defeito

## Error Cases

- Período inválido: recusar operação e devolver mensagem de validação.
- Box indisponível: recusar criação de reserva e sugerir alternativas.
- Check-in sem reserva válida: recusar operação.
- Check-out sem check-in: recusar operação.
- Pagamento sem método/estado: recusar registo.

## Auditabilidade e Segurança

- Todas as operações críticas (`criar`, `confirmar`, `cancelar`, `check-in`, `check-out`, `registar pagamento`) devem gerar evento de auditoria com utilizador, timestamp, entidade e resultado.
- Operações sem permissão de perfil devem devolver acesso negado sem exposição de dados sensíveis.
