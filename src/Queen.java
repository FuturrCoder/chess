import javalib.worldimages.Posn;

import java.util.List;

public class Queen extends Sliding {

  static final List<Posn> DELTAS = List.of(new Posn(0, 1), new Posn(1, 0), new Posn(1, 1));
  Queen(Side color, Posn pos) {
    super(color, pos, "Queen", DELTAS);
  }

  public boolean canCapture(Board board, Posn target) {
    if (Utils.diffRow(pos, target) && Utils.diffColumn(pos, target) && Utils.diffDiagonal(pos, target)) {
      return false;
    }
    return super.canCapture(board, target);
  }

  public Piece afterMove(Posn dest) {
    return new Queen(color, dest);
  }
}
