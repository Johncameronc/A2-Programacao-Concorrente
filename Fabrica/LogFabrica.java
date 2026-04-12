package Fabrica;

import Models.Veiculo;
import java.util.concurrent.Semaphore;
 
public class LogFabrica {
 
    private final Semaphore mutex = new Semaphore(1);

    private String dadosProducao(Veiculo v) {
        return String.format(
                "ID: V%03d | Cor: %s | Tipo: %s | Estacao: %d | Funcionario: %d | PosEsteira: %d",
                v.getId(),
                v.getCor(),
                v.getTipo(),
                v.getIdEstacao(),
                v.getIdFuncionario(),
                v.getPosicaoEsteiraFabrica());
    }
 
    public void registrarProducao(Veiculo v) throws InterruptedException {
        mutex.acquire();
        try {
            System.out.println("[PRODUCAO] " + dadosProducao(v));
        } finally {
            mutex.release();
        }
    }
 
    public void registrarVendaLoja(Veiculo v) throws InterruptedException {
        mutex.acquire();
        try {
            System.out.println(String.format(
                    "[VENDA_LOJA] %s | Loja: %d | PosLojaEsteira: %d",
                    dadosProducao(v),
                    v.getIdLoja(),
                    v.getPosicaoEsteiraLoja()));
        } finally {
            mutex.release();
        }
    }
}