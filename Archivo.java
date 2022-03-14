
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
    	
    	String linea;
    	int cant_moviles = Main.cant_mobil;
    	
    	for (int j=0; j < cant_moviles; j++) {
    		
    		int id_movil = j + 1;
    		linea = "M�vil: " + id_movil + "\n" + "\n";
    		writer.write(linea);
    		
    		int comienzo_bloque = j*10;   // considero que cada bloque tiene 10 componentes
    		    		
    		for (int p=0; p<10; p++) {
    			
    			int posicion = p + comienzo_bloque;
    			int valor_posicion;
    			
    			if(p==0) {
    				
    				valor_posicion = solucion.get(posicion).intValue();
    				if (valor_posicion == 0)
    				     linea = "Tarea: 1" + "\n" + "Tipo de Tarea: Carga" + "\n";
    				else
    					linea = "Tarea: 1" + "\n" + "Tipo de Tarea: Descarga" + "\n";
    				writer.write(linea);    								
    			}
    			else
    			if(p==5) {
    				
    				valor_posicion = solucion.get(posicion).intValue();
    				if (valor_posicion == 0)
    				     linea = "Tarea: 2" + "\n" + "Tipo de Tarea: Carga" + "\n";
    				else
    					linea = "Tarea: 2" + "\n" + "Tipo de Tarea: Descarga" + "\n";
    				writer.write(linea);    				
    			}
    			else
    			if(p==1 || p==6) {
    				valor_posicion = solucion.get(posicion).intValue();
    				linea = "Nivel de bater�a origen: " + valor_posicion + "\n";
    				writer.write(linea);    				
    			}
    			else
        		if(p==2 || p==7) {
        			valor_posicion = solucion.get(posicion).intValue();
        			linea = "Nivel de bater�a destino: " + valor_posicion + "\n";
        			writer.write(linea);
        		}	
        		else
        		if(p==3 || p==8) {
        			valor_posicion = solucion.get(posicion).intValue();
        			linea = "Nivel de CPU (%): " + valor_posicion + "\n";
        			writer.write(linea);        			
        		}
        		else
        		if(p==4 || p==9) {
        			valor_posicion = solucion.get(posicion).intValue();
        			String estado = "Apagada";
        			if(valor_posicion == 1)
        				estado = "Prendida";        			      			
        			linea = "Estado de pantalla: " + estado + "\n" + "\n";
        			writer.write(linea);        			
        		}
    			
    		} // for del bloque
    		    		
    	} // for movil
    		
    	int comienzo_tiempos = cant_moviles*10;
    	
    	linea = "Tiempo de los m�vles (milisegundos) :" + "\n" + "\n";
    	writer.write(linea);
    	linea = "";
    	int pos, valor, k;
    	
    	for (k = 0; k < cant_moviles; k++) {
    		pos = comienzo_tiempos + k;
    		valor = solucion.get(pos).intValue();
    		linea = linea + valor + " ";	    	
    	}
    	
    	linea = linea + "\n" + "\n";  
    	writer.write(linea);
    	
    	pos = comienzo_tiempos + k;
    	valor = solucion.get(pos).intValue();
    	
    	linea = "Fitness (Error cuadr�tico promedio respecto del m�ximo valor �ptimo): " + valor + "\n" + "\n";
    	writer.write(linea);
    	
    	linea = "Tiempos �ptimos de los m�viles (milisegundos) : " + "\n" + "\n";
    	    	   	    	
    	for(int i=0; i<Main.target.size(); i++){
    		valor = Main.target.get(i).intValue();
    		linea = linea + valor + " ";   		
    	}
    	
    	writer.write(linea);
    	
    	writer.close(); 	
    	
    	
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