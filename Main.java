
import java.io.IOException;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Random;
import java.util.stream.IntStream;


public class Main {
	
    //Variables a considerar
    static int long_sec = 2;                        //Longitud de Secuencia
    static int cant_parametros = 5;                 //< carga/descarga , bat_act , bat_obj , estado_CPU , estado_pant >
    
    static Integer estado_CPU[] = {0,30,50,75,100}; //Estado del CPU del dispositivo
    static Integer estado_pant[] = {0, 1};          //Estado de la pantalla
    
    static int cant_gen = 5000;                       //Cantidad de Generaciones
    static int cant_pob = 200;                        //Cantidad de individuos de la poblacion
    static int cant_ind_torneo = 10;                   //Cantidad de individuos que compiten en un torneo
    static double prob_cruce = 1;                     //Probabilidad de Cruce
    static double prob_cruce_uniforme = 0.5;          //Probabilidad de Cruce Uniforme
    static double prob_mutacion = 0.1;                //Probabilidad de Mutacion
    static int cant_st = 50;                         //Variable de seleccion para Steady-State

    static ArrayList<ArrayList<Double>> poblacion = new ArrayList<>(); //Poblacion de todas las soluciones
    static List<Integer> pob_padres = new ArrayList<>();               //Contenedor de los ID de los individuos elegidos en la seleccion de padres
    static ArrayList<ArrayList<Double>> pob_hijos = new ArrayList<>(); //Poblacion de todas las soluciones hijas

    //Datos de entrada
    static int cant_mobil;            //Cantidad de dispositivos
    static String modelo[];           //Modelo de dispositivo
    static Integer nivel_bat_act[];   //Nivel bateria actual
    static Integer nivel_bat_obj[];   //Nivel bateria objetivo
    static int[] secuencia_mobil; //Nivel maximo de secuencias de carga de cada movil
    static List<Double> target = new ArrayList<>();              
    
    //Dato de salida
    static StringBuilder salida = new StringBuilder();


    public static void setSecuencia(){
        int aux=-1;
        secuencia_mobil= new int[cant_mobil];

        for(int i=0; i<cant_mobil; i++){
            aux= Math.abs(nivel_bat_obj[i]-nivel_bat_act[i]);
            
            if(aux < long_sec || aux == 0){
                secuencia_mobil[i]= aux;
            }
            else{
                secuencia_mobil[i]= long_sec;
            }
        }
    }

    public static void crearSolucion(ArrayList<Double> solucion, double tarea, double bat_act, double bat_obj){        
        solucion.add(tarea);  //0.0 -> carga ; 1.0 -> descarga
        solucion.add(bat_act);
        solucion.add(bat_obj);
        solucion.add((double)estado_CPU[(int)(estado_CPU.length*Math.random())]);
        solucion.add((double)estado_pant[(int)(estado_pant.length*Math.random())]);
    }

    public static void cargaDescarga(ArrayList<Double> solucion, int indice){
        int bat_inter_1, bat_inter_2, pos;
        
        List<Integer> niveles_bateria= new ArrayList<>();
        for(int i=0; i<=100; i++){ //Inicializo variable con niveles de bateria
            niveles_bateria.add(i);
        }
        
        List<Integer> nivel_bat_aux= new ArrayList<>(niveles_bateria);
        nivel_bat_aux= nivel_bat_aux.subList(nivel_bat_act[indice]+1, 101);
        //System.out.println("Lista aux: "+nivel_bat_aux);
        pos= (int)(Math.random()*nivel_bat_aux.size());

        bat_inter_1= nivel_bat_act[indice];
        bat_inter_2= nivel_bat_aux.get(pos);
        
        crearSolucion(solucion, 0.0, (double)bat_inter_1, (double)bat_inter_2); //Carga
        crearSolucion(solucion, 1.0, (double)bat_inter_2, (double)bat_inter_1); //Descarga
        secuencia_mobil[indice]= 2;
    }

