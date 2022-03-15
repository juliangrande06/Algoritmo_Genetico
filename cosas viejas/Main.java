
import java.io.IOException;
import java.lang.reflect.Array;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Random;


public class Main {
	
    //Variables a considerar
    static int long_sec = 4;                        //Longitud de Secuencia
    static int cant_parametros = 5;                 //< carga/descarga , bat_act , bat_obj , estado_CPU , estado_pant >
    
    static Integer estado_CPU[] = {0,30,50,75,100}; //Estado del CPU del dispositivo
    static Integer estado_pant[] = {0, 1};          //Estado de la pantalla
    
    static int cant_gen = 3;                       //Cantidad de Generaciones
    static int cant_pob = 10;                        //Cantidad de individuos de la poblacion
    static int cant_ind_torneo = 2;                   //Cantidad de individuos que compiten en un torneo
    static double prob_cruce = 1.0;                     //Probabilidad de Cruce
    static int tam_bloque = long_sec*cant_parametros; //Tamano del bloque para el Cruce Uniforme
    static double prob_cruce_uniforme = 0.5;          //Probabilidad de Cruce Uniforme
    static double prob_mutacion = 0.4;                //Probabilidad de Mutacion
    static int cant_st = 2;                         //Variable de seleccion para Steady-State

    static ArrayList<ArrayList<Double>> poblacion = new ArrayList<>(); //Poblacion de todas las soluciones
    static List<Integer> pob_padres = new ArrayList<>();               //Contenedor de los ID de los individuos elegidos en la seleccion de padres
    static ArrayList<ArrayList<Double>> pob_hijos = new ArrayList<>(); //Poblacion de todas las soluciones hijas

    //Datos de entrada
    static int cant_mobil;          //Cantidad de dispositivos
    static String modelo[];         //Modelo de dispositivo
    static Integer nivel_bat_act[]; //Nivel bateria actual
    static Integer nivel_bat_obj[]; //Nivel bateria objetivo
    static List<Double> target = new ArrayList<>();

    //Dato de salida
    static StringBuilder salida = new StringBuilder();

    public static void crearSolucion(ArrayList<Double> solucion, double bat_act, double bat_obj){        
        solucion.add(0.0);    // se usa para expresar que la tarea es de carga
        solucion.add(bat_act);
        solucion.add(bat_obj);
        solucion.add((double)estado_CPU[(int)(estado_CPU.length*Math.random())]);
        solucion.add((double)estado_pant[(int)(estado_pant.length*Math.random())]);
    }

