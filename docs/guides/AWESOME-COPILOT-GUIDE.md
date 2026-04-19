# Awesome GitHub Copilot Guide for LI4

> Using GitHub Copilot's advanced features for professional, LLM-assisted software engineering

---

## 🌟 What is Awesome Copilot?

Awesome Copilot refers to using GitHub Copilot's most powerful features:
- **Chat Context**: Multi-file awareness
- **#file**: Reference specific files
- **#selection**: Work with selected code
- **Slash commands**: `/explain`, `/review`, `/fix`, `/tests`
- **Agents**: GitHub Copilot with agentic capabilities
- **Deep repository understanding**: Semantic search across codebase

---

## 🚀 Awesome Copilot Features Explained

### Feature 1: #file Reference
**Purpose**: Tell Copilot exactly which file to analyze  
**Syntax**: `#file:path/to/file.md`  
**Benefit**: More accurate than description

#### Example:
```markdown
❌ WEAK:
"Review our requirements"

✅ STRONG:
#file:docs/requirements/03-functional-requirements.md

Review each RF for:
- Testability
- Completeness
- Alignment with IEEE 830
```

### Feature 2: #selection
**Purpose**: Reference highlighted code in editor  
**Syntax**: Select code → mention in chat  
**Benefit**: Immediate context

#### Example:
Select ReservationService.createReservation() method → Chat:
```markdown
/explain #selection

Why would this code fail under concurrent booking requests?
What concurrency pattern would you suggest?
```

### Feature 3: Slash Commands
**Commands Available**:
```
/explain  - Explain code/concept
/review   - Code review
/fix      - Suggest fixes
/tests    - Generate tests
/doc      - Generate documentation
```

#### Example:
```markdown
/explain #selection

Explain the transaction isolation strategy here and 
why it matters for hotel availability.
```

### Feature 4: Agents
**When Available**: When Copilot has agentic capabilities  
**Benefit**: Multi-step reasoning, better planning

#### Example:
```markdown
I need to implement the availability control feature (RF-05).

Please:
1. Analyze current components
2. Suggest architecture pattern
3. Create implementation plan
4. Identify potential pitfalls
5. Outline testing strategy
```

---

## 🎯 Example Prompts for LI4 Project

### Prompt 1: Requirement Clarity Review
```markdown
#file:docs/requirements/03-functional-requirements.md

Review RF-05 (Availability Control):
1. Is it testable? (If not, what metrics needed?)
2. Is implementation clear? (If not, what's ambiguous?)
3. Missing criteria? (List any edge cases)
4. Ready for development? (Yes/No + reasoning)

Output as simple checklist.
```

### Prompt 2: Design Pattern Feedback
```markdown
#file:docs/architecture/02-domain-model.md

Analyze the domain model:
1. Are relationships correct for hotel domain?
2. Any missing entities?
3. Would this support RF-05 (real-time availability)?
4. Scalability from 32 to 100+ boxes?

Suggest improvements if needed.
```

### Prompt 3: Code Quality Check
```markdown
/review #selection

This is ReservationService.checkAvailability()
Check for:
- SQL injection risks
- Concurrency issues
- Performance (should be < 100ms)
- SOLID violations

Suggest the top 2 improvements.
```

### Prompt 4: Test Generation
```markdown
/tests #file:src/models/Accommodation.java

Generate tests that verify:
- RF-05: Only clean boxes can be available
- Only boxes without active stays are available
- Status transitions are correct
- Concurrent updates handled

Use JUnit 5, include edge cases.
```

---

## ⚙️ Setup: Enable Awesome Features

### Step 1: GitHub Copilot Settings
```
VS Code → Settings → Extensions → GitHub Copilot

✓ Enable Chat
✓ Enable Slash Commands
✓ Enable Inline Completions
✓ Auto-suggest: ON
```

### Step 2: Create Copilot Chat Shortcuts
```json
// keyboard shortcuts
[
  {
    "key": "cmd+k cmd+r",
    "command": "github.copilot.chat.openSymbolFromEditor"
  },
  {
    "key": "cmd+k cmd+e",
    "command": "github.copilot.chat.explain"
  }
]
```


