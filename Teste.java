import Loja.LojaServico;
import Loja.ServidorLoja;
import Models.Veiculo;
import Fabrica.ServidorFabrica;

import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

public class Teste {
    public static void main(String[] args) {
        try {
            Registry registry = LocateRegistry.getRegistry("localhost", ServidorFabrica.PORTA_RMI);

            LojaServico loja1 = (LojaServico) registry.lookup(ServidorLoja.NOME_BASE + "1");
            LojaServico loja2 = (LojaServico) registry.lookup(ServidorLoja.NOME_BASE + "2");
            LojaServico loja3 = (LojaServico) registry.lookup(ServidorLoja.NOME_BASE + "3");

            System.out.println("Conectado as 3 lojas via RMI!");
            System.out.println("Comprando 3 veiculos da Loja 1...");

            for (int i = 0; i < 3; i++) {
                Veiculo v = loja1.comprarVeiculo(99);
                System.out.println("  Comprado: " + v);
            }

            System.out.println("Comprando 2 veiculos da Loja 2...");

            for (int i = 0; i < 2; i++) {
                Veiculo v = loja2.comprarVeiculo(99);
                System.out.println("  Comprado: " + v);
            }

            System.out.println("Comprando 1 veiculo da Loja 3...");

            Veiculo v = loja3.comprarVeiculo(99);
            System.out.println("  Comprado: " + v);

            System.out.println("\nTeste concluido com sucesso!");

        } catch (Exception e) {
            System.err.println("Erro no teste: " + e.getMessage());
            e.printStackTrace();
        }
    }
}