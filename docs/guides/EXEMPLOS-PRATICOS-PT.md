# 🎯 EXEMPLOS PRÁTICOS - Prompts Reais para Usar AGORA

> Copia e cola estes prompts diretos no Copilot Chat

---

## 📋 Como Usar Este Ficheiro

```
1. Abrir Copilot Chat em VS Code
2. Escolher exemplo que queres tentar
3. COPIA O TEXTO (exatamente)
4. COLA no Copilot
5. CARREGAR ENTER
6. Ver o resultado
```

---

## 🔴 EXEMPLO 1: Refinar Requisito Ambíguo (FÁCIL)

**Quando usar**: Quando um requisito tem palavras vagas tipo "rápido", "eficiente", "fácil"

**Tempo esperado**: 10-15 minutos

**Copilot Chat - COPIA E COLA ISTO:**

```
#file:.instructions.md #file:docs/requirements/03-functional-requirements.md

Este requisito está vago:
"O sistema deve controlar a disponibilidade das boxes em tempo real"

Por favor:
1. Identifica: Qual é a palavra ambígua? ("tempo real" = quanto? 100ms? 5s?)
2. Sugere: Criério mensurável (ex: "< 2 segundos em 95% dos casos")
3. Output: Requisito refinado RF-05 com acceptance criteria específica
4. Formato: Markdown, com testes que validam

```

**O que esperas receber**:
- ✅ Identificação de ambiguidade
- ✅ Critério mensurável proposto
- ✅ Requisito refinado com teste
- ✅ Pronto para guardar em `docs/requirements/`

---

## 🟠 EXEMPLO 2: Gerar Use Cases (MÉDIO)

**Quando usar**: Quando precisas transformar um requisito em casos de uso

**Tempo esperado**: 15-20 minutos

**Copilot Chat - COPIA E COLA ISTO:**

```
#file:.instructions.md

Task: Gerar use case structure para RF-06 (criar/cancelar reservas)

Requirements:
- Hotel animals: cães e gatos
- Rececionista faz reservas
- Não pode ter overbooking (2 tutores mesma box = erro!)
- Se cancelar, box volta a ficar disponível

Generate:
1. Use Case Name: "Criar Reserva"
2. Main Flow: Passo a passo (como rececionista faz)
3. Alternative Flows: E se box ocupada? E se tutor sem animal registado?
4. Postconditions: O que deve estar garantido depois

Format: Markdown com tabela

```

**O que esperas receber**:
- ✅ Use case bem estruturado
- ✅ Happy path + edge cases
- ✅ Precondições e postcondições
- ✅ Pronto para guardar

---

## 🟡 EXEMPLO 3: Code Review em Inglês (DIFÍCIL)

**Quando usar**: Quando algum developer escreve código e queres LLM revistar

**Tempo esperado**: 10 minutos

**Copilot Chat - COPIA E COLA ISTO:**

```
/review

This is a ReservationService method that checks availability.

Performance requirement: < 100ms for RF-05
Check for:
1. N+1 query problem?
2. Deadlock risk with concurrent updates?
3. Does it handle cleaning status correctly?
4. Performance assertion for < 100ms?

Suggest improvements (top 3).

[DEPOIS: COLA O CÓDIGO QUE QUERES REVISTAR]

```

**Nota importante**: Selecciona o código em VS Code primeiro, depois Copilot tira automaticamente

---

## 🟢 EXEMPLO 4: Gerar Testes (FÁCIL)

**Quando usar**: Quando já tens código e queres testes automáticos

**Tempo esperado**: 10 minutos

**Copilot Chat - COPIA E COLA ISTO:**

```
/tests

Generate unit tests for: ReservationService.checkAvailability()

Requirements RF-05:
- Happy path: Box é livre + limpa → return TRUE
- Error 1: Box ocupada → return FALSE
- Error 2: Box não limpa → return FALSE
- Concurrency: 2 simultaneous checkAvailability calls → correct result
- Performance: Execution < 100ms assertion

Framework: JUnit 5
Include: Happy path + 4 error scenarios + performance test

```

**O que esperas receber**:
- ✅ Testes em JUnit 5
- ✅ Todos os cenários cobertos
- ✅ Assertion de performance incluída
- ✅ Pronto para copiar para teste

---

## 💎 EXEMPLO 5: Usar Explore Agent (AVANÇADO)

**Quando usar**: Quando precisas de investigação profunda

**Tempo esperado**: 15-20 minutos

**Copilot Chat - COPIA E COLA ISTO:**

```
@explore

Task: Find architectural patterns for real-time availability control

Context: Hotel with 32 accommodations, concurrent bookings expected
Constraint: Response time < 100ms, zero overbooking

Find:
1. Database patterns: Pessimistic/optimistic locking?
2. Caching strategies: Should we cache availability?
3. Concurrency handling: Race conditions?
4. Trade-offs: Consistency vs Performance

Thoroughness: thorough (complete analysis)

```

**O que esperas receber**:
- ✅ 3-5 padrões explicados
- ✅ Pros/cons para cada
- ✅ Recomendação do agent
- ✅ Links para recursos extras

