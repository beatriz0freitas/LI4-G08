# Contract: Tutores e Animais

## Purpose

Documentar o fluxo de interface para registo e consulta de tutores e animais na fase de receção.

## Interactions

### Consultar tutor

- **Method**: `GET`
- **Path**: `/tutores`
- **Query**: `termo` opcional para pesquisa por nome ou NIF
- **Result**: lista de tutores correspondentes e respetivos dados resumidos

### Criar tutor

- **Method**: `POST`
- **Path**: `/tutores`
- **Payload**:
  - `nome`
  - `nif`
  - `contacto`
  - `email`
- **Validation**:
  - `nif` obrigatório e único
  - campos obrigatórios não podem ser vazios
- **Success**: tutor criado e disponível para associação a animais

### Criar animal associado a tutor

- **Method**: `POST`
- **Path**: `/tutores/{tutorId}/animais`
- **Payload**:
  - `nome`
  - `especie`
  - `raca`
  - `dataNascimento`
  - `peso`
  - `estadoSaude`
  - `necessidadesAlimentares`
  - `medicacaoCurso`
- **Validation**:
  - tutor existente é obrigatório
  - espécie limitada a `CAO` ou `GATO`
  - animal deve ficar associado ao tutor
- **Success**: animal criado e visível na ficha do tutor

## Error Cases

- NIF duplicado: recusar criação do tutor e devolver mensagem de validação.
- Tutor inexistente: recusar criação do animal.
- Campos obrigatórios em falta: recusar submissão e manter o formulário com erros.