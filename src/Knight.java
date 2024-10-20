import javalib.worldimages.Posn;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class Knight extends Teleporting {
  private final Set<Posn> deltas;
  public Knight(Side color, Posn pos) {
    super(color, pos, "Knight");
    Set<Posn> half = new HashSet<>(Set.of(new Posn(1, 2), new Posn(1, -2), new Posn(-1, 2), new Posn(-1, -2)));
    deltas = new HashSet<>(8);
    half.forEach(p -> deltas.addAll(List.of(p, Utils.flip(p))));
  }

  protected Set<Posn> getAllDeltas() {
    return deltas;
  }

  public Piece afterMove(Posn dest) {
    return new Knight(color, dest);
  }
}
