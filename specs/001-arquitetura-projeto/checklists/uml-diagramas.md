# Checklist de Qualidade dos Diagramas UML

**Propósito:** Garantir que todos os diagramas produzidos seguem os standards UML, as regras da linguagem e as restrições definidas no plano e especificação.
**Criado:** 2026-04-21
**Fonte:** specs/001-arquitetura-projeto/plan.md e spec.md

## Conformidade com Standards UML

- [ ] CHK001 Todos os diagramas utilizam apenas notação, símbolos e agrupamentos definidos pela especificação UML? [Clarity, UML Standard]
- [ ] CHK002 Não existem cores, estilos ou símbolos inventados ou não standard? [Consistency, UML Standard]
- [ ] CHK003 Diagramas de classes, sequência e componentes seguem as regras de ligação, direção e agrupamento da UML? [Clarity, UML Standard]
- [ ] CHK004 Diagramas exportados mantêm a notação e legibilidade conforme visualizado nas ferramentas UML (ex: Visual Paradigm)? [Measurability, Export]

## Cobertura dos Fluxos e Entidades

- [ ] CHK005 Existem diagramas para todos os fluxos principais: reserva, check-in, check-out, cuidados, faturação, limpeza, intervenção do veterinário? [Coverage, Spec §FR-7]
- [ ] CHK006 Todos os diagramas obrigatórios (classes, sequência, componentes) estão presentes e completos? [Completeness, Spec §FR-1]

## Atualização e Versionamento

- [ ] CHK007 Sempre que há alteração na arquitetura, os diagramas são atualizados e versionados? [Completeness, Plan]
- [ ] CHK008 Todos os diagramas estão acessíveis em docs/architecture/ e referenciados no README? [Traceability, Plan]

## Revisão e Validação

- [ ] CHK009 Todos os diagramas foram revistos por pelo menos um membro da equipa para garantir conformidade com UML? [Acceptance Criteria, Plan]
- [ ] CHK010 Diagramas exportados (imagem/PDF) mantêm clareza e legibilidade? [Measurability, Export]

---

Esta checklist deve ser usada em cada entrega de diagramas e revista sempre que as regras ou standards forem atualizados.
