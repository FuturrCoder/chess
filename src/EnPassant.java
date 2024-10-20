import javalib.worldimages.Posn;

public class EnPassant extends Capture {
  private Posn toCapture;

  public EnPassant(Piece target, Posn dest, Posn toCapture) {
    super(target, dest);
    this.toCapture = toCapture;
  }

  public Board apply(Board board) {
    return board.enPassantResult(target, dest, toCapture);
  }
}
