package Cliente;

import Loja.LojaServico;
import Models.Veiculo;

import java.rmi.RemoteException;
import java.util.concurrent.ThreadLocalRandom;

public class ClienteComprador extends Thread {

    private final int idCliente;
    private final LojaServico[] lojas;
    private final Garagem garagem;
    private final LogCliente log;
    private final int maxCompras;

    public ClienteComprador(int idCliente,
                            LojaServico[] lojas,
                            Garagem garagem,
                            LogCliente log,
                            int maxCompras) {
        super("Cliente-" + idCliente);
        this.idCliente = idCliente;
        this.lojas = lojas;
        this.garagem = garagem;
        this.log = log;
        this.maxCompras = maxCompras;
    }

    @Override
    public void run() {
        int totalCompras = 0;

        while (!Thread.currentThread().isInterrupted()
                && (maxCompras <= 0 || totalCompras < maxCompras)) {
            int idLoja = ThreadLocalRandom.current().nextInt(1, lojas.length + 1);

            try {
                Veiculo veiculo = lojas[idLoja - 1].comprarVeiculo(idCliente);
                int posGaragem = garagem.produzir(veiculo);
                totalCompras++;
                log.registrarCompra(idCliente, idLoja, posGaragem, veiculo);

                Thread.sleep(ThreadLocalRandom.current().nextInt(25, 101));
            } catch (RemoteException e) {
                try {
                    log.registrarErro(idCliente, "Falha RMI ao comprar: " + e.getMessage());
                    Thread.sleep(1000);
                } catch (InterruptedException ie) {
                    Thread.currentThread().interrupt();
                }
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }

        try {
            log.registrarEncerramento(idCliente, totalCompras);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
}
