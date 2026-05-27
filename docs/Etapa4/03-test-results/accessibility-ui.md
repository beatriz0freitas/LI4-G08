# Resultados de Validação UI / Acessibilidade

Data: 2026-05-27

## Âmbito

Validação rápida das alterações de interface aplicadas em:

- `PatasBigodesApp/src/main/resources/templates/alojamento/listar.html`
- `PatasBigodesApp/src/main/resources/templates/cuidados/lista-planos.html`
- `PatasBigodesApp/src/main/resources/templates/limpeza/listar.html`
- `PatasBigodesApp/src/main/resources/templates/estadias/lista.html`
- `PatasBigodesApp/src/main/resources/templates/fragments/head.html`
- `PatasBigodesApp/src/main/resources/templates/fragments/navbar.html`

## Checklist executado

- [x] Contraste visível para estados e botões principais
- [x] Campo de pesquisa com `aria-label`
- [x] Filtros com labels explícitos
- [x] Feedback visual em ações demoradas (`estadias/lista.html`)
- [x] Estados vazios com mensagem clara e calma
- [x] Navegação por cartões/tabelas mantém hierarquia legível
- [x] Botões de ação principal distinguem-se dos secundários

## Observações

- A linguagem visual ficou consistente entre páginas críticas.
- As cores de estado estão unificadas na base de tokens.
- O filtro de alojamentos passou a ter debounce e estado vazio dedicado.
- O formulário de estadias apresenta feedback durante submissão.

## Pendências recomendadas

- Teste manual com teclado em ecrã pequeno.
- Revisão visual dos restantes templates do domínio de reservas, animais e relatórios.
- Eventual extração do CSS para pipeline Sass real no futuro.
