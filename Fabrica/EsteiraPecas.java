package Fabrica;

import java.util.concurrent.Semaphore;
 
public class EsteiraPecas {
 
    private static final int SLOTS = 5;
    private Semaphore slots;
 
    public EsteiraPecas() {
        this.slots = new Semaphore(SLOTS);
    }
 
    public void iniciarRequisicao() throws InterruptedException {
        slots.acquire();
    }
 
    public void finalizarRequisicao() {
        slots.release();
    }
}