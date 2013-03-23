import java.io.FileInputStream;
import java.io.DataInputStream;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.io.FileReader;
import java.io.File;
import opennlp.maxent.*;
import opennlp.maxent.io.*;
import opennlp.model.EventStream;

public class Train {
  public static void main(String[] args) throws IOException {
    Train t = new Train();
    t.procWithFeatureSet1();
    t.procWithFeatureSet2();
    t.procWithFeatureSet3();
    t.trainWithSpecFeatureSet("events1", "model1");
    t.trainWithSpecFeatureSet("events2", "model2");
    t.trainWithSpecFeatureSet("events3", "model3");
  }

  private void procWithFeatureSet1() throws IOException {
    FileInputStream fis = new FileInputStream("training.txt");
    DataInputStream dis = new DataInputStream(fis);
    BufferedReader br = new BufferedReader(new InputStreamReader(dis));
    PrintWriter out =
        new PrintWriter(new BufferedWriter(new FileWriter("events1")));
    String line;
    while ((line = br.readLine()) != null) {
      if (!line.isEmpty()) { 
        String[] strs = line.split(" ");
        out.print("current=" + strs[0]);
        out.print(" pos=" + strs[1]);
        out.println(" " + strs[2]);
      }
    }
    out.flush();
  }

  private void procWithFeatureSet2() throws IOException {
    FileInputStream fis = new FileInputStream("training.txt");
    DataInputStream dis = new DataInputStream(fis);
    BufferedReader br = new BufferedReader(new InputStreamReader(dis));
    PrintWriter out =
        new PrintWriter(new BufferedWriter(new FileWriter("events2")));
    String line;
    while ((line = br.readLine()) != null) {
      if (!line.isEmpty()) { 
        String[] strs = line.split(" ");
        out.print("current=" + strs[0]);
        out.print(" pos=" + strs[1]);
        out.print(" isCap=" + Character.isUpperCase(strs[0].charAt(0)));
        out.println(" " + strs[2]);
      }
    }
    out.flush();
  }

  private void procWithFeatureSet3() throws IOException {
    FileInputStream fis = new FileInputStream("training.txt");
    DataInputStream dis = new DataInputStream(fis);
    BufferedReader br = new BufferedReader(new InputStreamReader(dis));
    PrintWriter out =
        new PrintWriter(new BufferedWriter(new FileWriter("events3")));
    String line;
    String[] prevLine = null;
    String outputLine = null;
    while ((line = br.readLine()) != null) {
      if (!line.isEmpty()) { 
        String[] strs = line.split(" ");
        if (prevLine != null) {
          // Output the previous line.
          outputLine = outputLine.substring(0, outputLine.length() - 1) +
              "next=" + strs[0] + " nextPos=" + strs[1] + " " +
              outputLine.substring(outputLine.length() - 1); 
          out.println(outputLine);
          
          // Make output for the current line.
          outputLine = "prev=" + prevLine[0] + " prevPos=" + prevLine[1] +
              " current=" + strs[0] + " pos=" + strs[1] +
              " isCap=" + Character.isUpperCase(strs[0].charAt(0)) +
              " " + strs[2];
        } else {
          outputLine = "prev=null prevPos=null" + 
              " current=" + strs[0] + " pos=" + strs[1] +
              " isCap=" + Character.isUpperCase(strs[0].charAt(0)) +
              " " + strs[2];
        }
        prevLine = strs;
      } else {
        outputLine = outputLine.substring(0, outputLine.length() - 1) +
            "next=null nextPos=null " + outputLine.substring(outputLine.length() - 1);
        out.println(outputLine);
        prevLine = null;
      }
    }
    out.flush();
  }

  private void trainWithSpecFeatureSet(String fs, String modelFile) {
      try {
          FileReader datafr = new FileReader(new File(fs));
          EventStream es = new BasicEventStream(new PlainTextByLineDataStream(datafr));
          GISModel model = GIS.trainModel(es, 100, 4);
          File outputFile = new File(modelFile);
          GISModelWriter writer = new SuffixSensitiveGISModelWriter(model, outputFile);
          writer.persist();
      } catch (Exception e) {
          System.out.print("Unable to create model due to exception: ");
          System.out.println(e);
      }
  }
}

