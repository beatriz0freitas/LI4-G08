# RNF-01 - Tempo de resposta

**Descrição:**

O sistema deve responder a qualquer operação em menos de 2 segundos
em condições normais de utilização

**Refinamento:**
O sistema deve garantir um tempo de resposta inferior a 2 segundos para
operações de leitura (consulta de disponibilidade, pesquisa de animais/tutores, visualização de
histórico) e inferior a 3 segundos para operações de escrita (criação de reservas, registo de
check-in/check-out, registo de pagamentos), medidos desde a submissão do pedido até à apresentação do resultado. Este desempenho deve ser assegurado em condições normais de utilização, definidas como até 7 utilizadores simultâneos em rede local.

**Origem:** US-01, US-08, US-14

**Prioridade:** Must Have
