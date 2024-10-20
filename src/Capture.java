import javalib.worldimages.Posn;

public class Capture extends Move {
  public Capture(Piece target, Posn dest) {
    super(target, dest);
  }

  public Capture(Piece target, Posn dest, Promotion sideEffect) {
    super(target, dest, sideEffect);
  }

  public Board apply(Board board) {
    Board updated = board.captureResult(target, dest);
    if (hasSideEffect) {
      updated = sideEffect.apply(updated);
    }
    return updated;
  }
}
