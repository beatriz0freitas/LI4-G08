# RNF-01 - Tempo de resposta

**Descrição:**
O sistema deve garantir um tempo de resposta inferior a 2 segundos para operações de leitura (consulta de disponibilidade, pesquisa de animais/tutores, visualização de histórico) e inferior a 3 segundos para operações de escrita (criação de reservas, registo de check-in/check-out, registo de pagamentos), medidos desde a submissão do pedido até à apresentação do resultado. Este desempenho deve ser assegurado em condições normais de utilização, definidas como até 10 utilizadores simultâneos em rede local. Caso o tempo de resposta exceda 2 segundos, o sistema deve apresentar uma indicação visual de processamento em curso.

**Origem:** US-06, US-07

**Prioridade:** Must Have
