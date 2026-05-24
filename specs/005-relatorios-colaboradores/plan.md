# Plano de Implementação — Relatórios e Colaboradores (Spec 005)

## Visão Geral

Objetivo: implementar geração/exportação de relatórios operacionais e gestão de colaboradores conforme `spec.md`, respeitando a arquitetura Spring MVC + Thymeleaf descrita em `docs/Etapa2/01-architecture/architecture.md`.

## Entregáveis

- Controllers MVC que devolvem páginas Thymeleaf, redirecionamentos ou downloads de ficheiros.
- Vistas Thymeleaf de relatórios e gestão de colaboradores.
- Serviços e repositórios para agregação de métricas, cálculo de ocupação, faturação e serviços extra.
- Modelo persistente de `Colaborador` com `tipoColaborador` como enum.
- Documentação do código com Javadoc.
- Geração HTML via Maven Javadoc Plugin.
- Testes de controllers MVC, serviços e autorização por perfil.
- Documentação técnica de rotas MVC e permissões.

## Premissas

- A implementação segue controllers MVC, templates Thymeleaf, formulários e downloads server-side.
- Autenticação/autorização segue RNF-04 e a matriz `docs/Etapa2/06-role-permissions/permissoes.md`.
- Apenas o `DIRETOR` pode gerir colaboradores e consultar/exportar relatórios financeiros.
- Controllers, services, DTOs e exceptions devem ter Javadoc suficiente para explicar responsabilidades públicas e regras de negócio relevantes.

## Ordem de Implementação

1. Contrato de interface MVC
  - Documentar rotas de página, templates, formulários, query parameters e downloads em `contracts/contract.md`.
  - Validar rotas contra `architecture.md` e `permissoes.md`.

2. Fundação de segurança e colaboradores
  - Implementar `TipoColaborador` como enum, se ainda não existir na aplicação.
  - Implementar `Colaborador`, `ColaboradorRepository`, `IColaboradorService` e `ColaboradorService`.
  - Migrar autenticação de utilizadores em memória para colaboradores persistidos quando a equipa avançar para Etapa 3.

3. Gestão de colaboradores
  - Implementar `ColaboradorController` com rotas MVC de listagem, registo, edição e desativação.
  - Criar templates `colaboradores/list.html` e `colaboradores/form.html`.
  - Garantir que o formulário de registo é acessível apenas ao `DIRETOR`.

4. Relatórios
  - Implementar `IRelatorioService`/`RelatorioService` com agregações de ocupação, estadias, reservas, pagamentos e serviços extra.
  - Implementar `RelatorioController` com filtros por período e exportações CSV/PDF.
  - Criar templates `relatorios/list.html` ou integrar no dashboard do diretor.

5. Testes e QA
  - Testes de serviços para agregações e validação de colaboradores.
  - Testes MVC para renderização de páginas, submissão de formulários e downloads.
  - Testes de autorização para confirmar a matriz de permissões.

6. Documentação e entrega
  - Atualizar `quickstart.md` com passos de navegação na interface.
  - Gerar Javadoc HTML com Maven Javadoc Plugin.
  - Rever `spec.md`, `tasks.md` e `permissoes.md` antes da entrega.

## Tarefas Técnicas Chave

- MVC: `GET /relatorios` para página de filtros e resultados.
- MVC: `POST /relatorios/gerar` para gerar relatório e regressar à página.
- Export: `GET /relatorios/exportar/csv` e `GET /relatorios/exportar/pdf`.
- MVC: `GET /colaboradores`, `GET /colaboradores/novo`, `POST /colaboradores`, `GET /colaboradores/{id}/editar`, `POST /colaboradores/{id}`, `POST /colaboradores/{id}/desativar`.
- Segurança: aplicar `hasRole('DIRETOR')` nas rotas de relatórios financeiros e colaboradores.
- Dados: validar `tipoColaborador` como enum `TipoColaborador`.
- Documentação: `mvn javadoc:javadoc` deve gerar HTML em `target/reports/apidocs`.

## Critérios de Aceitação

- Relatório padrão é gerado e mostrado na interface com métricas definidas em `spec.md`.
- CSV/PDF são descarregados a partir da interface e preservam os filtros aplicados.
- Gestão de colaboradores suporta criação, edição e desativação pelo `DIRETOR`.
- Lista de colaboradores reflete alterações imediatamente.
- A documentação HTML de código é gerada pelo Maven sem erros bloqueantes.
- Testes automatizados cobrem agregações, validações de formulário e regras de permissão.

## Riscos e Mitigação

- Risco: agregações lentas em períodos grandes. Mitigação: índices por datas e fallback com estado de processamento na página.
- Risco: permissões divergentes entre controller e navegação. Mitigação: matriz única em `permissoes.md` e testes por perfil.
- Risco: enum de colaborador tratado como texto livre no formulário. Mitigação: popular select a partir de `TipoColaborador.values()` e validar no DTO.
