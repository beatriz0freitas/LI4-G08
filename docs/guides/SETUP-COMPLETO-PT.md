# 🇵🇹 SETUP COMPLETO - Como Tudo Isto Funciona

> Guia simples e prático em português para entender e usar Spec Kit + LLM de verdade

---

## 🎯 A Ideia Principal (Simples!)

### O Problema:
Tens 5 engenheiros a trabalhar num projeto. Cada um faz coisas diferentes:
- Um escreve requisitos (muito texto, muito cansado)
- Outro desenha arquitetura (muitas decisões)
- Outro escreve código (muitos bugs a revistar)
- Outro testa (muitos casos de teste)

**Resultado**: Tempo excessivo, qualidade inconsistente, comunicação confusa.

### A Solução (Spec Kit + LLM):
- **Spec Kit** = Define EXATAMENTE o que queres (estrutura clara)
- **LLM** = Faz o trabalho repetitivo muito rápido
- **Validação** = Tu decides se está bom ou mau (decisões continuam humanas)

**Resultado**: 5x mais rápido, qualidade consistente, melhor comunicação.

---

## 🧠 Como Funciona (Simples)

### O Ciclo Diário:

```
MANHÃ:
  1. Tu defines: "Preciso de refinamento de 3 requisitos"
  2. Tu descreves: "São sobre reservas, pagamentos e limpeza"
  3. LLM (Copilot) faz todo o trabalho pesado (15 min)
  4. Tu revês: "Ficou bom? Precisa outro passo?"
  
SE FICOU BOM:
  ✅ Pronto para usar nо projeto
  
SE NÃO FICOU BOM:
  🔄 Tu di-lo: "Está muito vago, falta critério de teste"
  🔄 LLM refaz (5 min mais)
  ✅ Pronto!

RESULTADO: 20 min por artefato de alta qualidade
              (vs 2 horas se fizesses à mão)
```

---

## 📋 O QUE É SPEC KIT (Simples!)

### Não é:
- ❌ Uma ferramenta
- ❌ Um código
- ❌ Um framework

### É:
- ✅ Uma **metodologia** = "Como fazer perguntas ao LLM de forma que a resposta seja útil"
- ✅ Uma **estrutura** = Template que diz "meter contexto aqui, resultado assim"
- ✅ Um **checklist** = Validação "é que a resposta tem qualidade?"

### Analogy:
```
SEM SPEC KIT:
  Tu: "Faz um requisito"
  LLM: [Gera algo aleatório, vago, sem critério]
  Tu: "Isto é inútil"

COM SPEC KIT:
  Tu: "Contexto: hotel de animais
       Role: Requirements analyst com experiência
       Action: Refine this requirement, identify ambiguity
       Output: Markdown format, testable criteria"
  LLM: [Gera exatamente o que pediste, bem estruturado]
  Tu: "Perfeito!"
```

**Diferença**: A qualidade do que pedes ao LLM 🚀


---

## 🗂️ ONDE METER OS AGENTS E SKILLS

### 1️⃣ O .instructions.md (JÁ EXISTE)

**Caminho**: `/Users/alicesoares/Desktop/LEI/Projetos/LI4-G08/.instructions.md`

**O que é**: Ficheiro que Copilot lê automaticamente  
**Como funciona**: Quando abres Copilot Chat, ele lê este ficheiro e sabe o contexto do projeto


### 2️⃣ Prompts de Uso (Skills Prácticas)

**Pasta**: `docs/llm-patterns/` (criar se não existir)

**O que guardar aqui**: Prompts que funcionam bem para teu projeto

```
Criar ficheiros assim:

docs/llm-patterns/
├── requirements-elicitation.md      ← Prompts para requisitos
├── architecture-review.md            ← Prompts para arquitetura
├── code-generation.md                ← Prompts para código
└── testing-patterns.md               ← Prompts para testes
```

**Exemplo de ficheiro:**
```markdown
# requirements-elicitation.md

## Prompt A: Refinar requisito ambíguo

```
#file:.instructions.md #file:docs/requirements/03-functional-requirements.md

Requisito a refinar:
[COLA AQUI O REQUISITO]

1. Identify: Vague terms?
2. Suggest: Specific metrics
3. Output: Refined RF-XX with acceptance criteria
```

## Prompt B: Gerar use cases

[... outro prompt ...]
```
```

### 3️⃣ Agents (Copilot Built-in)

**NÃO TÊM "PASTA"** - Estão integrados no Copilot Chat

**Como usar:**

```markdown
AGENT 1: Explore Agent
─────────────────────
Quando: Fazer investigação rápida
Como: Escrever no chat:

"@explore"

Find patterns for: Real-time availability control
Looking for: Examples, trade-offs, implementation patterns
Thoroughness: medium

