# LLM Prompting Best Practices

## 🎯 Framework: CRANE Prompting

Para máxima eficiência na modelação de requisitos, use:  
**C**ontext | **R**ole | **A**ction | **N**arratives | **E**xecution

---

## 1️⃣ CONTEXT (Contexto)

### Sempre incluir primero no prompt:

```markdown
# CONTEXT
Project: Sistema de Gestão Hotel Animais
Domain: Pet Care Management
Scale: 32 accommodations, 7 staff, 1-2 vet checks/day
Tech Level: User-friendly for non-technical staff
Quality: Production-ready, ISO/IEC 25010 compliant
```

### Exemplo de contexto BAD ❌
```
"Create user stories for reservation system"
```

### Exemplo de contexto GOOD ✅
```
Project: Hotel Animals Management System (Portugal)
Context: Small hotel with 32 boxes (dogs/cats only)
Users: 5-7 staff with limited tech skills + director
Current Problem: Manual reservations in notebook → overbookings
Goal: Centralized system, zero overbookings, real-time availability
```

---

## 2️⃣ ROLE (Papel do LLM)

### Atribua um papel profissional específico:

```markdown
# ROLE
You are a Senior Requirements Engineer
- 15+ years in SaaS/Enterprise software
- Expert in IEEE 830 standard
- Familiar with hospitality domain
- Speaks Portuguese (EU)
```

### Papéis úteis para LI4:

| Papel | Melhor para | Prompt |
|-------|------------|--------|
| **Requirements Analyst** | Elicitação, validação | "As a requirements specialist..." |
| **Solutions Architect** | Design, trade-offs | "As a solutions architect..." |
| **QA Engineer** | Testes, casos de teste | "As a QA specialist..." |
| **Code Reviewer** | Review, refactoring | "As a senior code reviewer..." |
| **Tech Lead** | Decisions, planning | "As a tech lead..." |

---

## 3️⃣ ACTION (Ação Específica)

### Seja explícito sobre o QUÊ fazer:

### BAD ❌
```
"Help with requirements"
```

### GOOD ✅
```
ACTION: Identify ambiguous requirements
- Look for vague terms (e.g., "efficient", "quickly")
- Flag missing criteria
- Suggest specific metrics
- Output: Numbered list with severity
```

### Template Action para LI4:

```markdown
## ACTION: [ACTION_NAME]

**Input**: [What you're providing]
**Process**: [Steps to follow]
  1. Analyze for [criteria]
  2. Check against [standard]
  3. Validate with [checklist]

**Output Format**:
  - [Markdown structure expected]
  - [Table if comparison]
  - [Code blocks if code]

**Constraints**:
  - Maximum X lines
  - No hallucination of features
  - Portuguese context only
```

---

## 4️⃣ NARRATIVES (Contexto de Negócio)

### Inclua a história e problema central:

```markdown
# NARRATIVE

## Current State (Manual Process)
Ana (Receptionist):
1. Receives call from client
2. Opens notebook (written calendar)
3. Checks excel sheet (payments)
4. Checks papers (care notes)
5. Manual calculation of prices
→ Risk: Wrong box assigned, overbooking

## Desired State
Ana:
1. Receives call
2. Opens system
3. Sees available boxes instantly
4. Creates reservation (2 clicks)
5. System confirms & notifies caretakers

## Expected Outcome
- 45-min/day time saved
- ZERO overbookings
- 100% payment tracking
```

---

## 5️⃣ EXECUTION (Como Executar)

### Modo 1: Single Pass (Rápido)
```
Contexto + Role + Action + Narrative
→ Execute
→ Review output
→ If < 80% quality: refine
```

### Modo 2: Iterative (Melhor qualidade)
```
Pass 1: Generate (50% effort)
  → LLM creates draft

Pass 2: Validate (40% effort)
  → Team gives feedback
  → Prepare refinement prompt

Pass 3: Execute Refinement (10% effort)
  → LLM incorporates feedback
  → Final review
```

### Modo 3: Multi-Agent (Complex)
```
Agent 1 (Requirements): Generate RF
        ↓
Agent 2 (Validator):   Cross-check
        ↓
Agent 1 (Refine):      Incorporate feedback
        ↓
TEAM:                  Approve
```

---

## 🎯 Prompts Específicos para LI4

### Prompt A: Elicitar Requisitos de um Stakeholder

```markdown
# Eliciting Requirements from Caretaker Perspective

## CONTEXT
- Hotel dogs/cats, currently 32 boxes
- 5 caretakers, 3 shifts per day
- Currently: care notes on paper at box door
- Problem: notes fall, get illegible, lost between shifts

## ROLE
You are an experienced requirements analyst interviewing a caretaker
- You understand animal care workflows
- Portuguese fluent
- Focus on hidden needs

## ACTION
Simulate an interview with a caretaker about daily challenges:

1. Ask about current process (care recording)
2. Identify pain points (at least 5)
3. Ask desired system features
4. Extract 5 user stories
5. Note any missing info

## NARRATIVE
Maria is a caretaker. At 8 AM, she arrives:
- Walks the line of 6 dogs/cats in her section
- Needs to know: diet, meds, special needs, notes from prev shift
- Currently: paper notes already falling from boxes
- Problem: medicaton given to wrong animal once (mix-up)

## EXECUTION
Interview output format:
```yaml
pain_points:
  - name: "..."
    impact: "..."
    frequency: "..."
