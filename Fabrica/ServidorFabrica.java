package Fabrica;

import Models.Veiculo;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;

public class ServidorFabrica extends UnicastRemoteObject implements FabricaServico {

    public static final int PORTA_RMI = Registry.REGISTRY_PORT;
    public static final String NOME_SERVICO = "FabricaServico";

    private final EsteiraFabrica esteiraFabrica;
    private final LogFabrica log;

    public ServidorFabrica(EsteiraFabrica esteiraFabrica, LogFabrica log) throws RemoteException {
        super();
        this.esteiraFabrica = esteiraFabrica;
        this.log = log;
    }

    public void iniciar() {
        try {
            try {
                LocateRegistry.createRegistry(PORTA_RMI);
            } catch (RemoteException ignored) {
                // O registry pode já estar em execução.
            }

            Registry registry = LocateRegistry.getRegistry(PORTA_RMI);
            registry.rebind(NOME_SERVICO, this);
            System.out.println("Servico RMI publicado em rmi://localhost/" + NOME_SERVICO);
        } catch (RemoteException e) {
            System.err.println("Falha ao publicar servico RMI: " + e.getMessage());
        }
    }

    @Override
    public Veiculo solicitarVeiculo(int idLoja) throws RemoteException, InterruptedException {
        Veiculo veiculo = esteiraFabrica.consumir();
        veiculo.setIdLoja(idLoja);
        return veiculo;
    }

    @Override
    public void confirmarRecebimento(Veiculo veiculo) throws RemoteException {
        try {
            log.registrarVendaLoja(veiculo);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new RemoteException("Falha ao registrar venda da loja", e);
        }
    }
}
