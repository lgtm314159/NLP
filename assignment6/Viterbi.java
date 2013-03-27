import opennlp.maxent.*;
import opennlp.maxent.io.*;
import opennlp.model.EventStream;
import java.io.*;
import java.util.HashMap;
import java.util.regex.Pattern;
import java.util.regex.Matcher;
import java.util.LinkedList;

public class Viterbi {
  private LinkedList<LinkedList<Double>> probMatrixSimple =
      new LinkedList<LinkedList<Double>>();
  private LinkedList<LinkedList<LinkedList<Double>>> probMatrixComplex =
      new LinkedList<LinkedList<LinkedList<Double>>>();
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
    v.tagWithViterbiUsingModel8Simple();
    v.tagWithViterbiUsingModel8Complex();
  }

  public void tagWithViterbiUsingModel8Simple() throws IOException {
    GISModel m =
        (GISModel) new SuffixSensitiveGISModelReader(new File("model8")).getModel();
 
    FileInputStream fis = new FileInputStream("test.txt");
    DataInputStream dis = new DataInputStream(fis);
    BufferedReader br = new BufferedReader(new InputStreamReader(dis));
    out = new PrintWriter(new BufferedWriter(new FileWriter("resultsVitModel8Simple")));
    String line;
    String[] prevLine = null;
    String[] features = null;
    while ((line = br.readLine()) != null) {
      if (!line.isEmpty()) { 
        String[] strs = line.split(" ");
        sentence.add(strs[0] + " " + strs[1]);
        if (prevLine != null) {
          features[6] = "next=" + strs[0];
          features[7] = "nextPos=" + strs[1];
          //features[8] = "nextTag=" + strs[2];
          features[8] = "nextTag=null";
          
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
            probMatrixSimple.add(l);
          }
          
          // Make featuresf for the current line.
          features = new String[9];
          features[0] = "prev=" + prevLine[0];
          features[1] = "prevPos=" + prevLine[1];
          //features[2] = "prevTag=" + prevLine[2];
          features[2] = "prevTag=null";
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
          probMatrixSimple.add(l);
        }
        // Tag the sentence.
        viterbiSimple();
      }
    }
    out.flush();
  }

  public void tagWithViterbiUsingModel8Complex() throws IOException {
    GISModel m =
        (GISModel) new SuffixSensitiveGISModelReader(new File("model8")).getModel();
 
    FileInputStream fis = new FileInputStream("test.txt");
    DataInputStream dis = new DataInputStream(fis);
    BufferedReader br = new BufferedReader(new InputStreamReader(dis));
    out = new PrintWriter(new BufferedWriter(new FileWriter("resultsVitModel8Complex")));
    String line;
    String[] prevLine = null;
    String[] features = null;
    while ((line = br.readLine()) != null) {
      if (!line.isEmpty()) { 
        String[] strs = line.split(" ");
        sentence.add(strs[0] + " " + strs[1]);
        if (prevLine != null) {
          features[6] = "next=" + strs[0];
          features[7] = "nextPos=" + strs[1];
          //features[8] = "nextTag=" + strs[2];
          features[8] = "nextTag=null";
          // Add to the posterior probability matrix.
          LinkedList<LinkedList<Double>> l = new LinkedList<LinkedList<Double>>();
          if (!features[2].equals("prevTag=null")) {
            for (int i = 1; i <= 3; ++i) {
              features[2] = "prevTag=" + states.get(i);
              l.add(calcPostProb(m, features));
            }
          } else {
            l.add(calcPostProb(m, features));
          }
          probMatrixComplex.add(l);
          
          // Make features for the current line.
          features = new String[9];
          features[0] = "prev=" + prevLine[0];
          features[1] = "prevPos=" + prevLine[1];
          features[2] = "prevTag=";
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

        // Add to the posterior probability matrix.
        LinkedList<LinkedList<Double>> l = new LinkedList<LinkedList<Double>>();
        if (!features[2].equals("prevTag=null")) {
          for (int i = 1; i <= 3; ++i) {
            features[2] = "prevTag=" + states.get(i);
            l.add(calcPostProb(m, features));
          }
        } else {
          l.add(calcPostProb(m, features));
        }
        probMatrixComplex.add(l);
        // Tag the sentence.
        viterbiComplex();

        prevLine = null;
      }
    }
    out.flush();
  }

/*
  public void tagWithViterbiUsingModel1Simple() throws IOException {
    GISModel m =
        (GISModel) new SuffixSensitiveGISModelReader(new File("model1")).getModel();
 
    FileInputStream fis = new FileInputStream("test.txt");
    DataInputStream dis = new DataInputStream(fis);
    BufferedReader br = new BufferedReader(new InputStreamReader(dis));
    out = new PrintWriter(new BufferedWriter(new FileWriter("resultsVitModel1Simple")));

    String line;
    while ((line = br.readLine()) != null) {
      if (!line.isEmpty()) {
        String[] strs = line.split(" ");
        sentence.add(strs[0] + " " + strs[1]);
        String[] features = new String[2];
        features[0] = "current=" + strs[0];
        features[1] = "pos=" + strs[1];

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
          probMatrixSimple.add(l);
        }
      } else {
        viterbiSimple();
      }
    }
    out.flush();
  }

  public void tagWithViterbiUsingModel1Complex() throws IOException {
    GISModel m =
        (GISModel) new SuffixSensitiveGISModelReader(new File("model1")).getModel();
 
    FileInputStream fis = new FileInputStream("test.txt");
    DataInputStream dis = new DataInputStream(fis);
    BufferedReader br = new BufferedReader(new InputStreamReader(dis));
    out = new PrintWriter(new BufferedWriter(new FileWriter("resultsVitModel1Complex")));

    String line;
    while ((line = br.readLine()) != null) {
      if (!line.isEmpty()) {
        String[] strs = line.split(" ");
        sentence.add(strs[0] + " " + strs[1]);
        String[] features = new String[3];
        features[0] = "current=" + strs[0];
        features[1] = "pos=" + strs[1];
        features[2] = "";

        // Add to the posterior probability matrix.
        LinkedList<LinkedList<Double>> l = new LinkedList<LinkedList<Double>>();
        for (int i = 1; i <= 3; ++i) {
          features[2] = "prevState=" + states.get(i);
          l.add(calcPostProb(m, features));
        }
        probMatrixComplex.add(l);
      } else {
        viterbiComplex();
      }
    }
    out.flush();
  }
*/

  private void viterbiSimple() throws IOException {
    int t = probMatrixSimple.size();
    double[][] vMatrix = new double[5][t];
    int[][] backPtr = new int[5][t];
    for (int i = 1; i <= 3; ++i) {
      vMatrix[i][0] = probMatrixSimple.get(0).get(i - 1);
      backPtr[i][0] = 0;
    }
    for (int i = 1; i < t; ++i) {
      for (int j = 1; j <= 3; ++j) {
        double max = 0;
        for (int k = 1; k <= 3; ++k) {
          double tmp = vMatrix[k][i - 1] * probMatrixSimple.get(i).get(j - 1);
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
    sentence.clear();
    probMatrixSimple.clear();
  }

  private void viterbiComplex() throws IOException {
    int t = probMatrixComplex.size();
    double[][] vMatrix = new double[5][t];
    int[][] backPtr = new int[5][t];
    for (int i = 1; i <= 3; ++i) {
      vMatrix[i][0] = probMatrixComplex.get(0).get(0).get(i - 1);
      backPtr[i][0] = 0;
    }
    for (int i = 1; i < t; ++i) {
      for (int j = 1; j <= 3; ++j) {
        double max = 0;
        for (int k = 1; k <= 3; ++k) {
          double tmp = vMatrix[k][i - 1] *
              probMatrixComplex.get(i).get(k - 1).get(j - 1);
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
    sentence.clear();
    probMatrixComplex.clear();
  }

  private LinkedList<Double> calcPostProb(GISModel m, String[] features) {
    String allOutcomes = m.getAllOutcomes(m.eval(features));
    Pattern probPatt = Pattern.compile(
        "I\\[([0-1]*\\.[0-9]+)\\]  O\\[([0-1]*\\.[0-9]+)\\]" +
        "  B\\[([0-1]*\\.[0-9]+)\\]");
    Matcher matcher = probPatt.matcher(allOutcomes);
    LinkedList<Double> l = new LinkedList<Double>();
    if (matcher.matches()) {
      for (int i = 1; i <= 3; ++i) {
        l.add(new Double(matcher.group(i)));
      }
    }
    return l;
  }
}

