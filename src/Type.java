import javalib.worldimages.Posn;

public enum Type {
  QUEEN, KING, ROOK, BISHOP, KNIGHT, PAWN;

  public Piece makePiece(Side color, Posn p) {
    switch (this) {
      case QUEEN:
        return new Queen(color, p);
      case KING:
        return new King(color, p);
      case ROOK:
        return new Rook(color, p);
      case BISHOP:
        return new Bishop(color, p);
      case KNIGHT:
        return new Knight(color, p);
      case PAWN:
        return new Pawn(color, p);
      default:
        throw new IllegalStateException("Unexpected value: " + this);
    }
  }
}
