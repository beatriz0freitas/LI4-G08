# Diagrama de Classes de Design (Etapa 2)

## Objetivo
Este artefacto apresenta o diagrama de classes de design do sistema de gestao de hotel de animais, alinhado com:
- o modelo de dominio da Etapa 1;
- a arquitetura definida na Etapa 2 (MVC em camadas com Spring Boot).

Ficheiros do diagrama:
- `class-diagram.mmd` (Mermaid)
- `class-diagram.puml` (PlantUML)

## Escopo
O diagrama inclui:
- classes da camada de apresentacao (controllers);
- classes da camada de aplicacao (services);
- classes da camada de dados (repositories);
- entidades e enumeracoes de dominio;
- associacoes de dominio e dependencias tecnicas entre camadas.

## Decisoes de modelacao
1. O diagrama e de design tecnico (Etapa 2), por isso acrescenta controllers, services e repositories ao modelo de dominio.
2. As relacoes entre entidades preservam as cardinalidades definidas na Etapa 1.
3. Os controllers dependem de interfaces de service (`INomeService`), e as classes concretas sao modeladas sem sufixo adicional (`NomeService`).
4. Os metodos dos services refletem operacoes principais descritas em arquitetura e casos de uso.
5. Os repositories mostram apenas operacoes relevantes para regras de negocio criticas (disponibilidade, check-in/check-out, pagamentos e historico clinico).
6. A navegabilidade das associacoes de dominio privilegia quem detem a referencia (por exemplo, `Reserva -> Animal`, `Estadia -> Reserva`, `Nota -> Colaborador`, `RegistoCuidado -> Colaborador`).
7. As associacoes de dominio diferenciam composicao (`*--`) e agregacao (`o--`) nos dois formatos do diagrama.

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
1. O detalhe de assinaturas de metodos e indicativo e orientado ao design; pode ser refinado na Etapa 3.
2. O diagrama representa a estrutura estatica principal, nao substitui diagramas de sequencia para fluxos comportamentais.
