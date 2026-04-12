package Fabrica;

public class FabricaMain {
 
    private static final int NUM_ESTACOES = 4;
 
    public static void main(String[] args) {
        EsteiraFabrica esteiraFabrica = new EsteiraFabrica();
        EstoquePecas estoque = new EstoquePecas();
        EsteiraPecas esteiraPecas = new EsteiraPecas();
        LogFabrica log = new LogFabrica();
 
        for (int i = 1; i <= NUM_ESTACOES; i++) {
            new EstacaoProducao(i, esteiraFabrica, estoque, esteiraPecas, log).iniciar();
        }
 
        try {
            ServidorFabrica servidor = new ServidorFabrica(esteiraFabrica, log);
            servidor.iniciar();
        } catch (Exception e) {
            System.err.println("Nao foi possivel iniciar o servico RMI: " + e.getMessage());
        }
 
        System.out.println("Fabrica iniciada com servico RMI " + ServidorFabrica.NOME_SERVICO);
    }
}
 