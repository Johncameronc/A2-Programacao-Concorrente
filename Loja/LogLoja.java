package Loja;

import Models.Veiculo;
import java.util.concurrent.Semaphore;

public class LogLoja {

    private final int idLoja;
    private final Semaphore mutex = new Semaphore(1);

    public LogLoja(int idLoja) {
        this.idLoja = idLoja;
    }

    public void registrarRecebimento(Veiculo v) throws InterruptedException {
        mutex.acquire();
        try {
            System.out.println(String.format(
                "[RECEBIMENTO] Loja: %d | ID: V%03d | Cor: %s | Tipo: %s | Estacao: %d | Funcionario: %d | PosEsteiraFab: %d | PosEsteiraLoja: %d",
                idLoja, v.getId(), v.getCor(), v.getTipo(),
                v.getIdEstacao(), v.getIdFuncionario(),
                v.getPosicaoEsteiraFabrica(), v.getPosicaoEsteiraLoja()));
        } finally {
            mutex.release();
        }
    }

    public void registrarVendaCliente(Veiculo v, int idCliente) throws InterruptedException {
        mutex.acquire();
        try {
            System.out.println(String.format(
                "[VENDA_CLIENTE] Loja: %d | Cliente: %d | ID: V%03d | Cor: %s | Tipo: %s | Estacao: %d | Funcionario: %d | PosEsteiraFab: %d | PosEsteiraLoja: %d",
                idLoja, idCliente, v.getId(), v.getCor(), v.getTipo(),
                v.getIdEstacao(), v.getIdFuncionario(),
                v.getPosicaoEsteiraFabrica(), v.getPosicaoEsteiraLoja()));
        } finally {
            mutex.release();
        }
    }
}