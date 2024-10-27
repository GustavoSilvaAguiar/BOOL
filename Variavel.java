import java.util.HashMap;

// tipo pode ser const, int (associado a uma variavel) ou um objeto, no ultimo caso terá atributos
/* public class Variavel {
    
    public HashMap<String,Variavel> atributos = new HashMap<String,Variavel>();
    public int valor;
    public String tipo ;
    public Variavel(String tipo, int valor){
        this.tipo = tipo;
        this.valor = valor;
    }


} */

public class Variavel {
    public HashMap<String, Variavel> atributos = new HashMap<>();
    public int valor;
    public String tipo;

    // Construtor para inicializar tipo e valor
    public Variavel(String tipo, int valor) {
        this.tipo = tipo;
        this.valor = valor;
    }

    // Método para definir um atributo em um objeto
    public void setAtributo(String nome, Variavel valor) {
        if (this.tipo.equals("objeto")) {
            this.atributos.put(nome, valor);
        } else {
            System.out.println("Erro: Atributos só podem ser definidos para objetos.");
        }
    }

    // Método para obter um atributo de um objeto
    public Variavel getAtributo(String nome) {
        if (this.tipo.equals("objeto")) {
            return this.atributos.getOrDefault(nome, new Variavel("int", 0));
        } else {
            System.out.println("Erro: Apenas objetos possuem atributos.");
            return null;
        }
    }

    // Método para verificar se a variável é um objeto
    public boolean isObjeto() {
        return this.tipo.equals("objeto");
    }

    // Método para representação em String (para depuração)
    @Override
    public String toString() {
        if (tipo.equals("const") || tipo.equals("int")) {
            return String.valueOf(valor);
        } else {
            return tipo + " com atributos: " + atributos.keySet();
        }
    }
}
