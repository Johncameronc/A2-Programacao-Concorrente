package Cliente;

import Models.BufferCircular;

public class Garagem extends BufferCircular {

    private static final int CAPACIDADE = 40;

    public Garagem() {
        super(CAPACIDADE);
    }
}
