# Diagrama de Classes de Design (Etapa 2)

## Objetivo
Este artefacto apresenta o diagrama de classes de design do sistema de gestao de hotel de animais, alinhado com:
- o modelo de dominio da Etapa 1;
- a arquitetura definida na Etapa 2 (MVC em camadas).

Ficheiros do diagrama:
- `class-diagram.mmd` (Mermaid)
- `class-diagram.puml` (PlantUML)
- `class-diagram-simple.puml` (PlantUML simplificado)

## Escopo
O diagrama inclui:
- classes da camada de apresentacao (controllers);
- classes da camada de aplicacao (services);
- classes da camada de dados (repositories);
- entidades e enumeracoes de dominio;
- associacoes de dominio e dependencias tecnicas entre camadas.

## Decisoes de modelacao
1. O diagrama e de design tecnico (Etapa 2), por isso acrescenta controllers, services e repositories ao modelo de dominio.
2. As relacoes entre entidades refletem as associacoes JPA implementadas na aplicacao atual.
3. Os controllers dependem de interfaces de service (`INomeService`), e as classes concretas sao modeladas sem sufixo adicional (`NomeService`).
4. As classes de servico e repositories sao representadas ao nivel estrutural para evitar assinaturas desatualizadas face ao codigo.
5. A navegabilidade das associacoes de dominio privilegia quem detem a referencia JPA (por exemplo, `Reserva -> Animal`, `Estadia -> Reserva`, `Nota -> Reserva`).
6. As associacoes de dominio diferenciam composicao (`*--`) e agregacao (`o--`) nos dois formatos do diagrama.

## Rastreabilidade resumida
- UC-01: autenticacao e autorizacao (`AuthController`, `IColaboradorService`)
- UC-02, UC-12: disponibilidade e limpeza (`IAlojamentoService`, `AlojamentoRepository`)
- UC-03: registo de tutor e animal (`TutorAnimalController`, `ITutorService`, `IAnimalService`)
- UC-04, UC-05: criacao e cancelamento de reservas (`ReservaController`, `IReservaService`)
- UC-06, UC-07, UC-09: check-in, check-out e cuidados (`EstadiaController`, `IEstadiaService`)
- UC-08: pagamentos (`PagamentoController`, `IPagamentoService`)
- UC-10: servicos extra (`IServicoExtraService`)
- UC-11: intervencoes clinicas (`ClinicaController`, `IClinicaService`)
- UC-13: relatorios (`RelatorioController`, `IRelatorioService`)

## Pressupostos
1. O diagrama representa a estrutura estatica principal, nao substitui diagramas de sequencia para fluxos comportamentais.
2. As classes tecnicas externas ao projeto sao omitidas para manter o diagrama centrado nas classes da aplicacao.
