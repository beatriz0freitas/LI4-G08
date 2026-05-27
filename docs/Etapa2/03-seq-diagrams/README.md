# Diagramas de Sequência

Esta pasta contém um diagrama de sequência por caso de uso da Etapa 1, em dois formatos:
- Mermaid (`.mmd`)
- PlantUML (`.puml`)

## Mapeamento
- UC-01 - Autenticar no Sistema
- UC-02 - Consultar Disponibilidade de Alojamentos
- UC-03 - Registar Tutor e Animal
- UC-04 - Criar Reserva
- UC-05 - Cancelar Reserva
- UC-06 - Registar Check-in
- UC-07 - Registar Check-out
- UC-08 - Processar Pagamento
- UC-09 - Registar Cuidados e Notas Operacionais
- UC-10 - Registar Servico Extra
- UC-11 - Gerir Historial Clinico
- UC-12 - Registar Limpeza de Alojamento
- UC-13 - Consultar Dashboard e Gerar Relatorios
- UC-14 - Gerir Colaboradores e Perfis
- UC-15 - Gerir Tarifas e Catalogos
- UC-16 - Consultar Auditoria

## Convencoes
- Os diagramas seguem a arquitetura atual da aplicação: controller -> service -> repository, incluindo serviços de domínio e auditoria quando existem no código.
- Os fluxos alternativos dos casos de uso foram modelados com `alt` sempre que relevante.
