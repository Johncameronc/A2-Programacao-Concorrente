package Fabrica;

import Models.BufferCircular;
 
public class EsteiraFabrica extends BufferCircular {
 
    private static final int CAPACIDADE = 40;
 
    public EsteiraFabrica() {
        super(CAPACIDADE);
    }
}