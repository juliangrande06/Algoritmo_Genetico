import java.io.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

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
}