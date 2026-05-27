# Validação da Interface e Navegação

Data da validação: 27 de maio de 2026.

## Âmbito

Esta validação cruza os mockups `wf02` a `wf09` com os templates e rotas atuais da aplicação. A reorganização da navegação solicitada substitui o menu plano dos mockups por uma estrutura por área funcional, mantendo a visibilidade por perfil definida em `docs/Etapa2/06-role-permissions/permissoes.md`.

## Navegação Adotada

| Área | Destino | Perfis com acesso | Observação |
| --- | --- | --- | --- |
| Início | `/` | Autenticado | Entrada funcional da aplicação. |
| Disponibilidade | `/reservas/disponibilidade` | Direção, Receção | Mapa único por período e tipo, com os estados operacionais de UC-02/RD-01. |
| Dashboard Direção / Visão geral | `/dashboard` | Direção | Subitem necessário para abrir o dashboard existente. |
| Dashboard Direção / Relatórios | `/relatorios` | Direção | Mantido sob direção. |
| Dashboard Direção / Configurações | `/admin/tarifas`, `/admin/tipos-servicos-extra`, `/colaboradores` | Direção | Administração agrupada num único nó. |
| Dashboard Direção / Auditoria | `/auditoria` | Direção | Consulta dos rastos funcionais. |
| Estadias / Plano Cuidados | `/plano-cuidados` | Direção, Receção, Cuidador, Médico Veterinário | Quadro agregado de turno com abertura do plano por estadia ativa. |
| Estadias / Histórico | `/historico` | Direção, Receção, Médico Veterinário | Consulta histórica existente. |
| Estadias / Check-in / Check-out | `/estadias` | Direção, Receção | Mantido por ser uma operação existente essencial ao fluxo de estadia. |
| Receção | `/tutores`, `/animais`, `/reservas` | Direção, Receção | Agrupa os três registos operacionais pedidos. |
| Clínica | `/clinica` | Direção, Médico Veterinário | Entrada direta. |
| Limpeza | `/limpeza` | Direção, Responsável de Limpeza | Entrada direta. |

## Problemas Corrigidos

| Problema detetado | Impacto | Correção aplicada |
| --- | --- | --- |
| Cartão inicial de cuidados apontava para `/plano-cuidados` sem `estadiaId` obrigatório. | Pedido inválido ao entrar a partir da página inicial. | Criada listagem inicial de planos ativos e seleção explícita da estadia. |
| Cartão inicial de limpeza era visível à receção. | Acesso a rota proibida e resposta `403`. | Visibilidade alinhada com Direção e Responsável de Limpeza. |
| Links de detalhe de animal usavam `/tutores/animais/{id}`. | Navegação para rota inexistente em confirmação de reserva e detalhe do tutor. | Ligações alteradas para `/animais/{id}`. |
| Plano de cuidados usava atributos e classes Bootstrap 5 sobre dependências Bootstrap 4. | Botões de expandir formulário não funcionavam e badges perdiam estilo. | Componentes ajustados para Bootstrap 4 e tema visual uniformizado. |
| A vista de alojamentos ficou sem item de menu próprio após a reorganização e o filtro falhava quando não existiam linhas. | Navegação sem contexto ativo e erro JavaScript numa lista vazia. | Vista integrada em Disponibilidade e filtro protegido para resultados vazios. |
| Rotas `/admin/**` dependiam apenas de anotações nos controladores. | Proteção menos explícita face ao menu de configurações. | Regra de segurança web explícita para `DIRETOR`. |
| O assistente de reservas aceitava abrir passos posteriores pelo fragmento do URL, mantendo a validação apenas no browser, e validava também o botão `Anterior`. | Progressão com dados incompletos ou datas inválidas; impossibilidade de regressar para corrigir certos dados. | Passo pedido passado por `step` e validado no controlador; datas validadas antes do alojamento; `Anterior` regressa sem bloquear a correção. |
| As ações de reserva a partir do tutor e do animal reabriam o assistente no início apesar do contexto já selecionado. | Passos redundantes e maior risco de seleção incoerente. | A ação do tutor abre a seleção de animal e a ação do animal abre a definição do período, preservando os identificadores validados. |
| A ação `Reservar` na consulta de disponibilidade perdia o alojamento pré-selecionado antes da escolha do animal. | O botão não preservava a escolha que motivou a criação da reserva. | A pré-seleção é mantida até haver animal e período suficientes para validar a compatibilidade e disponibilidade. |
| O botão de `Check-out` no histórico submetia diretamente a operação sem método de pagamento. | O controlador rejeitava sempre a ação disponibilizada nessa tabela. | O histórico passa a abrir o formulário de check-out para completar os dados obrigatórios. |
| `wf08` tinha mapa, estados, filtro e painel lateral sem equivalente funcional. | A receção não conseguia distinguir ocupação, reserva futura e limpeza numa única vista. | `/reservas/disponibilidade` apresenta todos os alojamentos classificados como livre, ocupado, reservado ou em limpeza, filtra por tipo e abre o detalhe selecionado. |
| O plano de cuidados dependia de criação manual com `animalId` recebido da interface. | Uma estadia podia não ter plano ou receber associação incoerente. | O `check-in` cria idempotentemente o plano com o animal da própria estadia; o `check-out` encerra-o. |
| `wf04` previa acompanhamento agregado do turno, mas só existia a edição isolada do plano. | O cuidador não tinha visão conjunta de animais e tarefas pendentes. | `/plano-cuidados` passa a apresentar cartões por animal em estadia ativa e progresso global do turno. |