    public static void inicializacionAleatoria(){
        ArrayList<Double> solucion;
        int nivel_bat_intermedio,nivel_bat_intermedio_2;

        //System.out.println("\n   Inicializacion Aleatoria");
        for(int j=0; j<cant_pob; j++){
            solucion= new ArrayList<>();

            for(int i=0; i<cant_mobil; i++){
                if(long_sec == 1){
                    crearSolucion(solucion, (double)nivel_bat_act[i], (double)nivel_bat_obj[i]);
                }
                else if(secuencia_mobil[i] == 2){
                    if(nivel_bat_act[i] < nivel_bat_obj[i]){  //Tarea de carga
                        if(nivel_bat_act[i]+2 != nivel_bat_obj[i]) //Mas de un valor intermedio
                            nivel_bat_intermedio= ((int)(Math.random()*(nivel_bat_obj[i] - nivel_bat_act[i] -1)) + nivel_bat_act[i]+1);
                        else
                            nivel_bat_intermedio= nivel_bat_act[i]+1;
                        
                        crearSolucion(solucion, 0.0, (double)nivel_bat_act[i], nivel_bat_intermedio);
                        crearSolucion(solucion, 0.0, nivel_bat_intermedio, (double)nivel_bat_obj[i]);
                    }
                    else{ //Tarea de descarga
                        if(nivel_bat_act[i] != nivel_bat_obj[i]+2) //Mas de un valor intermedio
                            nivel_bat_intermedio= ((int)(Math.random()*(nivel_bat_act[i] - nivel_bat_obj[i] -1)) + nivel_bat_obj[i]+1);
                        else
                            nivel_bat_intermedio= nivel_bat_act[i]-1;
                        
                        crearSolucion(solucion, 1.0, (double)nivel_bat_act[i], nivel_bat_intermedio);
                        crearSolucion(solucion, 1.0, nivel_bat_intermedio, (double)nivel_bat_obj[i]);
                    }
                }
                else{

                    if(nivel_bat_act[i]+1 != nivel_bat_obj[i])
                        nivel_bat_intermedio= ((int)(Math.random()*(nivel_bat_obj[i] - nivel_bat_act[i] -1)) + nivel_bat_act[i]+1);
                    else
                        nivel_bat_intermedio= ((int)(Math.random()*2) + nivel_bat_act[i]);
                    
                    crearSolucion(solucion, (double)nivel_bat_act[i], (double)nivel_bat_intermedio);


                    for(int k=2; k<long_sec; k++){
                        if(nivel_bat_intermedio+1 != nivel_bat_obj[i])
                            nivel_bat_intermedio_2= ((int)(Math.random()*(nivel_bat_obj[i] - nivel_bat_intermedio -1)) + nivel_bat_intermedio+1);
                        else
                            nivel_bat_intermedio_2= ((int)Math.random() + nivel_bat_intermedio);
                        
                        crearSolucion(solucion, (double)nivel_bat_intermedio, (double)nivel_bat_intermedio_2);
                        nivel_bat_intermedio= nivel_bat_intermedio_2;
                    }
                    
                    crearSolucion(solucion, (double)nivel_bat_intermedio, (double)nivel_bat_obj[i]);
                }
            }
            poblacion.add(solucion);
        }
    }

    public static void setTarget(){
        double target_inter= -1.0;

        for(int i=0; i<cant_mobil; i++){
            switch(modelo[i]){
                case "motorola_moto_g6":
                    target_inter= Profiles.model1_ScreenOff[nivel_bat_obj[i]][0] - Profiles.model1_ScreenOff[nivel_bat_act[i]][0];
                    break;
                case "samsung_SM_A305G":
                    target_inter= Profiles.model2_ScreenOff[nivel_bat_obj[i]][0] - Profiles.model2_ScreenOff[nivel_bat_act[i]][0];
                    break;
                case "Xiaomi_Mi_A2_Lite":
                    target_inter= Profiles.model3_ScreenOff[nivel_bat_obj[i]][0] - Profiles.model3_ScreenOff[nivel_bat_act[i]][0];
                    break;
                case "Xiaomi_Redmi_Note_7":
                    target_inter= Profiles.model4_ScreenOff[nivel_bat_obj[i]][0] - Profiles.model4_ScreenOff[nivel_bat_act[i]][0];
                    break;
                default:
                    System.out.println("Modelo no encontrado");
                    break;
            }
            target.add(target_inter);
        }
    }

