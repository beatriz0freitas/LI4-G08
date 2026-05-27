# ADR-11 - Exportação de relatórios com agregação única e geração PDF/CSV

**Estado:** Aceite
**Data:** 2026-05-27
**Decisores:** Equipa LI4-G08
**Contexto:** Etapa 2 - Arquitetura e Design; speckit 005

## Contexto
Os relatórios operacionais e financeiros precisam de ser consultados na aplicação e exportados para formatos portáteis. O speckit 005 define exportação CSV/PDF e limita o período de exportação para evitar operações longas no servidor.

A implementação atual gera relatórios de forma síncrona a partir dos repositórios existentes, sem criar uma entidade persistente `Relatorio`.

## Decisão
Centralizar a agregação de dados em `RelatorioService` e reutilizar a mesma origem de dados para a vista web, CSV e PDF. O formato PDF é gerado no servidor com Apache PDFBox 3.0.0, enquanto CSV é produzido como texto estruturado.

As exportações são servidas de forma síncrona por `RelatorioController` e limitadas a períodos até três meses. A geração do relatório é registada em auditoria como evento funcional, sem persistir o relatório como entidade de domínio.

## Alternativas consideradas
- Gerar PDF manualmente como texto simples, rejeitado por não produzir um documento PDF válido.
- Persistir cada relatório gerado, rejeitado por não existir requisito de arquivo documental dos ficheiros exportados.
- Gerar exportações de qualquer período sem limite, rejeitado por risco de degradação de desempenho e tempos de resposta altos.
- Duplicar lógica de agregação por formato, rejeitado por aumentar divergências entre vista web, CSV e PDF.

## Consequências
### Positivas
- Web, CSV e PDF apresentam métricas coerentes.
- A dependência PDF fica isolada no serviço de relatórios.
- O limite de período reduz risco operacional em pedidos síncronos.
- A auditoria conserva rastreabilidade da geração sem armazenar ficheiros.

### Negativas
- Relatórios muito extensos exigiriam uma abordagem assíncrona futura.
- Alterações ao formato PDF implicam manutenção de código específico de layout.

## Impacto na arquitetura
- [architecture.md](../01-architecture/architecture.md)
- [components.mmd](../01-architecture/components.mmd)
- Implementação: `RelatorioController`, `RelatorioService`, `IRelatorioService`, `RelatorioResumoDto`, `RelatorioAgrupamentoDto`, `AuditoriaOperacaoService`.
- Dependência técnica: `org.apache.pdfbox:pdfbox:3.0.0`.

## Rastreabilidade
- Requisitos: RF-03, RF-10, RF-17, RF-19, RNF-07, RNF-09.
- Speckits: `specs/005-relatorios-colaboradores/`.
- Decisões dependentes: ADR-01, ADR-03, ADR-04, ADR-06, ADR-08.

## Conformidade com a implementação atual
- `RelatorioController` valida o limite de três meses antes de consultar ou exportar.
- `RelatorioService` agrega reservas, estadias, pagamentos e serviços extra por período.
- `gerarCsv` e `gerarPdf` reutilizam o resultado de `gerarRelatorioPeriodo`.
- `pom.xml` inclui Apache PDFBox 3.0.0 para produzir ficheiros PDF válidos.

## Status atual
- [ ] Proposta
- [x] Aprovada
- [x] Implementada
- [ ] Validada
