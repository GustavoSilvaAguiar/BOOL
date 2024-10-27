import java.util.Arrays;
import java.util.regex.Pattern;
import java.util.regex.Matcher;
import java.util.HashMap;
import java.util.Stack;



public class Interpretador {
    public static HashMap<String,Variavel> variaveis = new HashMap<String,Variavel>();
    public static HashMap<String,Classe> classes = new HashMap<String,Classe>();
    public Interpretador(){
    }

    // Metodo principal, inicia a leitura do código estabelece as classes e métodos
    public static void interpretar(String codigo){
        String[] lines = codigo.split("\n");
        Pattern pattern;
        Matcher matcher;
        String estado = "";
        String classe_atual = "";
        String metodo_atual = "";
        for(int i = 0;i<lines.length;i++){
            pattern = Pattern.compile("main\\(\\)");
            matcher = pattern.matcher(lines[i]);
            if (matcher.find()) {
                interpreta_main(lines, i);
                return;
            }

            pattern = Pattern.compile("end-method");
            matcher = pattern.matcher(lines[i]);
            if(matcher.find()){
                estado = "em classe";
                metodo_atual = "";
                continue;
            }
            pattern = Pattern.compile("end-class");
            matcher = pattern.matcher(lines[i]);
            if(matcher.find()){
                estado = "";
                continue;
            }
            if(estado =="em classe"){
                pattern = Pattern.compile("vars (.+)");
                matcher = pattern.matcher(lines[i]);
                if (matcher.find()){
                    classes.get(classe_atual).atributos = matcher.group(1);
                    continue;
                }
            }
            
            if(estado == "em metodo"){
                Classe atual = classes.get(classe_atual);
                atual.metodos.put(metodo_atual,atual.metodos.get(metodo_atual)+"\n"+lines[i]);
                continue;
            }

            pattern = Pattern.compile("class (\\w+)");
            matcher = pattern.matcher(lines[i]);
            if(matcher.find()){
                classes.put(matcher.group(1),new Classe());
                estado = "em classe";
                classe_atual = matcher.group(1);
                continue;
            }
            pattern = Pattern.compile("method (\\w+)");
            matcher = pattern.matcher(lines[i]);
            if(matcher.find()){
                estado = "em metodo";
                metodo_atual = matcher.group(1);
                classes.get(classe_atual).metodos.put(metodo_atual,lines[i]);
                continue;
            }

        }
    }
    
// é chamado pelo método anterior, executa o código mesmo
// obs.: retornando variável pois o plano é usar esse método pra chamar os métodos do código 
    public static Variavel interpreta_main(String[] lines,int index){
        Pattern pattern;
        Matcher matcher;
        Stack<Variavel> pilha = new Stack<Variavel>();
        for(int i = index;i<lines.length;i++){
            pattern = Pattern.compile ("ret (.+)");
            matcher = pattern.matcher(lines[i]);
            if (matcher.find()) {
                if (variaveis.get(matcher.group(1)) == null) {
                    return new Variavel("const", Integer.parseInt(matcher.group(1)));
                }
                return variaveis.get(matcher.group(1));
            }

            pattern = Pattern.compile("vars (.+)");
            matcher = pattern.matcher(lines[i]);
            if (matcher.find()){
                cria_variaveis(lines[i]);
                imprime_pilha(pilha);
                continue;
            }
            
            pattern = Pattern.compile("const (\\d+)");
            matcher = pattern.matcher(lines[i]);
            if(matcher.find()){
                pilha.push(new Variavel("const",Integer.parseInt(matcher.group(1))));
                imprime_pilha(pilha);
                continue;
            }

            pattern = Pattern.compile("load (\\w+)");
            matcher = pattern.matcher(lines[i]);
            if(matcher.find()){
                pilha.push(variaveis.get(matcher.group(1)));
                imprime_pilha(pilha);
                continue;
            }

            pattern = Pattern.compile("store (\\w+)");
            matcher = pattern.matcher(lines[i]);
            if(matcher.find()){
                variaveis.put(matcher.group(1), pilha.pop());
                imprime_pilha(pilha);
                continue;
            }


            pattern = Pattern.compile("add|sub|div|mul");
            matcher = pattern.matcher(lines[i]);
            if(matcher.find()){
                Variavel b = pilha.pop();
                Variavel a = pilha.pop();
                pilha.push(new Variavel("const", operacoes(matcher.group(0), a.valor, b.valor)));
                imprime_pilha(pilha);
                continue;
            }

            pattern = Pattern.compile("new (\\w+)");
            matcher = pattern.matcher(lines[i]);
            if (matcher.find()) {
                Variavel nova_variavel = new Variavel(matcher.group(1), 0);
                nova_variavel.atributos = cria_atributos_objeto(matcher.group(1));
                pilha.push(nova_variavel);
                imprime_pilha(pilha);
                continue;
            }

            pattern = Pattern.compile("get (\\w+)");
            matcher = pattern.matcher(lines[i]);
            if (matcher.find()) {
                Variavel objeto = pilha.pop();
                pilha.push(objeto);
                pilha.push(objeto.atributos.get(matcher.group(1)));
                imprime_pilha(pilha);
                continue;
            }

            pattern = Pattern.compile("set (\\w+)");
            matcher = pattern.matcher(lines[i]);
            if (matcher.find()) {
                pilha.push(pilha.pop().atributos.get(matcher.group(1)));
                imprime_pilha(pilha);
                continue;
            }

            pattern = Pattern.compile("call (\\w+)");
            matcher = pattern.matcher(lines[i]);
            if (matcher.find()) {
                imprime_pilha(pilha);
                continue;
            }

            pattern = Pattern.compile("io\\.print\\((\\w+)\\)");
            matcher = pattern.matcher(lines[i]);
            if (matcher.find()){
                System.out.println(variaveis.get(matcher.group(1)).valor);
                imprime_pilha(pilha);
                continue;
            }
        }
        
        return new Variavel("main", 0);
    }
    
