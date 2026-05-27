# Atualizações de Requisitos e Use Cases por Lacunas Speckit

**Data:** 2026-05-27  
**Âmbito:** consolidação das alterações aplicadas após a análise de `docs/Etapa4/analise-lacunas-implementacao-speckit.md`, cobrindo todas as lacunas `LAC-01` a `LAC-15`.

## Síntese

As lacunas foram tratadas sem criar novos identificadores desnecessários. Sempre que possível, os requisitos `RF-*`, regras `RD-*` e casos de uso `UC-*` existentes foram reforçados para preservar rastreabilidade. As specs Speckit `001` a `005` foram alinhadas com o estado real da implementação.

| Lacuna | Requisitos/regras atualizados | Use cases afetados | Atualização aplicada |
| --- | --- | --- | --- |
| `LAC-01` | Rastreabilidade Speckit transversal | Todos os UC envolvidos nas specs `001`-`005` | Estados das specs revistos, feature ativa corrigida, checklists/tarefas regularizadas e `qa-results.md` criado. |
| `LAC-02` | `RF-11`, `RD-10` | `UC-09` | Plano de cuidados clarificado como funcionalidade real associada à estadia, com tarefas, prioridade, instruções e encerramento. |
| `LAC-03` | `RF-09`, `RF-10`, `RD-03`, `RD-04`, `RD-09` | `UC-07`, `UC-08`, `UC-10`, `UC-11` | Check-out passou a calcular cobrança complementar com serviços extra, intervenções clínicas e dias adicionais. |
| `LAC-04` | `RF-08`, `RF-10`, `RF-18`, `RD-04`, `RD-11` | `UC-06`, `UC-08` | Tarifa base estabilizada como tarifa diária ativa por tipo de alojamento. |
| `LAC-05` | `RF-08`, `RF-10`, `RD-04` | `UC-06`, `UC-07`, `UC-08` | Método de pagamento passou a ser obrigatório no check-in e no check-out; `NAO_DEFINIDO` deixou de existir. |
| `LAC-06` | `RF-06`, `RD-01`, `RD-08` | `UC-02`, `UC-04` | Disponibilidade centralizada, considerando reservas sobrepostas, estadias ativas, limpeza, espécie/capacidade e alojamento ativo. |
| `LAC-07` | `RF-07`, `RD-02`, `RD-06` | `UC-04`, `UC-05`, `UC-06`, `UC-07` | Ciclo de vida da reserva separado em `ATIVA`, `CONFIRMADA`, `CANCELADA` e `CONCLUIDA`. |
| `LAC-08` | `RF-08`, `RD-07` | `UC-06` | Regra explícita: um animal não pode ter duas estadias ativas. |
| `LAC-09` | `RF-09`, `RD-03`, `RD-04`, `RD-01` | `UC-07` | Check-out tratado como operação transacional: falha em pagamento ou limpeza aborta o fecho. |
| `LAC-10` | `RF-14`, `RF-17`, `RF-18`, `RD-09`, `RD-11` | `UC-10`, `UC-11` | Serviços extra e intervenções clínicas passaram a exigir estadia ativa, tipo/catálogo válido, responsável quando aplicável e custo não negativo. |
| `LAC-11` | `RF-05`, `RF-14` | `UC-11`, `UC-13` | Histórico consolidado clarificado com filtros combinados por animal, cliente/tutor, estadia, período e tipo de evento. |
| `LAC-12` | Regras arquiteturais da Etapa 2 e specs `003`/`004` | `UC-02`, `UC-04`, `UC-06`, `UC-07`, `UC-11` | Controladores passaram a delegar regras em serviços de aplicação; regras críticas ficam fora da camada web. |
| `LAC-13` | `RF-19`, `FR-011`, `SC-008`, `SC-009` | `UC-01`, `UC-05`, `UC-06`, `UC-07`, `UC-09`, `UC-10`, `UC-11`, `UC-13` | Auditoria centralizada para operações críticas com `AuditoriaEvento`, filtros, retenção e acesso restrito. |
| `LAC-14` | `RF-03`, `FR-001`, `FR-003`, `FR-012`, `FR-013`, `SC-010`, `SC-011`, `SC-012` | `UC-13` | Relatórios passaram a exigir PDF válido, agrupamento comum web/CSV/PDF e limite síncrono de 3 meses. |
| `LAC-15` | Critérios de verificação das specs `001`-`005` | Todos os UC críticos | Testes reforçados para fluxos de reserva, estadia, pagamento, cuidados, clínica, histórico, auditoria e relatórios. |

## Alterações Documentais Aplicadas

