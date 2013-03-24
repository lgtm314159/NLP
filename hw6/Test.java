public class Test {
  public static void main(String[] args) {
    Stemmer s = new Stemmer();
    s.add("Looked".toCharArray(), 6);
    s.stem();
    System.out.println(s.toString().length());
  }
}
