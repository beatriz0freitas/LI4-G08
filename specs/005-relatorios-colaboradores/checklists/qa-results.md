# QA Results: Relatórios e Colaboradores

**Data:** 2026-05-27
**Spec:** `005-relatorios-colaboradores`
**Âmbito:** verificação focada da LAC-14 e rastreabilidade documental da spec 005.

## Resultado

| Área | Estado | Evidência |
| --- | --- | --- |
| PDF válido | PASS | `RelatorioServiceTest.gerarPdfDeveSerParseavelEConterTextoEsperado` valida assinatura `%PDF-` e parse com PDFBox. |
| Agrupamento | PASS | `RelatorioServiceTest.gerarAgrupamentosDeveConsolidarDadosPorDia` valida totais por grupo. |
| Limite de período | PASS | `RelatorioServiceTest.periodoSuperiorATresMesesDeveFalhar` e `RelatorioControllerTest.exportarPeriodoSuperiorATresMesesDeveRetornarErro`. |
| Exportação MVC | PASS | `RelatorioControllerTest.exportarCsvDeveDevolverFicheiro` e `RelatorioControllerTest.exportarPdfDeveDevolverFicheiroPdf`. |
| Rastreabilidade Speckit | PASS | `spec.md`, `data-model.md`, `contract.md`, `tasks.md`, `RF-03`, `UC-13` e matriz de rastreabilidade atualizados. |

## Comando Executado

```bash
mvn test -Dtest=RelatorioServiceTest,RelatorioControllerTest
```

Resultado: 15 testes executados, 0 falhas, 0 erros, 0 ignorados.

## Observações

- A geração PDF usa Apache PDFBox 3.0.0.
- A agregação é calculada no serviço e reutilizada pela página, CSV e PDF.
- Exportações com período superior a 3 meses devolvem erro claro antes de processar o relatório.
