# Guia de Estudo para Prova de Autoria

Este documento descreve o comportamento de todas as classes do projeto, com foco em:
- responsabilidade de cada classe
- fluxo de dados ponta a ponta
- concorrencia e sincronizacao
- pontos que costumam cair em defesa oral

## 1. Visao geral da arquitetura

O sistema esta dividido em 3 processos principais:
1. Fabrica: produz veiculos em paralelo e publica servico RMI.
2. LojaMain: sobe 3 lojas no mesmo processo, cada uma consumindo da fabrica e expondo servico para clientes.
3. ClienteApp: processo separado com 20 threads de clientes comprando via RMI.

O objeto que atravessa toda a cadeia e Models.Veiculo.

## 2. Fluxo completo da cadeia produtiva

1. Funcionario retira peca do estoque usando controle de fila de requisicao.
2. Funcionario pega duas ferramentas da estacao, produz veiculo e publica na esteira da fabrica.
3. Loja (thread ClienteFabrica) solicita veiculo remoto para a fabrica.
4. Fabrica remove da esteira de fabrica, define idLoja e devolve para a loja.
5. Loja insere em sua esteira local, define posicao na esteira da loja e confirma recebimento para fabrica.
6. ClienteComprador escolhe loja aleatoria e chama comprarVeiculo via RMI.
7. Loja remove da esteira local e entrega ao cliente.
8. Cliente guarda o veiculo na sua garagem (buffer proprio).

## 3. Mapa de sincronizacao com semaforos

Semaforos em uso:
- estoque de pecas: controla 500 pecas disponiveis.
- esteira de pecas: controla no maximo 5 requisicoes simultaneas de retirada.
- ferramentas: 1 permissao por ferramenta, exclusao mutua por recurso.
- mutexContador: protege o id sequencial global de veiculos.
- buffer circular (fabrica, loja, garagem): trio mutex, vazio, cheio.
- logs (fabrica, loja, cliente): mutex para nao misturar linhas no console.

Nao ha uso de synchronized, ReentrantLock, wait ou notify.

## 4. Comportamento por classe

## 4.1 Pacote Models

### 4.1.1 Models.BufferCircular

Papel:
- estrutura base de produtor consumidor com fila circular.

Estado interno:
- buffer de Veiculo com tamanho fixo.
- indices entrada e saida.
- semaforos mutex, vazio e cheio.

Comportamento:
- produzir(item):
  - espera vaga com vazio.acquire.
  - entra na secao critica com mutex.acquire.
  - escreve em buffer[entrada].
  - avanca entrada com modulo tamanho.
  - libera mutex e sinaliza cheio.
  - retorna a posicao usada para log.
- consumir():
  - espera item disponivel com cheio.acquire.
  - entra na secao critica com mutex.acquire.
  - le item em buffer[saida].
  - avanca saida com modulo tamanho.
  - libera mutex e sinaliza vazio.
  - retorna item.

Pontos de defesa:
- por que incrementa saida depois de ler: para manter FIFO e nao pular item.
- por que trio mutex/vazio/cheio: separa exclusao mutua de controle de capacidade.

### 4.1.2 Models.Veiculo

Papel:
- DTO serializavel compartilhado entre fabrica, loja e cliente.

Campos de negocio:
- id global.
- cor e tipo derivados do id.
- idEstacao e idFuncionario produtores.
- posicao na esteira da fabrica.
- idLoja compradora.
- posicao na esteira da loja.

Comportamento:
- construtor define id, estacao, funcionario e deriva cor/tipo.
- setters acumulam metadados ao longo da cadeia.
- toString imprime rastreabilidade completa.

Pontos de defesa:
- serialVersionUID garante compatibilidade de serializacao.
- cor e tipo sao deterministas por id.

## 4.2 Pacote Fabrica

### 4.2.1 Fabrica.FabricaMain

Papel:
- bootstrap do processo da fabrica.

Comportamento:
- instancia recursos compartilhados: esteira da fabrica, estoque, esteira de pecas e log.
- cria 4 estacoes de producao.
- inicia ServidorFabrica no RMI.

Ponto de defesa:
- ordem importa: recursos antes de subir threads e servico remoto.

### 4.2.2 Fabrica.EstacaoProducao

Papel:
- representar uma estacao com 5 funcionarios e 5 ferramentas em anel.

Comportamento:
- cria 5 ferramentas.
- cria 5 funcionarios, cada um com ferramenta esquerda e direita adjacentes.
- iniciar() liga todas as threads de funcionarios.

Ponto de defesa:
- topologia circular implementa o problema jantar dos filosofos adaptado.

### 4.2.3 Fabrica.Ferramenta

Papel:
- recurso exclusivo compartilhado entre funcionarios adjacentes.

Comportamento:
- semaforo binario de 1 permissao.
- pegar() bloqueia se outra thread estiver usando.
- largar() libera para proxima thread.

### 4.2.4 Fabrica.EstoquePecas

Papel:
- controlar quantidade total de pecas da fabrica.

