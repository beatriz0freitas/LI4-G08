# Skills & Agents Roadmap para LI4

> Mapping de GitHub Copilot Skills e Agents pelos LLM a usar em cada fase

---

## 📊 Matriz: Fases × Skills/Agents

```
FASE 1: Conceção & Requisitos (Semanas 1-3)
├─ Skill: agent-customization (YAML/prompts)
├─ Skill: get-search-view-results (buscar contexto)
├─ Agent: Explore (codebase analysis)
└─ Built-in: #file, #selection, /explain

FASE 2: Arquitetura & Design (Semanas 4-6)
├─ Skill: agent-customization (architecture patterns)
├─ Agent: Custom (architectural review)
└─ Built-in: /review, /suggest

FASE 3: Implementação (Semanas 7-9)
├─ Skill: typescript-upgrade (se aplicável)
├─ Agent: Explore (code patterns)
└─ Built-in: /explain, /fix, /tests

FASE 4: Testing & Validation (Semanas 10-13)
├─ Skills: appmod_* (quality metrics)
├─ Agent: Explore (test patterns)
└─ Built-in: /tests, /review, /doc
```

---

## 🎯 Fase 1: Conceção e Engenharia de Requisitos

### Skills a Usar

#### 1. **agent-customization**
**Propósito**: Customizar prompts e instructions para o projeto  
**Quando**: Start of project (this week)  
**O que fazer**:
```markdown
Create custom `.instructions.md` in root with:
- Project context (hotel animals)
- Stakeholders (5 types)
- Quality standards (IEEE 830)
- Preferred output formats
- Language (Portuguese)
```

**Output**: `.instructions.md` + `.prompt.md`

#### 2. **get-search-view-results**
**Propósito**: Buscar requisitos e context existentes  
**Quando**: When gathering info  
**O que fazer**:
```markdown
Search for patterns like:
- Existing hotel management systems
- Animal care workflows
- Payment processing systems
- Check-in/checkout flows
```

### Agents a Usar

#### 1. **Explore Agent** (Quick, Thorough)
**Propósito**: Rapid codebase exploration and Q&A  
**Quando**: Entender o projeto existente  
**Exemplo**:
```markdown
Task: Explore existing requirements/code in hotel context
Thoroughness: thorough (complete analysis)

Search for:
1. Any existing reservation systems
2. Payment handling examples
3. User management patterns
4. Validation examples
```

### Built-in Copilot Features

```markdown
FERRAMENTA                 QUANDO                  EXEMPLO
#file                      Reference docs          #file:docs/README.md
#selection                 Select requirement     Select text → analyze
/explain                   Understand concept      /explain IEEE 830
/suggest                   Get ideas              /suggest stakeholders
Chat context               Keep history           14-turn conversation
```

### Saídas Esperadas (Fase 1)

- ✅ Contexto Analysis (01-context-analysis.md) - DONE
- ✅ User Stories (02-user-stories.md) - DONE  
- ✅ Functional Requirements (03-functional-requirements.md) - DONE
- 🔄 Non-Functional Requirements (04-...)
- 🔄 Domain Requirements (05-...)
- 🔄 Use Cases (UC-01 through UC-13)

---

## 🎯 Fase 2: Arquitetura e Design

### Skills a Usar

#### 1. **agent-customization** (Continued)
**Propósito**: Architecture-specific patterns  
**Setup**:
```yaml
# .instructions.md (updated)
architecture_guidelines:
  - "Follow DDD principles"
  - "Layer: API → Service → Repository → DB"
  - "Consider concurrency for RF-05"
  - "Document rationale for each decision"
```

### Agents a Usar

#### 1. **Explore Agent** (Architecture Mode)
**Propósito**: Analyze similar hotel system architectures  
**Task**:
```markdown
Find patterns for:
1. Real-time availability (RF-05)
2. Concurrent booking handling
3. Check-in/checkout workflows
4. Multi-role access control

Thoroughness: medium (focus on relevant)
```

