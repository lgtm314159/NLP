import opennlp.maxent.*;
import opennlp.maxent.io.*;
import opennlp.model.EventStream;
import java.io.*;
import java.util.HashMap;

public class Tagger {
  public static void main(String[] args) throws IOException {
    Tagger tagger = new Tagger();
    tagger.tagWithModel1();
    tagger.tagWithModel2();
    tagger.tagWithModel3();
    tagger.tagWithModel4();
    tagger.tagWithModel5();
    tagger.tagWithModel6();
    tagger.tagWithModel7();
    tagger.tagWithModel8();
    tagger.evaluate("test.txt", "results1");
    tagger.evaluate("test.txt", "results2");
    tagger.evaluate("test.txt", "results3");
    tagger.evaluate("test.txt", "results4");
    tagger.evaluate("test.txt", "results5");
    tagger.evaluate("test.txt", "results6");
    tagger.evaluate("test.txt", "results7");
    tagger.evaluate("test.txt", "results8");
    
    tagger.produceNgroups("test.txt", "testNgroups");
    tagger.produceNgroups("results1", "resNgroups1");
    tagger.produceNgroups("results2", "resNgroups2");
    tagger.produceNgroups("results3", "resNgroups3");
    tagger.produceNgroups("results4", "resNgroups4");
    tagger.produceNgroups("results5", "resNgroups5");
    tagger.produceNgroups("results6", "resNgroups6");
    tagger.produceNgroups("results7", "resNgroups7");
    tagger.produceNgroups("results8", "resNgroups8");
    tagger.produceNgroups("resultsVit", "resNgroupsVit");
    
    tagger.calcNgroupPrecisionAndRecall("testNgroups", "resNgroups1");
    tagger.calcNgroupPrecisionAndRecall("testNgroups", "resNgroups2");
    tagger.calcNgroupPrecisionAndRecall("testNgroups", "resNgroups3");
    tagger.calcNgroupPrecisionAndRecall("testNgroups", "resNgroups4");
    tagger.calcNgroupPrecisionAndRecall("testNgroups", "resNgroups5");
    tagger.calcNgroupPrecisionAndRecall("testNgroups", "resNgroups6");
    tagger.calcNgroupPrecisionAndRecall("testNgroups", "resNgroups7");
    tagger.calcNgroupPrecisionAndRecall("testNgroups", "resNgroups8");
    tagger.calcNgroupPrecisionAndRecall("testNgroups", "resNgroupsVit");
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
        features[2] = "isInitialCap=" + Character.isUpperCase(strs[0].charAt(0));
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
        features[4] = "isInitialCap=" + Character.isUpperCase(strs[0].charAt(0));
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

  private void tagWithModel4() throws IOException {
    GISModel m =
        (GISModel) new SuffixSensitiveGISModelReader(new File("model4")).getModel();
 
    FileInputStream fis = new FileInputStream("test.txt");
    DataInputStream dis = new DataInputStream(fis);
    BufferedReader br = new BufferedReader(new InputStreamReader(dis));
    PrintWriter out =
        new PrintWriter(new BufferedWriter(new FileWriter("results4")));
    String line;
    String[] prevLine = null;
    String[] features = null;
    while ((line = br.readLine()) != null) {
      if (!line.isEmpty()) { 
        String[] strs = line.split(" ");
        if (prevLine != null) {
          // Tag the previous line.
          features[6] = "next=" + strs[0];
          features[7] = "nextPos=" + strs[1];
          String tag = m.getBestOutcome(m.eval(features));
          out.println(prevLine[0] + " " + prevLine[1] + " " + tag);
          
          // Make featuresf for the current line.
          features = new String[8];
          features[0] = "prev=" + prevLine[0];
          features[1] = "prevPos=" + prevLine[1];
        } else {
          features = new String[8];
          features[0] = "prev=null";
          features[1] = "prevPos=null";
        }
        features[2] = "current=" + strs[0];
        features[3] = "pos=" + strs[1];
        features[4] = "isInitialCap=" + Character.isUpperCase(strs[0].charAt(0));
        if (strs[0].length() >= 3) {
          features[5] = "isSuffixIng=" +
              strs[0].substring(strs[0].length() - 3).equals("ing");
        } else {
          features[5] = "isSuffixIng=false";
        }
        prevLine = strs;
      } else {
        features[6] = "next=null";
        features[7] = "nextPos=null";
        String tag = m.getBestOutcome(m.eval(features));
        out.println(prevLine[0] + " " + prevLine[1] + " " + tag);
        out.println();
        prevLine = null;
      }
    }
    out.flush();
  }


  private void tagWithModel5() throws IOException {
    GISModel m =
        (GISModel) new SuffixSensitiveGISModelReader(new File("model5")).getModel();
 
    FileInputStream fis = new FileInputStream("test.txt");
    DataInputStream dis = new DataInputStream(fis);
    BufferedReader br = new BufferedReader(new InputStreamReader(dis));
    PrintWriter out =
        new PrintWriter(new BufferedWriter(new FileWriter("results5")));
    String line;
    String[] prevLine = null;
    String[] features = null;
    while ((line = br.readLine()) != null) {
      if (!line.isEmpty()) { 
        String[] strs = line.split(" ");
        if (prevLine != null) {
          // Tag the previous line.
          features[12] = "next=" + strs[0];
          features[13] = "nextPos=" + strs[1];
          String tag = m.getBestOutcome(m.eval(features));
          out.println(prevLine[0] + " " + prevLine[1] + " " + tag);
          
          // Make featuresf for the current line.
          features = new String[14];
          features[0] = "prev=" + prevLine[0];
          features[1] = "prevPos=" + prevLine[1];
        } else {
          features = new String[14];
          features[0] = "prev=null";
          features[1] = "prevPos=null";
        }
        features[2] = "current=" + strs[0];
        features[3] = "pos=" + strs[1];
        features[4] = "isInitialCap=" + Character.isUpperCase(strs[0].charAt(0));
        if (strs[0].length() >= 3) {
          features[5] = "isSuffixIng=" +
              strs[0].substring(strs[0].length() - 3).equals("ing");
        } else {
          features[5] = "isSuffixIng=false";
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
        features[6] = "isAllUpper=" + isAllUpper;
        features[7] = "isAllLower=" + isAllLower;
        features[8] = "isMixedCase=" + isMixedCase;
        if (strs[0].length() >= 2 &&
            Character.isUpperCase(strs[0].charAt(0)) &&
            strs[0].charAt(1) == '.') {
          features[9] = "capInitWithPeriod=true";
        } else {
          features[9] = "capInitWithPeriod=false";
        }
        if (Character.isDigit(strs[0].charAt(strs[0].length() - 1))) {
          features[10] = "endWithDigit=true";
        } else {
          features[10] = "endWithDigit=false";
        }
        if (strs[0].indexOf("-") != -1) {
          features[11] = "hasHyphen=true";
        } else {
          features[11] = "hasHyphen=false";
        }

        prevLine = strs;
      } else {
        features[12] = "next=null";
        features[13] = "nextPos=null";
        String tag = m.getBestOutcome(m.eval(features));
        out.println(prevLine[0] + " " + prevLine[1] + " " + tag);
        out.println();
        prevLine = null;
      }
    }
    out.flush();
  }

  private void tagWithModel6() throws IOException {
    GISModel m =
        (GISModel) new SuffixSensitiveGISModelReader(new File("model6")).getModel();
 
    FileInputStream fis = new FileInputStream("test.txt");
    DataInputStream dis = new DataInputStream(fis);
    BufferedReader br = new BufferedReader(new InputStreamReader(dis));
    PrintWriter out =
        new PrintWriter(new BufferedWriter(new FileWriter("results6")));
    String line;
    String[] prevLine = null;
    String[] features = null;
    while ((line = br.readLine()) != null) {
      if (!line.isEmpty()) { 
        String[] strs = line.split(" ");
        if (prevLine != null) {
          // Tag the previous line.
          features[13] = "next=" + strs[0];
          features[14] = "nextPos=" + strs[1];
          String tag = m.getBestOutcome(m.eval(features));
          out.println(prevLine[0] + " " + prevLine[1] + " " + tag);
          
          // Make featuresf for the current line.
          features = new String[15];
          features[0] = "prev=" + prevLine[0];
          features[1] = "prevPos=" + prevLine[1];
        } else {
          features = new String[15];
          features[0] = "prev=null";
          features[1] = "prevPos=null";
        }
        features[2] = "current=" + strs[0];
        features[3] = "pos=" + strs[1];
        features[4] = "isInitialCap=" + Character.isUpperCase(strs[0].charAt(0));
        if (strs[0].length() >= 3) {
          features[5] = "isSuffixIng=" +
              strs[0].substring(strs[0].length() - 3).equals("ing");
        } else {
          features[5] = "isSuffixIng=false";
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
        features[6] = "isAllUpper=" + isAllUpper;
        features[7] = "isAllLower=" + isAllLower;
        features[8] = "isMixedCase=" + isMixedCase;
        if (strs[0].length() >= 2 &&
            Character.isUpperCase(strs[0].charAt(0)) &&
            strs[0].charAt(1) == '.') {
          features[9] = "capInitWithPeriod=true";
        } else {
          features[9] = "capInitWithPeriod=false";
        }
        if (Character.isDigit(strs[0].charAt(strs[0].length() - 1))) {
          features[10] = "endWithDigit=true";
        } else {
          features[10] = "endWithDigit=false";
        }
        if (strs[0].indexOf("-") != -1) {
          features[11] = "hasHyphen=true";
        } else {
          features[11] = "hasHyphen=false";
        }
        Stemmer s = new Stemmer();
        s.add(strs[0].toCharArray(), strs[0].length());
        s.stem();
        features[12] = s.toString();

        prevLine = strs;
      } else {
        features[13] = "next=null";
        features[14] = "nextPos=null";
        String tag = m.getBestOutcome(m.eval(features));
        out.println(prevLine[0] + " " + prevLine[1] + " " + tag);
        out.println();
        prevLine = null;
      }
    }
    out.flush();
  }

  private void tagWithModel7() throws IOException {
    GISModel m =
        (GISModel) new SuffixSensitiveGISModelReader(new File("model7")).getModel();
 
    FileInputStream fis = new FileInputStream("test.txt");
    DataInputStream dis = new DataInputStream(fis);
    BufferedReader br = new BufferedReader(new InputStreamReader(dis));
    PrintWriter out =
        new PrintWriter(new BufferedWriter(new FileWriter("results7")));
    String line;
    String[] prevLine = null;
    String[] features = null;
    while ((line = br.readLine()) != null) {
      if (!line.isEmpty()) { 
        String[] strs = line.split(" ");
        if (prevLine != null) {
          // Tag the previous line.
          features[14] = "next=" + strs[0];
          features[15] = "nextPos=" + strs[1];
          features[16] = "nextTag=" + strs[2];
          String tag = m.getBestOutcome(m.eval(features));
          out.println(prevLine[0] + " " + prevLine[1] + " " + tag);
          
          // Make featuresf for the current line.
          features = new String[17];
          features[0] = "prev=" + prevLine[0];
          features[1] = "prevPos=" + prevLine[1];
          features[2] = "prevTag=" + prevLine[2];
        } else {
          features = new String[17];
          features[0] = "prev=null";
          features[1] = "prevPos=null";
          features[2] = "prevTag=null";
        }
        features[3] = "current=" + strs[0];
        features[4] = "pos=" + strs[1];
        features[5] = "isInitialCap=" + Character.isUpperCase(strs[0].charAt(0));
        if (strs[0].length() >= 3) {
          features[6] = "isSuffixIng=" +
              strs[0].substring(strs[0].length() - 3).equals("ing");
        } else {
          features[6] = "isSuffixIng=false";
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
        features[7] = "isAllUpper=" + isAllUpper;
        features[8] = "isAllLower=" + isAllLower;
        features[9] = "isMixedCase=" + isMixedCase;
        if (strs[0].length() >= 2 &&
            Character.isUpperCase(strs[0].charAt(0)) &&
            strs[0].charAt(1) == '.') {
          features[10] = "capInitWithPeriod=true";
        } else {
          features[10] = "capInitWithPeriod=false";
        }
        if (Character.isDigit(strs[0].charAt(strs[0].length() - 1))) {
          features[11] = "endWithDigit=true";
        } else {
          features[11] = "endWithDigit=false";
        }
        if (strs[0].indexOf("-") != -1) {
          features[12] = "hasHyphen=true";
        } else {
          features[12] = "hasHyphen=false";
        }
        Stemmer s = new Stemmer();
        s.add(strs[0].toCharArray(), strs[0].length());
        s.stem();
        features[13] = s.toString();

        prevLine = strs;
      } else {
        features[14] = "next=null";
        features[15] = "nextPos=null";
        features[16] = "nextTag=null";
        String tag = m.getBestOutcome(m.eval(features));
        out.println(prevLine[0] + " " + prevLine[1] + " " + tag);
        out.println();
        prevLine = null;
      }
    }
    out.flush();
  }

  private void evaluate(String testFile, String resultFile) throws IOException {
    int correct = 0;
    FileInputStream fis1 = new FileInputStream(testFile);
    DataInputStream dis1 = new DataInputStream(fis1);
    BufferedReader br1 = new BufferedReader(new InputStreamReader(dis1));
    FileInputStream fis2 = new FileInputStream(resultFile);
    DataInputStream dis2 = new DataInputStream(fis2);
    BufferedReader br2 = new BufferedReader(new InputStreamReader(dis2));
    String line1, line2;
    while ((line1 = br1.readLine()) != null &&
        (line2 = br2.readLine()) != null) {
      if (!line1.isEmpty()) {
        if (line1.equals(line2))
          ++correct;
      }
    }
    /*
    double recall = ((double) correct) / total; 
    double precision = ((double) correct) / total; 
    double f1 = 2 * precision * recall / (precision + recall);
    double fPoint5 =
        (1 + 0.5 * 0.5) * precision * recall / (0.5 * 0.5 * precision + recall);
    */
    System.out.println("Correct tags: " + correct);
  }

  private void tagWithModel8() throws IOException {
    GISModel m =
        (GISModel) new SuffixSensitiveGISModelReader(new File("model8")).getModel();
 
    FileInputStream fis = new FileInputStream("test.txt");
    DataInputStream dis = new DataInputStream(fis);
    BufferedReader br = new BufferedReader(new InputStreamReader(dis));
    PrintWriter out =
        new PrintWriter(new BufferedWriter(new FileWriter("results8")));
    String line;
    String[] prevLine = null;
    String[] features = null;
    while ((line = br.readLine()) != null) {
      if (!line.isEmpty()) { 
        String[] strs = line.split(" ");
        if (prevLine != null) {
          // Tag the previous line.
          features[6] = "next=" + strs[0];
          features[7] = "nextPos=" + strs[1];
          features[8] = "nextTag=" + strs[2];
          String tag = m.getBestOutcome(m.eval(features));
          out.println(prevLine[0] + " " + prevLine[1] + " " + tag);
          
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
        String tag = m.getBestOutcome(m.eval(features));
        out.println(prevLine[0] + " " + prevLine[1] + " " + tag);
        out.println();
        prevLine = null;
      }
    }
    out.flush();
  }
  
  private void calcNgroupPrecisionAndRecall(String testNGFile, String resultNGFile)
      throws IOException {
    FileInputStream fis1 = new FileInputStream(testNGFile);
    DataInputStream dis1 = new DataInputStream(fis1);
    BufferedReader br1 = new BufferedReader(new InputStreamReader(dis1));
    FileInputStream fis2 = new FileInputStream(resultNGFile);
    DataInputStream dis2 = new DataInputStream(fis2);
    BufferedReader br2 = new BufferedReader(new InputStreamReader(dis2));
    String line1, line2;
    int testTotal = 0;
    int resTotal = 0;
    int correct = 0;
    while (true) {
      line1 = br1.readLine();
      line2 = br2.readLine();
      if (line1 == null) {
        break;
      }
      if (line2 == null) {
        break;
      }
      HashMap<String, Boolean> map = new HashMap<String, Boolean>();
      while (true) {
        if (!line1.isEmpty()) {
          map.put(line1, true);
          ++testTotal;
        }
        line1 = br1.readLine();
        if (line1.isEmpty())
          break;
      }
      while (true) {
        if (!line2.isEmpty()) {
          ++resTotal;
          if (map.containsKey(line2))
            ++correct;
        }
        line2 = br2.readLine();
        if (line2.isEmpty())
          break;
      }
    }

    if (line1 == null) {
      if (line2 != null) {
        if (!line2.isEmpty())
          ++resTotal;
        while ((line2 = br2.readLine()) != null) {
          if (!line2.isEmpty())
            ++resTotal;
        }
      }
    }
    if (line2 == null) {
      if (line1 != null) {
        if (!line1.isEmpty())
          ++testTotal;
        while ((line1 = br1.readLine()) != null) {
          if (!line1.isEmpty())
          ++testTotal;
        }
      }
    }
    
    double precision = ((double) correct) / resTotal;
    double recall = ((double) correct) / testTotal;
    double f1 = 2 * precision * recall / (precision + recall);
    double fPoint5 = 1.25 * precision * recall / (0.25 * precision + recall);
    double f2 = 5 * precision * recall / (4 * precision + recall);
    System.out.println("precision:" + precision + " recall:" + recall +
        " F1:" + f1 + " F0.5:" + fPoint5 + " F2:" + f2);
  }

  private void produceNgroups(String resultFile, String ngroupFile)
      throws IOException {
    FileInputStream fis = new FileInputStream(resultFile);
    DataInputStream dis = new DataInputStream(fis);
    BufferedReader br = new BufferedReader(new InputStreamReader(dis));
    PrintWriter out =
        new PrintWriter(new BufferedWriter(new FileWriter(ngroupFile)));
    String line;
    String outputLine = "";
    boolean isGrouping = false;
    while ((line = br.readLine()) != null) {
      if (!line.isEmpty()) {
        String[] strs = line.split(" ");
        if (!isGrouping && strs[2].equals("I")) {
          outputLine += strs[0] + " ";
          //out.print(strs[0] + " ");
        } else if (isGrouping && !strs[2].equals("I")) {
          out.println(outputLine);
          if (strs[2].equals("O")) {
            isGrouping = false;
            outputLine = "";
          } else {
            isGrouping = true;
            outputLine += strs[0] + " ";
            //out.print(strs[0] + " ");
          }
        } else if (isGrouping && strs[2].equals("I")) {
          outputLine += strs[0] + " ";
          //out.print(strs[0] + " ");
        } else if (!isGrouping && !strs[2].equals("I")) {
          if (!outputLine.isEmpty()) {
            out.println(outputLine);
            outputLine = "";
          }
          if (strs[2].equals("B")) {
            outputLine += strs[0] + " ";
            //out.print(strs[0] + " ");
            isGrouping = true;
          }
        }
      } else {
        if (isGrouping) {
          out.println(outputLine);
          outputLine = "";
          isGrouping = false;
        }
        out.println();
      }
    }
    out.flush();
  }
}

