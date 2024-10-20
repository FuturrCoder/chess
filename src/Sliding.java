import javalib.worldimages.Posn;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Consumer;
import java.util.function.Function;

public abstract class Sliding extends Piece {
  protected final List<Posn> deltas;

  public Sliding(Side color, Posn pos, String imageName, List<Posn> deltas) {
    super(color, pos, imageName);
    this.deltas = deltas;
  }

  protected void iterateMovesBreak(Board board, Function<Posn, Boolean> forFrees, Consumer<Posn> forPieces) {
    Set<Posn> allDeltas = getAllDeltas();
    for (Posn d : allDeltas) {
      Posn current = Utils.add(pos, d);
      while (!board.hasPiece(current) && board.inBounds(current)) {
        if (forFrees.apply(current)) {
          return;
        }
        current = Utils.add(current, d);
      }
      if (board.inBounds(current) && !board.getPieceSimple(current).getColor().equals(color)) {
        forPieces.accept(current);
      }
    }
  }

  public boolean canCapture(Board board, Posn target) {
    AtomicBoolean toReturn = new AtomicBoolean(false);
    Function<Posn, Boolean> forFrees = p -> {
      if (p.equals(target)) {
        toReturn.set(true);
        return true;
      }
      return false;
    };
    Consumer<Posn> forPieces = p -> {
      if (p.equals(target)) {
        toReturn.set(true);
      }
    };
    iterateMovesBreak(board, forFrees, forPieces);
    return toReturn.get();
  }

  protected Set<Posn> getAllDeltas() {
    Set<Posn> soFar = new HashSet<>(deltas.size() * 4);
    for (Posn d : deltas) {
      soFar.addAll(List.of(d, Utils.mult(d, new Posn(1, -1)), Utils.mult(d, new Posn(-1, 1)),
          Utils.mult(d, new Posn(-1, -1))));
    }
    return soFar;
  }
}
