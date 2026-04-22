# Contract: Class Diagram

## Escopo

Define o contrato minimo para o diagrama de classes de negocio.

## Itens obrigatorios

- Packages: `dominio.*`, `servico`, `repositorio`, `fabrica`, `estrategia`, `excecao`
- Services com interface + implementacao
- Repositories com interface + implementacao
- Metodos minimos de repository definidos no spec
- Enumeracoes obrigatorias
- Cardinalidades obrigatorias
- Excecoes de dominio nos contratos de service

## Rastreabilidade

- Cada metodo principal de service deve mapear para pelo menos um UC (UC-01..UC-13).
- Cada relacao critica deve mapear para pelo menos uma regra RD relevante.

## Validacao

- Visibilidade UML (`+`, `-`, `#`) presente em atributos e metodos
- Estereotipos UML presentes
- Sem elementos HTTP/controller
