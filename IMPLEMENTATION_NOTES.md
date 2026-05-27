# Implementação: Check-in/Check-out e Listagens de Reservas/Estadias

## Data: 27 de Maio de 2026

### Objetivo
Implementar funcionalidades de check-in/check-out acessíveis através da página de disponibilidade, criando páginas de listagem para Reservas e Estadias com filtros avançados.

### Mudanças Implementadas

#### 1. **Disponibilidade Modal - Check-in/Check-out Dinâmico**
- **Arquivo**: `src/main/resources/templates/reservas/disponibilidade.html`
- **Mudança**: Substituído lógica estática de ações por lógica dinâmica que:
  - Mostra botão "Realizar Check-in" quando alojamento está LIVRE e existe uma Reserva associada
  - Mostra botão "Realizar Check-out" quando alojamento está OCUPADO e existe uma Estadia em curso
  - Mostra botão "Criar reserva" quando alojamento está disponível
  - Cada ação está conectada aos endpoints corretos (`/estadias/check-in`, `/estadias/check-out`)

#### 2. **DTOs para Listagens**

**ReservaListDto** (`src/main/java/pt/hotel/animais/dto/ReservaListDto.java`)
- Contém campos essenciais para exibição em lista: `id`, `animalNome`, `tutorNome`, `alojamentoIdentificacao`, `dataInicio`, `dataFim`, `estado`, `estadoLabel`, `estadoCss`
- Facilita conversão de entidades para DTO no controlador

**EstadiaListDto** (`src/main/java/pt/hotel/animais/dto/EstadiaListDto.java`)
- Contém campos: `id`, `reservaId`, `animalNome`, `alojamentoIdentificacao`, `dataInicio`, `dataFim`, `duracao`, `estado`, `estadoLabel`, `estadoCss`
- Campo `duracao` calcula automaticamente o tempo decorrido em formato legível (ex: "2 dias, 3 horas")

#### 3. **Serviços com Filtros**

**IReservaService** - Novo método:
```java
List<ReservaListDto> listarComFiltros(EstadoReserva estado);
```

**ReservaService** - Implementação:
- Filtra reservas por estado (ATIVA, CONFIRMADA, CANCELADA, CONCLUIDA)
- Converte para DTO com labels e classes CSS para visualização
- Método auxiliar `converterParaDto()` com formatação de estado

**IEstadiaService** - Novo método:
```java
List<EstadiaListDto> listarComFiltros(EstadoEstadia estado, LocalDate dataInicio, LocalDate dataFim);
```

**EstadiaService** - Implementação:
- Filtra estadias por estado, data de início e data de fim
- Calcula duração automática entre check-in e check-out (ou agora se ainda em curso)
- Retorna dados ordenados por `dataInicio DESC` (mais recentes primeiro)

#### 4. **Repositórios - Queries Customizadas**

**ReservaRepository**:
```java
List<Reserva> findByEstadoOrderByDataInicioDesc(EstadoReserva estado);
```

**EstadiaRepository**:
```java
List<Estadia> findComFiltros(
    EstadoEstadia estado, 
    LocalDateTime dataInicio, 
    LocalDateTime dataFim
);
```

#### 5. **Controladores - Novos Endpoints**

**ReservaController** - Modificação do endpoint GET `/reservas`:
- Agora aceita parâmetro `estado` para filtrar
- Renderiza novo template `reservas/lista.html`
- Passa lista de estados disponíveis para dropdown no template

**EstadiaController** - Novo endpoint GET `/estadias/lista`:
- Aceita parâmetros opcionais: `estado`, `dataInicio`, `dataFim`
- Renderiza template `estadias/lista.html`
- Ordena resultados por `dataInicio DESC`

#### 6. **Templates de Listagem**

**reservas/lista.html**:
- Tabela com colunas: Animal, Tutor, Alojamento, Entrada, Saída, Estado, Ações
- Filtro por estado com dropdown
- Botão "Nova Reserva" no topo
- Ações contextuais: Ver, Editar, Cancelar (conforme estado)
- Status visual com badges coloridas

**estadias/lista.html**:
- Tabela com colunas: Animal, Alojamento, Check-in, Check-out, Duração, Estado, Ações
- Filtros por: Estado, Data Inicial, Data Final
- Botão "Check-out" disponível apenas para estadias em curso
- Total de registos exibido no rodapé