#### 2. Custom Architecture Review Agent (Needed)
**Purpose**: Review architecture decisions  
**Would help with**: 
- Domain model validation
- Component interaction review
- Database schema review
- Integration points

### Built-in Copilot Features

```markdown
/review          → Code/architecture review
/suggest         → Design pattern suggestions
/explain         → Explain architectural decision
#file            → Reference domain model
Chat context     → Multi-file awareness
```

### Saídas Esperadas (Fase 2)

- 🔄 Domain Model (01-domain-model.md)
- 🔄 System Architecture (01-system-architecture.md)
- 🔄 Component Design (02-component-design.md)
- 🔄 Data Model (04-data-model.md)
- 🔄 API Specification (05-api-specification.md)
- 🔄 Architecture Decision Log (DECISION-LOG.md)

---

## 🎯 Fase 3: Implementação e Desenvolvimento

### Skills Potenciais

#### 1. **appmod-run-typescript-task** (Se usar TypeScript)
**Propósito**: Package upgrade e modernization  
**Quando**: If TypeScript is chosen  
**Type**: Not applicable yet (no packages to upgrade)

#### 2. **activate_typescript_build_and_test_tools** (If needed)
**Quando**: Setup build pipeline  
**Ferramentas**: tsc, npm, jest

### Agents a Usar

#### 1. **Explore Agent** (Code Patterns)
**Task**:
```markdown
Find best practices for:
1. Error handling in reservation service
2. Database transaction patterns
3. Concurrent update handling
4. Input validation patterns
5. API response formatting

Search in: existing hotel systems, Spring/Node examples
```

### Built-in Copilot Features

```markdown
/explain         → Explain design pattern
/fix             → Fix code issues
/tests           → Generate test cases
/review          → Code quality review
#selection       → Highlight code to analyze
/doc             → Generate documentation
```

### Saídas Esperadas (Fase 3)

- 🔄 Module A: Reservations (ReservationService, etc)
- 🔄 Module B: Check-in/Check-out  
- 🔄 Module C: Care Management
- 🔄 Module D: Clinical History
- 🔄 Module E: Billing & Payments
- 🔄 API Endpoints (all 20+ endpoints)
- 🔄 Database migrations
- 🔄 Test suites (unit + integration)

---

## 🎯 Fase 4: Verificação, Validação e Qualidade

### Skills a Usar

#### 1. **mcp_github_copilo_typescript_validate_webapp**
**Propósito**: Validate running webapp  
**Quando**: Phase 4, weeks 12-13  
**O que faz**:
```markdown
- Navigate to URL
- Collect console errors
- Take page snapshot
- Record network requests
- Analyze diagnostics
```

#### 2. **Quality Metrics Tools** (If using)
**Purpose**: Measure ISO/IEC 25010  
**Metrics**:
- Code coverage > 80%
- Performance < 2 sec
- Reliability: zero crashes
- Security: no vulnerabilities
- Usability: staff adoption > 90%

### Agents a Usar

#### 1. **Explore Agent** (Test Patterns)
**Task**:
```markdown
Find test patterns for:
1. Concurrent booking scenarios
2. Payment failure handling
3. Invalid data scenarios
4. Performance testing (load)
5. Security testing (SQL injection)

Thoroughness: thorough
```

### Built-in Copilot Features

```markdown
/tests           → Generate test cases
/review          → Review test coverage
/doc             → Generate test documentation
/explain         → Understand test logic
#file            → Reference requirements
```

### Saídas Esperadas (Fase 4)

- 🔄 Unit Tests (> 80% coverage)
- 🔄 Integration Tests
- 🔄 System Tests  
- 🔄 Acceptance Tests (UAT)
- 🔄 Performance Tests
- 🔄 Security Tests
- 🔄 Quality Report (ISO/IEC 25010)
- 🔄 Test Coverage Report
- 🔄 Bug Log & Resolution

---