    public static void fitness(ArrayList<ArrayList<Double>> pob){
        //System.out.println("\n   Funcion de Fitness");
        int suma, bloque, fila1, fila2, columna;
        double sumatoria;
        ArrayList<Integer> sumados= new ArrayList<>();
        double max_target= Collections.max(target);
        ArrayList<Double> solucion;

        for(int i=0; i<cant_pob; i++){ //Por cada individuo de la poblacion
            solucion= pob.get(i);
            sumatoria= 0.0;
            
            for(int j=0; j<cant_mobil; j++){ //Por cada movil (cantidad de bloques)
                suma= 0;

                for(int k=0; k<long_sec; k++){ //Dentro del bloque
                    bloque= k*cant_parametros + j*tam_bloque;
                    fila1= solucion.get(bloque+2).intValue();
                    fila2= solucion.get(bloque+1).intValue();
                    columna= Arrays.asList(estado_CPU).indexOf(solucion.get(bloque+3).intValue());

                    //System.out.println(fila1+" ... "+fila2+" ... "+columna);
                    if(solucion.get(bloque+4) == 0.0){
                        
                        switch(modelo[j]){
                            case "motorola_moto_g6":
                                suma += Profiles.model1_ScreenOff[fila1][columna] - Profiles.model1_ScreenOff[fila2][columna];
                                break;
                            case "samsung_SM_A305G":
                                suma += Profiles.model2_ScreenOff[fila1][columna] - Profiles.model2_ScreenOff[fila2][columna];
                                break;
                            case "Xiaomi_Mi_A2_Lite":
                                suma += Profiles.model3_ScreenOff[fila1][columna] - Profiles.model3_ScreenOff[fila2][columna];
                                break;
                            case "Xiaomi_Redmi_Note_7":
                                suma += Profiles.model4_ScreenOff[fila1][columna] - Profiles.model4_ScreenOff[fila2][columna];
                                break;
                        }
                    }
                    else{
                        switch(modelo[j]){
                            case "motorola_moto_g6":
                                suma += Profiles.model1_ScreenOn[fila1][columna] - Profiles.model1_ScreenOn[fila2][columna];
                                break;
                            case "samsung_SM_A305G":
                                suma += Profiles.model2_ScreenOn[fila1][columna] - Profiles.model2_ScreenOn[fila2][columna];
                                break;
                            case "Xiaomi_Mi_A2_Lite":
                                suma += Profiles.model3_ScreenOn[fila1][columna] - Profiles.model3_ScreenOn[fila2][columna];
                                break;
                            case "Xiaomi_Redmi_Note_7":
                                suma += Profiles.model4_ScreenOn[fila1][columna] - Profiles.model4_ScreenOn[fila2][columna];
                                break;
                        }
                    }
                }
                sumados.add(suma);
                sumatoria += Math.pow(suma - max_target, 2);
            }
            //System.out.println("Sumatoria: "+sumatoria);
            for(int j=0; j<sumados.size(); j++){
                solucion.add((double)sumados.get(j));
            }
            solucion.add(Math.sqrt(sumatoria/cant_mobil));
            sumados.clear();
        }
    }

    public static int torneo(){
        List<Integer> lista_id= new ArrayList<>();
        double sol_intermedia;                        //Solucion intermedia
        double mejor_sol= Double.POSITIVE_INFINITY;   //Mejor solucion
        int indice_int_lista;                         //Indice intermedio de lista
        int indice_int_pob;                           //Indice intermedio de poblacion
        int indice_mejor_sol= -1;                     //Indice del mejor individuo del torneo
        int indice_valor_fitness= poblacion.get(0).size()-1; //Posicion donde se almacena el valor de Fitness del individuo
        
        //System.out.println("\n  Torneo");
        for(int i=0; i<cant_pob; i++){ //Creo una lista con todos los ID de los individuos de la poblacion
            lista_id.add(i);
        }

        for(int i=0; i<cant_ind_torneo; i++){
            indice_int_lista= (int)(Math.random()*lista_id.size());
            indice_int_pob= lista_id.get(indice_int_lista);
            sol_intermedia= poblacion.get(indice_int_pob).get(indice_valor_fitness);

            if(sol_intermedia < mejor_sol){
                indice_mejor_sol= indice_int_pob;
                mejor_sol= sol_intermedia;
            }
            //mostrarIndividuo(poblacion.get(indice_int_pob));
            lista_id.remove(indice_int_lista);
        }
        lista_id= null; //Para que el Garbage Collector marque el espacio de memoria como libre
        //System.out.println("\n*** Solucion Elegida: "+ indice_mejor_sol);
        //mostrarIndividuo(poblacion.get(indice_mejor_sol));

        return indice_mejor_sol;
    }

    public static void seleccionPadres(){
        //System.out.println("\n   Seleccion de Padres");
        int sol_parcial=-1;
        for(int i=0; i<cant_pob; i++){
            sol_parcial= torneo();
            if(i%2 == 0){
                pob_padres.add(sol_parcial);
            }
            else{
                if(pob_padres.get(i-1) != sol_parcial){
                    pob_padres.add(sol_parcial);
                }
                else{
                    i--;
                }
            }
        }
    }

