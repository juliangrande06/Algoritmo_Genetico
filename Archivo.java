import java.io.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class Archivo {
    private static Archivo archivo;
	private Archivo() {};
	
	public static Archivo getInstance() {
		if (archivo == null)
			archivo= new Archivo();
		return archivo;
	}

    public void read(String arch){}

    public void write(String codigo, String name) throws IOException {
		DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy_MM_dd_HH_mm_ss");

		File file = new File(name+"_"+dtf.format(LocalDateTime.now())+".txt");
		BufferedWriter writer = null;
		try {
			writer = new BufferedWriter(new FileWriter(file));
			writer.write(codigo);
		} finally {
			if (writer != null) writer.close();
		}	
	}
}