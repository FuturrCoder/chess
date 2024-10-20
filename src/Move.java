import javalib.worldimages.Posn;

import java.util.Set;

public class Move extends Action {
  protected boolean hasSideEffect = false;
  protected Promotion sideEffect;

  public Move(Piece target, Posn dest) {
    super(target, dest);
  }

  public Move(Piece target, Posn dest, Promotion sideEffect) {
    super(target, dest);
    this.sideEffect = sideEffect;
    this.hasSideEffect = true;
  }

  public void setSideEffect(Promotion sideEffect) {
    this.sideEffect = sideEffect;
    this.hasSideEffect = true;
  }

  public Board apply(Board board) {
    Board updated = board.moveResult(target, dest);
    if (hasSideEffect) {
      updated = sideEffect.apply(updated);
    }
    return updated;
  }

  // add the move's side effect to the set if it has one
  public void addSideEffect(Set<Promotion> set) {
    if (hasSideEffect) {
      set.add(sideEffect);
    }
  }
}