    public static void descargaCarga(ArrayList<Double> solucion, int indice){
        int bat_inter_1,bat_inter_2,pos;
        
        List<Integer> niveles_bateria= new ArrayList<>();
        for(int i=0; i<=100; i++){ //Inicializo variable con niveles de bateria
            niveles_bateria.add(i);
        }

        List<Integer> nivel_bat_aux= new ArrayList<>(niveles_bateria);
        nivel_bat_aux= nivel_bat_aux.subList(0, nivel_bat_act[indice]);

        pos= (int)(Math.random()*nivel_bat_aux.size());

        bat_inter_1= nivel_bat_act[indice];
        bat_inter_2= nivel_bat_aux.get(pos);
        
        crearSolucion(solucion, 1.0, (double)bat_inter_1, (double)bat_inter_2); //Descarga
        crearSolucion(solucion, 0.0, (double)bat_inter_2, (double)bat_inter_1); //Carga
        secuencia_mobil[indice]= 2;
    }

    public static void inicializacionAleatoria(){
        ArrayList<Double> solucion;
        int bat_inter_1,bat_inter_2,pos;
        double prob_aleatoria;
        List<Integer> nivel_bat_intermedio;
        List<Integer> nivel_bat_aux;
        List<Integer> niveles_bateria= new ArrayList<>();
        
        for(int i=0; i<=100; i++){ //Inicializo variable con niveles de bateria
            niveles_bateria.add(i);
        }

        //System.out.println("\n   Inicializacion Aleatoria");
        for(int j=0; j<cant_pob; j++){
            solucion= new ArrayList<>();

            for(int i=0; i<cant_mobil; i++){
                if(secuencia_mobil[i] == 0){
                    if(nivel_bat_act[i] < 25){
                        cargaDescarga(solucion, i);
                    }
                    else if(nivel_bat_act[i] > 75)
                        descargaCarga(solucion, i);
                    else{
                        prob_aleatoria= Math.random();
                        if(prob_aleatoria < 0.5)                     
                            descargaCarga(solucion, i);
                        else
                            cargaDescarga(solucion, i);
                    }
                }
                else if(secuencia_mobil[i] == 1){
                    if(nivel_bat_act[i] < nivel_bat_obj[i]){                        
                        crearSolucion(solucion, 0.0, (double)nivel_bat_act[i], (double)nivel_bat_obj[i]);
                    }
                    else{
                        crearSolucion(solucion, 1.0, (double)nivel_bat_act[i], (double)nivel_bat_obj[i]);
                    }
                }
                else{
                    nivel_bat_aux= new ArrayList<>(niveles_bateria);
                    nivel_bat_intermedio= new ArrayList<>();

                    if(nivel_bat_act[i] < nivel_bat_obj[i]){  //Tarea de carga
                        nivel_bat_aux= nivel_bat_aux.subList(nivel_bat_act[i]+1, nivel_bat_obj[i]);
                        //System.out.println("Lista aux: "+nivel_bat_aux);
                        
                        for(int k=0; k<secuencia_mobil[i]-1; k++){
                            pos= (int)(Math.random()*nivel_bat_aux.size());
                            nivel_bat_intermedio.add(nivel_bat_aux.get(pos));
                            nivel_bat_aux.remove(pos);
                        }
                        Collections.sort(nivel_bat_intermedio);
                        //System.out.println("Lista aux: "+nivel_bat_intermedio);
                        bat_inter_1= nivel_bat_act[i];
                        
                        for(int k=0; k<secuencia_mobil[i]-1; k++){
                            bat_inter_2= nivel_bat_intermedio.get(k);
                            crearSolucion(solucion, 0.0, (double)bat_inter_1, (double)bat_inter_2);
                            bat_inter_1= bat_inter_2;
                        }
                        crearSolucion(solucion, 0.0, (double)bat_inter_1, (double)nivel_bat_obj[i]);
                    }
                    else{ //Tarea de descarga
                        nivel_bat_aux= nivel_bat_aux.subList(nivel_bat_obj[i]+1, nivel_bat_act[i]);

                        for(int k=0; k<secuencia_mobil[i]-1; k++){
                            pos= (int)(Math.random()*nivel_bat_aux.size());
                            nivel_bat_intermedio.add(nivel_bat_aux.get(pos));
                            nivel_bat_aux.remove(pos);
                        }
                        nivel_bat_intermedio.sort(Comparator.reverseOrder());
                        //System.out.println("Lista aux reversa: "+nivel_bat_intermedio);
                        bat_inter_1= nivel_bat_act[i];
                        
                        for(int k=0; k<secuencia_mobil[i]-1; k++){
                            bat_inter_2= nivel_bat_intermedio.get(k);
                            crearSolucion(solucion, 1.0, (double)bat_inter_1, (double)bat_inter_2);
                            bat_inter_1= bat_inter_2;
                        }
                        crearSolucion(solucion, 1.0, (double)bat_inter_1, (double)nivel_bat_obj[i]);
                    }
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
                	if(nivel_bat_act[i] < nivel_bat_obj[i])
                		target_inter= Profiles.model1_ScreenOff[nivel_bat_obj[i]][0] - Profiles.model1_ScreenOff[nivel_bat_act[i]][0];
                	else	
                		target_inter= Profiles.model1_ScreenOn_Discharging[nivel_bat_obj[i]][4] - Profiles.model1_ScreenOn_Discharging[nivel_bat_act[i]][4];
                    break;
                case "samsung_SM_A305G":
                	if(nivel_bat_act[i] < nivel_bat_obj[i])
                		target_inter= Profiles.model2_ScreenOff[nivel_bat_obj[i]][0] - Profiles.model2_ScreenOff[nivel_bat_act[i]][0];
                	else
                        target_inter= Profiles.model2_ScreenOn_Discharging[nivel_bat_obj[i]][4] - Profiles.model2_ScreenOn_Discharging[nivel_bat_act[i]][4];
                    break;
                case "Xiaomi_Mi_A2_Lite":
                	if(nivel_bat_act[i] < nivel_bat_obj[i])
                		target_inter= Profiles.model3_ScreenOff[nivel_bat_obj[i]][0] - Profiles.model3_ScreenOff[nivel_bat_act[i]][0];
                	else                	
                        target_inter= Profiles.model3_ScreenOn_Discharging[nivel_bat_obj[i]][4] - Profiles.model3_ScreenOn_Discharging[nivel_bat_act[i]][4];
                    break;
                case "Xiaomi_Redmi_Note_7":
                	if(nivel_bat_act[i] < nivel_bat_obj[i])
                		target_inter= Profiles.model4_ScreenOff[nivel_bat_obj[i]][0] - Profiles.model4_ScreenOff[nivel_bat_act[i]][0];
                	else                	
                        target_inter= Profiles.model4_ScreenOn_Discharging[nivel_bat_obj[i]][4] - Profiles.model4_ScreenOn_Discharging[nivel_bat_act[i]][4];
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
        int suma, bloque, bloque_ant, fila1, fila2, columna;
        double sumatoria;
        ArrayList<Integer> sumados= new ArrayList<>();
        double max_target= Collections.max(target);
        ArrayList<Double> solucion;

        for(int i=0; i<cant_pob; i++){ //Por cada individuo de la poblacion
            solucion= pob.get(i);
            sumatoria= 0.0;
            bloque_ant= 0;
            
            for(int j=0; j<cant_mobil; j++){ //Por cada movil (cantidad de bloques)
                suma= 0;

                for(int k=0; k<secuencia_mobil[j]; k++){ //Dentro del bloque
                    bloque= k*cant_parametros + bloque_ant;
                    fila1= solucion.get(bloque+2).intValue();
                    fila2= solucion.get(bloque+1).intValue();
                    columna= Arrays.asList(estado_CPU).indexOf(solucion.get(bloque+3).intValue());

                    if ( (solucion.get(bloque) == 0.0) && (solucion.get(bloque+4) == 0.0) )  {
                        
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
                    else
                    if ( (solucion.get(bloque) == 0.0) && (solucion.get(bloque+4) == 1.0) ){
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
                    else
                    if ( (solucion.get(bloque) == 1.0) && (solucion.get(bloque+4) == 0.0) )  {
                            
                         switch(modelo[j]){
                             case "motorola_moto_g6":
                                 suma += Profiles.model1_ScreenOff_Discharging[fila1][columna] - Profiles.model1_ScreenOff_Discharging[fila2][columna];
                                 break;
                             case "samsung_SM_A305G":
                                 suma += Profiles.model2_ScreenOff_Discharging[fila1][columna] - Profiles.model2_ScreenOff_Discharging[fila2][columna];
                                 break;
                             case "Xiaomi_Mi_A2_Lite":
                                 suma += Profiles.model3_ScreenOff_Discharging[fila1][columna] - Profiles.model3_ScreenOff_Discharging[fila2][columna];
                                 break;
                             case "Xiaomi_Redmi_Note_7":
                                 suma += Profiles.model4_ScreenOff_Discharging[fila1][columna] - Profiles.model4_ScreenOff_Discharging[fila2][columna];
                                 break;
                        }
                    }	
                    else
                    if ( (solucion.get(bloque) == 1.0) && (solucion.get(bloque+4) == 1.0) ){
                         switch(modelo[j]){
                             case "motorola_moto_g6":
                                 suma += Profiles.model1_ScreenOn_Discharging[fila1][columna] - Profiles.model1_ScreenOn_Discharging[fila2][columna];
                                 break;
                             case "samsung_SM_A305G":
                                 suma += Profiles.model2_ScreenOn_Discharging[fila1][columna] - Profiles.model2_ScreenOn_Discharging[fila2][columna];
                                 break;
                             case "Xiaomi_Mi_A2_Lite":
                                 suma += Profiles.model3_ScreenOn_Discharging[fila1][columna] - Profiles.model3_ScreenOn_Discharging[fila2][columna];
                                 break;
                             case "Xiaomi_Redmi_Note_7":
                                 suma += Profiles.model4_ScreenOn_Discharging[fila1][columna] - Profiles.model4_ScreenOn_Discharging[fila2][columna];
                                 break;
                         }
                    }
                }  // dentro del bloque de un m�vil, miro los tiempos de sus tareas, y se acumulan en suma
                bloque_ant += secuencia_mobil[j]*cant_parametros;
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
        for(int i= 0; i<cant_pob; i++){
            pob_padres.add(torneo());
        }
    }

    public static void efectuarCruce(ArrayList<Double> hijo1, ArrayList<Double> hijo2, int pos){
        double valor_intermedio= hijo1.get(pos);
        hijo1.set(pos, hijo2.get(pos));
        hijo2.set(pos, valor_intermedio);
    }
    
    public static void cruceUniformeSimple(){
        //System.out.println("\n   Cruce Uniforme Simple");
        int inicio_bloque, pos, tam_bloque;
        double prob_aleatoria;
        ArrayList<Double> hijo1;
        ArrayList<Double> hijo2;

        for(int i=0; i<cant_pob; i= i+2){   //Por cada Individuo
            hijo1= new ArrayList<>(poblacion.get(pob_padres.get(i)));
            hijo2= new ArrayList<>(poblacion.get(pob_padres.get(i+1)));
            prob_aleatoria= Math.random();
            inicio_bloque= 0;

            if(prob_aleatoria < prob_cruce){
                for(int j=0; j<cant_mobil; j++){    //Por cada movil
                    prob_aleatoria= Math.random();
                    tam_bloque= secuencia_mobil[j]*cant_parametros;
                    
                    if(prob_aleatoria < prob_cruce_uniforme){
                        for(int k=0; k<tam_bloque; k++){
                            pos= inicio_bloque + k;
                            efectuarCruce(hijo1, hijo2, pos);
                        }
                    }
                    inicio_bloque += tam_bloque;
                }
            }
            //Limpio valores de Fitness acarreado por los padres
            int max_indice= hijo1.size()-1;
            for(int j=0; j<cant_mobil+1; j++){
                hijo1.remove(max_indice);
                hijo2.remove(max_indice);
                max_indice--;
            }

            pob_hijos.add(hijo1);
            pob_hijos.add(hijo2);
        }
        pob_padres.clear();
    }
    
    public static void cruceUniformeExtremo(){
        //System.out.println("\n   Cruce Uniforme Extremo");
        int inicio_bloque, bloque_anterior, pos;
        double prob_aleatoria;
        int[] lista_pos_cruce= {2,3,4}; //Lista de posiciones del bloque que voy a cruzar sin contar posicion bateria
        ArrayList<Double> hijo1;
        ArrayList<Double> hijo2;

        for(int i=0; i<cant_pob; i= i+2){   //Por cada Individuo
            hijo1= new ArrayList<>(poblacion.get(pob_padres.get(i)));
            hijo2= new ArrayList<>(poblacion.get(pob_padres.get(i+1)));
            bloque_anterior= 0;
            prob_aleatoria= Math.random();

            if(prob_aleatoria < prob_cruce){    //Chequeo si debo cruzar
                prob_aleatoria= Math.random();
                
                for(int j=0; j<cant_mobil; j++){    //Por cada movil
                    prob_aleatoria= Math.random();
                    int bandera= -1; //-1: no hay decision - 0: no se cruza - 1: se cruza

                    for(int k=0; k<secuencia_mobil[j]; k++){    //Por cada tarea de carga/descarga del movil
                        inicio_bloque= k*cant_parametros + bloque_anterior;

                        for(int l=0; l<lista_pos_cruce.length; l++){    //Por cada elemento a cruzar
                            prob_aleatoria= Math.random();
                            
                            switch(lista_pos_cruce[l]){
                                case 2:
                                    if(bandera == -1){
                                        if(prob_aleatoria < prob_cruce_uniforme){
                                            bandera= 1;
                                        }
                                        else{
                                            bandera= 0;
                                        }
                                    }

                                    if(bandera == 1){
                                        pos= inicio_bloque+1;
                                        for(int ll=0;ll<2; ll++){
                                            efectuarCruce(hijo1, hijo2, pos);
                                            pos++;
                                        }
                                    }
                                    break;
                                case 3:
                                    if(prob_aleatoria < prob_cruce_uniforme){
                                        pos= inicio_bloque+3;
                                        efectuarCruce(hijo1, hijo2, pos);
                                    }
                                    break;
                                case 4:
                                    if(prob_aleatoria < prob_cruce_uniforme){
                                        pos= inicio_bloque+4;
                                        efectuarCruce(hijo1, hijo2, pos);
                                    }
                                    break;
                            }
                        }
                    }
                    bloque_anterior += secuencia_mobil[j]*cant_parametros;
                }
            }
            //Limpio valores de Fitness acarreado por los padres
            int max_indice= hijo1.size()-1;
            for(int j=0; j<cant_mobil+1; j++){
                hijo1.remove(max_indice);
                hijo2.remove(max_indice);
                max_indice--;
            }

            pob_hijos.add(hijo1);
            pob_hijos.add(hijo2);
        }
        pob_padres.clear();
    }

    public static void bateriaRandom(List<Integer> nivel_bat_aux, List<Integer> nivel_bat_intermedio, int indice){
        int pos;

        for(int i=0; i<secuencia_mobil[indice]-1; i++){
            pos= (int)(Math.random()*nivel_bat_aux.size());
            nivel_bat_intermedio.add(nivel_bat_aux.get(pos));
            nivel_bat_aux.remove(pos);
        }
    }

    public static void mutacion(){
        //System.out.println("\n   Mutacion");
        int inicio_bloque, bloque_anterior, pos;
        double prob_aleatoria, valor_intermedio;
        int[] lista_pos_mutar= {2,3,4}; //Lista de posiciones del bloque que voy a mutar
        List<Integer> nivel_bat_aux, nivel_bat_intermedio, cpu_aux, niveles_bateria= new ArrayList<>();
        
        for(int i=0; i<=100; i++){ //Inicializo variable con niveles de bateria
            niveles_bateria.add(i);
        }

        for(int i=0; i<pob_hijos.size(); i++){ //Por cada hijo
            inicio_bloque= bloque_anterior= 0;

            for(int j=0; j<cant_mobil; j++){ //Por cada movil
                nivel_bat_aux= new ArrayList<>(niveles_bateria);
                nivel_bat_intermedio= new ArrayList<>();
                prob_aleatoria= Math.random();
                int bandera= 0; //0: no se muta - 1: se muta

                if(prob_aleatoria < prob_mutacion && secuencia_mobil[j] > 1){
                    bandera= 1;

                    if(nivel_bat_act[j] < nivel_bat_obj[j]){  //Tarea de carga
                        nivel_bat_aux= nivel_bat_aux.subList(nivel_bat_act[j]+1, nivel_bat_obj[j]);
                        bateriaRandom(nivel_bat_aux, nivel_bat_intermedio, j);
                        Collections.sort(nivel_bat_intermedio);
                    }
                    else{ //Tarea de descarga
                        nivel_bat_aux= nivel_bat_aux.subList(nivel_bat_obj[j]+1, nivel_bat_act[j]);
                        bateriaRandom(nivel_bat_aux, nivel_bat_intermedio, j);
                        nivel_bat_intermedio.sort(Comparator.reverseOrder());
                    }
                }

                for(int k=0; k<secuencia_mobil[j]; k++){    //Por cada tarea de carga/descarga del movil
                    inicio_bloque= k*cant_parametros + bloque_anterior;

                    for(int l=0; l<lista_pos_mutar.length; l++){    //Por cada elemento a mutar
                        prob_aleatoria= Math.random();
                        
                        switch(lista_pos_mutar[l]){
                            case 2: //Mutar Bateria
                                if(bandera == 1){
                                    if(k == 0){ //Mutar solo posicion 2 de la tarea
                                        pos= inicio_bloque+2;
                                        valor_intermedio= (double)nivel_bat_intermedio.get(0);
                                        pob_hijos.get(i).set(pos, valor_intermedio);
                                        nivel_bat_intermedio.remove(0);
                                    }
                                    else if(k == secuencia_mobil[j]-1){ //Mutar solo posicion 1 de la tarea
                                        pos= inicio_bloque+1;
                                        pob_hijos.get(i).set(pos, pob_hijos.get(i).get(pos-cant_parametros+1));
                                    }
                                    else{ //Mutar posicion 2 y 1
                                        pos= inicio_bloque+1;
                                        pob_hijos.get(i).set(pos, pob_hijos.get(i).get(pos-cant_parametros+1));

                                        pos++;
                                        valor_intermedio= (double)nivel_bat_intermedio.get(0);
                                        pob_hijos.get(i).set(pos, valor_intermedio);
                                        nivel_bat_intermedio.remove(0);
                                    }
                                }
                                break;
                            case 3: //Mutar CPU
                                if(prob_aleatoria < prob_cruce_uniforme){
                                    pos= inicio_bloque+3;
                                    cpu_aux= new ArrayList<>(Arrays.asList(estado_CPU));
                                    cpu_aux.remove(cpu_aux.indexOf(pob_hijos.get(i).get(pos).intValue()));
                                    valor_intermedio= cpu_aux.get((int)(cpu_aux.size()*Math.random()));
                                    pob_hijos.get(i).set(pos, (double)valor_intermedio);
                                }
                                break;
                            case 4: //Mutar Pantalla
                                if(prob_aleatoria < prob_cruce_uniforme){
                                    pos= inicio_bloque+4;
                                    valor_intermedio= 0;
                                    if(pob_hijos.get(i).get(pos) == 0.0){
                                        valor_intermedio= 1;
                                    }
                                    pob_hijos.get(i).set(pos, (double)valor_intermedio);
                                }
                                break;
                        }
                    }
                }
                bloque_anterior += secuencia_mobil[j]*cant_parametros;
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
        int contador, largo= 0;
        int pos= 0;
        int[] long_bloque= new int[cant_mobil];
        for(int i=0; i<cant_mobil; i++){
            long_bloque[i]= secuencia_mobil[i]*cant_parametros;
            largo += long_bloque[i];
        }

        System.out.println("\n** Mostrando la Poblacion");
        for(int j=0; j<cant_pob; j++){
            contador= long_bloque[pos];
            System.out.println("* Solucion "+j);
            
            for(int i=0; i<pob.get(j).size(); i++){
                //System.out.print(pob.get(j).get(i).intValue() + ",");
                if(i+1 == largo){
                    System.out.print(pob.get(j).get(i).intValue() + ";    ");
                }
                else if(i < contador-1){
                    System.out.print(pob.get(j).get(i).intValue() + ",");
                }
                else if(i == contador-1){
                    System.out.print(+pob.get(j).get(i).intValue() + ";  ");
                    pos++;
                    contador += long_bloque[pos];
                }
                else{
                    System.out.print(pob.get(j).get(i).intValue() + ",");
                }
            }
            pos= 0;
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
        System.out.println("\n** Mostrando Lista de Padres");
        for(int i=0; i<pob_padres.size(); i++){
            System.out.print(pob_padres.get(i) + ",");
        }
        System.out.println("");
    }


    public static void inicioArchivo(){
        salida.append("Cantidad de Mobiles: "+cant_mobil+" \n");
        salida.append("Cantidad de Generaciones: "+cant_gen+" \n");
        salida.append("Longitud de Secuencia por Movil: ");
        for(int i=0; i<cant_mobil; i++){
            salida.append(secuencia_mobil[i]+",");
        }
        salida.append("\nCantidad de individuos de la Poblacion: "+cant_pob+" \n");
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

    public static void main(String[] args) throws IOException {
        NumberFormat nf= new DecimalFormat("##.###");
        double bestFit= 0.0;
        
        try {
            Profiles.filterProfiles();
            Profiles.filterProfilesDischarging();
            Archivo.readMoviles();
            setTarget();
        } catch (Exception e) {
            e.printStackTrace();
        }
/*
        for (int i = 0; i < cant_mobil; i++) 
        	System.out.println(i + " " + modelo[i] + " " + nivel_bat_act[i] + " " + nivel_bat_obj[i]);
*/
        setSecuencia();
        inicioArchivo();
        
        if(!IntStream.of(secuencia_mobil).anyMatch(x -> x > 0)){
            salida.append("\nTodos los moviles ya estan listos.");
        }
        else{
            long startTime = System.currentTimeMillis();
            
            inicializacionAleatoria();        
            fitness(poblacion);
            //mostrarPoblacion(poblacion);

            salida.append("\n< Generacion: Mejor Fitness >");
            for(int i=0; i<cant_gen; i++){
                seleccionPadres();
                //mostrarListaPadres();
                
                //cruceUniformeSimple();
                cruceUniformeExtremo();
                //mostrarPoblacion(pob_hijos);
                
                mutacion();
                //mostrarPoblacion(pob_hijos);
                
                fitness(pob_hijos);
                bestFit= stady_State();
                salida.append("\n"+i+": "+nf.format(bestFit));
            }

            finArchivo(startTime, bestFit);
            Archivo.writeMejorSolucion(poblacion.get(0));
        }
        Archivo.write(salida.toString());
    }
}

