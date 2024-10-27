import java.util.regex.Matcher;
import java.util.regex.Pattern;
public class Compilador {
    public static String identacao = "";
    public  Compilador(){
    }
    public String compilar(String codigo){
		String[] lines = codigo.split("\n");
		String compiled_code = compila_codigo(lines,0,false);
        return compiled_code;
    }

	public static String compila_codigo(String[] lines,int index,boolean is_if) {
		Pattern pattern;
		Matcher matcher;
		String codigo_compilado = "";
		identacao = "";
		for(int i = index;i<lines.length;i++) {
            // captura a identacao atual
			pattern = Pattern.compile("^(\\s*)");
			matcher = pattern.matcher(lines[i]);
			if(matcher.find()) {
				identacao = matcher.group(1);
			}

            // Esta função é chamada na compila_if_statement, neste caso ela deve retornar aqui
			if ( lines[i].contains("end-if")) {
				if(is_if) break;
			}

			// verifica se � atribuicao
			pattern = Pattern.compile("(\\w+\\.?\\w*) = .+");
			matcher = pattern.matcher(lines[i]);
			if (matcher.find()) {
				codigo_compilado = codigo_compilado+compila_atribuicao(lines[i]);
				continue;
			}
			
			// verifica se � chamada de m�todo
			pattern = Pattern.compile("(\\w+)\\.(\\w+)\\(([^\\)]*)\\)");
			matcher = pattern.matcher(lines[i]);
			if (matcher.find()) {
				codigo_compilado = codigo_compilado
						+compila_load(matcher.group(3))
						+ identacao + "load "+matcher.group(1)+"\n"
						+ identacao + "call "+matcher.group(2)+"\n"
						+ identacao + "pop\n";
				continue;
			}
			
			// verifica se � if-statement
			pattern = Pattern.compile("if [^\\s]+ \\w\\w [^\\s]+ then");
			matcher = pattern.matcher(lines[i]);
			if (matcher.find()) {
				String if_statement = compila_if_statement(lines[i],lines,i,false);
				codigo_compilado = codigo_compilado+if_statement;
				continue;
			}
   
            // verifica se é um else
			pattern = Pattern.compile("else");
			matcher = pattern.matcher(lines[i]);
			if (matcher.find()) {
                if(is_if) break;
				String else_statement = compila_if_statement(lines[i],lines,i,true);
				codigo_compilado = codigo_compilado+else_statement;
				continue;
			}

            
			
			// verifica se � retorno
			pattern = Pattern.compile("return (\\w+)");
			matcher = pattern.matcher(lines[i]);
			if (matcher.find()) {
				codigo_compilado = codigo_compilado
						+ identacao + "load "+matcher.group(1)+"\n"
						+ identacao + "ret\n";
				continue;
			}
			codigo_compilado = codigo_compilado+lines[i]+"\n";
		}
		return codigo_compilado;
	}
	
	
	// traduz "a = x", sendo a qualquer nome de vari�vel e x qualquer valor que ser� atribuido,
	// desde que seja simples (sem opera��es), para a vers�o do compilador
	public static String compila_atribuicao(String line) {
		// captura o lado esquerdo e o lado direito da atribui��o
		Pattern pattern = Pattern.compile("(\\w+\\.?\\w*) = (.+)");
		Matcher matcher = pattern.matcher(line);
		matcher.find();
		String LHS = matcher.group(1);
		String RHS = matcher.group(2);
		
		String armazena = "";
		pattern = Pattern.compile("(\\w+)\\.(\\w+)");
		matcher = pattern.matcher(LHS);

		if (matcher.find()) {
			armazena = setter(matcher.group(1),matcher.group(2));
		}
		else {
			armazena = identacao + "store "+LHS+"\n";
		}
		
		pattern = Pattern.compile("^(\\d+)$");
		matcher = pattern.matcher(RHS);
		if(matcher.find()) {
			return 	identacao + "const "+matcher.group(1)+"\n"+
					armazena;
		}
		else return verifica_RHS(RHS)+
					armazena;
	}
	public static String verifica_RHS(String line) {
		//verifica se � uma opera��o
		Pattern pattern = Pattern.compile("(\\w+) (\\+|-|/|\\*) (\\w+)");
		Matcher matcher = pattern.matcher(line);
		if (matcher.find()) {
			return verifica_operacao(matcher);
		}
		
		// verifica se � chamada de m�todo
		pattern = Pattern.compile("(\\w+)\\.(\\w+)\\(([^\\)]*)\\)");
		matcher = pattern.matcher(line);
		if (matcher.find()) {
			return compila_load(matcher.group(3))
					+ identacao + "load "+matcher.group(1)+"\n"
					+ identacao + "call "+matcher.group(2)+"\n";
		}
		
		// verifica se � atributo de um objeto
		pattern = Pattern.compile("(\\w+)\\.(\\w+)");
		matcher = pattern.matcher(line);
		if (matcher.find()) {
			return getter(matcher.group(1),matcher.group(2));
		}
		
		// verifica se � um novo objeto
		pattern = Pattern.compile("new (\\w+)");
		matcher = pattern.matcher(line);
		if(matcher.find()) {
			return identacao + "new " + matcher.group(1)+"\n";
		}
		
		
		else return"";
		
	}
	