Comportamento:
- inicia com 500 permissoes.
- retirarPeca() consome 1 permissao.
- disponiveis() mostra estoque restante.

Ponto de defesa:
- quando zerar estoque, funcionarios bloqueiam em retirarPeca.

### 4.2.5 Fabrica.EsteiraPecas

Papel:
- limitar throughput de solicitacoes ao estoque.

Comportamento:
- semaforo com 5 permissoes.
- iniciarRequisicao() ocupa slot.
- finalizarRequisicao() devolve slot.

Ponto de defesa:
- modela o requisito de ate 5 solicitacoes simultaneas.

### 4.2.6 Fabrica.EsteiraFabrica

Papel:
- buffer circular final de veiculos prontos.

Comportamento:
- herda BufferCircular com capacidade 40.

### 4.2.7 Fabrica.Funcionario

Papel:
- thread produtora principal da cadeia.

Fluxo de run():
1. retirar peca.
2. pegar ferramentas.
3. produzir veiculo.
4. largar ferramentas.
5. inserir na esteira da fabrica.
6. registrar log de producao.
7. repetir ate interrupcao.

Detalhes criticos:
- retirarPeca usa try/finally para sempre liberar slot da esteira de pecas.
- pegarFerramentas usa estrategia assimetrica:
  - id par pega esquerda depois direita.
  - id impar pega direita depois esquerda.
- produzirVeiculo protege contador global com mutexContador.
- simula tempo de producao com sleep aleatorio de 50 a 200 ms.

Pontos de defesa:
- assimetria reduz risco de deadlock no jantar dos filosofos.
- mutexContador garante unicidade e ordem do id global.

### 4.2.8 Fabrica.LogFabrica

Papel:
- log serializado da fabrica para evitar intercalacao de mensagens.

Comportamento:
- registrarProducao imprime dados do veiculo quando sai da fabrica.
- registrarVendaLoja imprime dados com loja e posicao na esteira da loja.
- usa semaforo mutex de 1 permissao para secao critica de I O.

### 4.2.9 Fabrica.FabricaServico

Papel:
- contrato remoto RMI da fabrica para lojas.

Metodos:
- solicitarVeiculo(idLoja): devolve proximo veiculo disponivel.
- confirmarRecebimento(veiculo): confirma que loja inseriu na esteira local.

### 4.2.10 Fabrica.ServidorFabrica

Papel:
- implementacao do servico remoto da fabrica.

Comportamento:
- iniciar():
  - cria registry RMI na porta padrao se nao existir.
  - faz bind do servico FabricaServico.
- solicitarVeiculo(idLoja):
  - consome da EsteiraFabrica.
  - seta idLoja no objeto.
  - retorna para a loja.
- confirmarRecebimento(veiculo):
  - registra venda para loja no log da fabrica.

Pontos de defesa:
- se a esteira estiver vazia, solicitarVeiculo bloqueia ate nova producao.
- PORTA_RMI representa porta do Registry, nao necessariamente a porta do trafego remoto final.

## 4.3 Pacote Loja

### 4.3.1 Loja.LojaMain

Papel:
- bootstrap do processo de lojas.

Comportamento:
- faz lookup do servico da fabrica.
- conecta ao mesmo Registry RMI da fabrica.
- para i de 1 a 3:
  - cria EsteiraLoja.
  - cria LogLoja.
  - publica ServidorLoja com nome LojaServico_i.
  - inicia thread ClienteFabrica para abastecimento continuo.

Ponto de defesa:
- 3 lojas rodam no mesmo processo, cada uma com esteira propria e thread propria.

### 4.3.2 Loja.EsteiraLoja

Papel:
- buffer local de cada loja para estoque de venda.

Comportamento:
- herda BufferCircular com capacidade 10.

### 4.3.3 Loja.LojaServico

Papel:
- contrato remoto da loja para clientes.

Metodo:
- comprarVeiculo(idCliente): bloqueia se esteira vazia, devolve veiculo quando disponivel.

### 4.3.4 Loja.ServidorLoja

Papel:
- implementacao de LojaServico para cada loja.

Comportamento:
- iniciar(registry): publica LojaServico_idLoja no Registry.
- comprarVeiculo(idCliente):
  - consome veiculo da esteira da loja.
  - registra log de venda ao cliente.
  - retorna veiculo.

Ponto de defesa:
- compra bloqueante em esteira vazia atende requisito de espera do cliente.

### 4.3.5 Loja.ClienteFabrica

Papel:
- thread de integracao loja com fabrica.

Fluxo:
1. solicitar veiculo para fabrica via RMI.
2. inserir na esteira da loja.
3. atualizar posicao da esteira da loja no objeto.
4. confirmar recebimento na fabrica.
5. registrar recebimento no log da loja.

Tratamento de falhas:
- em erro remoto, loga e tenta novamente apos 1 segundo.
- em interrupcao, encerra loop.

### 4.3.6 Loja.LogLoja

