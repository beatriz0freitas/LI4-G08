# Traceability Matrix

Este documento liga os casos de uso as user stories e aos requisitos da Etapa 1. A matriz evita pormenores de implementacao e foca-se na rastreabilidade funcional, de dominio e de qualidade.

| Use Case | Atores principais | User Stories cobertas | Requisitos relacionados | Observacoes |
|---|---|---|---|---|
| UC-01 - Autenticar no Sistema | Todos os colaboradores | US-03 | RNF-04, RNF-06 | Garante acesso individual e aplicacao do perfil atribuido. |
| UC-02 - Consultar Disponibilidade de Alojamentos | Rececao, Diretor | US-01, US-08, US-14 | RF-06, RD-01, RNF-01, RNF-03, RNF-06 | Suporta decisao antes da criacao de reservas. |
| UC-03 - Registar Tutor e Animal | Rececao | US-11 | RF-04, RD-05, RD-08, RNF-05 | Garante dados base de tutor e animal para reservas e historico. |
| UC-04 - Criar Reserva | Rececao | US-08, US-15 | RF-06, RF-07, RF-16, RD-01, RD-05 | Inclui validacao de disponibilidade e possibilidade de agendar servicos opcionais. |
| UC-05 - Cancelar Reserva | Rececao | US-08 | RF-07, RD-06 | Apenas aplicavel a reservas ainda nao convertidas em estadia. |
| UC-06 - Registar Check-in | Rececao | US-09, US-12, US-16 | RF-08, RF-10, RF-11, RD-02, RD-04, RD-07 | Abre estadia, regista pagamento base e disponibiliza plano de cuidados. |
| UC-07 - Registar Check-out | Rececao | US-09, US-13, US-22 | RF-09, RF-10, RF-15, RD-03, RD-04 | Fecha estadia, cobra complemento e coloca alojamento pendente de limpeza. |
| UC-08 - Processar Pagamento | Rececao | US-12, US-13 | RF-10, RD-04 | Caso de uso incluido por check-in e check-out. |
| UC-09 - Registar Cuidados e Notas Operacionais | Cuidador, Rececao | US-16, US-17, US-18, US-19, US-21 | RF-11, RF-12, RF-13, RF-16, RD-10, RNF-02, RNF-09 | Cobre continuidade operacional e alteracoes ao estado de saude. |
| UC-10 - Registar Servico Extra | Cuidador | US-20 | RF-17, RD-09, RD-11 | Regista servicos extra realizados durante estadia ativa. |
| UC-11 - Gerir Historial Clinico | Medico Veterinario | US-24, US-25, US-26 | RF-13, RF-14, RD-09, RNF-05 | Cobre consulta de historial, alteracoes recentes e intervencoes. |
| UC-12 - Registar Limpeza de Alojamento | Responsavel pela Limpeza | US-22, US-23 | RF-15, RD-01 | Atualiza estado de limpeza para permitir nova disponibilidade. |
| UC-13 - Consultar Dashboard e Gerar Relatorios | Diretor | US-01, US-02, US-04, US-05 | RF-01, RF-03, RNF-06, RNF-07 | Consolida indicadores e relatorios operacionais/financeiros. |

## Regras de manutencao

- Cada UC deve ter pelo menos uma ligacao a uma user story e a um requisito.
- Os fluxos devem manter-se ao nivel de negocio, sem elementos tecnicos ou detalhes de persistencia.
- Sempre que forem adicionadas ou removidas user stories ou requisitos, esta matriz deve ser atualizada.
