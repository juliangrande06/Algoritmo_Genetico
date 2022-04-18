
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
    
    public static void writeMejorSolucion(ArrayList<Double> solucion) throws IOException {
    	DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy_MM_dd_HH_mm_ss");
    	File file = new File("./bestSolution_"+dtf.format(LocalDateTime.now())+".txt");
    	BufferedWriter writer = new BufferedWriter(new FileWriter(file));
    	
    	StringBuffer linea= new StringBuffer();
    	int cant_moviles = Main.cant_mobil;
		int valor_posicion, posicion, id_movil;
		posicion= id_movil= 0;
    	
    	for(int i=0; i<cant_moviles; i++){ //Por cada movil	
    		id_movil++;
    		linea.append("Movil: " + id_movil + "\n\n");
    		    		
    		for(int j=1; j<=Main.secuencia_mobil[i]; j++){ //Por cada tarea del movil
				for(int k=0; k<Main.cant_parametros; k++){ //Por cada parametro de la tarea
					valor_posicion = solucion.get(posicion).intValue();
					
					switch(k){
						case 0:
							if(valor_posicion == 0)
								linea.append("Tarea: "+j+"\nTipo de Tarea: Carga\n");
							else
								linea.append("Tarea: "+j+"\nTipo de Tarea: Descarga\n");
							break;
						case 1:
							linea.append("Nivel de bateria origen: "+valor_posicion+"\n"); 
							break;
						case 2:
							linea.append("Nivel de bateria destino: "+valor_posicion+"\n"); 
							break;
						case 3:
							linea.append("Nivel de CPU (%): "+valor_posicion+"\n"); 
							break;
						case 4:
							String estado = "Apagada";
							if(valor_posicion == 1)
								estado = "Prendida";
							linea.append("Estado de pantalla: " + estado +"\n\n"); 
							break;
					}
					posicion++;
				}
    		}	
			linea.append("---------------------------------\n");
    	}

    	linea.append("\nTiempo de los moviles (milisegundos) : ");
    	int valor;
    	
    	for(int i=0; i<cant_moviles; i++){
    		valor= solucion.get(posicion).intValue();
    		linea.append(valor+" ");
    		posicion++;
    	}
    	linea.append("\n\n");
    	valor = solucion.get(posicion).intValue();
    	
    	linea.append("Fitness (Error cuadratico promedio respecto del maximo valor optimo): "+valor +"\n\n");
    	linea.append("Tiempos optimos de los moviles (milisegundos): ");
    	    	   	    	
    	for(int i=0; i<Main.target.size(); i++){
    		valor = Main.target.get(i).intValue();
    		linea.append(valor + " ");   		
    	}
    	
    	writer.write(linea.toString());
		writer.close();
		valor= 0;
    }
    
    public static void readMoviles() throws Exception {
    	ArrayList<String> modelo = new ArrayList<>();
    	ArrayList<Integer> nivel_bat_act = new ArrayList<>();
    	ArrayList<Integer> nivel_bat_obj = new ArrayList<>();
    	int cant_mobil = 0;
    	
    	File moviles = new File("./moviles.txt");
	    BufferedReader movilesRead = new BufferedReader(new FileReader(moviles));
    	
	    String linea;
	    
	    while ((linea = movilesRead.readLine()) != null)
	    {	            
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