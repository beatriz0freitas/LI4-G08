# Guia de Utilização - Check-in/Check-out e Listagens

## 🚀 Como Testar as Novas Funcionalidades

### 1. **Acesso à Aplicação**

```
URL: http://localhost:8080
Login: (Use credenciais de teste disponibilizadas)
```

### 2. **Mapa de Disponibilidade com Check-in/Check-out**

**Localização**: Menu → Reservas → Mapa de Disponibilidade
- URL: `http://localhost:8080/reservas/disponibilidade`

**Como usar**:
1. Selecione o período (data entrada/saída)
2. Selecione tipo de alojamento (opcional)
3. Clique em "Atualizar"
4. Clique num alojamento para abrir modal com detalhes
5. **Se alojamento está LIVRE com reserva**: Botão "Realizar Check-in" estará visível
6. **Se alojamento está OCUPADO com estadia**: Botão "Realizar Check-out" estará visível

**Fluxo de Check-in**:
```
Clica no botão "Realizar Check-in"
→ POST /estadias/check-in com reservaId
→ Sistema confirma reserva
→ Cria registro de estadia
→ Registra pagamento base
→ Redireciona com mensagem de sucesso
```

### 3. **Listagem de Reservas com Filtros**

**Localização**: Menu → Reservas → (Link "Reservas" na navegação)
- URL: `http://localhost:8080/reservas`

**Filtros disponíveis**:
- **Estado**: Ativa, Confirmada, Cancelada, Concluída
- Exemplo com filtro: `http://localhost:8080/reservas?estado=ATIVA`

**Ações por reserva**:
- 👁️ **Ver**: Visualizar detalhes da reserva
- ✏️ **Editar**: Modificar datas (apenas ATIVA)
- 🗑️ **Cancelar**: Cancelar reserva (apenas ATIVA)

### 4. **Listagem de Estadias com Filtros**

**Localização**: Menu → Estadias → (ou navegação direta)
- URL: `http://localhost:8080/estadias/lista`

**Filtros disponíveis**:
- **Estado**: Em curso, Terminada
- **De**: Data inicial do check-in
- **Até**: Data final do check-in
- Exemplo: `http://localhost:8080/estadias/lista?estado=EM_CURSO`

**Colunas exibidas**:
- Animal (nome)
- Alojamento (identificação)
- Check-in (data e hora)
- Check-out (data e hora, vazio se em curso)
- Duração (calculada automaticamente)
- Estado (badge colorida)

**Ações por estadia**:
- 👁️ **Ver**: Visualizar detalhes
- 🚪 **Check-out**: Realizar check-out (apenas EM_CURSO)

### 5. **Estados e Cores**

#### Estados de Reserva
- 🟢 **ATIVA**: Verde (resumido/destacado)
- 🔴 **CONFIRMADA**: Vermelho (ativa e processada)
- 🟠 **CANCELADA**: Laranja (não mais válida)
- ⚪ **CONCLUIDA**: Verde (concluída com sucesso)

#### Estados de Estadia
- 🟠 **EM_CURSO**: Laranja (em andamento)
- 🟢 **TERMINADA**: Verde (finalizada)

## 📋 Dados de Teste Sugeridos

Para testar todas as funcionalidades, você precisa de dados neste estado:

### Scenario 1: Check-in direto
```
Reserva existente:
- Estado: ATIVA
- Data início: Hoje
- Data fim: Amanhã
- Alojamento: Disponível (LIVRE)

Resultado esperado:
→ Modal mostra botão "Realizar Check-in"
→ Ao clicar: Estadia criada, reserva confirmada, pagamento registado
```

### Scenario 2: Check-out direto
```
Estadia existente:
- Estado: EM_CURSO
- Alojamento: OCUPADO
- Duração: Variável

Resultado esperado:
→ Modal mostra botão "Realizar Check-out"
→ Ao clicar: Estadia terminada, alojamento marcado para limpeza
```

### Scenario 3: Filtrar reservas por estado
```
Filtro: ?estado=ATIVA
Resultado esperado:
→ Tabela mostra apenas reservas ativas
→ Mostra ações (Editar, Cancelar)
```

### Scenario 4: Filtrar estadias com período
```
Filtro: ?estado=EM_CURSO&dataInicio=2026-05-27&dataFim=2026-06-27
Resultado esperado:
→ Tabela mostra estadias em curso neste período
→ Durações calculadas e exibidas
→ Botões de check-out disponíveis
```

## 🔧 Endpoints REST (Para Testes Manuais)

### Obter Lista de Reservas
```bash
GET http://localhost:8080/reservas
GET http://localhost:8080/reservas?estado=ATIVA
```

### Obter Lista de Estadias
```bash
GET http://localhost:8080/estadias/lista
GET http://localhost:8080/estadias/lista?estado=EM_CURSO
GET http://localhost:8080/estadias/lista?dataInicio=2026-05-27&dataFim=2026-06-27
```

### Realizar Check-in
```bash
POST http://localhost:8080/estadias/check-in
Body: reservaId=1&metodoPagamento=TRANSFERENCIA_BANCARIA
```

### Realizar Check-out
```bash
POST http://localhost:8080/estadias/check-out
Body: estadiaId=1&metodoPagamento=TRANSFERENCIA_BANCARIA
```

## ⚠️ Notas Importantes

1. **Autenticação**: Todos os endpoints requerem login. Use credenciais de `FUNCIONARIO_RECEPCAO` ou superior para check-in/check-out.

2. **Validações Automáticas**:
   - Check-in falha se: reserva não está em estado válido, animal já tem estadia em curso
   - Check-out falha se: estadia não está em curso
   - Filtros: parâmetros inválidos são ignorados (seguro)

3. **Formatação de Datas**:
   - Entrada: `YYYY-MM-DD` (ex: 2026-05-27)
   - Exibição: `dd/MM/yyyy` (ex: 27/05/2026)
   - Horários: `dd/MM/yyyy HH:mm` (ex: 27/05/2026 15:30)

4. **Duração de Estadias**:
   - Calcula automaticamente entre check-in e check-out
   - Se em curso, calcula até "agora"
   - Formato: "X dias, Y horas"

## 🐛 Troubleshooting

### Problema: Modal não aparece após clicar
**Solução**: Verifique se o JavaScript está habilitado no navegador. Verifique console (F12) para erros.

### Problema: Botão de check-in não aparece
**Solução**: Verifique:
- Alojamento está com estado LIVRE?
- Existe uma reserva associada (data-reserva está preenchido)?

### Problema: Check-in retorna erro
**Solução**: Verifique:
- Animal já tem uma estadia em curso?
- Reserva está em estado ATIVA?
- Tem permissão de FUNCIONARIO_RECEPCAO?

### Problema: Filtro não funciona
**Solução**: 
- Verifique URL do filtro (case-sensitive para estado)
- Estado deve ser: `ATIVA`, `CONFIRMADA`, `CANCELADA`, `CONCLUIDA` (para reservas)
- Estado deve ser: `EM_CURSO`, `TERMINADA` (para estadias)

## 📞 Suporte

Para mais informações, consulte:
- Logs da aplicação: `docker logs hotelanimais-app`
- Estrutura do projeto: `/docs/Etapa3/`
- Implementação: `IMPLEMENTATION_NOTES.md`