user_stories:
  - "Como [role], quero [action], para [benefit]"
missing_info:
  - "Need to clarify..."
```

---

### Prompt B: Validar Requisitos para Ambiguidade

```markdown
# Ambiguity & Clarity Review

## CONTEXT
Requirement Specification for Hotel.Animals.Book
Standard: IEEE 830/29148
Status: Draft, needs validation

## ROLE
You are a strict quality assurance analyst
- Detect ambiguous language
- Flag unmeasurable criteria
- Suggest improvements

## ACTION
Review this requirement systematically:
1. Identify vague terms (efficient, quickly, easy, etc)
2. Check if testable/measurable
3. Look for missing criteria
4. Rate clarity 1-10
5. Suggest improvement

## INPUT
[Paste 1-3 requirements to review]

## OUTPUT
For each requirement:
```
### RF-XX: [Name]
**Clarity**: 7/10
**Issues**:
  - "real-time" undefined (< 100ms? < 5s?)
  - "user-friendly" too vague
**Suggestion**:
  - Change to: "System responds in < 2 seconds 95% of requests"
  - Add metric: "Adoption by non-tech staff > 90% in 2h training"
**Revised**:
  [Improved requirement]
```
```

---

### Prompt C: Design Pattern Recommendation

```markdown
# Architecture Design Pattern Recommendation

## CONTEXT
Hotel Animal Management system
- 32 boxes → future 100+ boxes
- 7 staff (poor tech) → future 20+
- 24/7 availability needed
- Real-time occupancy control critical

## ROLE
You are a Solutions Architect with 15+ years experience
- Expert in SaaS, scalable systems
- Familiar with hospitality systems
- Focus on pragmatic solutions

## ACTION
For feature: "Real-time availability control with simultaneous updates"

1. List 3 architectural approaches
2. Compare on: Complexity, Scalability, Consistency, Cost
3. Identify risks for each
4. Recommend best + justify
5. Suggest PoC approach

## OUTPUT
```markdown
## Approach 1: [Name]
**How**: [Explain]
**Pros**: [List positives]
**Cons**: [List negatives]
**Scalability**: [Comment]

## Approach 2: ...

## Recommendation: [Name]
**Why**: [3-4 reasons]
**PoC Approach**: [How to validate]
```
```

---

## 📋 Checklist para Validar Prompt

Antes de rodar um prompt, confirme:

- [x] **Context claro?** - Projeto, domínio, escala, restrições
- [x] **Role atribuído?** - Quem é o LLM? Qual experiência?
- [x] **Action específica?** - O QUÊ fazer exatamente?
- [x] **Narrative incluída?** - Contexto de negócio + problema?
- [x] **Output format definido?** - Markdown? YAML? Tabela?
- [x] **Constraints claros?** - Limites? Standard? Português?
- [x] **Exemplos de BAD output?** - O que NÃO queremos?
- [x] **Critério de sucesso?** - Como saber se ficou bem?

---

## 🔄 Padrão de Execução Diária

### 09:00 - Leitura de Contexto
```
LLM Review: "Read requirements/01-context-analysis.md and summarize key points"
→ 5 min
```

### 10:00 - Geração de Artefato
```
CRANE Prompt: [Follow template above]
→ 10 min
```

### 11:00 - Validação
```
Review against checklist
↓
Se < 80% qualidade: Prepare feedback
Se > 80%: Sign-off
→ 15 min
```

### 11:30 - Refinement (se necessário)
```
Second pass with feedback
→ 5 min
```

---

## 🚫 Erros Comuns a Evitar

| Erro | Impacto | Solução |
|------|---------|---------|
| Prompt muito genérico | Vago output | Incluir contexto específico |
| Sem role | LLM escolhe persona aleatória | Atribuir papel claro |
| Action implícita | Misunderstandings | Explicitar output format |
| Falta de exemplos | Hallucinação | Incluir "o que NÃO fazer" |
| Sem constraints | Scope creep | Listar limites |
| Inglês misturado | Confusão | Decidir: PT ou EN |

---

## 📚 Recursos Adicionais

- [SPEC-KIT.md](../SPEC-KIT.md) - Setup e configuração
- [AWESOME-COPILOT-GUIDE.md](AWESOME-COPILOT-GUIDE.md) - Next doc
- IEEE 829 Template - [link externo]
- Prompt Engineering Guide - [course]

---

**Documento**: LLM Prompting Best Practices  
**Versão**: 1.0  
**Data**: 19 Abril 2026  
**Status**: ✅ Ready to Use

