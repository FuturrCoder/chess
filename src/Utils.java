import javalib.worldimages.Posn;

public class Utils {
  public static Posn add(Posn p1, Posn p2) {
    return new Posn(p1.x + p2.x, p1.y + p2.y);
  }

  // p1 - p2
  public static Posn sub(Posn p1, Posn p2) {
    return new Posn(p1.x - p2.x, p1.y - p2.y);
  }

  public static Posn addX(Posn p, int x) {
    return new Posn(p.x + x, p.y);
  }

  public static Posn abs(Posn p) {
    return new Posn(Math.abs(p.x), Math.abs(p.y));
  }

  public static Posn mult(Posn p1, Posn p2) {
    return new Posn(p1.x * p2.x, p1.y * p2.y);
  }

  public static Posn mult(Posn p, int n) {
    return new Posn(p.x * n, p.y * n);
  }

  public static Posn negate(Posn p) {
    return mult(p, -1);
  }

  @SuppressWarnings("SuspiciousNameCombination")
  public static Posn flip(Posn p) {
    return new Posn(p.y, p.x);
  }

  public static boolean diffRow(Posn p1, Posn p2) {
    return p1.y != p2.y;
  }

  public static boolean diffColumn(Posn p1, Posn p2) {
    return p1.x != p2.x;
  }

  public static boolean diffDiagonal(Posn p1, Posn p2) {
    Posn diff = abs(sub(p1, p2));
    return diff.x != diff.y;
  }

  // is the posn in row 0 or 7
  public static boolean atEnd(Posn p) {
    return p.y == 0 || p.y == 7;
  }
}
