import javalib.worldimages.Posn;

import java.util.List;

public class Bishop extends Sliding {
  Bishop(Side color, Posn pos) {
    super(color, pos, "Bishop", List.of(new Posn(1, 1)));
  }

  public boolean canCapture(Board board, Posn target) {
    if (Utils.diffDiagonal(pos, target)) {
      return false;
    }
    return super.canCapture(board, target);
  }

  public Piece afterMove(Posn dest) {
    return new Bishop(color, dest);
  }
}