    public static void cruceUniforme(){
        //System.out.println("\n   Cruce Uniforme");
        double prob_aleatoria;
        ArrayList<Double> hijo1;
        ArrayList<Double> hijo2;

        for(int i=0; i<cant_pob; i= i+2){
            hijo1= new ArrayList<>(poblacion.get(pob_padres.get(i)));
            //System.out.println("");
            //mostrarIndividuo(hijo1);
            hijo2= new ArrayList<>(poblacion.get(pob_padres.get(i+1)));
            //mostrarIndividuo(hijo2);
            prob_aleatoria= Math.random();

            if(prob_aleatoria < prob_cruce){
                for(int j=0; j<cant_mobil; j++){
                    prob_aleatoria= Math.random();
                    
                    if(prob_aleatoria < prob_cruce_uniforme){
                        int indice_intermedio= j*tam_bloque;

                        for(int k=0; k<tam_bloque; k++){
                            double valor_intermedio= hijo1.get(indice_intermedio);
                            hijo1.set(indice_intermedio, hijo2.get(indice_intermedio));
                            hijo2.set(indice_intermedio, valor_intermedio);
                            indice_intermedio++;
                        }
                    }
                }
                //System.out.println("\n### Hubo Cruce Uniforme");
                //mostrarIndividuo(hijo1);
                //mostrarIndividuo(hijo2);
            }
            //Limpio valores de Fitnes acarreado por los padres
            int indice_inter= cant_mobil*tam_bloque;
            int max_indice= hijo1.size()-indice_inter;
            for(int j=0; j<max_indice; j++){
                hijo1.remove(indice_inter);
                hijo2.remove(indice_inter);
            }

            pob_hijos.add(hijo1);
            pob_hijos.add(hijo2);
        }
    }

    public static void cruceUniformeExtremo(){
        //System.out.println("\n   Cruce Uniforme Extremo");
        double prob_aleatoria;
        ArrayList<Double> hijo1;
        ArrayList<Double> hijo2;
        List<Integer> lista_pos_cruce= new ArrayList<>(); //Lista de posiciones del bloque que voy a cruzar
        lista_pos_cruce.add(2);
        lista_pos_cruce.add(3);
        lista_pos_cruce.add(4);
        
        if(long_sec > 1){
            for(int i=1; i<long_sec; i++){
                lista_pos_cruce.add(cant_parametros*i + 3);
                lista_pos_cruce.add(cant_parametros*i + 4);
            }
        }

        for(int i=0; i<cant_pob; i= i+2){
            hijo1= new ArrayList<>(poblacion.get(pob_padres.get(i)));
            //System.out.println("");
            //mostrarIndividuo(hijo1);
            hijo2= new ArrayList<>(poblacion.get(pob_padres.get(i+1)));
            //mostrarIndividuo(hijo2);
            prob_aleatoria= Math.random();

            if(prob_aleatoria < prob_cruce){
                for(int j=0; j<cant_mobil; j++){ //Por cada mobil
                    for(int k=0; k<lista_pos_cruce.size(); k++){ //Por cada valor del bloque que quiero cruzar
                        prob_aleatoria= Math.random();

                        if(prob_aleatoria < prob_cruce_uniforme){ //Uso la misma probabilidad que la de Cruce Uniforme
                            int pos_bloque_inter= lista_pos_cruce.get(k);
                            double valor_intermedio;
                            int cont_frecuencia= 1;

                            if(long_sec == 1){
                                if(lista_pos_cruce.get(k) != 2){
                                    valor_intermedio= hijo1.get(pos_bloque_inter);
                                    hijo1.set(pos_bloque_inter, hijo2.get(pos_bloque_inter));
                                    hijo2.set(pos_bloque_inter, valor_intermedio);
                                }
                            }
                            else{
                                int corte= 1;
                                if(lista_pos_cruce.get(k) == 2){ //Si es 2 significa que tengo que cambiar los valores de carga de bateria
                                    if(long_sec == 2)
                                        corte= 2;
                                    else if(long_sec == 3)
                                        corte= 4;
                                    else
                                        corte= (long_sec-3)*2 + 4;
                                }

                                int rta= 1;
                                for(cont_frecuencia=0; cont_frecuencia<corte; cont_frecuencia++){ //optimizarlo separando los casos
                                    valor_intermedio= hijo1.get(pos_bloque_inter);
                                    hijo1.set(pos_bloque_inter, hijo2.get(pos_bloque_inter));
                                    hijo2.set(pos_bloque_inter, valor_intermedio);
                                
                                    if(rta == 1){
                                        rta= 0;
                                        pos_bloque_inter +=  4;
                                    }
                                    else{
                                        rta= 1;
                                        pos_bloque_inter +=  1;
                                    }
                                }
                            }
                        }
                    }
                }
                //System.out.println("\n### Hubo Cruce Uniforme Extremo");
                //mostrarIndividuo(hijo1);
                //mostrarIndividuo(hijo2);
            }
            //Limpio valores de Fitnes acarreado por los padres
            int indice_inter= cant_mobil*tam_bloque;
            int max_indice= hijo1.size()-indice_inter;
            for(int j=0; j<max_indice; j++){
                hijo1.remove(indice_inter);
                hijo2.remove(indice_inter);
            }

            pob_hijos.add(hijo1);
            pob_hijos.add(hijo2);
        }
    }

