import java.io.*;

public class Profiles {
	// modelo motorola_moto_g6
	public static int[][] model1_ScreenOn = new int[101][5];
	public static int[][] model1_ScreenOff = new int[101][5];
	
	// modelo samsung_SM_A305G
    public static int[][] model2_ScreenOn = new int[101][5];
    public static int[][] model2_ScreenOff = new int[101][5];
 
    // modelo Xiaomi_Mi_A2_Lite
	public static int[][] model3_ScreenOn = new int[101][5];
	public static int[][] model3_ScreenOff = new int[101][5];
	
    // modelo Xiaomi_Redmi_Note_7
	public static int[][] model4_ScreenOn = new int[101][5];
	public static int[][] model4_ScreenOff = new int[101][5];
	
	// crear metodo new
	
	public static int get_CPU_index(String origen) {
		int index = -1;

		if (origen.contains("battery0"))
			index = 0;
		else if (origen.contains("battery30"))
			index = 1;
		else if (origen.contains("battery50"))
			index = 2;
		else if (origen.contains("battery75"))
			index = 3;
		else if (origen.contains("battery100"))
			index = 4;

		return index;
	}
	
	public static void set_milisegundos(String origen, int nivel_bat, int milisegundos) {
		
		if (origen.contains("motorola_moto_g6")) {
			if (origen.contains("screenOn"))
				model1_ScreenOn[nivel_bat][get_CPU_index(origen)] = milisegundos;
			else
			if (origen.contains("screenOff"))
				model1_ScreenOff[nivel_bat][get_CPU_index(origen)] = milisegundos;
		}
        else
        if (origen.contains("samsung_SM_A305G")) {
     	    if (origen.contains("screenOn"))
				model2_ScreenOn[nivel_bat][get_CPU_index(origen)] = milisegundos;
			else
			if (origen.contains("screenOff"))
				model2_ScreenOff[nivel_bat][get_CPU_index(origen)] = milisegundos;	
        }
        else	
        if (origen.contains("Xiaomi_Mi_A2_Lite")) {
     	    if (origen.contains("screenOn"))
				model3_ScreenOn[nivel_bat][get_CPU_index(origen)] = milisegundos;
			else
			if (origen.contains("screenOff"))
				model3_ScreenOff[nivel_bat][get_CPU_index(origen)] = milisegundos;	
        }
        else
        if (origen.contains("Xiaomi_Redmi_Note_7")) {
     	    if (origen.contains("screenOn"))
				model4_ScreenOn[nivel_bat][get_CPU_index(origen)] = milisegundos;
			else
			if (origen.contains("screenOff"))
				model4_ScreenOff[nivel_bat][get_CPU_index(origen)] = milisegundos;
        }		
	}
	 	
	public static void filter(String origen, String destino) throws Exception {
		File profiles = new File(origen);
		BufferedReader profilesRead = new BufferedReader(new FileReader(profiles));
		File file = new File(destino);

		if (!file.exists()) {
			file.createNewFile();
		}

		FileWriter fw = new FileWriter(file);
		BufferedWriter bw = new BufferedWriter(fw);

		String strng;
		String marca_comienzo = "ADD_NODE";
		String marca_final = "LEFT_NODE";
		int valor_anterior = -1;

		while ((strng = profilesRead.readLine()) != null) {
			String[] linea = strng.split(";");

			if (!linea[0].equals(marca_comienzo) && !linea[0].equals(marca_final)) {
				int nivel_bat = Integer.parseInt(linea[3]);
				int nivel_bat_real = nivel_bat / 100000;

				if (nivel_bat_real != valor_anterior) {
					String contenido;

					if (nivel_bat_real < 100)
						contenido = nivel_bat_real + " " + linea[1] + "\n";
					else
						contenido = nivel_bat_real + " " + linea[1];

					bw.write(contenido);

					// carga los milisegundos para nivel_bat_real en la matriz correspondiente
					int milisegundos = Integer.parseInt(linea[1]);
					set_milisegundos(origen, nivel_bat_real, milisegundos);

					valor_anterior = nivel_bat_real;
				}
			}
		}

		profilesRead.close();
		bw.close();
	}

	public static void mostrar_matrices() throws Exception {
		File file = new File("./matrices.txt");

		if (!file.exists()) {
			file.createNewFile();
		}

		FileWriter fw = new FileWriter(file);
		BufferedWriter bw = new BufferedWriter(fw);

		bw.write("Xiaomi_Redmi_Note_7, screen On");
		bw.write("\n");

		for (int k = 0; k < 101; k++) {
			String contenido;
			contenido = Integer.toString(model4_ScreenOn[k][0]) + " " + Integer.toString(model4_ScreenOn[k][1]) + " "
					+ Integer.toString(model4_ScreenOn[k][2]) + " " + Integer.toString(model4_ScreenOn[k][3]) + " "
					+ Integer.toString(model4_ScreenOn[k][4]);

			bw.write(contenido);
			bw.write("\n");
		}

		bw.write("Xiaomi_Redmi_Note_7, screen Off");
		bw.write("\n");

		for (int k = 0; k < 101; k++) {
			String contenido;
			contenido = Integer.toString(model4_ScreenOff[k][0]) + " " + Integer.toString(model4_ScreenOff[k][1]) + " "
					+ Integer.toString(model4_ScreenOff[k][2]) + " " + Integer.toString(model4_ScreenOff[k][3]) + " "
					+ Integer.toString(model4_ScreenOff[k][4]);

			bw.write(contenido);
			bw.write("\n");
		}

		bw.close();
		//System.out.println("cargo matrices");
	}
		
	public static void filterProfiles() throws Exception {
		File profiles = new File("./profilesFiltered.txt");
		BufferedReader profilesRead = new BufferedReader(new FileReader(profiles));
		File file = new File("./seguimiento.txt");

		if (!file.exists()) {
			file.createNewFile();
		}

		FileWriter fw = new FileWriter(file);
		BufferedWriter bw = new BufferedWriter(fw);
		String strng;

		while ((strng = profilesRead.readLine()) != null) {
			String[] linea = strng.split(" ");
			filter(linea[0], linea[1]);

			bw.write(linea[0]);
			bw.write("\n");
		}

		profilesRead.close();
		bw.close();
		mostrar_matrices();
	}
}

