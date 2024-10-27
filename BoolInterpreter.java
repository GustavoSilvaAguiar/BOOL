import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;

public class BoolInterpreter {
    
    public static void main(String[] args) {
		String arquivo_a_ler = "programa.boolc";
		if(args.length>0){
			arquivo_a_ler = args[0];
		}
        interpretar(arquivo_a_ler);
    }
    public static void interpretar(String arquivo_a_ler){
		String codigo = "";
		try {
			File myObj = new File(arquivo_a_ler);
			Scanner myReader = new Scanner(myObj);
			while (myReader.hasNextLine()) {
				String data = myReader.nextLine();
				codigo += data+"\n";
			}
			myReader.close();

            Interpretador interpretador = new Interpretador();
            interpretador.interpretar(codigo);

		} catch (FileNotFoundException e) {
			System.out.println("Um erro ocorreu.");
			e.printStackTrace();
		}
    }
}
