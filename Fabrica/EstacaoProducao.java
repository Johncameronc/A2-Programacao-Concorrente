package Fabrica;

public class EstacaoProducao {
 
    private static final int NUM_FUNCIONARIOS = 5;
 
    private final int id;
    private final Ferramenta[] ferramentas;
    private final Funcionario[] funcionarios;
 
    public EstacaoProducao(int id,
                           EsteiraFabrica esteiraFabrica,
                           EstoquePecas estoque,
                           EsteiraPecas esteiraPecas,
                           LogFabrica log) {
        this.id = id;
        this.ferramentas = new Ferramenta[NUM_FUNCIONARIOS];
        this.funcionarios = new Funcionario[NUM_FUNCIONARIOS];
 
        for (int i = 0; i < NUM_FUNCIONARIOS; i++) {
            ferramentas[i] = new Ferramenta(i + 1);
        }
 
        for (int i = 0; i < NUM_FUNCIONARIOS; i++) {
            Ferramenta esquerda = ferramentas[i];
            Ferramenta direita = ferramentas[(i + 1) % NUM_FUNCIONARIOS];
            funcionarios[i] = new Funcionario(
                    i + 1,
                    id,
                    esquerda,
                    direita,
                    esteiraFabrica,
                    estoque,
                    esteiraPecas,
                    log);
        }
    }
 
    public void iniciar() {
        for (Funcionario funcionario : funcionarios) {
            funcionario.start();
        }
    }
 
    public int getId() {
        return id;
    }
}