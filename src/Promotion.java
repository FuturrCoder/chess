import javalib.worldimages.Posn;

public class Promotion extends Action {
  public Promotion(Piece target, Posn dest) {
    super(target, dest);
  }

  public Board apply(Board board) {
    return board.promotionResult(target, dest);
  }
}
