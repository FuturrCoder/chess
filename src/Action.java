import javalib.worldimages.Posn;

import java.util.function.Function;

public abstract class Action implements Function<Board, Board> {
  protected final Piece target; // the piece that is moving
  protected Posn dest; // the destination of the target

  public Action(Piece target, Posn dest) {
    this.target = target;
    this.dest = dest;
  }

  public Posn getDest() {
    return dest;
  }
}
