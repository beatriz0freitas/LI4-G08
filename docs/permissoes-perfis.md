# Permissões por Perfil

Este documento especifica, em português europeu, as permissões associadas a cada perfil de utilizador na aplicação Patas & Bigodes. Inclui as páginas/endpoints principais que cada perfil pode aceder e ações típicas permitidas.

> Notas:
- As rotas são apresentadas na forma relativa à aplicação (ex.: `/clinica/animais/{id}`).
- Quando pertinente, referimos se a ação exige permissões adicionais (ex.: papel de médico veterinário).

## Perfis

### Administrador (Admin)
- Acesso total à aplicação e à área administrativa.
- Páginas e endpoints principais:
  - `/` (Início)
  - `/admin/**` (painel administrativo, gestão de utilizadores, configurações)
  - `/estadias/**` (listar, criar, editar estadias)
  - `/plano-cuidados` — ver lista e acessar planos (turno / detalhe)
  - `/plano-cuidados?estadiaId={id}` — abrir plano de cuidados de uma estadia
  - `/plano-cuidados/{id}/tarefa` (POST) — adicionar tarefas
  - `/plano-cuidados/tarefa/{id}/concluir` (POST) — marcar tarefa como concluída
  - `/plano-cuidados/{id}/prioridade` (POST) — atualizar prioridade
  - `/clinica/**` — acesso total à área clínica
  - `/clinica/animais/{animalId}` — ver ficha clínica, registar intervenções
  - `/relatorios/**` — geração e visualização de relatórios
  - APIs de gestão: CRUD completo sobre entidades principais
- Ações permitidas: gerir utilizadores, alterar permissões, configurar parâmetros do sistema.

### Médico Veterinário (Vet)
- Perfil clínico com capacidade de registar eventos clínicos, intervenções e alterações de estado.
- Páginas e endpoints principais:
  - `/` (Início) — atalhos para clínico
  - `/clinica` — painel clínico com lista de animais
  - `/clinica/animais/{animalId}` — ficha clínica do animal (ver/editar eventos)
  - `/clinica/intervencoes?estadiaId={id}` — listar intervenções da estadia
  - `/clinica/intervencoes/create` (POST) — registar uma intervenção (requere papel de veteriário)
  - `/clinica/alteracoes/create` (POST) — registar alteração do estado de saúde
  - `/plano-cuidados?estadiaId={id}` — consultar plano de cuidados e registar tarefas (quando permitido)
  - `/plano-cuidados/animal/{animalId}/historico` — histórico de planos do animal
- Ações permitidas: criar/editar intervenções, adicionar notas clínicas, fechar procedimentos.

### Staff / Operador (Staff)
- Pessoal de apoio com foco em execução de tarefas operacionais (alimentação, higiene, medicação sob prescrição).
- Páginas e endpoints principais:
  - `/` (Início) — atalhos operacionais
  - `/plano-cuidados` — visualizar planos do turno
  - `/plano-cuidados?estadiaId={id}` — abrir plano para uma estadia específica
  - `/plano-cuidados/{id}/tarefa` (POST) — adicionar tarefas (consoante permissões locais)
  - `/plano-cuidados/tarefa/{id}/concluir` (POST) — marcar tarefa como concluída
  - `/plano-cuidados/{id}/instrucoes` (POST) — visualizar e (quando autorizado) adicionar instruções
  - `/estadias/{id}` — ver detalhe da estadia (acesso limitado)
- Ações permitidas: executar e marcar tarefas; consultar instruções; registar observações simples.

### Receção / Atendimento (Front Desk)
- Perfil orientado à recepção: gerir check-ins/check-outs, aceder a dados de estadias e planos resumidos.
- Páginas e endpoints principais:
  - `/` (Início)
  - `/estadias/**` — criar e finalizar estadias
  - `/plano-cuidados` — ver lista de planos do turno
  - `/plano-cuidados/{planoId}/encerrar` (POST) — encerrar plano no check-out
  - `/clinica/animais/{animalId}` — ver ficha básica (leitura)
- Ações permitidas: criar estadias, gerar check-out, aceder a informação de contacto do tutor.

### Cliente / Tutor (Cliente)
- Perfil do proprietário do animal; acesso restrito à sua informação e histórico.
- Páginas e endpoints principais:
  - `/` (Início)
  - `/perfil` — área do cliente (dados pessoais, animais associados)
  - `/plano-cuidados/animal/{animalId}/historico` — consultar histórico de planos do próprio animal (quando autorizado)
  - `/reservas/**` ou `/servicos/**` — agendar e ver serviços (dependente de funcionalidades do projecto)
- Ações permitidas: ver histórico, consultar faturas e comunicações; não podem registar intervenções clínicas.

## Regras comuns e observações
- As rotas de escrita (POST) devem validar permissões no lado servidor (Security + validações de serviço).
- A aplicação usa `SecurityConfig` para proteger endpoints: as rotas `"/plano-cuidados/**"` e `"/clinica/**"` requerem autenticação e papéis adequados.
- Algumas ações críticas (ex.: registar intervenção clínica) necessitam do papel `MEDICO_VETERINARIO`. Mesmo que o botão seja visível, a operação será bloqueada pelo serviço se o utilizador não tiver permissões.
- Recomenda-se rever os controllers que obtêm `autorId` diretamente do `Principal` e tratá-lo de forma robusta (ex.: fallback ou extração através do `SecurityContext`) para evitar erros em sessões com nomes não-numéricos.

## Como usar este documento
- Actualizar sempre que um novo endpoint for adicionado.
- Usar como referência para configurar `SecurityConfig` e testes de integração.
- Quando necessário, adicionar uma coluna extra com o nível de acesso (`ler`, `criar`, `editar`, `apagar`) por endpoint.

---
*Documento gerado automaticamente pelo agente — rever e ajustar conforme políticas de acesso e roles definidas no projecto.*
