# Contract: Sequence Diagrams

## Escopo

Define o contrato dos diagramas de sequencia de negocio.

## Fluxos obrigatorios

- `seq-reserva`
- `seq-checkin`
- `seq-checkout`
- `seq-cuidados`
- `seq-faturacao`
- `seq-limpeza`
- `seq-veterinario`

## Formato obrigatorio

- Um ficheiro Mermaid (`.mmd`) por fluxo
- Um ficheiro `.txt` por fluxo em sintaxe compatível com WebSequenceDiagrams (www.websequencediagrams.com), importavel no Visual Paradigm

## Regras de modelacao

- Participantes: entidades de dominio + services/repositorios relevantes
- Sem HTTP, controllers, endpoints, payloads
- Mensagens ordenadas pelo fluxo do use case

## Mapeamento minimo UC -> Sequencia

- UC-04 -> seq-reserva
- UC-06 -> seq-checkin
- UC-07 -> seq-checkout
- UC-09 -> seq-cuidados
- UC-08 -> seq-faturacao
- UC-12 -> seq-limpeza
- UC-11 -> seq-veterinario
