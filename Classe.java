import java.util.HashMap;
// Serve mais como interface, quando for criado um objeto ele verá os atributos da classe
// TODO: metodos
/* public class Classe {
    String atributos;
    HashMap<String,String> metodos = new HashMap<String,String>();
    public Classe(){}
} */


public class Classe {
    String atributos; // Lista de atributos da classe, separados por vírgula
    HashMap<String, String> metodos = new HashMap<>(); // Métodos da classe, onde a chave é o nome e o valor é o código do método

    public Classe() {}

    // Método para adicionar um método à classe
    public void adicionarMetodo(String nome, String codigo) {
        metodos.put(nome, codigo);
    }

    // Método para criar uma nova instância (objeto) da classe
    public Variavel criarInstancia() {
        Variavel objeto = new Variavel("objeto", 0);
        objeto.atributos = criaAtributos();
        return objeto;
    }

    // Método para criar os atributos de um objeto baseado nos atributos da classe
    private HashMap<String, Variavel> criaAtributos() {
        HashMap<String, Variavel> atributosObjeto = new HashMap<>();
        if (atributos != null && !atributos.isEmpty()) {
            String[] listaAtributos = atributos.split(",");
            for (String atributo : listaAtributos) {
                atributosObjeto.put(atributo.trim(), new Variavel("int", 0));
            }
        }
        return atributosObjeto;
    }

    // Método para obter um método da classe
    public String getMetodo(String nome) {
        return metodos.get(nome);
    }
}
