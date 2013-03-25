import opennlp.maxent.*;
import opennlp.maxent.io.*;
import opennlp.model.EventStream;
import java.io.*;
import java.util.HashMap;
import java.util.regex.Pattern;
import java.util.regex.Matcher;
import java.util.LinkedList;

public class Viterbi {
  private LinkedList<LinkedList<Double>> probMatrix =
      new LinkedList<LinkedList<Double>>();
  private LinkedList<String> sentence = new LinkedList<String>();
  private static final HashMap<Integer, String> states =
      new HashMap<Integer, String>();
  PrintWriter out;

  public Viterbi() { 
    states.put(1, "I");
    states.put(2, "O");
    states.put(3, "B");
  }

  public static void main(String[] args) throws IOException {
    Viterbi v = new Viterbi();
    v.tagWithModel8();
  }

  private void tagWithModel8() throws IOException {
    GISModel m =
        (GISModel) new SuffixSensitiveGISModelReader(new File("model8")).getModel();
 
    FileInputStream fis = new FileInputStream("test.txt");
    DataInputStream dis = new DataInputStream(fis);
    BufferedReader br = new BufferedReader(new InputStreamReader(dis));
    out = new PrintWriter(new BufferedWriter(new FileWriter("resultVit")));
    String line;
    String[] prevLine = null;
    String[] features = null;
    while ((line = br.readLine()) != null) {
      if (!line.isEmpty()) { 
        String[] strs = line.split(" ");
        sentence.add(strs[0] + " " + strs[1]);
        if (prevLine != null) {
          // Tag the previous line.
          features[6] = "next=" + strs[0];
          features[7] = "nextPos=" + strs[1];
          features[8] = "nextTag=" + strs[2];
          //String tag = m.getBestOutcome(m.eval(features));
          //out.println(prevLine[0] + " " + prevLine[1] + " " + tag);
          
          // Add to the posterior probability matrix.
          String allOutcomes = m.getAllOutcomes(m.eval(features));
          Pattern probPatt = Pattern.compile(
              "I\\[([0-1]*\\.[0-9]+)\\]  O\\[([0-1]*\\.[0-9]+)\\]" +
              "  B\\[([0-1]*\\.[0-9]+)\\]");
          Matcher matcher = probPatt.matcher(allOutcomes);
          if (matcher.matches()) {
            LinkedList<Double> l = new LinkedList<Double>();
            for (int i = 1; i <= 3; ++i) {
              l.add(new Double(matcher.group(i)));
            }
            probMatrix.add(l);
          }
          
          // Make featuresf for the current line.
          features = new String[9];
          features[0] = "prev=" + prevLine[0];
          features[1] = "prevPos=" + prevLine[1];
          features[2] = "prevTag=" + prevLine[2];
        } else {
          features = new String[9];
          features[0] = "prev=null";
          features[1] = "prevPos=null";
          features[2] = "prevTag=null";
        }
        features[3] = "current=" + strs[0];
        features[4] = "pos=" + strs[1];
        features[5] = "isInitialCap=" + Character.isUpperCase(strs[0].charAt(0));
        prevLine = strs;
      } else {
        features[6] = "next=null";
        features[7] = "nextPos=null";
        features[8] = "nextTag=null";
        //String tag = m.getBestOutcome(m.eval(features));
        //out.println(prevLine[0] + " " + prevLine[1] + " " + tag);
        //out.println();
        prevLine = null;

        // Add to the posterior probability matrix.
        String allOutcomes = m.getAllOutcomes(m.eval(features));
        Pattern probPatt = Pattern.compile(
            "I\\[([0-1]*\\.[0-9]+)\\]  O\\[([0-1]*\\.[0-9]+)\\]" +
            "  B\\[([0-1]*\\.[0-9]+)\\]");
        Matcher matcher = probPatt.matcher(allOutcomes);
        if (matcher.matches()) {
          LinkedList<Double> l = new LinkedList<Double>();
          for (int i = 1; i <= 3; ++i) {
            l.add(new Double(matcher.group(i)));
          }
          probMatrix.add(l);
        }
        viterbi();
      }
    }
    out.flush();
  }

  public void viterbi() throws IOException {
    // State: B, I, O
    // German bureaucracy has for some time been pushing for relaxation within their country.
    // vt(j) = max vt-1(i) * P(sj|si,ot)
    int t = probMatrix.size();
    double[][] vMatrix = new double[5][t];
    int[][] backPtr = new int[5][t];
    for (int i = 1; i <= 3; ++i) {
      vMatrix[i][0] = probMatrix.get(0).get(i - 1);
      backPtr[i][0] = 0;
    }
    for (int i = 1; i < t; ++i) {
      for (int j = 1; j <= 3; ++j) {
        double max = 0;
        for (int k = 1; k <= 3; ++k) {
          double tmp = vMatrix[k][i - 1] * probMatrix.get(i).get(j - 1);
          if (tmp > max) {
            max = tmp;
            backPtr[j][i] = k;
          }
        }
        vMatrix[j][i] = max;
      }
    }

    double max = 0;
    for (int i = 1; i <= 3; ++i) {
      double tmp = vMatrix[i][t - 1];
      if (tmp > max) {
        max = tmp;
        backPtr[4][t - 1] = i;
      }
    }
    vMatrix[4][t - 1] = max; 

    String path = "";
    int currentState = backPtr[4][t - 1];
    path = states.get(currentState) + " " + path;
    for (int i = t - 1; i >= 1; --i){
      currentState = backPtr[currentState][i];
      path = states.get(currentState) + " " + path;
    }
    String[] tags = path.split(" ");
    for (int i = 0; i < tags.length; ++i) {
      out.println(sentence.get(i) + " " + tags[i]);
    }
    out.println();
    out.flush();
    sentence.clear();
    probMatrix.clear();
  }
}

