import java.io.FileInputStream;
import java.io.DataInputStream;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.IOException;

public class Vertibi {
  private double[][] transProbs;

  private initialize(String filename) throws IOException {
    FileInputStream fis = new FileInputStream(filename);
    DataInputStream dis = new DataInputStream(fis);
    BufferedReader bf = new BufferedReader(new InputStreamReader(dis));
    String line;
    boolean isStart = true;
    while ((line = bf.readLine()) != null) {
      if (isStart) {
        String[] fields = line.split();
        if (fields[1].equals(start))
      }
    }
    
  }
}
