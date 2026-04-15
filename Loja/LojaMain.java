package Loja;

import Fabrica.FabricaServico;
import Fabrica.ServidorFabrica;

import java.rmi.Naming;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

public class LojaMain {

    private static final int NUM_LOJAS = 3;

    public static void main(String[] args) {
        try {
            FabricaServico fabrica = (FabricaServico) Naming.lookup(
                "rmi://localhost/" + ServidorFabrica.NOME_SERVICO);

            Registry registry = LocateRegistry.getRegistry(ServidorFabrica.PORTA_RMI);

            for (int i = 1; i <= NUM_LOJAS; i++) {
                EsteiraLoja esteiraLoja = new EsteiraLoja();
                LogLoja logLoja = new LogLoja(i);

                ServidorLoja servidor = new ServidorLoja(i, esteiraLoja, logLoja);
                servidor.iniciar(registry);

                ClienteFabrica cliente = new ClienteFabrica(i, fabrica, esteiraLoja, logLoja);
                cliente.start();
            }

            System.out.println("LojaMain iniciada com " + NUM_LOJAS + " lojas ativas.");

        } catch (Exception e) {
            System.err.println("Falha ao iniciar LojaMain: " + e.getMessage());
        }
    }
}