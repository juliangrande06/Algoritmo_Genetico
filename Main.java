import java.io.IOException;
import java.util.ArrayList;


public class Main {
	
    //Variables a considerar
    static int long_sec = 2;                        //Longitud de Secuencia
    
    static Integer estado_CPU[] = {0,30,50,75,100}; //Estado del CPU del dispositivo
    static Integer estado_pant[] = {0, 1};          //Estado de la pantalla
    
    static int cant_pob = 10;                       //Cantidad de individuos de la poblacion
    static ArrayList<ArrayList<Integer>> poblacion = new ArrayList<>(); //Poblacion de todas las soluciones

    //Datos de entrada
    static int cant_mobil = 3;                                //Cantidad de dispositivos
    static String modelo[] = {"Xiaomi", "Motorola", "Samsung"}; //new String[cant_mobil];          //Modelo de dispositivo
    static Integer nivel_bat_act[] = {20, 30, 40}; //new Integer[cant_mobil]; //Nivel bateria actual
    static Integer nivel_bat_obj[] = {85, 75, 90}; //new Integer[cant_mobil]; //Nivel bateria objetivo

    public static void inicializacionAleatoria(){
        ArrayList<Integer> solucion;  // es necesario este new? porque se hace debajo

        System.out.println("*** Inicializacion Aleatoria");
        for(int j=0; j<cant_pob; j++){
            solucion= new ArrayList<>();

            for(int i=0; i<cant_mobil; i++){
            	solucion.add(0);    // se usa para expresar que la tarea es de carga
                solucion.add(nivel_bat_act[i]);

                int nivel_bat_intermedio= ((int)(Math.random()*(nivel_bat_obj[i] - nivel_bat_act[i] -1)) + nivel_bat_act[i]+1);
                
                solucion.add(nivel_bat_intermedio);

                solucion.add(estado_CPU[(int)(estado_CPU.length*Math.random())]);
                solucion.add(estado_pant[(int)(estado_pant.length*Math.random())]);

                if(long_sec == 2){
                	solucion.add(0);    // se usa para expresar que la tarea es de carga
                    solucion.add(nivel_bat_intermedio);
                    solucion.add(nivel_bat_obj[i]);
                    solucion.add(estado_CPU[(int)(estado_CPU.length*Math.random())]);
                    solucion.add(estado_pant[(int)(estado_pant.length*Math.random())]); //habia entendido que no se cambiaba el estado de la pantalla
                }
            }
            poblacion.add(solucion);
        }
    }

    public static void mostrarPoblacion(ArrayList<ArrayList<Integer>> poblacion){
        System.out.println("** Mostrando la Poblacion");
        for(int j=0; j<cant_pob; j++){
            System.out.println("* Solucion "+j);
            for(int i=0; i<poblacion.get(j).size(); i++){
            	System.out.print(poblacion.get(j).get(i) + ",");
            }
            System.out.println("");
        }
    }

    public static void main(String[] args) throws IOException {
        inicializacionAleatoria();
        mostrarPoblacion(poblacion);
    }
}
