import javalib.worldimages.Posn;

import java.util.Set;
import java.util.function.Consumer;
import java.util.function.Function;

public abstract class Teleporting extends Piece {
  Teleporting(Side color, Posn pos, String imageName) {
    super(color, pos, imageName);
  }

  protected void iterateMovesBreak(Board board, Function<Posn, Boolean> forFrees, Consumer<Posn> forPieces) {
    Set<Posn> allDeltas = getAllDeltas();
    for (Posn d : allDeltas) {
      Posn current = Utils.add(pos, d);
      if (!board.hasPiece(current) && board.inBounds(current)) {
        if (forFrees.apply(current)) {
          return;
        }
      } else if (board.inBounds(current) && !board.getPieceSimple(current).getColor().equals(color)) {
        forPieces.accept(current);
      }
    }
  }

  public boolean canCapture(Board board, Posn p) {
    return getAllDeltas().contains(Utils.sub(p, pos));
  }
}