    // Não testado ainda
    public static void busca__metodo_prototype(Variavel objeto,String metodo){
        Variavel aux = objeto;
        while (classes.get(aux.tipo).metodos.get(metodo)==null && aux.atributos.get("_prototype")!=null) {
            aux = aux.atributos.get("_prototype");
        }
    }
    
    // apenas para testes etc, não necessário no projeto
    public static void imprime_pilha(Stack<Variavel> pilha){
        System.out.print("[");
        Object[] p = pilha.toArray();
        for(int i = 0;i<p.length;i++){
            Variavel v = (Variavel)p[i];
            if (v.tipo == "const" || v.tipo == "int") {
                System.out.print(v.valor+",");
            }else{
                System.out.print(v.tipo+",");
            }
        }
        System.out.println("]");
    }

    public static int operacoes(String op, int a, int b){
        switch (op) {
            case "add":
                return a+b;
        
            case "sub":
                return a-b;
            
            case "mul":
                return a*b;

            case "div":
                return a/b;
            default:
                return 0;
        }

    }

    // para adicionar os atributos no objeto
    public static HashMap<String,Variavel> cria_atributos_objeto(String classe){
        HashMap<String,Variavel> atributos = new HashMap<String,Variavel>();
        String[] lista_atributos = classes.get(classe).atributos.split(",");
        for(int i = 0;i<lista_atributos.length;i++){
            atributos.put(lista_atributos[i],new Variavel("int", 0));
        }
        return atributos;
    }

    // Cria as variaveis (vars a,b,c,d)
    public static void cria_variaveis(String line){
        Pattern pattern_local = Pattern.compile("vars (.+)");
        Matcher matcher_local = pattern_local.matcher(line);
        matcher_local.find();
        String[] lista_variaveis = matcher_local.group(1).split(",");
        
        for (int i = 0;i<lista_variaveis.length;i++){
            String variavel = lista_variaveis[i].trim();
            variaveis.put(variavel,new Variavel("int",0));
        }
    }
}
