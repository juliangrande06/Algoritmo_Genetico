import java.io.IOException;
import java.util.ArrayList;
import java.util.List;


public class Main {
	
    //Variables a considerar
    static int long_sec = 2;                        //Longitud de Secuencia
    
    static Integer estado_CPU[] = {0,30,50,75,100}; //Estado del CPU del dispositivo
    static Integer estado_pant[] = {0, 1};          //Estado de la pantalla
    
    static int cant_pob = 10;                       //Cantidad de individuos de la poblacion
    static double cant_ind_torneo = 5;              //Cantidad de individuos que compiten en un torneo
    static ArrayList<ArrayList<Integer>> poblacion = new ArrayList<>(); //Poblacion de todas las soluciones
    static List<Integer> pob_padres = new ArrayList<>(); //Contenedor de los ID de los individuos elegidos en la seleccion de padres

    //Datos de entrada
    static int cant_mobil = 3;                                //Cantidad de dispositivos
    static String modelo[] = {"Xiaomi", "Motorola", "Samsung"}; //new String[cant_mobil];          //Modelo de dispositivo
    static Integer nivel_bat_act[] = {20, 30, 40}; //new Integer[cant_mobil]; //Nivel bateria actual
    static Integer nivel_bat_obj[] = {85, 75, 90}; //new Integer[cant_mobil]; //Nivel bateria objetivo

    public static void inicializacionAleatoria(){
        ArrayList<Integer> solucion;

        //System.out.println("\n    Inicializacion Aleatoria");
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
                    solucion.add(estado_pant[(int)(estado_pant.length*Math.random())]);
                }
            }
            poblacion.add(solucion);
        }
    }

    public static void fitness(){ //Hice una funcion fitness para tener con que comparar
        //System.out.println("\n   Funcion de Fitness");
        int suma= 0;
        ArrayList<Integer> solucion;

        for(int i=0; i<cant_pob; i++){
            solucion= poblacion.get(i);

            for(int j=0; j<solucion.size(); j++){
                suma= suma+solucion.get(j);
            }

            solucion.add(suma);
        }
    }

    public static int torneo(){
        List<Integer> lista= new ArrayList<>();
        int sol_intermedia;                           //Solucion intermedia
        int mejor_sol= (int)Double.POSITIVE_INFINITY; //Mejor solucion
        int indice_int_lista;                         //Indice intermedio de lista
        int indice_int_pob;                           //Indice intermedio de poblacion
        int indice_mejor_sol= -1;                     //Indice del mejor individuo del torneo
        int indice_valor_fitness= poblacion.get(0).size()-1; //Posicion donde se almacena el valor de Fitness del individuo
        
        //System.out.println("\n  Torneo");
        for(int i=0; i<cant_pob; i++){ //Creo una lista con todos los ID de los individuos de la poblacion
            lista.add(i);
        }

        for(int i=0; i<cant_ind_torneo; i++){
            indice_int_lista= (int)(Math.random()*lista.size());
            indice_int_pob= lista.get(indice_int_lista);
            sol_intermedia= poblacion.get(indice_int_pob).get(indice_valor_fitness);

            if(sol_intermedia < mejor_sol){
                indice_mejor_sol= indice_int_pob;
                mejor_sol= sol_intermedia;
            }
            //mostrarIndividuo(poblacion.get(indice_int_pob));
            lista.remove(indice_int_lista);
        }
        lista= null; //Para que el Garbage Collector marque el espacio de memoria como libre
        //System.out.println("\n*** Solucion Elegida: "+ indice_mejor_sol);
        //mostrarIndividuo(poblacion.get(indice_mejor_sol));

        return indice_mejor_sol;
    }

    public static void seleccionPadres(){
        //System.out.println("\n   Seleccion de Padres");
        for(int i= 0; i<cant_pob; i++){
            pob_padres.add(torneo());
        }
    }

    public static void mostrarPoblacion(){
        System.out.println("** Mostrando la Poblacion");
        for(int j=0; j<cant_pob; j++){
            System.out.println("* Solucion "+j);
            for(int i=0; i<poblacion.get(j).size(); i++){
            	System.out.print(poblacion.get(j).get(i) + ",");
            }
            System.out.println("");
        }
    }

    public static void mostrarIndividuo(ArrayList<Integer> solucion){
        System.out.println("** Mostrando un Individuo");

        for(int i=0; i<solucion.size(); i++){
            System.out.print(solucion.get(i) + ",");
        }
        System.out.println("");
    }

    public static void mostrarListaPadres(){
        System.out.println("** Mostrando Lista de Padres");
        for(int i=0; i<pob_padres.size(); i++){
            System.out.print(pob_padres.get(i) + ",");
        }
        System.out.println("");
    }

    public static void main(String[] args) throws IOException {
        inicializacionAleatoria();
        //mostrarPoblacion();
        fitness();
        seleccionPadres();
        //mostrarListaPadres();
    }
}
