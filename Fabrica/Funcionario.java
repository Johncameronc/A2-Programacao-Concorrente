package Fabrica;

import Models.Veiculo;
import java.util.concurrent.Semaphore;
import java.util.concurrent.ThreadLocalRandom;
 
public class Funcionario extends Thread {
 
    private final int id;
    private final int idEstacao;
    private final Ferramenta ferramentaEsquerda;
    private final Ferramenta ferramentaDireita;
    private final EsteiraFabrica esteiraFabrica;
    private final EstoquePecas estoque;
    private final EsteiraPecas esteiraPecas;
    private final LogFabrica log;
 
    private static int contadorVeiculos = 0;
    private static final Semaphore mutexContador = new Semaphore(1);
 
    public Funcionario(int id, int idEstacao,
                       Ferramenta esquerda, Ferramenta direita,
                       EsteiraFabrica esteiraFabrica,
                       EstoquePecas estoque, EsteiraPecas esteiraPecas,
                       LogFabrica log) {
        this.id = id;
        this.idEstacao = idEstacao;
        this.ferramentaEsquerda = esquerda;
        this.ferramentaDireita = direita;
        this.esteiraFabrica = esteiraFabrica;
        this.estoque = estoque;
        this.esteiraPecas = esteiraPecas;
        this.log = log;
    }
 
    @Override
    public void run() {
        while (true) {
            try {
                retirarPeca();
                pegarFerramentas();
                Veiculo veiculo = produzirVeiculo();
                largarFerramentas();
                int posicao = esteiraFabrica.produzir(veiculo);
                veiculo.setPosicaoEsteiraFabrica(posicao);
                log.registrarProducao(veiculo);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                break;
            }
        }
    }
 
    private void retirarPeca() throws InterruptedException {
        esteiraPecas.iniciarRequisicao();
        try {
            estoque.retirarPeca();
        } finally {
            esteiraPecas.finalizarRequisicao();
        }
    }
 
    private void pegarFerramentas() throws InterruptedException {
        if (id % 2 == 0) {
            ferramentaEsquerda.pegar();
            ferramentaDireita.pegar();
        } else {
            ferramentaDireita.pegar();
            ferramentaEsquerda.pegar();
        }
    }
 
    private void largarFerramentas() {
        ferramentaEsquerda.largar();
        ferramentaDireita.largar();
    }
 
    private Veiculo produzirVeiculo() throws InterruptedException {
        int novoId;
        mutexContador.acquire();
        try {
            novoId = ++contadorVeiculos;
        } finally {
            mutexContador.release();
        }

        Thread.sleep(ThreadLocalRandom.current().nextInt(50, 201));
        return new Veiculo(novoId, idEstacao, id);
    }
}
