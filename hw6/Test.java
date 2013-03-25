import opennlp.maxent.*;
import opennlp.maxent.io.*;
import opennlp.model.EventStream;
import java.io.*;
import java.util.HashMap;
import java.util.regex.Pattern;
import java.util.regex.Matcher;
import java.util.LinkedList;

public class Test {
  public static void main(String[] args) throws IOException {
    String allOutcomes = "I[0.9163]  O[0.0826]  B[0.0010]";
    Pattern probPatt = Pattern.compile("I\\[([0-1]*\\.[0-9]+)\\]  O\\[([0-1]*\\.[0-9]+)\\]  B\\[([0-1]*\\.[0-9]+)\\]");
    Matcher matcher = probPatt.matcher(allOutcomes);
    //System.out.println(matcher.matches());
    Test test = new Test();
    test.tagWithModel1();
  }

  private void tagWithModel1() throws IOException {
    GISModel m =
        (GISModel) new SuffixSensitiveGISModelReader(new File("model1")).getModel();
 
    FileInputStream fis = new FileInputStream("tmp");
    DataInputStream dis = new DataInputStream(fis);
    BufferedReader br = new BufferedReader(new InputStreamReader(dis));
    PrintWriter out =
        new PrintWriter(new BufferedWriter(new FileWriter("tmpRes")));

    String line;
    while ((line = br.readLine()) != null) {
      if (!line.isEmpty()) {
        String[] strs = line.split(" ");
        String[] features = new String[2];
        features[0] = "current=" + strs[0];
        features[1] = "pos=" + strs[1];
        String tag = m.getBestOutcome(m.eval(features));
        double[] probs = m.eval(features);
        for (int i = 0; i < probs.length; ++i) {
          System.out.print(probs[i] + " ");
        }
        System.out.println();
        System.out.println(m.getAllOutcomes(m.eval(features)));
        out.println(strs[0] + " " + strs[1] + " " + tag);
      } else {
        out.println();
      }
    }
    out.flush();
  }
}
