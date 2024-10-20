import javalib.worldimages.Posn;

public class Castle extends Action {
  final Piece targetRook;
  final Posn rookDest;

  public Castle(Piece target, Piece targetRook) {
    super(target, target.getPos());
    int factor = targetRook.getPos().x < dest.x ? -1 : 1;
    rookDest = Utils.add(dest, new Posn(factor, 0));
    dest = Utils.add(dest, new Posn(2 * factor, 0));
    this.targetRook = targetRook;
  }

  public Board apply(Board board) {
    return board.castleResult(target, targetRook, dest, rookDest);
  }
}
