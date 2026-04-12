package Models;

import java.io.Serializable;

public class Veiculo implements Serializable {

    private static final long serialVersionUID = 1L;

    private static final String[] CORES = { "R", "G", "B" };
    private static final String[] TIPOS = { "SUV", "SEDAN" };

    private int id;
    private String cor;
    private String tipo;
    private int idEstacao;
    private int idFuncionario;
    private int posicaoEsteiraFabrica;
    private int idLoja;
    private int posicaoEsteiraLoja;

    public Veiculo(int id, int idEstacao, int idFuncionario) {
        this.id = id;
        this.idEstacao = idEstacao;
        this.idFuncionario = idFuncionario;
        this.cor = CORES[id % CORES.length];
        this.tipo = TIPOS[id % TIPOS.length];
    }

    public int getId() {
        return id;
    }

    public String getCor() {
        return cor;
    }

    public String getTipo() {
        return tipo;
    }

    public int getIdEstacao() {
        return idEstacao;
    }

    public int getIdFuncionario() {
        return idFuncionario;
    }

    public int getPosicaoEsteiraFabrica() {
        return posicaoEsteiraFabrica;
    }

    public int getIdLoja() {
        return idLoja;
    }

    public int getPosicaoEsteiraLoja() {
        return posicaoEsteiraLoja;
    }

    public void setPosicaoEsteiraFabrica(int p) {
        this.posicaoEsteiraFabrica = p;
    }

    public void setIdLoja(int id) {
        this.idLoja = id;
    }

    public void setPosicaoEsteiraLoja(int p) {
        this.posicaoEsteiraLoja = p;
    }

    @Override
    public String toString() {
        return String.format(
                "[V%03d | %s | %s | Est.%d | Func.%d | PosF:%d | Loja:%d | PosL:%d]",
                id, cor, tipo, idEstacao, idFuncionario,
                posicaoEsteiraFabrica, idLoja, posicaoEsteiraLoja);
    }
}