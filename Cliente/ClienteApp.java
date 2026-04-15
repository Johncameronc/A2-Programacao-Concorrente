package Cliente;

import Fabrica.ServidorFabrica;
import Loja.LojaServico;
import Loja.ServidorLoja;

import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

public class ClienteApp {

    private static final int NUM_CLIENTES = 20;
    private static final int NUM_LOJAS = 3;

    public static void main(String[] args) {
        String host = "localhost";
        int porta = ServidorFabrica.PORTA_RMI;
        int maxCompras = 0;

        try {
            Registry registry = LocateRegistry.getRegistry(host, porta);

            LojaServico[] lojas = new LojaServico[NUM_LOJAS];
            
            for (int i = 0; i < NUM_LOJAS; i++) {
                lojas[i] = (LojaServico) registry.lookup(ServidorLoja.NOME_BASE + (i + 1));
            }

            LogCliente log = new LogCliente();
            ClienteComprador[] clientes = new ClienteComprador[NUM_CLIENTES];

            for (int i = 0; i < NUM_CLIENTES; i++) {
                int idCliente = i + 1;
                Garagem garagem = new Garagem();
                clientes[i] = new ClienteComprador(idCliente, lojas, garagem, log, maxCompras);
                clientes[i].start();
            }

            System.out.println(String.format(
                "ClienteApp iniciado com %d clientes. host=%s porta=%d maxCompras=%d",
                NUM_CLIENTES, host, porta, maxCompras));

            Runtime.getRuntime().addShutdownHook(new Thread(() -> {
                for (ClienteComprador cliente : clientes) {
                    cliente.interrupt();
                }
            }));

            for (ClienteComprador cliente : clientes) {
                cliente.join();
            }

        } catch (NumberFormatException e) {
            System.err.println("Uso: java Cliente.ClienteApp [host] [porta] [maxComprasPorCliente]");
        } catch (Exception e) {
            System.err.println("Falha ao iniciar ClienteApp: " + e.getMessage());
        }
    }
}
