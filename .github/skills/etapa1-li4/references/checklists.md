# Checklist de Consistência da Etapa 1

## Stakeholders e Contexto
- Stakeholders, atores e contexto operacional do hotel de animais estão identificados.
- Restrições relevantes (negocio, regulamentares, tecnicas) estão registadas.

## User Stories
- Cada story identifica claramente papel, objetivo e benefício.
- A prioridade está explícita e usa a convenção já adotada.
- A linguagem da story não mistura solução técnica com necessidade de negócio.

## Requisitos
- Cada requisito é atómico, verificável, identificado e tem origem rastreável, em linha com IEEE 830/29148.
- Os requisitos usam a terminologia já presente nas user stories.
- Regras de negócio e comportamentos operacionais não ficam misturados no mesmo requisito sem necessidade.
- Requisitos não funcionais expressam critérios mensuráveis sempre que possível.

## Casos de Uso
- Cada caso de uso está ligado a um ator claro e segue convenções UML.
- O fluxo principal e as alternativas refletem requisitos existentes.
- Pré-condições e pós-condições não contradizem `RF`, `RD` ou `RNF`.

## Modelo de Domínio
- O diagrama contém conceitos de negócio, não classes técnicas.
- As entidades principais têm relações justificadas por requisitos ou casos de uso.
- Multiplicidades, estados e enumerações aparecem apenas quando há suporte documental.
- Os nomes das classes e relações coincidem com a terminologia dos documentos da Etapa 1.

## Coerência Cruzada
- Disponibilidade de alojamento depende de ocupação no período e estado de limpeza.
- O pagamento da estadia base ocorre no check-in e os extras ou atos veterinários no check-out, salvo alteração futura dos requisitos.
- Qualquer novo elemento importante do modelo consegue ser explicado por pelo menos uma `US`, `RF`, `RD` ou `UC`.
- Conteúdo gerado por LLM está validado contra o enunciado e os artefactos já existentes.