### URLs Geradas

- **Listagem de Reservas**: `GET /reservas` (com filtro opcional: `?estado=ATIVA`)
- **Listagem de Estadias**: `GET /estadias/lista` (com filtros opcionais: `?estado=EM_CURSO&dataInicio=2026-05-27&dataFim=2026-06-27`)
- **Check-in via Modal**: `POST /estadias/check-in` com parâmetro `reservaId`
- **Check-out via Modal**: `POST /estadias/check-out` com parâmetro `estadiaId`

### Fluxos de Negócio Habilitados

#### Fluxo 1: Check-in através da Disponibilidade
1. Usuário acessa `/reservas/disponibilidade`
2. Seleciona período e tipo de alojamento
3. Clica num alojamento com status "LIVRE" e com reserva associada
4. Modal exibe botão "Realizar Check-in"
5. Clica em "Realizar Check-in"
6. Sistema: confirma reserva, cria estadia, registra pagamento base
7. Redireciona para `/estadias` com mensagem de sucesso

#### Fluxo 2: Check-out através da Disponibilidade
1. Usuário acessa `/reservas/disponibilidade`
2. Seleciona período
3. Clica num alojamento com status "OCUPADO" (estadia em curso)
4. Modal exibe botão "Realizar Check-out"
5. Clica em "Realizar Check-out"
6. Sistema: define data/hora de fim, calcula cobrança, registra pagamento, marca para limpeza
7. Redireciona para `/historico` com mensagem de sucesso

#### Fluxo 3: Gestão de Reservas
1. Usuário acessa `/reservas`
2. Seleciona filtro de estado (opcional)
3. Visualiza lista com ações por reserva
4. Pode editar/cancelar reservas ativas

#### Fluxo 4: Gestão de Estadias
1. Usuário acessa `/estadias/lista`
2. Seleciona filtros opcionais (estado, período)
3. Visualiza histórico de estadias com durações calculadas
4. Pode realizar check-out para estadias em curso

### Tecnologias Utilizadas

- **Backend**: Spring Boot 3.3.5, Spring Data JPA, Thymeleaf
- **Frontend**: Bootstrap 4.6, AdminLTE 3.2, jQuery
- **Database**: MySQL 8.0, Flyway migrations
- **Architecture**: MVC com DTOs, Services, Repositories

### Testes Realizados

✅ Compilação: Maven build bem-sucedido  
✅ Docker: Construção de imagem bem-sucedida  
✅ Inicialização: Aplicação iniciada em 21.565 segundos  
✅ Segurança: Autenticação funcionando (redireciona para login)  
✅ Endpoints: Estrutura de rotas e templates validada  

### Próximos Passos (Recomendado)

1. **Seed Data**: Inserir dados de teste através de migrations para validar funcionalidades
2. **Testes Automatizados**: Criar testes unitários para services e testes de integração para controllers
3. **Permissões**: Validar que apenas `FUNCIONARIO_RECEPCAO` e acima podem fazer check-in/check-out
4. **UI Melhorias**: 
   - Adicionar confirmação visual antes de check-out
   - Mostrar valor de cobrança estimado no modal
   - Adicionar busca/filtro rápido nas listagens

### Arquivos Criados/Modificados

**Criados:**
- `src/main/java/pt/hotel/animais/dto/ReservaListDto.java`
- `src/main/java/pt/hotel/animais/dto/EstadiaListDto.java`
- `src/main/resources/templates/reservas/lista.html`
- `src/main/resources/templates/estadias/lista.html`

**Modificados:**
- `src/main/resources/templates/reservas/disponibilidade.html`
- `src/main/java/pt/hotel/animais/service/IReservaService.java`
- `src/main/java/pt/hotel/animais/service/ReservaService.java`
- `src/main/java/pt/hotel/animais/repository/ReservaRepository.java`
- `src/main/java/pt/hotel/animais/service/IEstadiaService.java`
- `src/main/java/pt/hotel/animais/service/EstadiaService.java`
- `src/main/java/pt/hotel/animais/repository/EstadiaRepository.java`
- `src/main/java/pt/hotel/animais/controller/ReservaController.java`
- `src/main/java/pt/hotel/animais/controller/EstadiaController.java`
