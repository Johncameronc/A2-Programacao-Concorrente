package Loja;

import java.rmi.Remote;
import java.rmi.RemoteException;

import Models.Veiculo;

public interface LojaServico extends Remote {
    Veiculo comprarVeiculo(int idCliente) throws RemoteException, InterruptedException;
}