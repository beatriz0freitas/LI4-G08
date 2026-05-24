# Matriz de Permissões por Perfil

**Etapa:** 2 — Arquitetura e Design  
**Base arquitetural:** Spring MVC com Thymeleaf, sessão HTTP e autorização por `TipoColaborador`  
**Referências:** [architecture.md](../01-architecture/architecture.md), [RNF-04](../../Etapa1/02-requirements/non-functional/RNF-04.md), [RF-02](../../Etapa1/02-requirements/functional/RF-02.md), [UC-01](../../Etapa1/03-use-cases/UC-01.md)

## Perfis

Os perfis do sistema correspondem à enumeração `TipoColaborador` definida no modelo de domínio:

- `DIRETOR`
- `FUNCIONARIO_RECEPCAO`
- `CUIDADOR`
- `MEDICO_VETERINARIO`
- `RESPONSAVEL_LIMPEZA`

O controlo de acesso deve ser aplicado nos controllers Spring MVC com `@PreAuthorize`/`hasRole()` ou configuração equivalente em `SecurityConfig`. Os controllers devolvem páginas Thymeleaf, redirecionamentos ou ficheiros de exportação.

## Matriz por Funcionalidade

| Funcionalidade | Rotas MVC previstas/existentes | Diretor | Receção | Cuidador | Veterinário | Limpeza | Origem |
|---|---|---:|---:|---:|---:|---:|---|
| Login e logout | `/login`, `/logout` | Sim | Sim | Sim | Sim | Sim | UC-01, RNF-04 |
| Dashboard operacional e financeiro | `/dashboard` | Sim | Não | Não | Não | Não | RF-01, RF-03, UC-13 |
| Relatórios por período | `/relatorios`, `/relatorios/gerar`, `/relatorios/exportar/csv`, `/relatorios/exportar/pdf` | Sim | Não | Não | Não | Não | RF-03, UC-13 |
| Gestão de colaboradores | `/colaboradores`, `/colaboradores/novo`, `/colaboradores/{id}/editar`, `/colaboradores/{id}/desativar` | Sim | Não | Não | Não | Não | RF-02, US-03 |
| Registo de novo colaborador | `/colaboradores/novo` | Sim | Não | Não | Não | Não | RF-02, US-03 |
| Consulta de alojamentos/disponibilidade | `/alojamentos`, `/reservas/disponibilidade` | Sim | Sim | Não | Não | Sim | RF-01, UC-02, UC-12 |
| Tutores e animais | `/tutores`, `/tutores/novo`, `/tutores/{id}`, `/tutores/{id}/editar`, `/animais` | Sim | Sim | Não | Não | Não | UC-03 |
| Reservas | `/reservas`, `/reservas/novo`, `/reservas/{id}`, `/reservas/{id}/cancelar`, `/reservas/{id}/confirmar` | Sim | Sim | Não | Não | Não | UC-04, UC-05 |
| Check-in e check-out | `/estadias`, `/estadias/check-in`, `/estadias/check-out` | Sim | Sim | Não | Não | Não | UC-06, UC-07, RF-09 |
| Pagamentos | `/pagamentos` | Sim | Sim | Não | Não | Não | RF-10, UC-08 |
| Plano de cuidados | `/plano-cuidados` | Sim | Sim | Sim | Sim | Não | UC-06, UC-09, UC-11 |
| Registo de cuidados diários | `/cuidados`, `/cuidados/create` | Sim | Não | Sim | Não | Não | UC-09 |
| Notas operacionais | `/notas/create` | Sim | Sim | Sim | Sim | Não | US-17, UC-09, UC-11 |
| Serviços extra | `/extras`, `/extras/create` | Sim | Não | Sim | Sim | Não | RF-17, UC-10 |
| Clínica e alterações de saúde | `/clinica/intervencoes`, `/clinica/alteracoes` | Sim | Não | Não | Sim | Não | UC-11 |
| Histórico de estadias e pagamentos | `/historico`, `/historico/eventos` | Sim | Sim | Não | Sim | Não | US-05, RF-10 |
| Limpeza de alojamentos | `/limpeza`, `/limpeza/{id}/limpo` | Sim | Não | Não | Não | Sim | UC-12 |

## Regras Normativas

- O `DIRETOR` é o único perfil autorizado a aceder à página de registo, edição e desativação de colaboradores.
- O campo `tipoColaborador` de um colaborador deve ser persistido e validado como enum `TipoColaborador`, nunca como texto livre.
- Os dados financeiros detalhados, relatórios executivos e exportações de relatórios pertencem ao perfil `DIRETOR`.
- As rotas de escrita devem usar formulários Thymeleaf com `POST`, token CSRF ativo e retorno por template ou `redirect:`.
- Tentativas de acesso sem perfil suficiente devem resultar em página de erro/autorização negada ou redirecionamento definido pela configuração de segurança, preservando sessão e auditoria.

## Pressupostos

- A implementação atual ainda tem alguns controllers sem `@PreAuthorize`; esta matriz define o comportamento esperado para consolidar a autorização na Etapa 3.
- O `DIRETOR` tem acesso transversal por responsabilidade de gestão, mas as operações continuam sujeitas às regras de domínio de cada módulo.
