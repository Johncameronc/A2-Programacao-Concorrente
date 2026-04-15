# A2-Programacao-Concorrente

# Como testar

Abra 3 terminais na raiz do projeto.

## Terminal 1 - Compilar e subir a Fabrica
```
javac -d out Models/*.java Fabrica/*.java Loja/*.java Cliente/*.java Teste.java
java -cp out Fabrica.FabricaMain
```
Aguarde aparecer: Servico RMI publicado em rmi://localhost/FabricaServico

## Terminal 2 - Subir as Lojas
```
java -cp out Loja.LojaMain
```
Aguarde aparecer: LojaMain iniciada com 3 lojas ativas.

## Terminal 3 - Rodar ClienteApp (processo separado)
```
java -cp out Cliente.ClienteApp
```

O ClienteApp inicia 20 clientes (threads), cada um comprando de forma aleatoria nas 3 lojas via RMI.

## Modelo atual de execucao

1. Fabrica publica FabricaServico no Registry RMI.
2. LojaMain cria 3 lojas no mesmo processo e publica LojaServico_1, LojaServico_2 e LojaServico_3 no mesmo Registry.
3. ClienteApp (processo separado) faz lookup das 3 lojas e inicia 20 threads de clientes.
4. Cada cliente escolhe loja aleatoriamente, compra via RMI e guarda o veiculo em garagem propria (capacidade 40).

## Observacoes

1. O ClienteApp esta hardcoded para host localhost, porta padrao do Registry RMI e compras continuas (maxCompras = 0).
2. O arquivo Teste.java continua disponivel para testes manuais simples, mas nao e mais o fluxo principal.
