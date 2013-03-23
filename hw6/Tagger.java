import opennlp.maxent.*;
import opennlp.maxent.io.*;
import opennlp.model.EventStream;
import java.io.*;

public class Tagger {
  public static void main(String[] args) throws IOException {
    Tagger tagger = new Tagger();
    tagger.tagWithModel1();
    tagger.tagWithModel2();
    tagger.tagWithModel3();
  }

  private void tagWithModel1() throws IOException {
    GISModel m =
        (GISModel) new SuffixSensitiveGISModelReader(new File("model1")).getModel();
 
    FileInputStream fis = new FileInputStream("test.txt");
    DataInputStream dis = new DataInputStream(fis);
    BufferedReader br = new BufferedReader(new InputStreamReader(dis));
    PrintWriter out =
        new PrintWriter(new BufferedWriter(new FileWriter("results1")));

    String line;
    while ((line = br.readLine()) != null) {
      if (!line.isEmpty()) {
        String[] strs = line.split(" ");
        String[] features = new String[2];
        features[0] = "current=" + strs[0];
        features[1] = "pos=" + strs[1];
        String tag = m.getBestOutcome(m.eval(features));
        out.println(strs[0] + " " + strs[1] + " " + tag);
      } else {
        out.println();
      }
    }
    out.flush();
  }

  private void tagWithModel2() throws IOException {
    GISModel m =
        (GISModel) new SuffixSensitiveGISModelReader(new File("model2")).getModel();
 
    FileInputStream fis = new FileInputStream("test.txt");
    DataInputStream dis = new DataInputStream(fis);
    BufferedReader br = new BufferedReader(new InputStreamReader(dis));
    PrintWriter out =
        new PrintWriter(new BufferedWriter(new FileWriter("results2")));

    String line;
    while ((line = br.readLine()) != null) {
      if (!line.isEmpty()) {
        String[] strs = line.split(" ");
        String[] features = new String[3];
        features[0] = "current=" + strs[0];
        features[1] = "pos=" + strs[1];
        features[2] = "isCap=" + Character.isUpperCase(strs[0].charAt(0));
        String tag = m.getBestOutcome(m.eval(features));
        out.println(strs[0] + " " + strs[1] + " " + tag);
      } else {
        out.println();
      }
    }
    out.flush();
  }

  private void tagWithModel3() throws IOException {
    GISModel m =
        (GISModel) new SuffixSensitiveGISModelReader(new File("model3")).getModel();
 
    FileInputStream fis = new FileInputStream("test.txt");
    DataInputStream dis = new DataInputStream(fis);
    BufferedReader br = new BufferedReader(new InputStreamReader(dis));
    PrintWriter out =
        new PrintWriter(new BufferedWriter(new FileWriter("results3")));
    String line;
    String[] prevLine = null;
    String[] features = null;
    while ((line = br.readLine()) != null) {
      if (!line.isEmpty()) { 
        String[] strs = line.split(" ");
        if (prevLine != null) {
          // Tag the previous line.
          features[5] = "next=" + strs[0];
          features[6] = "nextPos=" + strs[1];
          String tag = m.getBestOutcome(m.eval(features));
          out.println(prevLine[0] + " " + prevLine[1] + " " + tag);
          
          // Make featuresf for the current line.
          features = new String[7];
          features[0] = "prev=" + prevLine[0];
          features[1] = "prevPos=" + prevLine[1];
        } else {
          features = new String[7];
          features[0] = "prev=null";
          features[1] = "prevPos=null";
        }
        features[2] = "current=" + strs[0];
        features[3] = "pos=" + strs[1];
        features[4] = "isCap=" + Character.isUpperCase(strs[0].charAt(0));
        prevLine = strs;
      } else {
        features[5] = "next=null";
        features[6] = "nextPos=null";
        String tag = m.getBestOutcome(m.eval(features));
        out.println(prevLine[0] + " " + prevLine[1] + " " + tag);
        out.println();
        prevLine = null;
      }
    }
    out.flush();
  }
}