    public static void mutacion(){
        //System.out.println("\n   Mutacion");
        double prob_aleatoria;
        List<Integer> lista_pos_mutar= new ArrayList<>(); //Lista de posiciones del bloque que voy a mutar
        lista_pos_mutar.add(2);
        lista_pos_mutar.add(3);
        lista_pos_mutar.add(4);
        
        if(long_sec > 1){
            for(int i=1; i<long_sec; i++){
                lista_pos_mutar.add(cant_parametros*i + 3);
                lista_pos_mutar.add(cant_parametros*i + 4);
            }
        }

        List<Integer> niveles_bateria= new ArrayList<>();
        for(int i=0; i<=100; i++){ //Inicializo variable con niveles de bateria
            niveles_bateria.add(i);
        }

        for(int i=0; i<pob_hijos.size(); i++){ //Por cada hijo
            for(int j=0; j<cant_mobil; j++){ //Por cada mobil
                for(int k=0; k<lista_pos_mutar.size(); k++){ //Por cada valor del bloque que quiero mutar
                    prob_aleatoria= Math.random();

                    if(prob_aleatoria < prob_mutacion){
                        int pos_bloque_inter= lista_pos_mutar.get(k);
                        int valor_intermedio= -1;
                        List<Integer> aux;

                        switch(pos_bloque_inter % 10){
                            case 2:
                                if(long_sec > 1){
                                    int bat_inicial= pob_hijos.get(i).get(pos_bloque_inter-1).intValue();
                                    int bat_inter;
                                    
                                    for(int ciclo=0; ciclo<long_sec-1; ciclo++){
                                        aux= new ArrayList<>(niveles_bateria);
                                        bat_inter= pob_hijos.get(i).get(pos_bloque_inter).intValue();
                                        
                                        if(bat_inicial+2 < nivel_bat_obj[j]){
                                            if(bat_inicial < bat_inter){
                                                aux.remove(bat_inter);
                                                //System.out.println("bat_I: "+bat_inicial+" ... bat_int: "+bat_inter);
                                                aux= aux.subList(bat_inicial+1, nivel_bat_obj[j]-2);
                                            }
                                            else{
                                                aux= aux.subList(bat_inicial+1, nivel_bat_obj[j]-1);
                                            }

                                            if(aux.size() != 0)
                                                valor_intermedio= aux.get((int)(Math.random()*aux.size()));
                                            else
                                                valor_intermedio= bat_inicial+1;
                                        }
                                        else{
                                            if(bat_inter == nivel_bat_act[j])    
                                                valor_intermedio=nivel_bat_obj[j];
                                            else
                                                if(bat_inter == nivel_bat_obj[j])	
                                                    valor_intermedio=nivel_bat_act[j];
                                        }

                                        pob_hijos.get(i).set(pos_bloque_inter, (double)valor_intermedio);
                                        pos_bloque_inter += 4;
                                        pob_hijos.get(i).set(pos_bloque_inter, (double)valor_intermedio);
                                        pos_bloque_inter += 1;
                                        bat_inicial= bat_inter;
                                    }
                                }
                                break;
                            
                            case 3:case 8: 
                                aux= new ArrayList<>(Arrays.asList(estado_CPU));
                                aux.remove(aux.indexOf(pob_hijos.get(i).get(pos_bloque_inter).intValue()));

                                valor_intermedio= aux.get((int)(aux.size()*Math.random()));
                                pob_hijos.get(i).set(pos_bloque_inter, (double)valor_intermedio);
                                break;

                            case 4:case 9:
                                valor_intermedio= 0;
                                if(pob_hijos.get(i).get(pos_bloque_inter) == 0.0){
                                    valor_intermedio= 1;
                                }
                                pob_hijos.get(i).set(pos_bloque_inter, (double)valor_intermedio);
                                break;

                            default: System.out.println("Error de caso");
                        }
                    }
                }
            }
        }
    }