| Artefacto | Atualização |
| --- | --- |
| `.specify/feature.json` | Feature ativa alinhada com `specs/005-relatorios-colaboradores`. |
| `specs/001-fundacao-hotel-animais/spec.md` | Estado atualizado para implementação validada. |
| `specs/002-registo-clientes-alojamentos/spec.md` e `tasks.md` | Estado/tarefas alinhados com a implementação existente. |
| `specs/003-reservas-estadias-pagamentos/spec.md` | Regras de reserva, disponibilidade, check-in, check-out, pagamentos, tarifa e atomicidade consolidadas. |
| `specs/004-cuidados-clinica-limpeza/spec.md` e `tasks.md` | Plano de cuidados, serviços extra, clínica, histórico e testes descritos como implementados. |
| `specs/005-relatorios-colaboradores/spec.md` | Relatórios, PDF, agrupamentos, auditoria e critérios de sucesso alinhados. |
| `specs/005-relatorios-colaboradores/checklists/requirements.md` | Checklist de requisitos fechada. |
| `specs/005-relatorios-colaboradores/checklists/qa-results.md` | Criado registo de QA para validação de relatórios. |
| `docs/Etapa1/04-domain-model/domain-model.md` | Acrescentados `TipoAlojamentoTarifa`, `TipoServicoExtra`, `PlanoCuidados`, `PrioridadePlano` e regra de método de pagamento obrigatório. |
| `docs/Etapa1/03-use-cases/UC-04.md` | Reserva fica `ATIVA`; confirmação operacional ocorre no check-in. |
| `docs/Etapa1/03-use-cases/UC-06.md` | Check-in confirma reserva, impede estadia duplicada e processa pagamento base por tarifa ativa. |
| `docs/Etapa1/03-use-cases/UC-07.md` | Check-out inclui cobrança complementar, conclusão de reserva, limpeza pendente e atomicidade. |
| `docs/Etapa1/03-use-cases/UC-08.md` | Pagamento dividido entre valor base no check-in e valor complementar no check-out, com método obrigatório. |
| `docs/Etapa1/03-use-cases/UC-10.md` | Serviço extra limitado a estadia ativa, catálogo controlado e custo não negativo. |
| `docs/Etapa1/03-use-cases/UC-11.md` | Intervenção clínica exige estadia ativa, responsável clínico, descrição, data/hora e custo válido. |
| `docs/Etapa1/03-use-cases/UC-13.md` | Relatórios com filtros, agrupamentos, PDF válido e limite de exportação síncrona. |
| `docs/Etapa1/03-use-cases/traceability.md` | Métodos de serviço atualizados para refletir os contratos reais da implementação. |
| `docs/Etapa4/analise-lacunas-implementacao-speckit.md` | Todas as lacunas passaram a ter estado após correção, evidência e fecho coerente. |

## Evidência Técnica

Foram verificados, entre outros, os seguintes pontos de implementação:

- `PagamentoService` calcula valor base por `TipoAlojamentoTarifa`, exige método de pagamento e calcula cobrança complementar.
- `EstadiaService` valida check-in com método obrigatório, bloqueia duplicação de estadia ativa e executa check-out transacional.
- `AvailabilityDomainService` centraliza disponibilidade com reservas, estadias ativas, limpeza e compatibilidade.
- `ReservaController` e `HistoricoController` delegam regras em serviços.
- `ServicoExtraService`, `IntervencaoClinicaService`, `HistoricoService`, `PlanoCuidadosService`, `AuditoriaService` e `RelatorioService` implementam as regras que fecharam as lacunas.

Testes relevantes:

```bash
mvn test -Dtest=PagamentoServiceTest,EstadiaServiceTest,CheckInServiceTest,CheckOutSequenceServiceTest,AvailabilityDomainServiceTest,ReservaServiceUnitTest,ReservaConfirmIntegrationTest,ServicoExtraServiceTest,IntervencaoClinicaServiceTest,HistoricoServiceTest,PlanoCuidadosServiceTest,RelatorioServiceTest,RelatorioControllerTest,AuditoriaServiceTest,AuditoriaControllerTest,FluxoOperacionalEndToEndIntegrationTest
```

Resultado da validação após recriação limpa da BD `db-tests`: `127` testes executados, `0` falhas, `0` erros.

## Pressupostos e Limites

- A operação `EDITAR_RESERVA` permanece apenas como expectativa de auditoria futura porque não existe fluxo de edição de reserva na aplicação atual.
- Tarefas de melhoria técnica como Javadoc e medição quantitativa de cobertura não reabrem as lacunas funcionais; ficam como qualidade residual da Etapa 4.
- Não foram criados novos IDs de requisitos ou use cases quando os IDs existentes já cobriam a alteração com rastreabilidade suficiente.
