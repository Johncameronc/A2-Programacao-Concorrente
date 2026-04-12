package Fabrica;

import java.rmi.Remote;
import java.rmi.RemoteException;

import Models.Veiculo;

public interface FabricaServico extends Remote {
    Veiculo solicitarVeiculo(int idLoja) throws RemoteException, InterruptedException;

    void confirmarRecebimento(Veiculo veiculo) throws RemoteException;
}
