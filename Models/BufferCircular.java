package Models;

import java.util.concurrent.Semaphore;
 
public class BufferCircular {
 
    private Veiculo[] buffer;
    private int tamanho;
    private int entrada, saida;
 
    private Semaphore mutex;
    private Semaphore vazio;
    private Semaphore cheio;
 
    public BufferCircular(int tamanho) {
        this.tamanho = tamanho;
        buffer = new Veiculo[tamanho];
        entrada = 0;
        saida = 0;
        mutex = new Semaphore(1);
        vazio = new Semaphore(tamanho);
        cheio = new Semaphore(0);
    }
 
    // Retorna a posição onde o item foi inserido (necessário para o log)
    public int produzir(Veiculo item) throws InterruptedException {
        vazio.acquire();
        mutex.acquire();
        int posicao = entrada;
        buffer[entrada] = item;
        entrada = (entrada + 1) % tamanho;
        mutex.release();
        cheio.release();
        return posicao;
    }
 
    public Veiculo consumir() throws InterruptedException {
        cheio.acquire();
        mutex.acquire();
        Veiculo item = buffer[saida];
        saida = (saida + 1) % tamanho;
        mutex.release();
        vazio.release();
        return item;
    }
}