Papel:
- registrar recebimento e venda ao cliente de forma thread safe.

Comportamento:
- registrarRecebimento: loga dados do veiculo ao entrar na loja.
- registrarVendaCliente: loga dados quando sai para cliente.
- usa semaforo mutex para proteger output.

## 4.4 Pacote Cliente

### 4.4.1 Cliente.ClienteApp

Papel:
- bootstrap do processo separado de clientes.

Comportamento:
- usa host hardcoded localhost.
- usa porta do Registry RMI (constante da fabrica).
- faz lookup de LojaServico_1, LojaServico_2 e LojaServico_3.
- cria um LogCliente compartilhado.
- cria 20 threads ClienteComprador.
- cria 1 garagem por cliente.
- instala shutdown hook para interromper clientes.
- aguarda termino com join.

Pontos de defesa:
- clientes falam com lojas, nao com fabrica, apesar de usar a mesma porta de Registry.
- maxCompras hardcoded em 0 significa compras continuas.

### 4.4.2 Cliente.ClienteComprador

Papel:
- thread de compra de um cliente individual.

Fluxo:
1. escolhe loja aleatoria entre as disponiveis.
2. chama comprarVeiculo(idCliente) via RMI.
3. insere veiculo na garagem do cliente.
4. registra log da compra.
5. pausa curta aleatoria e repete.

Regras de parada:
- para ao receber interrupt.
- se maxCompras maior que zero, para ao atingir limite.

Tratamento de erro:
- RemoteException gera log de erro e backoff de 1 segundo.

### 4.4.3 Cliente.Garagem

Papel:
- buffer circular de armazenamento final do cliente.

Comportamento:
- herda BufferCircular com capacidade 40.

### 4.4.4 Cliente.LogCliente

Papel:
- log thread safe das compras dos clientes.

Comportamento:
- registrarCompra: imprime todos os dados de rastreio e posicao na garagem.
- registrarErro: imprime erros de compra remota.
- registrarEncerramento: resumo de compras por cliente.
- usa semaforo mutex para evitar mistura de linhas.

## 4.5 Classe utilitaria de teste

### 4.5.1 Teste

Papel:
- cliente de teste manual simples para validar lojas por RMI.

Comportamento:
- conecta ao Registry.
- faz lookup das 3 lojas.
- executa compras deterministicas:
  - 3 na loja 1
  - 2 na loja 2
  - 1 na loja 3

Uso:
- bom para smoke test rapido.
- nao e o fluxo principal de execucao apos introducao de ClienteApp.

## 5. Ordem de inicializacao e encerramento

Ordem recomendada:
1. subir FabricaMain.
2. subir LojaMain.
3. subir ClienteApp.

Motivo:
- lojas dependem de FabricaServico ja publicado.
- clientes dependem de LojaServico_1 a LojaServico_3 ja publicados.

Encerramento:
- ao encerrar ClienteApp, shutdown hook interrompe as 20 threads.
- funcionarios e clientes de abastecimento de loja continuam ativos enquanto processos de fabrica e loja estiverem rodando.

## 6. Perguntas classicas de defesa e resposta curta

1. Como evita corrida no id dos veiculos?
- mutexContador com Semaphore(1) em Funcionario.

2. Como evita deadlock nas ferramentas?
- estrategia assimetrica de ordem de aquisicao par impar.

3. O que acontece quando buffer esta vazio?
- consumidor bloqueia em cheio.acquire no BufferCircular.

4. O que acontece quando buffer esta cheio?
- produtor bloqueia em vazio.acquire no BufferCircular.

5. Por que ha 2 buffers de esteira (fabrica e loja)?
- desacoplar ritmo de producao da fabrica e ritmo de venda da loja.

6. Por que clientes usam a porta da fabrica no ClienteApp?
- e a porta do Registry compartilhado; lookup retorna stub da loja.

7. Onde a arquitetura e distribuida?
- FabricaMain, LojaMain e ClienteApp em processos separados, comunicando por RMI.

## 7. Roteiro rapido para voce se preparar

1. Explique o sistema em 30 segundos: cadeia em 3 processos e objeto Veiculo acumulando dados.
2. Explique os semaforos em 1 minuto: estoque, esteira de pecas, ferramentas, buffers, logs, contador.
3. Explique um ciclo completo em 1 minuto: funcionario produz, loja recebe, cliente compra, garagem armazena.
4. Explique um bloqueio em 30 segundos: exemplo esteira vazia bloqueando compra.
5. Explique porque nao tem lock proibida: somente Semaphore.

## 8. Arquivos para leitura antes da prova

Leitura obrigatoria:
- Models.BufferCircular
- Models.Veiculo
- Fabrica.Funcionario
- Fabrica.ServidorFabrica
- Loja.ClienteFabrica
- Loja.ServidorLoja
- Cliente.ClienteComprador
- Cliente.ClienteApp

Leitura de apoio:
- Fabrica.LogFabrica
- Loja.LogLoja
- Cliente.LogCliente
- Teste
