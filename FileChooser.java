import java.io.File;
import javax.swing.JFileChooser;

public class FileChooser {
    File selectedFile;
    JFileChooser chooser;

    public FileChooser(){
        chooser = new JFileChooser();
    }
    public String run(){
        chooser.showOpenDialog(null);
        selectedFile = chooser.getSelectedFile();
        System.out.println(selectedFile);
        return selectedFile.getAbsolutePath();
    }
}