---

## 🚀 EXEMPLO 6: Refinar Multiple Requisitos (SUPER AVANÇADO!)

**Quando usar**: Quando tens 3-5 requisitos para refinar de uma vez

**Tempo esperado**: 25-30 minutos

**Copilot Chat - COPIA E COLA ISTO:**

```
#file:.instructions.md #file:docs/requirements/03-functional-requirements.md

Batch refinement of 3 requirements:

RF-01: "O sistema deve disponibilizar um dashboard com indicadores de ocupação e faturação"
  → Issue: What indicators exactly? Clarity 4/10

RF-05: "O sistema deve controlar a disponibilidade das boxes em tempo real"
  → Issue: "Tempo real" não é mensurável. Clarity 3/10

RF-09: "O sistema deve permitir o registo do pagamento"
  → Issue: "Registo" como? Manual? Automático? Clarity 5/10

For each:
1. Identify ambiguity
2. Suggest measurable criteria
3. All acceptance criteria
4. Output: Refined version

Format: Markdown table com antes/depois

```

**O que esperas receber**:
- ✅ Análise de cada RF
- ✅ Suggestões de melhoria
- ✅ Versão refinada de cada
- ✅ Pronto para guardar

---

## 📊 MATRIX: Qual Exemplo Usar Quando?

| Tarefa | Exemplo | Tempo | Frequência |
|--------|---------|-------|-----------|
| Ambiguidade em requisito | #1 | 15 min | Diária |
| Transformar em use case | #2 | 20 min | 2-3x/semana |
| Revistar código | #3 | 10 min | 3-4x/semana |
| Gerar testes | #4 | 10 min | 2x/semana |
| Investigar padrão | #5 | 20 min | 1x/semana |
| Batch processing | #6 | 30 min | 1x/semana |

---

## 🎯 WORKFLOW REAL: Segunda-Feira

```
09:00 - Planning
  EQUIPA DECIDE: "Esta semana refinamos RF-01 a RF-05"

10:00 - Refinarmos RF-01 (EXEMPLO 1)
  ✅ Resultado: RF-01 agora com critério mensurável
  
11:00 - RF-05 (EXEMPLO 1)
  ✅ Resultado: RF-05 com timing exato (< 100ms)

11:30 - Batch RF-02, RF-03, RF-04 (EXEMPLO 6)
  ✅ Resultado: 3 requisitos refinados de uma vez

12:00 - Guardar e COMMIT
  └─ Copy outputs para docs/requirements/
  └─ git commit "refine: RF-01 to RF-05"

RESULTADO: 
  5 requisitos de alta qualidade em 3 horas (vs 8-10h manualmente)
  Qualidade consistente
  Equipa aprendeu Spec Kit
```

---

## ⚡ SHORTCUTS ÚTEIS

### Para Refinar Rápido (1 min):
```
#file:.instructions.md

Is this ambiguous? "[COLA TEXTO]"

1-2 sentence fix.
```

### Para Validar Rápido (2 min):
```
Does this meet IEEE 830? "[COLA REQUISITO]"

Yes/No + 1-2 reasons
```

### Para Listar Casos Teste (5 min):
```
#file:docs/requirements/03-functional-requirements.md

RF-05: List 10 test cases for availability control
(happy path, edge cases, concurrency, performance)

Format: Numbered list
```

---

## 🛑 Erros Comuns (EVITA!)

### ❌ ERRADO:
```
"Faz-me um requisito para reservas"
```
→ Muito vago! Resultado: Lixo genérico

### ✅ CORRETO:
```
#file:.instructions.md

Requirements: Refine "creation and cancellation of reservations"
Specifics: Two roles (receptionist, director), overbooking prevention
Output: RF-06 with acceptance criteria
```

---

## 📝 Como Guardar Results

Depois de cada prompt executado:

```
1. COPIAR output do Copilot (Cmd+A, Cmd+C)

2. CRIAR ficheiro:
   docs/llm-patterns/[name]-001.md
   
   Exemplo:
   docs/llm-patterns/rf-refinement-batch-001.md

3. GUARDAR resultado:
   # RF Refinement - Batch 1
   
   Generated: 19 Abril 2026
   Prompt: [Link para este exemplário ou cola breve]
   Result: [COLA AQUI O OUTPUT]
   
4. COMMIT:
   git add docs/llm-patterns/
   git commit "docs: add llm-pattern rf-refinement-batch-001"
```

---

## 🎓 PRÓXIMO PASSO

1. ✅ Copia exemplo #1 (o mais simples)
2. ✅ Cola no Copilot Chat
3. ✅ Vê o resultado
4. ✅ Se gostou: Vem cá mostrar-me!
5. ✅ Se não gostou: Ajustamos o prompt juntos

**Não há risco!** Podes fazer quantas vezes quiseres até ficar bom.

---

**Documento**: Exemplos Práticos  
**Versão**: 1.0  
**Data**: 19 Abril 2026  
**Status**: 🚀 Copia e Cola JÁ!

Pronto para experimentar? Qual exemplo queres tentar primeiro?

