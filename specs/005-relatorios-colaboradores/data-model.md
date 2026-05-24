# Data Model: Relatórios e Colaboradores (Spec 005)

## Overview
Define as entidades e campos usados pela feature de relatórios e gestão de colaboradores. Este ficheiro serve como referência para JPA entities e para as migrações Flyway.

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

## Projections / DTOs
- **RelatorioRequest**: `dataInicio`, `dataFim`, `tipoAlojamento?`, `incluirServicosExtra?`, `agruparPor` (DIA/SEMANA/MES)
- **RelatorioResumoDTO**: `periodoStart`, `periodoEnd`, `ocupacaoPerc`, `estadiasCount`, `reservasCount`, `faturacaoTotal`, `faturacaoPorMetodo` (map), `servicosExtraTotal`

## Indexes & Performance
- Índices recomendados:
  - `pagamento(dataHora)` para consultas por período
  - `servico_extra(dataHora)` para relatórios por período
  - `estadia(estado, data_checkout)` para checagens de imutabilidade

## Constraints & Integrity
- Imutabilidade: assegurar que alterações a `Pagamento` e `ServicoExtra` após `Estadia` em estado `CHECKED_OUT` são rejeitadas a nível de aplicação e auditadas.
- Passwords: armazenar `passwordHash` usando BCrypt; migração de valores em texto plano não permitida.

## Sample SQL mapping notes
- A migration `V005__create_colaborador.sql` foi adicionada como base; ajustar conforme JPA mappings.
