package Loja;

import Models.Veiculo;
import Fabrica.FabricaServico;

public class ClienteFabrica extends Thread {

    private final int idLoja;
    private final FabricaServico fabrica;
    private final EsteiraLoja esteiraLoja;
    private final LogLoja log;

    public ClienteFabrica(int idLoja, FabricaServico fabrica,
                          EsteiraLoja esteiraLoja, LogLoja log) {
        this.idLoja = idLoja;
        this.fabrica = fabrica;
        this.esteiraLoja = esteiraLoja;
        this.log = log;
    }

    @Override
    public void run() {
        while (true) {
            try {
                Veiculo veiculo = fabrica.solicitarVeiculo(idLoja);

                int posicao = esteiraLoja.produzir(veiculo);
                veiculo.setPosicaoEsteiraLoja(posicao);

                fabrica.confirmarRecebimento(veiculo);

                log.registrarRecebimento(veiculo);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                break;
            } catch (Exception e) {
                System.err.println("Loja " + idLoja + " erro ao solicitar veiculo: " + e.getMessage());
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException ie) {
                    Thread.currentThread().interrupt();
                    break;
                }
            }
        }
    }
}