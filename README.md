# LI4

# Ferramentas para bom uso de LLM's 

- [Speck Kit](https://github.com/github/spec-kit)

## 🛠️ Instalação de Dependências para Spec Kit

Para usar as funcionalidades automáticas do Spec Kit/Specify CLI, é necessário instalar a ferramenta:

```bash
# Instalar o Specify CLI (recomendado)
pip install uv
#Ou 
brew install uv


uv tool install specify-cli --from git+https://github.com/github/spec-kit.git
# Ou, se preferires, consulta a documentação oficial para versões mais recentes
```

**Nota:**
- A pasta `.github/` criada pelo Specify pode (e deve) ser versionada e comitada no repositório, pois contém prompts, templates e configurações importantes para automação e colaboração.
- Não adicionar `.github/` ao `.gitignore`.

---

# 🚀 Comandos Slash do Spec Kit (Specify CLI)

Depois de instalar e inicializar o Specify CLI, podes usar estes comandos diretamente no chat do teu AI agent (ex: Copilot Chat):

### Comandos principais:
- `/speckit.constitution` — Estabelece os princípios do projeto
- `/speckit.specify` — Cria a especificação base
- `/speckit.plan` — Gera o plano de implementação
- `/speckit.tasks` — Gera tarefas acionáveis
- `/speckit.implement` — Executa a implementação

### Comandos opcionais (para melhorar qualidade/confiança):
- `/speckit.clarify` — Fazer perguntas estruturadas para clarificar áreas ambíguas (antes do /speckit.plan)
- `/speckit.analyze` — Relatório de consistência e alinhamento entre artefatos (após /speckit.tasks, antes do /speckit.implement)
- `/speckit.checklist` — Gerar checklists de qualidade para validar requisitos (após /speckit.plan)

Usa estes comandos no chat do Copilot ou outro LLM para acelerar e garantir qualidade no ciclo de especificação e implementação!