## 🚀 Immediate Next Steps (This Week)

### Step 1: Create .instructions.md
```bash
# In root of project
cat > .instructions.md << 'EOF'
# LI4 Project Instructions

## Project
- **Name**: Sistema de Gestão Hotel Animais
- **Team**: 5 engineers, Portuguese
- **Standard**: IEEE 830/29148, ISO/IEC 25010

## Stakeholders
- Director (proprietary + veterinary)
- Receptionist (reservations)
- 5 Caretakers (daily care)
- Cleaner (maintenance)
- Veterinarian (clinical)

## Quality Standards
- Production-ready code
- > 80% test coverage
- < 2 second response time
- No technical debt from start

## Output Format Preference
- Markdown for docs
- YAML for configs
- Portuguese for business logic comments
- English for code comments

## Guardrails
- **Do**: Follow DDD, SOLID, design patterns
- **Don't**: Hallucinate features
- **Must**: Cite requirements (RF-XX, US-0Y)
- **Must**: Include error scenarios
EOF
```

### Step 2: Setup Spec Definitions
```bash
# Create spec files
touch docs/architecture/SPEC-ARCHITECTURE.md
touch docs/requirements/SPEC-REQUIREMENTS.md
touch docs/implementation/SPEC-CODING.md
```

### Step 3: First Awesome Copilot Session
```markdown
In VS Code Chat:

#file:docs/README.md #file:docs/SPEC-KIT.md

Quick orientation:
1. What are the 4 phases of this project?
2. Who are the 5 main stakeholders?
3. What's the top constraint? (Why matters?)
4. What's the primary risk to mitigate?
5. What should we complete by end of week?
```

### Step 4: First Agent Usage (Explore)
```markdown
I need to build a hotel reservation system for LI4.

Task: Find best practices
- Real-time availability control patterns
- Concurrent booking handling
- Multi-role access control
- Pet care tracking systems

Thoroughness: medium
Return: Top 5 patterns with pros/cons
```

---

## 📊 Skills/Agents Summary Table

| Fase | Skill | Agent | Built-in | Frequência |
|------|-------|-------|----------|-----------|
| 1 | agent-customization | Explore | #file, /explain | Daily |
| 1 | get-search-view-results | Custom | /suggest | Weekly |
| 2 | agent-customization | Explore | /review, /suggest | Daily |
| 2 | - | Custom* | /suggest | 3x/week |
| 3 | typescript-upgrade* | Explore | /fix, /tests | Weekly |
| 3 | - | - | /review, /doc | Daily |
| 4 | validate-webapp* | Explore | /tests, /review | 2x/week |
| 4 | - | - | /doc, /explain | As needed |

*Only if using specific tech stack

---

## 🎓 Training Plan

### Week 1:
- [ ] Install GitHub Copilot
- [ ] Read AWESOME-COPILOT-GUIDE.md
- [ ] Read SPEC-KIT.md
- [ ] Try 5 prompts from examples
- [ ] Create .instructions.md

### Week 2:
- [ ] Use Explore agent for research
- [ ] Run 10 requirement-elicitation prompts
- [ ] Get comfortable with multi-file context
- [ ] Document working prompts

### Week 3+:
- [ ] Deploy skills gradually
- [ ] Track time saved
- [ ] Adjust prompts based on feedback
- [ ] Share successful patterns with team

---

## 📝 Notes

**Not Using (Out of Scope for Hotel System)**:
- Java upgrade tools (not applicable yet)
- Docker containerization tools (Phase 4+)
- Azure deployment tools (Phase 4+)
- Security scanning tools (Phase 4)

**Possible Future (Scope Creep)**:
- Mobile app (portal do tutor)
- Video monitoring
- ML-based recommendation
- Advanced analytics

---

**Documento**: Skills & Agents Roadmap  
**Versão**: 1.0  
**Data**: 19 April 2026  
**Status**: ✅ Ready for Implementation Workshop  
**Next**: Share with team for input/feedback

