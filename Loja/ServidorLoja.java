package Loja;

import Models.Veiculo;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;

public class ServidorLoja extends UnicastRemoteObject implements LojaServico {

    public static final String NOME_BASE = "LojaServico_";

    private final int idLoja;
    private final EsteiraLoja esteiraLoja;
    private final LogLoja log;

    public ServidorLoja(int idLoja, EsteiraLoja esteiraLoja, LogLoja log) throws RemoteException {
        super();
        this.idLoja = idLoja;
        this.esteiraLoja = esteiraLoja;
        this.log = log;
    }

    public void iniciar(Registry registry) {
        try {
            String nome = NOME_BASE + idLoja;
            registry.rebind(nome, this);
            System.out.println("Loja " + idLoja + " publicada em rmi://localhost/" + nome);
        } catch (RemoteException e) {
            System.err.println("Falha ao publicar Loja " + idLoja + ": " + e.getMessage());
        }
    }

    @Override
    public Veiculo comprarVeiculo(int idCliente) throws RemoteException, InterruptedException {
        Veiculo veiculo = esteiraLoja.consumir();
        log.registrarVendaCliente(veiculo, idCliente);
        return veiculo;
    }
}