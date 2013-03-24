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
    /*
    t.procWithFeatureSet1();
    t.procWithFeatureSet2();
    t.procWithFeatureSet3();
    t.procWithFeatureSet4();*/
    //t.procWithFeatureSet5();
    //t.procWithFeatureSet6();
    /*
    t.trainWithSpecFeatureSet("events1", "model1");
    t.trainWithSpecFeatureSet("events2", "model2");
    t.trainWithSpecFeatureSet("events3", "model3");
    t.trainWithSpecFeatureSet("events4", "model4");*/
    //t.trainWithSpecFeatureSet("events5", "model5");
    //t.trainWithSpecFeatureSet("events6", "model6");
    //t.procWithFeatureSet7();
    //t.trainWithSpecFeatureSet("events7", "model7");
    t.procWithFeatureSet8();
    t.trainWithSpecFeatureSet("events8", "model8");
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
        out.print(" isInitialCap=" + Character.isUpperCase(strs[0].charAt(0)));
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
              " isInitialCap=" + Character.isUpperCase(strs[0].charAt(0)) +
              " " + strs[2];
        } else {
          outputLine = "prev=null prevPos=null" + 
              " current=" + strs[0] + " pos=" + strs[1] +
              " isInitialCap=" + Character.isUpperCase(strs[0].charAt(0)) +
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

  private void procWithFeatureSet4() throws IOException {
    FileInputStream fis = new FileInputStream("training.txt");
    DataInputStream dis = new DataInputStream(fis);
    BufferedReader br = new BufferedReader(new InputStreamReader(dis));
    PrintWriter out =
        new PrintWriter(new BufferedWriter(new FileWriter("events4")));
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
              " isInitialCap=" + Character.isUpperCase(strs[0].charAt(0));
        } else {
          outputLine = "prev=null prevPos=null" + 
              " current=" + strs[0] + " pos=" + strs[1] +
              " isInitialCap=" + Character.isUpperCase(strs[0].charAt(0));
        }

        // Add the isSuffixIng feature.
        if (strs[0].length() >= 3) {
          outputLine += " isSuffixIng=" +
              strs[0].substring(strs[0].length() - 3).equals("ing");
        } else {
          outputLine += " isSuffixingIng=false";
        }
        outputLine += " " + strs[2];
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


  private void procWithFeatureSet5() throws IOException {
    FileInputStream fis = new FileInputStream("training.txt");
    DataInputStream dis = new DataInputStream(fis);
    BufferedReader br = new BufferedReader(new InputStreamReader(dis));
    PrintWriter out =
        new PrintWriter(new BufferedWriter(new FileWriter("events5")));
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
          outputLine = "prev=" + prevLine[0] + " prevPos=" + prevLine[1];
        } else {
          outputLine = "prev=null prevPos=null"; 
        }

        outputLine += 
              " current=" + strs[0] + " pos=" + strs[1] +
              " isInitialCap=" + Character.isUpperCase(strs[0].charAt(0));
        // Add the isSuffixIng feature.
        if (strs[0].length() >= 3) {
          outputLine += " isSuffixIng=" +
              strs[0].substring(strs[0].length() - 3).equals("ing");
        } else {
          outputLine += " isSuffixingIng=false";
        }

        // Add more shape features.
        boolean isAllUpper = true;
        for (int i = 0; i < strs[0].length(); ++i) {
          if (Character.isLowerCase(strs[0].charAt(i))) {
            isAllUpper = false;
            break;
          }
        }
        boolean isAllLower = true;
        for (int i = 0; i < strs[0].length(); ++i) {
          if (Character.isUpperCase(strs[0].charAt(i))) {
            isAllLower = false;
            break;
          }
        }
        boolean isMixedCase = false;
        if (!isAllUpper && !isAllLower) {
          isMixedCase = true;
        }
        outputLine += " isAllUpper=" + isAllUpper +
            " isAllLower=" + isAllLower + " isMixedCase=" + isMixedCase;
        if (strs[0].length() >= 2 &&
            Character.isUpperCase(strs[0].charAt(0)) &&
            strs[0].charAt(1) == '.') {
          outputLine += " capInitWithPeriod=true";
        } else {
          outputLine += " capInitWithPeriod=false";
        }
        if (Character.isDigit(strs[0].charAt(strs[0].length() - 1))) {
          outputLine += " endWithDigit=true";
        } else {
          outputLine += " endWithDigit=false";
        }
        if (strs[0].indexOf("-") != -1) {
          outputLine += " hasHyphen=true";
        } else {
          outputLine += " hasHyphen=false";
        }

        // Add the tag.
        outputLine += " " + strs[2];
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

  // With more shape features added.
  private void procWithFeatureSet6() throws IOException {
    FileInputStream fis = new FileInputStream("training.txt");
    DataInputStream dis = new DataInputStream(fis);
    BufferedReader br = new BufferedReader(new InputStreamReader(dis));
    PrintWriter out =
        new PrintWriter(new BufferedWriter(new FileWriter("events6")));
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
          outputLine = "prev=" + prevLine[0] + " prevPos=" + prevLine[1];
        } else {
          outputLine = "prev=null prevPos=null"; 
        }

        outputLine += 
              " current=" + strs[0] + " pos=" + strs[1] +
              " isInitialCap=" + Character.isUpperCase(strs[0].charAt(0));
        // Add the isSuffixIng feature.
        if (strs[0].length() >= 3) {
          outputLine += " isSuffixIng=" +
              strs[0].substring(strs[0].length() - 3).equals("ing");
        } else {
          outputLine += " isSuffixingIng=false";
        }

        // Add more shape features.
        boolean isAllUpper = true;
        for (int i = 0; i < strs[0].length(); ++i) {
          if (Character.isLowerCase(strs[0].charAt(i))) {
            isAllUpper = false;
            break;
          }
        }
        boolean isAllLower = true;
        for (int i = 0; i < strs[0].length(); ++i) {
          if (Character.isUpperCase(strs[0].charAt(i))) {
            isAllLower = false;
            break;
          }
        }
        boolean isMixedCase = false;
        if (!isAllUpper && !isAllLower) {
          isMixedCase = true;
        }
        outputLine += " isAllUpper=" + isAllUpper +
            " isAllLower=" + isAllLower + " isMixedCase=" + isMixedCase;
        if (strs[0].length() >= 2 &&
            Character.isUpperCase(strs[0].charAt(0)) &&
            strs[0].charAt(1) == '.') {
          outputLine += " capInitWithPeriod=true";
        } else {
          outputLine += " capInitWithPeriod=false";
        }
        if (Character.isDigit(strs[0].charAt(strs[0].length() - 1))) {
          outputLine += " endWithDigit=true";
        } else {
          outputLine += " endWithDigit=false";
        }
        if (strs[0].indexOf("-") != -1) {
          outputLine += " hasHyphen=true";
        } else {
          outputLine += " hasHyphen=false";
        }
        Stemmer s = new Stemmer();
        s.add(strs[0].toCharArray(), strs[0].length());
        s.stem();
        outputLine += " stemmed=" + s;

        // Add the tag.
        outputLine += " " + strs[2];
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

  private void procWithFeatureSet7() throws IOException {
    FileInputStream fis = new FileInputStream("training.txt");
    DataInputStream dis = new DataInputStream(fis);
    BufferedReader br = new BufferedReader(new InputStreamReader(dis));
    PrintWriter out =
        new PrintWriter(new BufferedWriter(new FileWriter("events7")));
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
              "nextTag=" + strs[2] + " " +
              outputLine.substring(outputLine.length() - 1); 
          out.println(outputLine);
          
          // Make output for the current line.
          outputLine = "prev=" + prevLine[0] + " prevPos=" + prevLine[1] +
              " prevTag=" + prevLine[2];
        } else {
          outputLine = "prev=null prevPos=null prevTag=null"; 
        }

        outputLine += 
              " current=" + strs[0] + " pos=" + strs[1] +
              " isInitialCap=" + Character.isUpperCase(strs[0].charAt(0));
        // Add the isSuffixIng feature.
        if (strs[0].length() >= 3) {
          outputLine += " isSuffixIng=" +
              strs[0].substring(strs[0].length() - 3).equals("ing");
        } else {
          outputLine += " isSuffixingIng=false";
        }

        // Add more shape features.
        boolean isAllUpper = true;
        for (int i = 0; i < strs[0].length(); ++i) {
          if (Character.isLowerCase(strs[0].charAt(i))) {
            isAllUpper = false;
            break;
          }
        }
        boolean isAllLower = true;
        for (int i = 0; i < strs[0].length(); ++i) {
          if (Character.isUpperCase(strs[0].charAt(i))) {
            isAllLower = false;
            break;
          }
        }
        boolean isMixedCase = false;
        if (!isAllUpper && !isAllLower) {
          isMixedCase = true;
        }
        outputLine += " isAllUpper=" + isAllUpper +
            " isAllLower=" + isAllLower + " isMixedCase=" + isMixedCase;
        if (strs[0].length() >= 2 &&
            Character.isUpperCase(strs[0].charAt(0)) &&
            strs[0].charAt(1) == '.') {
          outputLine += " capInitWithPeriod=true";
        } else {
          outputLine += " capInitWithPeriod=false";
        }
        if (Character.isDigit(strs[0].charAt(strs[0].length() - 1))) {
          outputLine += " endWithDigit=true";
        } else {
          outputLine += " endWithDigit=false";
        }
        if (strs[0].indexOf("-") != -1) {
          outputLine += " hasHyphen=true";
        } else {
          outputLine += " hasHyphen=false";
        }
        Stemmer s = new Stemmer();
        s.add(strs[0].toCharArray(), strs[0].length());
        s.stem();
        outputLine += " stemmed=" + s;

        // Add the tag.
        outputLine += " " + strs[2];
        prevLine = strs;
      } else {
        outputLine = outputLine.substring(0, outputLine.length() - 1) +
            "next=null nextPos=null nextTag=null " + outputLine.substring(outputLine.length() - 1);
        out.println(outputLine);
        prevLine = null;
      }
    }
    out.flush();
  }

  private void procWithFeatureSet8() throws IOException {
    FileInputStream fis = new FileInputStream("training.txt");
    DataInputStream dis = new DataInputStream(fis);
    BufferedReader br = new BufferedReader(new InputStreamReader(dis));
    PrintWriter out =
        new PrintWriter(new BufferedWriter(new FileWriter("events8")));
    String line;
    String[] prevLine = null;
    String outputLine = null;
    while ((line = br.readLine()) != null) {
      if (!line.isEmpty()) { 
        String[] strs = line.split(" ");
        if (prevLine != null) {
          // Output the previous line.
          outputLine = outputLine.substring(0, outputLine.length() - 1) +
              "next=" + strs[0] + " nextPos=" + strs[1] +
              " nextTag=" + strs[2] + " " +
              outputLine.substring(outputLine.length() - 1); 
          out.println(outputLine);
          
          // Make output for the current line.
          outputLine = "prev=" + prevLine[0] + " prevPos=" + prevLine[1] +
              " prevTag=" + prevLine[2] +
              " current=" + strs[0] + " pos=" + strs[1] +
              " isInitialCap=" + Character.isUpperCase(strs[0].charAt(0)) +
              " " + strs[2];
        } else {
          outputLine = "prev=null prevPos=null prevTag=null" + 
              " current=" + strs[0] + " pos=" + strs[1] +
              " isInitialCap=" + Character.isUpperCase(strs[0].charAt(0)) +
              " " + strs[2];
        }
        prevLine = strs;
      } else {
        outputLine = outputLine.substring(0, outputLine.length() - 1) +
            "next=null nextPos=null nextTag=null " +
            outputLine.substring(outputLine.length() - 1);
        out.println(outputLine);
        prevLine = null;
      }
    }
    out.flush();
  }

  private void trainWithSpecFeatureSet(String fsFile, String modelFile) {
    try {
      FileReader datafr = new FileReader(new File(fsFile));
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

