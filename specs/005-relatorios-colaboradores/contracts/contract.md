# Contrato de Interface MVC — Relatórios e Colaboradores (Spec 005)

Este ficheiro descreve as rotas MVC, templates Thymeleaf e formulários da feature. A arquitetura adotada é Spring MVC server-side.

## Relatórios

### `GET /relatorios`

- Descrição: apresenta página de filtros e resultados de relatórios.
- Template: `relatorios/list.html`.
- Acesso: `DIRETOR`.
- Parâmetros opcionais: `dataInicio`, `dataFim`, `tipoAlojamento`, `incluirServicosExtra`, `agruparPor`.
- Resposta: página Thymeleaf com formulário de filtros, métricas agregadas e ações de exportação.

### `POST /relatorios/gerar`

- Descrição: submete filtros para geração de relatório.
- Form DTO: `RelatorioFiltroFormDto`.
- Acesso: `DIRETOR`.
- Sucesso: `redirect:/relatorios?...` ou renderização do template com resultados.
- Erro de validação: renderiza `relatorios/list.html` com mensagens.

### `GET /relatorios/exportar/csv`

- Descrição: exporta o relatório filtrado em CSV.
- Acesso: `DIRETOR`.
- Parâmetros: mesmos filtros do relatório.
- Resposta: ficheiro `text/csv`.

### `GET /relatorios/exportar/pdf`

- Descrição: exporta o relatório filtrado em PDF.
- Acesso: `DIRETOR`.
- Parâmetros: mesmos filtros do relatório.
- Resposta: ficheiro `application/pdf`.

## Colaboradores

### `GET /colaboradores`

- Descrição: lista colaboradores.
- Template: `colaboradores/list.html`.
- Acesso: `DIRETOR`.

### `GET /colaboradores/novo`

- Descrição: apresenta formulário de registo de colaborador.
- Template: `colaboradores/form.html`.
- Acesso: `DIRETOR`.
- Requisito: campo `tipoColaborador` deve ser uma seleção gerada a partir de `TipoColaborador.values()`.

### `POST /colaboradores`

- Descrição: cria colaborador.
- Form DTO: `ColaboradorFormDto`.
- Acesso: `DIRETOR`.
- Sucesso: `redirect:/colaboradores`.
- Erro de validação: renderiza `colaboradores/form.html`.

### `GET /colaboradores/{id}/editar`

- Descrição: apresenta formulário de edição.
- Template: `colaboradores/form.html`.
- Acesso: `DIRETOR`.

### `POST /colaboradores/{id}`

- Descrição: atualiza colaborador.
- Form DTO: `ColaboradorFormDto`.
- Acesso: `DIRETOR`.
- Sucesso: `redirect:/colaboradores`.
- Erro de validação: renderiza `colaboradores/form.html`.

### `POST /colaboradores/{id}/desativar`

- Descrição: desativa colaborador sem apagar histórico.
- Acesso: `DIRETOR`.
- Sucesso: `redirect:/colaboradores`.

## Form DTOs

### `RelatorioFiltroFormDto`

- `dataInicio: LocalDate`
- `dataFim: LocalDate`
- `tipoAlojamento: TipoAlojamento?`
- `incluirServicosExtra: boolean`
- `agruparPor: GrupoRelatorio` (`DIA`, `SEMANA`, `MES`)

### `ColaboradorFormDto`

- `username: String`
- `nome: String`
- `email: String`
- `password: String`
- `tipoColaborador: TipoColaborador`
- `ativo: boolean`

## Validação

- Intervalo de datas inválido regressa à página de relatório com mensagem.
- `username` e `email` duplicados regressam ao formulário de colaborador com mensagem.
- `tipoColaborador` inválido ou ausente regressa ao formulário; valores aceites são apenas os da enum `TipoColaborador`.
- Todas as submissões usam `POST` e token CSRF.
