import javalib.worldimages.Posn;

import java.util.HashSet;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

public class King extends Teleporting {
  private boolean hasMoved = false;
  private final Set<Posn> deltas;
  public King(Side color, Posn pos) {
    super(color, pos, "King");
    deltas = Set.of(new Posn(0, 1), new Posn(0, -1), new Posn(1, 0), new Posn(-1, 0), new Posn(1, 1),
        new Posn(-1, 1), new Posn(1, -1), new Posn(-1, -1));
  }

  public King(Side color, Posn pos, boolean hasMoved) {
    this(color, pos);
    this.hasMoved = hasMoved;
  }

  protected Set<Posn> getAllDeltas() {
    return deltas;
  }

  public Set<Castle> getCastles(Board board) {
    if (!hasMoved) {
      Set<Castle> toReturn = new HashSet<>();
      int row = color.equals(Side.BLACK) ? 0 : 7;
      Optional<Piece> rook1 = board.getPiece(new Posn(0, row));
      Optional<Piece> rook2 = board.getPiece(new Posn(7, row));
      if (rook1.map(Piece::canCastle).orElse(false) &&
          board.noPieces(Utils.addX(pos, -1), Utils.addX(pos, -2), Utils.addX(pos, -3)) &&
          board.noneTargeted(color.invert(), pos, Utils.addX(pos, -1), Utils.addX(pos, -2))) {
        toReturn.add(new Castle(this, rook1.get()));
      }
      if (rook2.map(Piece::canCastle).orElse(false) &&
          board.noPieces(Utils.addX(pos, 1), Utils.addX(pos, 2)) &&
          board.noneTargeted(color.invert(), pos, Utils.addX(pos, 1), Utils.addX(pos, 2))) {
        toReturn.add(new Castle(this, rook2.get()));
      }
      return toReturn;
    } else {
      return Set.of();
    }
  }

  public void updateKing(Map<Side, Posn> kingUpdated, Posn dest) {
    kingUpdated.put(color, dest);
  }

  public Piece afterMove(Posn dest) {
    return new King(color, dest, true);
  }
}
