package Fabrica;

import java.util.concurrent.Semaphore;
 
public class EstoquePecas {
 
    private static final int CAPACIDADE = 500;
    private Semaphore pecas;
 
    public EstoquePecas() {
        this.pecas = new Semaphore(CAPACIDADE);
    }
 
    public void retirarPeca() throws InterruptedException {
        pecas.acquire();
    }
 
    public int disponiveis() {
        return pecas.availablePermits();
    }
}
