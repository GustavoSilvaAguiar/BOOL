import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;
import java.io.IOException;
import java.io.FileWriter;

public class BoolCompiler {

	public static void main(String[] args) {
		String arquivo_a_ler = "programa.bool";
		String arquivo_a_escrever = "programa.boolc";

		if(args.length>0){
			arquivo_a_ler = args[0];
			arquivo_a_escrever = args[1];
		}
		compilar(arquivo_a_ler,arquivo_a_escrever);
	}

	public static void compilar(String arquivo_a_ler,String arquivo_a_escrever){
		String codigo = "";
		try {
			File myObj = new File(arquivo_a_ler);
			Scanner myReader = new Scanner(myObj);
			while (myReader.hasNextLine()) {
				String data = myReader.nextLine();
				codigo += data+"\n";
			}
			myReader.close();

			Compilador compilador = new Compilador();
			String codigo_compilado = compilador.compilar(codigo);
		

			abre_arquivo(arquivo_a_escrever);
			escreve_arquivo(arquivo_a_escrever, codigo_compilado);

		} catch (FileNotFoundException e) {
			System.out.println("Um erro ocorreu.");
			e.printStackTrace();
		}
	}

	public static void abre_arquivo(String arquivo){
			try {
				File myObj = new File(arquivo);
				if (myObj.createNewFile()) {
					System.out.println("Arquivo criado: " + myObj.getName());
				} else {
					System.out.println("Arquivo já existente: "+ myObj.getName());
				}
			} catch (IOException e) {
				System.out.println("Um erro ocorreu.");
				e.printStackTrace();
			}
	}

	public static void escreve_arquivo(String arquivo, String codigo){
		try {
			FileWriter myWriter = new FileWriter(arquivo);
			myWriter.write(codigo);
			myWriter.close();
			System.out.println("Compilado com sucesso.");
		} catch (IOException e) {
			System.out.println("Um erro ocorreu.");
			e.printStackTrace();
		}
	}
}
