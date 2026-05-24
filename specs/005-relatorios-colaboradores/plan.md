# Plano de Implementação — Relatórios e Colaboradores (Spec 005)

## Visão geral
Objetivo: Implementar geração/exportação de relatórios operacionais e gestão de colaboradores conforme spec `spec.md`, garantindo rastreabilidade com os RF/RD da Etapa 1.

## Entregáveis
- Endpoints backend para geração de relatórios filtráveis e exportação (CSV/PDF)
- UI/Thymeleaf: vistas de Relatórios e Gestão de Colaboradores
- Serviços e Repositórios: agregação de métricas, cálculo de ocupação, agregação de faturação e serviços extra
- Testes de aceitação (Gherkin) e unitários para serviços críticos
- Documentação técnica: contracts/ (API), quickstart.md

## Premissas
- Base de dados já contém `Reserva`, `Estadia`, `Pagamento`, `ServicoExtra` (ou serão criados mapas de migração mínimos)
- Autenticação/Autorização existente (RNF-04) — apenas perfis autorizados acedem às funcionalidades sensíveis

## Ordem de implementação

1. Especificação de API & Contracts
  - Definir endpoints REST: `/api/relatorios` (POST para gerar, GET para estado/export), `/api/colaboradores` (CRUD)
  - Documentar payloads (FiltroRelatorio, RelatorioResumo, ColaboradorDTO)
  - Deliverable: `specs/005-relatorios-colaboradores/contracts/` (OpenAPI snippet)

2. Implementação backend inicial
  - Implementar `IRelatorioService` e `RelatorioService` — agregações (ocupacaoPerc, estadiasCount, reservasCount, faturacaoTotal, servicosExtraTotal)
  - Criar queries em `PagamentoRepository` para sumarização por período/método
  - Implementar `ColaboradorController`, `IColaboradorService`, `ColaboradorService`, `ColaboradorRepository` (CRUD + desactivar)
  - Acceptance: endpoints devolvem JSON apropriado e autenticado

3. UI & Export
  - Desenvolver Thymeleaf views para Relatórios (filtros, tabelas, sumário) e Gestão de Colaboradores
  - Implementar export CSV e geração de PDF (server-side reporting simples)
  - Acceptance: export gera ficheiros com colunas esperadas e inclui sumário

4. Testes e QA
  - Testes unitários para `RelatorioService` e `PagamentoRepository`
  - Testes de aceitação Gherkin para `US-04` e `US-03`
  - Revisão de segurança: verificação de permissões (RNF-04)

5. Documentação e entrega
  - `quickstart.md` com endpoints, exemplos de requests/exports
  - Atualizar `spec.md` com decisões técnicas finais

## Tarefas técnicas (detalhadas)
- API: POST `/api/relatorios/generate` {filtros} -> jobId | blocking response
- API: GET `/api/relatorios/{jobId}` -> estado / resultado (download links)
- SQL: queries parametrizadas para sumarização por período (usar índices em `dataInicio`, `dataFim`, `reservaId`)
- Backend: suportar agrupamento por (dia, semana, mês) e paginação para tabelas
- Segurança: verificar roles `DIRETOR` para endpoints de faturação detalhada

## Critérios de aceitação (mínimos)
- Relatório padrão gerado e mostrado no UI com as métricas definidas em `spec.md` e exportável para CSV/PDF
- CSV produzido com cabeçalhos: `periodo_start,periodo_end,ocupacaoPerc,estadiasCount,reservasCount,faturacaoTotal,metodoPagamento,servicosExtraTotal`
- Gestão de colaboradores suporta CRUD e desactivação; lista reflecte alterações imediatamente
- Testes automatizados cobrem agregação de faturação e regras de permissão

## Riscos e mitigação
- Risco: grandes volumes tornam agregação lenta — Mitigação: executar como job assíncrono, pré-aggregate por dia em tabela materializada
- Risco: inconsistências nos pagamentos — Mitigação: validar campos obrigatórios (`valor`,`metodoPagamento`,`estado`) e usar transacções nas operações de check-out

## Próximos passos imediatos
1. Criar branch de feature `005-relatorios-colaboradores` (se ainda não existir)
2. Gerar `contracts/` com OpenAPI minimal
3. Implementar `IRelatorioService`/`RelatorioService` e testes básicos

---
