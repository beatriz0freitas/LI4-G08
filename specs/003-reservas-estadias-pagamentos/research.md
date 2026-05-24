# Research: Reservas, Estadias e Pagamentos

## 1. Alinhamento arquitetural com Etapa 2

**Decision**: Implementar a feature 003 sobre a arquitetura monolítica MVC já adotada, sem criação de novos módulos tecnológicos.

**Rationale**: Os artefactos de Etapa 2 já definem responsabilidades para `ReservaController`, `EstadiaController`, `PagamentoController`, respetivos serviços e repositórios. Reutilizar esta estrutura reduz risco e mantém rastreabilidade direta com os diagramas de componentes e sequência.

**Alternatives considered**: Criar API REST separada ou frontend SPA dedicado nesta fase. Rejeitado por aumentar complexidade e quebrar consistência com ADR-02 (SSR com Thymeleaf).

## 2. Regras de domínio para estados e transições

**Decision**: Centralizar validações de transição no service layer para `Reserva`, `Estadia`, `Pagamento` e `Alojamento`.

**Rationale**: RD-01, RD-02, RD-03, RD-04, RD-06, RD-07 e RD-09 exigem consistência transacional e bloqueio de estados inválidos (ex.: check-out sem check-in, reserva cancelada reativada, dupla estadia ativa).

**Alternatives considered**: Validar apenas no controller ou no frontend. Rejeitado por não garantir integridade em chamadas concorrentes ou pontos de entrada alternativos.

## 3. Estratégia de pagamentos

**Decision**: Modelar pagamentos por momento operacional (`CHECK_IN`, `CHECK_OUT`) com método e estado obrigatórios.

**Rationale**: RF-10 e RD-04 impõem separação entre pagamento base da estadia e faturação complementar de extras/intervenções no fim da estadia.

**Alternatives considered**: Pagamento único no final da estadia; estado implícito sem enum dedicado. Rejeitado por incompatibilidade com US-10/US-11 e menor rastreabilidade financeira.

## 4. Consultas de direção (dashboard e histórico)

**Decision**: Tratar dashboard operacional e histórico financeiro como fluxos de leitura independentes dos fluxos transacionais de receção. O dashboard mantém um par `IDashboardService`/`DashboardService` próprio, mas esse serviço atua apenas como orquestrador e consome métricas expostas pelas interfaces dos serviços de domínio (`IReservaService`, `IEstadiaService`, `IPagamentoService`, `IAlojamentoService`). O histórico deve usar consultas paginadas com filtros por cliente, animal, estado e intervalo temporal, preservando os parâmetros entre páginas.

**Rationale**: RF-01 e RF-05 têm perfil de consumo diferente (análise/monitorização), exigindo endpoints e templates de consulta com filtros por período, navegação entre páginas e visibilidade de pendentes sem sobrecarregar a interface.

**Alternatives considered**: Reaproveitar ecrãs de receção para direção sem separação. Rejeitado por comprometer usabilidade e políticas de acesso por perfil.

## 5. Estratégia de testes obrigatória

**Decision**: Definir cobertura mínima obrigatória por funcionalidade P1, por regra de domínio crítica e por caso de uso principal.

**Rationale**: A constituição (Verification Before Expansion) e a própria spec exigem verificação explícita antes de fechar a fase.

**Alternatives considered**: Apenas testes unitários; apenas validação manual. Rejeitado por insuficiência para capturar regressões de estado e fluxos transacionais multi-entidade.

## 6. Integração com UI e mockups

**Decision**: Alinhar interfaces com `wf03-reservas.html` (receção) e `wf02-dashboard-diretor.html` (direção), mantendo compatibilidade visual e de navegação com Etapa 2.

**Rationale**: Evita divergência entre implementação e design aprovado, reduzindo retrabalho na Etapa 3.

**Alternatives considered**: Reestruturar navegação para novos ecrãs fora dos mockups existentes. Rejeitado por menor rastreabilidade e aumento de risco de inconsistência.
