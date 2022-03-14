import java.io.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;

public class Archivo {
    public static void read(String arch){}
    
    public static void write(String codigo) throws IOException {
		DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy_MM_dd_HH_mm_ss");

		File file = new File("./evolutionReport_"+dtf.format(LocalDateTime.now())+".txt");
		BufferedWriter writer = null;
		try {
			writer = new BufferedWriter(new FileWriter(file));
			writer.write(codigo);
		} finally {
			if (writer != null) writer.close();
		}	
	}
    
    
	public static void writeMejorSolucion(ArrayList<Double> solucion) throws Exception {
		File file = new File("./bestSolution.txt");
		BufferedWriter writer = new BufferedWriter(new FileWriter(file));

		String linea;
		int cant = 0;

		// guardar la soluci�n en el archivo de una manera legible

	}

	public static void readMoviles() throws Exception {
		ArrayList<String> modelo = new ArrayList<>();
		ArrayList<Integer> nivel_bat_act = new ArrayList<>();
		ArrayList<Integer> nivel_bat_obj = new ArrayList<>();
		int cant_mobil = 0;

		File moviles = new File("./moviles.txt");
		BufferedReader movilesRead = new BufferedReader(new FileReader(moviles));

		String linea;

		while ((linea = movilesRead.readLine()) != null) {
			String[] lineaTemp = linea.split(" ");

			modelo.add(lineaTemp[1]);
			nivel_bat_act.add(Integer.parseInt(lineaTemp[2]));
			nivel_bat_obj.add(Integer.parseInt(lineaTemp[3]));
			cant_mobil++;
		}

		Main.cant_mobil = cant_mobil;
		Main.modelo = new String[cant_mobil];
		Main.nivel_bat_act = new Integer[cant_mobil];
		Main.nivel_bat_obj = new Integer[cant_mobil];

		for (int i = 0; i < cant_mobil; i++) {
			Main.modelo[i] = modelo.get(i);
			Main.nivel_bat_act[i] = nivel_bat_act.get(i);
			Main.nivel_bat_obj[i] = nivel_bat_obj.get(i);
		}
		movilesRead.close();
	}
}