# Quickstart: Fase 4 — Operação diária, clínica e limpeza avançada

## Pré-requisitos
- Java 21
- Maven
- Base de dados H2 para testes locais

## Executar a aplicação
1. Entrar no projeto:

```bash
cd /Users/alicesoares/Desktop/LEI/Projetos/LI4-G08/PatasBigodesApp
```

2. Compilar e correr os testes principais:

```bash
mvn test
```

3. Arrancar a aplicação em desenvolvimento:

```bash
mvn spring-boot:run
```

## Verificações úteis desta feature
- Confirmar que existe navegação para `Estadias` e `Histórico`.
- Confirmar que o check-in e o check-out continuam acessíveis a partir das listas de reservas e histórico.
- Confirmar que os novos registos clínicos e operacionais aparecem no historial com autor e timestamp.

## Fluxo de validação recomendado
1. Criar uma reserva.
2. Fazer check-in.
3. Registar um cuidado diário.
4. Registar uma alteração ao estado de saúde.
5. Registar um serviço extra.
6. Adicionar uma nota operacional.
7. Fazer check-out e confirmar que os extras são incluídos na faturação.

## Observações
- Se os testes de integração falharem por autenticação ou CSRF, rever a configuração dos testes com `spring-security-test`.
- Se surgir erro de integridade na base de dados, confirmar que as fixtures de teste preenchem chaves obrigatórias e relações com `Estadia`/`Reserva`.
