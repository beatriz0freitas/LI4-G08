# API Contract — Relatórios e Colaboradores (Spec 005)

Este ficheiro descreve, em formato Markdown legível, os endpoints e payloads principais usados pela feature.

## Endpoints

- POST `/api/relatorios/generate`
  - Descrição: Gera um relatório agregados com filtros e devolve um ID de relatório ou resultado imediato.
  - Request JSON: `RelatorioRequest` (ver esquema abaixo)
  - Responses:
    - `200 OK`: `{ "id": 123 }` — relatório gerado.

- GET `/api/relatorios/{id}/export/csv`
  - Descrição: Exporta o relatório em CSV.
  - Path param: `id` (integer)
  - Responses:
    - `200 OK`: CSV stream (`text/csv`)

- GET `/api/colaboradores`
  - Descrição: Lista colaboradores (paginação opcional).
  - Responses: `200 OK` list of `ColaboradorDTO`.

- POST `/api/colaboradores`
  - Descrição: Cria um novo colaborador.
  - Request JSON: `ColaboradorDTO`
  - Responses: `201 Created`.

- PUT `/api/colaboradores/{id}`
  - Descrição: Atualiza colaborador (soft-edit).
  - Responses: `200 OK`.

- DELETE `/api/colaboradores/{id}`
  - Descrição: Desactiva colaborador (soft delete).
  - Responses: `204 No Content`.

- GET `/api/indicadores/faturacao?start={date}&end={date}`
  - Descrição: Agrega faturação por período e por método de pagamento.
  - Responses: `200 OK` com estrutura de agregação.

- GET `/api/historico?reservaId={id}`
  - Descrição: Lista eventos relacionados a uma reserva/estadia (paginação, filtros).

## Schemas

- `RelatorioRequest` (request body)
  - `dataInicio` (string date, required)
  - `dataFim` (string date, required)
  - `tipoAlojamento` (string, optional)
  - `incluirServicosExtra` (boolean, default false)
  - `agruparPor` (enum: DIA, SEMANA, MES)

- `ColaboradorDTO`
  - `username` (string, required)
  - `nome` (string, required)
  - `email` (string)
  - `tipoColaborador` (enum: DIRETOR, FUNCIONARIO_RECEPCAO, CUIDADOR, MEDICO_VETERINARIO, RESPONSAVEL_LIMPEZA)

## Errors & HTTP codes

- `400 Bad Request` — validação de input
- `401 Unauthorized` — sem autenticação
- `403 Forbidden` — sem permissão
- `404 Not Found` — recurso inexistente
- `409 Conflict` — conflito de estado (imutabilidade pós-checkout)

## Notes
- Este contrato é um esqueleto para a implementação; quando as rotas estiverem implementadas, gerar um `openapi.yaml` com detalhes dos schemas e usar ferramentas para gerar clients ou documentação interativa.
