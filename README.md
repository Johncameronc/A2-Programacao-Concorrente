# A2-Programacao-Concorrente

# Como testar

Abra 3 terminais na raiz do projeto.

## Terminal 1 — Compilar e subir a Fábrica
```
javac -d out Fabrica/*.java Models/*.java Loja/*.java
java -cp out Fabrica.FabricaMain
```
Aguarde aparecer: `Servico RMI publicado em rmi://localhost/FabricaServico`

## Terminal 2 — Subir as Lojas
```
java -cp out Loja.LojaMain
```
Aguarde aparecer: `LojaMain iniciada com 3 lojas ativas.`

## Terminal 3 — Rodar o teste
```
javac -d out -cp out Teste.java
java -cp out Teste
```