─────────────────────────────────────

AGENT 2: Custom Code Reviewer (Built-in /review)
─────────────────────
Quando: Revistar código
Como:

/review #selection

E depois descrever o que queres revistar
```

### 4️⃣ Skills (Advanced - Fase 3+)

**Quando chegar à Fase 3 (Implementação)**:

Se decidires usar skills do GitHub Copilot:
- **Onde**: Settings → Extensions → GitHub Copilot → Skills
- **O que fazer**: Installer skills conforme precisares

```
Exemplo:

Fase 3 (Semana 7):
  └─ Se usar TypeScript:
     └─ Instalar skill "typescript-upgrade"
     └─ Instalar skill "typescript-testing"

Mas para agora (Fase 1):
  └─ Não precisa! Built-in é suficiente.
```

---

## 📊 Checklist: Tudo Configurado?

- [x] ✅ `.instructions.md` criado na raiz → Copilot consegue descobrir
- [x] ✅ `docs/` com subcategorias → Organização clara
- [x] ✅ `docs/guides/` com templates → Tu tens o CRANE framework
- [x] ✅ Requisitos iniciais prontos → Podem ser refinados
- [ ] 🔄 `docs/llm-patterns/` com teus prompts → Criar conforme usas


---

## 🎬 COMECE AGORA (5 MINUTOS!)

### Quick Start Real:

```
1. ABRIR TERMINAL:
   cd /Users/alicesoares/Desktop/LEI/Projetos/LI4-G08

2. ABRIR VS CODE:
   code .

3. ABRIR COPILOT CHAT:
   Cmd+Shift+I ou Cmd+K depois Cmd+I

4. COLAR ISTO NO CHAT:
   ─────────────────────────────────
   #file:.instructions.md
   
   Context: I'm working on the hotel animal system requirements.
   Task: From docs/requirements/02-user-stories.md, 
         identify the TOP 3 user stories most critical to implement first?
   
   Explain why + business impact.
   ─────────────────────────────────

5. CARREGAR ENTER e VER A MAGIA ACONTECER ✨

6. COPIAR A RESPOSTA e GUARDAR em:
   docs/llm-patterns/quick-analysis-001.md
   
7. CELEBRAR! 🎉
```

---

## 🤔 FAQ Rápido

### P: "Mas onde fica o agent?"
**R:** Não fica em lado nenhum! Agents vivem no GitHub Copilot. Tu usas assim:
```
Chat → @explore (para Explore Agent)
Chat → /review (para Code Review)
```

### P: "E as skills? Onde as guardo?"
**R:** Não guardas. Estão no VS Code Settings → Extensions → GitHub Copilot → Skills. Tu apenas as ativas quando precisas.

### P: "O .instructions.md é suficiente para sempre?"
**R:** Para Fase 1 sim. Quando mudas para Fase 2/3/4, podes criar MORE ficheiros `.instructions` específicos ou adaptar o existente.

### P: "Posso usar isto com ChatGPT em vez do Copilot?"
**R:** Sim! Basta colar o mesmo prompt no ChatGPT. Resultado é similar (talvez mais lento, sem #file references).

### P: "Quanto tempo economizo realmente?"
**R:** Estimativa:
```
Sem LLM:   1 requisito    = 2-3 horas
Com LLM:   1 requisito    = 20-30 minutos
           (geração + validação)

ECONOMIA: ~85% de tempo por artefato
          = 10+ horas/semana economizadas para equipa
```

---

## 📚 Recursos Principais (Consulta quando precisar)

| Ficheiro | Quando ler | Porque |
|----------|-----------|---------|
| `docs/QUICKSTART.md` | Hoje | Começar já |
| `docs/guides/LLM-PROMPTING-BEST-PRACTICES.md` | Amanhã | Framework CRANE |
| `docs/guides/AWESOME-COPILOT-GUIDE.md` | Esta semana | Features do Copilot |
| `.instructions.md` | Sempre (Copilot lê automaticamente) | Contexto do projeto |



---

## ⚡ TL;DR (Muito Curto!)

```
SPEC KIT = Forma de fazer perguntas ao LLM 
           que ele compreenda o projeto

PASSO 1:  Lê .instructions.md (automático)
PASSO 2:  Abre Copilot Chat
PASSO 3:  Cola prompt com #file references
PASSO 4:  Copilot gera artefato
PASSO 5:  Validas e guardas

REPETIR = Cada dia, cada artefato

RESULTADO = 5x mais rápido, qualidade igual
```

---

**Documento**: SETUP COMPLETO em Português  
**Versão**: 1.0  
**Data**: 19 Abril 2026  
**Status**: 🚀 PRONTO PARA USAR AGORA!

Próxima pergunta? Posso criar exemplo concreto de um prompt para requisito específico do vosso projeto?

