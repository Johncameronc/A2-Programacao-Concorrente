package Cliente;

import Models.Veiculo;
import java.util.concurrent.Semaphore;

public class LogCliente {

    private final Semaphore mutex = new Semaphore(1);

    public void registrarCompra(int idCliente, int idLoja, int posGaragem, Veiculo v) throws InterruptedException {
        mutex.acquire();
        try {
            System.out.println(String.format(
                "[COMPRA_CLIENTE] Cliente: %d | Loja: %d | ID: V%03d | Cor: %s | Tipo: %s | Estacao: %d | Funcionario: %d | PosEsteiraFab: %d | PosEsteiraLoja: %d | PosGaragem: %d",
                idCliente,
                idLoja,
                v.getId(),
                v.getCor(),
                v.getTipo(),
                v.getIdEstacao(),
                v.getIdFuncionario(),
                v.getPosicaoEsteiraFabrica(),
                v.getPosicaoEsteiraLoja(),
                posGaragem));
        } finally {
            mutex.release();
        }
    }

    public void registrarErro(int idCliente, String mensagem) throws InterruptedException {
        mutex.acquire();
        try {
            System.err.println("[ERRO_CLIENTE] Cliente " + idCliente + " | " + mensagem);
        } finally {
            mutex.release();
        }
    }

    public void registrarEncerramento(int idCliente, int totalCompras) throws InterruptedException {
        mutex.acquire();
        try {
            System.out.println("[FIM_CLIENTE] Cliente " + idCliente + " | Compras realizadas: " + totalCompras);
        } finally {
            mutex.release();
        }
    }
}
