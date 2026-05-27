# ADRs - Etapa 2

Este diretorio agrega as Decisoes Arquiteturais (ADR) da Etapa 2.

## Indice
- [ADR-01 - Arquitetura em camadas numa aplicação centralizada](ADR-01-monolito-camadas.md)
- [ADR-02 - Interface de utilizador com renderização no servidor](ADR-02-spring-mvc-thymeleaf-ssr.md)
- [ADR-03 - Persistência de dados em SGBD relacional](ADR-03-persistencia-sgbd-relacional.md)
- [ADR-04 - Persistência de dados com MySQL e padrão repositório](ADR-04-mysql-base-dados.md)
- [ADR-05 - Autenticação e autorização com controlo de acesso por perfil](ADR-05-controlo-acesso-perfil.md)
- [ADR-06 - Isolamento da camada de apresentação através de DTOs](ADR-06-isolamento-apresentacao-dtos.md)
- [ADR-07 - Utilização de Docker no ambiente de desenvolvimento e testes](ADR-07-docker-desenvolvimento-testes.md)
- [ADR-08 - Auditoria funcional persistente própria](ADR-08-auditoria-funcional-persistente.md)
- [ADR-09 - Catálogos persistentes para tarifas e serviços extra](ADR-09-catalogos-parametrizaveis-tarifas-servicos.md)
- [ADR-10 - Plano de cuidados dinâmico associado a animal e estadia](ADR-10-plano-cuidados-dinamico.md)
- [ADR-11 - Exportação de relatórios com agregação única e geração PDF/CSV](ADR-11-exportacao-relatorios-pdf-csv-agregacao.md)

## Convencoes
- Estado: Proposto | Aceite | Rejeitado | Substituido.
- Cada ADR deve referenciar impacto em arquitetura, classes, sequencias e requisitos (RF/RD/RNF/UC).
- Sempre que uma nova decisao alterar uma ADR anterior, atualizar o estado da ADR anterior para `Substituido`.