	public static String verifica_operacao(Matcher matcher) {
		String operacao = "";
		switch(matcher.group(2)) {
			case "+":
				operacao = "add";
				break;
			case "-":
				operacao = "sub";
				break;
			case "*":
				operacao = "mul";
				break;
			case "/":
				operacao = "div";
				break;
						
		}
		return identacao + "load " +matcher.group(1)+"\n"
				+ identacao + "load "+matcher.group(3)+"\n"
				+ identacao + operacao+"\n";
	}
	
	// retorna uma lista de loads x, sendo x qualquer nome de variavel
	public static String compila_load(String name_list) {
		String[] names = name_list.split(",");
		String saida = "";
		Pattern pattern_local = Pattern.compile("\\w+");
		Matcher  matcher_local = pattern_local.matcher(saida);
		for(int i = 0;i<names.length;i++) {
			 // evita de pegar caracteres invisíveis, o que acaba mexendo com a identação
			 matcher_local = pattern_local.matcher(names[i]);
			 if (matcher_local.find()) {
				saida = saida+identacao +"load "+names[i]+"\n";
			}
		}
		return saida;
	}
	
	// só cria as strings de get
	public static String getter(String object,String attribute) {
		return 	identacao + "load "+object+"\n"
			+ identacao + 	"get "+attribute+"\n";
	}
	
	public static String setter(String object,String attribute) {
		return 	identacao + "load "+object+"\n"
			+	identacao + "set "+attribute+"\n";
	}
	
	// compila o código localmente para ver quantas instruções serão puladas, o código compilado é descartado
	// na função principal é compilado e escrito novamente o bloco do if
	public static String compila_if_statement(String line,String[] lines,int index,boolean is_else) {
		Pattern pattern = Pattern.compile("if ([^\\s]) (\\w\\w) ([^\\s]) then");
		Matcher matcher = pattern.matcher(line);
		matcher.find();
		String bloco = "";
		
		String retorno_if = compila_codigo(lines,index+1,true);
		int numero_instrucoes = retorno_if.split("\n").length;
        if(is_else){
            bloco = identacao + "else " + numero_instrucoes+"\n\n";
            return bloco;
        }
		bloco = identacao + "load "+matcher.group(1)+"\n"
				+ identacao + "load "+matcher.group(3)+"\n"
				+ identacao + matcher.group(2)+"\n"
				+ identacao + "if "+numero_instrucoes+"\n\n";
		
		return bloco;
		
		
	}
}
