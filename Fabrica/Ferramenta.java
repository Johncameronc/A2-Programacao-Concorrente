package Fabrica;

import java.util.concurrent.Semaphore;
 
public class Ferramenta {
 
    private final int id;
    private final Semaphore semaphore;
 
    public Ferramenta(int id) {
        this.id = id;
        this.semaphore = new Semaphore(1);
    }
 
    public void pegar() throws InterruptedException {
        semaphore.acquire();
    }
 
    public void largar() {
        semaphore.release();
    }
 
    public int getId() {
        return id;
    }
}