---

## 📊 Effectiveness Metrics

Track these to measure Awesome Copilot ROI:

| Metric | Target | How to Measure |
|--------|--------|-----------------|
| Time/requirement review | < 5 min | Chat history |
| Requirements clarity score | > 85% | Team survey |
| Code review time | < 10 min | Calendar blocks |
| Test coverage | > 80% | Coverage report |
| First-pass quality | > 80% | # of revisions |

---

## 🚫 Common Mistakes

| Mistake | Impact | Fix |
|---------|--------|-----|
| Vague prompt | Bad output, wasted time | Use #file + specific question |
| No context | Hallucination | Include requirements, constraints |
| Too many files referenced | Confusion | Max 3-4 files per prompt |
| Generic reviews | Useless advice | Be specific: "for RF-05" not "review code" |

---

## 🎓 Example: Complete Session

### Goal: Implement RF-05 (Availability Control)

#### 10:00 - Understand requirement
```markdown
#file:docs/requirements/03-functional-requirements.md

/explain

Please explain RF-05 in simple terms:
1. What must the system check?
2. When should it check?
3. What should happen if conflict found?
4. Performance requirement?
```

#### 10:10 - Design approach
```markdown
I need to design availability checking. 
Current DB: PostgreSQL
Current framework: Spring Boot
Requirement: Check must be < 100ms

/suggest

What design pattern for checking (Box availability) considering:
- Concurrent booking requests (many tutors trying same box)
- Must include: reservations + active stays + cleaning status
- 95% response time < 100ms

Pros/cons of each approach?
```

#### 10:20 - Code review draft  
```markdown
/review #selection [my implementation]

Does this correctly implement RF-05?
Concerns:
- N+1 query problem?
- Deadlock risk?
- Performance good for 100+ boxes?

Suggest top 3 improvements.
```

#### 10:30 - Generate tests
```markdown
/tests #selection

Generate comprehensive tests for this method.
Must cover:
- Happy path (box available)
- Occupied box (should fail)
- Cleaning status handling
- Concurrent access (race condition)
- Performance assertion (< 100ms)

Use JUnit 5, mock DB.
```

---

## 📚 Awesome Copilot Best Practices

### Rule 1: Always Include Context
```
❌ "Fix this function"
✅ "#file:src/services/ReservationService.java for RF-05 (real-time availability)"
```

### Rule 2: Be Specific About Output
```
❌ "Review this code"
✅ "Review for: (1) concurrency issues (2) N+1 queries (3) SOLID violations"
```

### Rule 3: Use #file for Reference, Not Explanation
```
❌ "Here's my requirements document [paste 1000 lines]"
✅ "#file:docs/requirements/03-functional-requirements.md"
```

### Rule 4: Chain Prompts for Deep Analysis
```
Prompt 1: Understand the problem
Prompt 2: Design approach  
Prompt 3: Code review
Prompt 4: Test generation
```

---

## 🔗 Integration with Spec Kit

**Spec Kit** (from SPEC-KIT.md) defines the structure  
**Awesome Copilot** executes the work in parallel

```
Spec + Awesome Copilot Loop:

Define in Spec (5 min)
    ↓
Generate with Copilot (10 min)
    ↓
Validate with Checklist (5 min)
    ↓
Refine if needed (5-10 min)
    ↓
Sign-off (2 min)
```

---

## ✅ Daily Checklist

- [ ] Start day with context: #file:docs/SPEC-KIT.md quick review
- [ ] Each task: Use #file for clarity
- [ ] Code queries: Include RF number
- [ ] Reviews: Use /review command
- [ ] Tests: Use /tests command
- [ ] Document: Track time saved
- [ ] End of day: Note prompts that worked well

---

**Documento**: Awesome GitHub Copilot Guide  
**Versão**: 1.0  
**Data**: 19 Abril 2026  
**Status**: ✅ Ready to Use

