# Data Model: Relatórios e Colaboradores (Spec 005)

**Atualizado**: 2026-05-26 (incorpora LAC-13 — Auditoria)

## Overview
Define as entidades e campos usados pela feature de relatórios, gestão de colaboradores e auditoria centralizada. Este ficheiro serve como referência para JPA entities e para as migrações Flyway.

## Entities

- **Colaborador**
  - `id: Long` (PK)
  - `username: String` (unique, not null)
  - `nome: String` (not null)
  - `email: String`
  - `passwordHash: String` (not null)
  - `tipoColaborador: Enum` (DIRETOR, FUNCIONARIO_RECEPCAO, CUIDADOR, MEDICO_VETERINARIO, RESPONSAVEL_LIMPEZA)
  - `activo: boolean` (default true)
  - `dataCriacao: datetime`
  - `ultimoLogin: datetime`

- **Pagamento** (resumo utilizado em relatórios)
  - `id: Long` (PK)
  - `valor: decimal(10,2)`
  - `metodoPagamento: Enum` (NUMERARIO, CARTAO_DEBITO, CARTAO_CREDITO)
  - `estado: Enum` (PENDENTE, LIQUIDADO)
  - `momento: Enum` (CHECK_IN, CHECK_OUT)
  - `dataHora: datetime`
  - `reservaId: Long` (FK opcional)

- **ServicoExtra**
  - `id: Long` (PK)
  - `tipoServico: Enum` (BANHO, PASSEIO, OUTRO)
  - `custo: decimal(10,2)`
  - `dataHora: datetime`
  - `reservaId: Long` (FK)
  - **Nota:** validar imutabilidade pós-checkout (ver spec)

- **Estadia / Reserva (referência)**
  - Usar as entidades existentes em Etapa 1/2; as relações chave são: `Estadia` 1..* `Pagamento`, `ServicoExtra` 1..* `Estadia`.

- **AuditoriaEvento** *(novo, LAC-13)*
  - `id: Long` (PK)
  - `timestamp: datetime` (not null, instante em que evento foi registado)
  - `utilizadorId: Long` (FK para `Colaborador`, not null; quem executou a operação)
  - `operacao: String` (not null, ex: "CRIAR_RESERVA", "CHECK_IN", "PAGAMENTO", "CRIAR_COLABORADOR", etc.)
  - `entidade: String` (not null, ex: "Reserva", "Estadia", "Pagamento", "Colaborador", etc.)
  - `entityId: Long` (not null, PK da entidade afetada dentro do seu contexto)
  - `acao: String` (not null, ex: "CREATE", "UPDATE", "DELETE")
  - `detalhes: String` (JSON, optional, ex: `{"camposAlterados": ["valor", "estado"], "valorAnterior": "...", "valorNovo": "..."}`)
  - `resultado: Enum` (not null, "SUCESSO" ou "FALHA")
  - `motivoFalha: String` (optional, mensagem de erro se resultado=FALHA)
  - **Constraints**: `timestamp` deve corresponder ao `now()` do servidor; não permite datas futuras.

## Projections / DTOs
- **RelatorioRequest**: `dataInicio`, `dataFim`, `tipoAlojamento?`, `incluirServicosExtra?`, `agruparPor` (DIA/SEMANA/MES)
- **RelatorioResumoDTO**: `periodoStart`, `periodoEnd`, `ocupacaoPerc`, `estadiasCount`, `reservasCount`, `faturacaoTotal`, `faturacaoPorMetodo` (map), `servicosExtraTotal`
- **AuditoriaFiltroDTO** *(novo, LAC-13)*: `dataInicio?`, `dataFim?`, `utilizadorId?`, `operacao?`, `entidade?`, `resultado?`
- **AuditoriaEventoDTO** *(novo, LAC-13)*: `id`, `timestamp`, `utilizador` (nome ou email), `operacao`, `entidade`, `entityId`, `acao`, `detalhes`, `resultado`, `motivoFalha`

## Indexes & Performance
- Índices existentes (relatórios):
  - `pagamento(dataHora)` para consultas por período
  - `servico_extra(dataHora)` para relatórios por período
  - `estadia(estado, data_checkout)` para checagens de imutabilidade
- **Novos índices (LAC-13 - auditoria)**:
  - `auditoria_evento(timestamp)` — para limpeza de dados antigos
  - `auditoria_evento(utilizador_id, timestamp)` — para consultas filtradas por utilizador
  - `auditoria_evento(operacao, timestamp)` — para consultas por tipo de operação
  - `auditoria_evento(entidade, entity_id, timestamp)` — para rastreabilidade por entidade

## Constraints & Integrity
- Imutabilidade: assegurar que alterações a `Pagamento` e `ServicoExtra` após `Estadia` em estado `CHECKED_OUT` são rejeitadas a nível de aplicação e auditadas.
- Passwords: armazenar `passwordHash` usando BCrypt; migração de valores em texto plano não permitida.
- **Auditoria (LAC-13)**:
  - Cada evento deve ter `utilizadorId` (não null); operações sem utilizador autenticado lançam exceção antes de chegar a `AuditoriaService`.
  - Campo `detalhes` (JSON) está limitado a 2000 caracteres; schema é livre mas documentado em comentário de código.
  - Retenção: eventos com `timestamp` anterior a 12 meses são removidos por job de limpeza (não há soft delete).
  - Integridade referencial: `utilizadorId` aponta para `Colaborador` existente; validação no serviço.

## Sample SQL mapping notes
- A migration `V005__create_colaborador.sql` foi adicionada como base; ajustar conforme JPA mappings.
- **Novo (LAC-13)**: A migration `V006__create_auditoria_evento.sql` deve criar tabela `auditoria_evento` com campos conforme entidade `AuditoriaEvento` acima.
  - Exemplo para PostgreSQL:
    ```sql
    CREATE TABLE auditoria_evento (
      id SERIAL PRIMARY KEY,
      timestamp TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
      utilizador_id BIGINT NOT NULL,
      operacao VARCHAR(100) NOT NULL,
      entidade VARCHAR(100) NOT NULL,
      entity_id BIGINT NOT NULL,
      acao VARCHAR(50) NOT NULL,
      detalhes TEXT,
      resultado VARCHAR(20) NOT NULL,
      motivo_falha TEXT,
      FOREIGN KEY (utilizador_id) REFERENCES colaborador(id),
      INDEX idx_timestamp (timestamp),
      INDEX idx_utilizador_timestamp (utilizador_id, timestamp),
      INDEX idx_operacao_timestamp (operacao, timestamp),
      INDEX idx_entidade_id_timestamp (entidade, entity_id, timestamp)
    );
    ```
