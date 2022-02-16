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
        ArrayList<Integer> solucion= new ArrayList<>();

        System.out.println("*** Inicializacion Aleatoria");
        for(int j=0; j<cant_pob; j++){
            solucion= new ArrayList<>();

            for(int i=0; i<cant_mobil; i++){
                solucion.add(i);
                solucion.add(nivel_bat_act[i]);

                int aux_bat= ((int)(Math.random()*(nivel_bat_obj[i] - nivel_bat_act[i] -1)) + nivel_bat_act[i]+1);
                solucion.add(aux_bat);

                solucion.add(estado_CPU[(int)(estado_CPU.length*Math.random())]);
                solucion.add(estado_pant[(int)(estado_pant.length*Math.random())]);

                if(long_sec == 2){
                    solucion.add(i);
                    solucion.add(aux_bat);
                    solucion.add(nivel_bat_obj[i]);
                    solucion.add(estado_CPU[(int)(estado_CPU.length*Math.random())]);
                    solucion.add(solucion.get(i+5));
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
                System.out.println(i+": "+poblacion.get(j).get(i));
            }
        }
    }

    public static void main(String[] args) throws IOException {
        inicializacionAleatoria();
        mostrarPoblacion(poblacion);
    }
}