    public static void quickSort(ArrayList<ArrayList<Double>> pob, int start, int end){
        int q;

        if(start<end){
            q = partition(pob, start, end);
            quickSort(pob, start, q);
            quickSort(pob, q+1, end);
        }
    }

    static int nextIntInRange(int min, int max, Random rng) {
        if (min > max) {
           throw new IllegalArgumentException("Rango no valido [" + min + ", " + max + "].");
        }
        int diff = max - min;
        if (diff >= 0 && diff != Integer.MAX_VALUE) {
           return (min + rng.nextInt(diff + 1));
        }
        int i;
        do {
           i = rng.nextInt();
        } while (i < min || i > max);
        return i;
     }

    static int partition(ArrayList<ArrayList<Double>> pob, int start, int end){
        int init = start;
        int length = end;
        int indice_valor_fitness= pob.get(0).size()-1; //Posicion donde se almacena el valor de Fitness del individuo
        ArrayList<Double> pivot;
        ArrayList<Double> temp;
        
        Random r = new Random();
        int pivotIndex = nextIntInRange(start,end,r);
        pivot = pob.get(pivotIndex);
                
        while(true){
            while(pob.get(length).get(indice_valor_fitness) > pivot.get(indice_valor_fitness) && length > start){
                length--;
            }
            
            while(pob.get(init).get(indice_valor_fitness) < pivot.get(indice_valor_fitness) && init < end){
                init++;
            }
            
            if(init<length){
                temp = pob.get(init);
                pob.set(init, pob.get(length));
                pob.set(length, temp);
                length--;
                init++;
            }
            else{
                return length;
            }
        } 
    }

    public static double stady_State(){
        int pos=cant_pob-1;
        //System.out.println("   Seleccion de Sobrevivientes");
        quickSort(poblacion, 0, pos);
/*
        for(int i=0; i<poblacion.size(); i++){
            System.out.print(poblacion.get(i).get(poblacion.get(i).size()-1)+", ");
        }
*/
        quickSort(pob_hijos, 0, pos);

        double mejor_padre= poblacion.get(0).get(poblacion.get(0).size()-1);
        double mejor_hijo= pob_hijos.get(0).get(pob_hijos.get(0).size()-1);

        if(mejor_hijo < mejor_padre){
            mejor_padre= mejor_hijo;
        }

        for(int i=0; i<cant_st; i++){
            poblacion.set(pos, pob_hijos.get(i));
            pos--;
        }

        pob_hijos.clear();
        pob_padres.clear();

        return mejor_padre;
    }


    public static void mostrarPoblacion(ArrayList<ArrayList<Double>> pob){
        System.out.println("** Mostrando la Poblacion");
        for(int j=0; j<cant_pob; j++){
            System.out.println("* Solucion "+j);
            for(int i=0; i<pob.get(j).size(); i++){
                if(i+1 < pob.get(j).size())
            	    System.out.print(pob.get(j).get(i).intValue() + ",");
                else
                    System.out.print(pob.get(j).get(i).intValue());
            }
            System.out.println("");
        }
    }

