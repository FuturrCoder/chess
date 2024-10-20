import javalib.worldimages.Posn;

import java.util.List;

public class Rook extends Sliding {
  private boolean hasMoved = false;

  public Rook(Side color, Posn pos) {
    super(color, pos, "Rook", List.of(new Posn(0, 1), new Posn(1, 0)));
  }

  public Rook(Side color, Posn pos, boolean hasMoved) {
    this(color, pos);
    this.hasMoved = hasMoved;
  }

  public boolean canCapture(Board board, Posn target) {
    if (Utils.diffRow(pos, target) && Utils.diffColumn(pos, target)) {
      return false;
    }
    return super.canCapture(board, target);
  }

  public boolean canCastle() {
    return !hasMoved;
  }

  public Piece afterMove(Posn dest) {
    return new Rook(color, dest, true);
  }
}