## Conformidade com Mockups

| Mockup | Estado atual | Lacuna principal |
| --- | --- | --- |
| `wf02-dashboard-diretor.html` | Implementado em `/dashboard`. | Confirmar atualização periódica dos indicadores em execução real. |
| `wf03-reservas.html` | Implementado de forma funcional em `/reservas` e formulários associados. | O fluxo visual não replica integralmente o protótipo numa única vista. |
| `wf04-plano-cuidados.html` | Implementado funcionalmente em `/plano-cuidados` e no detalhe do plano. | Os cartões mostram apenas tarefas efetivamente registadas, sem gerar horários implícitos. |
| `wf05-historico-clinico.html` | Parcial através de `/clinica` e histórico. | Validar filtros e apresentação longitudinal contra o mockup. |
| `wf06-limpeza.html` | Implementado em `/limpeza`. | Sem evidência nesta revisão de atualizações em tempo real. |
| `wf07-colaboradores.html` | Implementado em `/colaboradores`. | Sem lacuna de navegação identificada. |
| `wf08-disponibilidade.html` | Implementado funcionalmente em `/reservas/disponibilidade`. | O estado reservado é avaliado para o período consultado; ocupado e limpeza exprimem a operação atual. |
| `wf09-historial.html` | Parcial através de `/historico` e `/historico/eventos`. | Rever consolidação visual de todos os eventos no mesmo percurso. |

## Pressupostos Registados

1. Na criação automática do plano, as instruções iniciais são compostas apenas pelas necessidades alimentares, medicação em curso e notas da reserva já registadas para o animal/estadia. Não são criadas tarefas, frequências ou horários sem suporte em dados introduzidos pelo utilizador.
2. Para o mapa, uma estadia em curso prevalece sobre os restantes estados; sem estadia, uma limpeza pendente prevalece sobre uma reserva no período, pois o alojamento não pode ser usado até limpeza concluída.

## Trabalho Pendente Identificado

1. Validar filtros e apresentação longitudinal da área clínica contra `wf05`.
2. Rever a consolidação visual dos eventos do historial contra `wf09`.
3. Acrescentar testes end-to-end de autorização que percorram a navegação por cada perfil e confirmem ausência de atalhos sem autorização.