    public static void mostrarIndividuo(ArrayList<Double> solucion){
        System.out.println("** Mostrando un Individuo");

        for(int i=0; i<solucion.size(); i++){
            if(i+1 < solucion.size())
                System.out.print(solucion.get(i).intValue() + ",");
            else
                System.out.print(solucion.get(i));
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


    public static void inicioArchivo(){
        salida.append("Cantidad de Mobiles: "+cant_mobil+" \n");
        salida.append("Longitud de Secuencia: "+long_sec+" \n");
        salida.append("Cantidad de Generaciones: "+cant_gen+" \n");
        salida.append("Cantidad de individuos de la Poblacion: "+cant_pob+" \n");
        salida.append("Probabilidad de Cruce: "+prob_cruce+" \n");
        salida.append("Probabilidad de Cruce inter-bloque: "+prob_cruce_uniforme+" \n");
        salida.append("Probabilidad de Mutacion: "+prob_mutacion+" \n");
        salida.append("Cantidad de individuos por Torneo: "+cant_ind_torneo+" \n");
        salida.append("Cantidad de individuos para Steady-State: "+cant_st+" \n");
    }
 
    public static void guardarMejorSolucion(ArrayList<Double> solucion){
        salida.append("\n\nMejor Solucion:\n");
        NumberFormat nf= new DecimalFormat("##.###");

        for(int i=0; i<solucion.size(); i++){
            if(i+1 < solucion.size())
                salida.append(solucion.get(i).intValue() + ",");
            else
                salida.append("\nFitness: "+nf.format(solucion.get(i)));
        }
    }

    public static void guardarTarget(double bestFit){
        salida.append("\n\nLista de Targets:\n");
        NumberFormat nf= new DecimalFormat("##.###"); 
        double max, min;

        for(int i=0; i<target.size(); i++){
            if(i+1 < target.size())
                salida.append(nf.format(target.get(i)) + ", ");
            else
                salida.append(nf.format(target.get(i)));
        }
        max= Collections.max(target);
        min= Collections.min(target);

        salida.append("    Target Critico: "+nf.format(max)+ "  Target Minimo: "+nf.format(min));
    }

    public static void finArchivo(long startTime, double bestFit){
        long endTime = System.currentTimeMillis();
        quickSort(poblacion, 0, cant_pob-1);
        guardarMejorSolucion(poblacion.get(0));
        guardarTarget(bestFit);

        salida.append("\n\nTiempo aproximado de ejecucion " + (endTime - startTime) + " milisegundos -> "+((endTime - startTime)/1000) + " segundos");
    }

    public static void running(){
        NumberFormat nf= new DecimalFormat("##.###");
        double bestFit= 0.0;

        inicioArchivo();
        long startTime = System.currentTimeMillis();
        
        inicializacionAleatoria();
        fitness(poblacion);
        
        //mostrarPoblacion(poblacion);

        salida.append("\n< Generacion: Mejor Fitness >");
        for(int i=0; i<cant_gen; i++){
            seleccionPadres();
            
            //mostrarListaPadres();
            
            //cruceUniforme();
            cruceUniformeExtremo();
            
            //mostrarPoblacion(pob_hijos);
            
            mutacion();
            
            //mostrarPoblacion(pob_hijos);
            
            fitness(pob_hijos);
            bestFit= stady_State();
            salida.append("\n"+i+": "+nf.format(bestFit));
        }

        finArchivo(startTime, bestFit);
        try {
            System.out.println("ENTRE");
            Archivo.write(salida.toString());
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }
    public static void main(String[] args) throws IOException {
        try {
            Profiles.filterProfiles();
            Archivo.readMoviles();
            setTarget();
        } catch (Exception e) {
            e.printStackTrace();
        }
        
        for (int i = 0; i < cant_mobil; i++){
        	System.out.println(i + " " + modelo[i] + " " + nivel_bat_act[i] + " " + nivel_bat_obj[i]);
        }

        for(int i=0; i<10; i++){
            running();
            poblacion.clear();
        }
    }
}