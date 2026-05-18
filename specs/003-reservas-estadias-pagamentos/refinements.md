# Refinements: Reservas, Estadias e Pagamentos

## 1. Dashboard como serviço orquestrador

O dashboard mantém um serviço próprio, mas sem acesso direto a repositórios.

O `DashboardService` passou a compor métricas vindas de `ReservaService`, `EstadiaService`, `PagamentoService` e `AlojamentoService`.

Motivo: reduzir acoplamento, manter a separação de responsabilidades e evitar duplicação de lógica de contagem na camada de apresentação.

## 2. Contagens expostas na camada de domínio

As contagens necessárias ao dashboard foram colocadas nos serviços de domínio, apoiadas por queries dedicadas nos repositórios.

O dashboard deixou de filtrar coleções em memória e passou a reutilizar a lógica já disponível no domínio.

Motivo: melhor alinhamento com a arquitetura em camadas e com a rastreabilidade dos requisitos RF-01 e RF-05.