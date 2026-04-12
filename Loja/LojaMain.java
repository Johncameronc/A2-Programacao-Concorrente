package Loja;

import Models.Veiculo;
import java.rmi.Naming;
import java.util.concurrent.ThreadLocalRandom;

import Fabrica.FabricaServico;
import Fabrica.ServidorFabrica;

public class LojaMain {

    public static void main(String[] args) {
        int idLoja = args.length > 0 ? Integer.parseInt(args[0]) : 1;
        int quantidade = args.length > 1 ? Integer.parseInt(args[1]) : 1;

        try {
            FabricaServico fabrica = (FabricaServico) Naming.lookup("rmi://localhost/" + ServidorFabrica.NOME_SERVICO);

            while (true) {
                Veiculo veiculo = fabrica.solicitarVeiculo(idLoja);
                veiculo.setPosicaoEsteiraLoja(ThreadLocalRandom.current().nextInt(0, 40));
                fabrica.confirmarRecebimento(veiculo);
                System.out.println("Loja " + idLoja + " recebeu " + veiculo);
            }
        } catch (Exception e) {
            System.err.println("Falha no cliente RMI da loja: " + e.getMessage());
        }
    }
}
