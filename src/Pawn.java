import javalib.worldimages.Posn;

import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class Pawn extends Teleporting {
  private boolean hasMoved = false;
  private boolean justMadeFirstMove = false;
  private final Set<Posn> captureDeltas;

  public Pawn(Side color, Posn pos) {
    super(color, pos, "Pawn");
    captureDeltas = flipIfWhite(Set.of(new Posn(-1, 1), new Posn(1, 1)));
  }

  public Pawn(Side color, Posn pos, boolean hasMoved, boolean justMadeFirstMove) {
    this(color, pos);
    this.hasMoved = hasMoved;
    this.justMadeFirstMove = justMadeFirstMove;
  }

  protected Set<Posn> getAllDeltas() {
    return captureDeltas; // for abstract class implementation of captures
  }

  // flip the given posns around the x-axis if the side is white
  private Set<Posn> flipIfWhite(Set<Posn> posns) {
    return color.equals(Side.WHITE) ?
        posns.stream().map(Utils::negate).collect(Collectors.toSet()) : posns;
  }

  private Posn flipIfWhite(Posn p) {
    return color.equals(Side.WHITE) ? Utils.negate(p) : p;
  }

  public Set<Move> getMoves(Board board) {
    Posn delta = color.equals(Side.WHITE) ? new Posn(0, -1) : new Posn(0, 1);
    Posn dest = Utils.add(pos, delta);
    Posn dest2 = Utils.add(dest, delta);
    HashSet<Move> soFar = new HashSet<>();
    if (board.hasPiece(dest)) {
      return soFar;
    } else if (!board.hasPiece(dest2) && !hasMoved) {
      addIfSafe(board, soFar, new Move(this, dest2));
    }
    addIfSafe(board, soFar, Utils.atEnd(dest) ?
        new Move(this, dest, new Promotion(this, dest)) : new Move(this, dest));
    return soFar;
  }

  public Set<Capture> getCaptures(Board board) {
    Set<Capture> toReturn = super.getCaptures(board);
    for (Capture c : toReturn) {
      if (Utils.atEnd(c.getDest())) {
        c.setSideEffect(new Promotion(this, c.getDest()));
      }
    }
    return toReturn;
  }

  public Set<Move> getMovesAndCaptures(Board board) {
    return Stream.concat(getMoves(board).stream(), getCaptures(board).stream())
        .collect(Collectors.toSet());
  }

  public Set<Promotion> getPromotions(Board board) {
    Set<Move> moves = getMovesAndCaptures(board);
    Set<Promotion> toReturn = new HashSet<>();
    for (Move m : moves) {
      m.addSideEffect(toReturn);
    }
    return toReturn;
  }

  public Set<EnPassant> getEnPassants(Board board) {
    Posn p1 = Utils.add(pos, new Posn(1, 0));
    Posn p2 = Utils.sub(pos, new Posn(1, 0));
    Set<EnPassant> toReturn = new HashSet<>(2);
    if (board.getPiece(p1).map(piece -> piece.canBeEnPassanted(pos)).orElse(false)) {
      toReturn.add(new EnPassant(this, Utils.add(p1, flipIfWhite(new Posn(0, 1))), p1));
    }
    if (board.getPiece(p2).map(piece -> piece.canBeEnPassanted(pos)).orElse(false)) {
      toReturn.add(new EnPassant(this, Utils.add(p2, flipIfWhite(new Posn(0, 1))), p2));
    }
    return toReturn;
  }

  public Piece afterMove(Posn dest) {
    if (!hasMoved) {
      return new Pawn(color, dest, true, true);
    }
    return new Pawn(color, dest, true, false);
  }

  protected boolean hasMoveOrCapture(Board board) {
    for (Move move : getMovesAndCaptures(board)) {
      if (safe(board, move)) {
        return true;
      }
    }
    return false;
  }

  public boolean canBeEnPassanted(Posn p) {
    return justMadeFirstMove && Utils.abs(Utils.sub(pos, p)).equals(new Posn(1, 0));
  }
}
