import javalib.impworld.WorldScene;
import javalib.worldimages.CircleImage;
import javalib.worldimages.OutlineMode;
import javalib.worldimages.Posn;
import javalib.worldimages.RectangleImage;

import java.awt.*;
import java.util.*;
import java.util.List;

public class Board {
  public static final int WIDTH = 8; // in squares
  public static final int HEIGHT = 8; // in squares
  public static final int SQUARE_SL = 60; // side length of squares
  public static final int DOT_RADIUS = SQUARE_SL / 7;
  public static final Color COLOR_1 = new Color(225, 191, 152); // color of top left square
  public static final Color COLOR_2 = new Color(144, 79, 46);
  public static final Color SELECT_COLOR_1 = new Color(124, 154, 101);
  public static final Color SELECT_COLOR_2 = new Color(66, 104, 49);
  public static final Color CHECK_COLOR_1 = new Color(206, 58, 38);
  public static final Color CHECK_COLOR_2 = new Color(184, 39, 18);
  public static final List<Type> ROW_1 = List.of(Type.ROOK, Type.KNIGHT, Type.BISHOP, Type.QUEEN,
      Type.KING, Type.BISHOP, Type.KNIGHT, Type.ROOK);

  private Map<Posn, Piece> pieces; // top left is (0, 0)
  private Map<Side, Posn> kingPos;
  private Action lastMove;

  public Board() {
    this.pieces = new HashMap<>();
    this.kingPos = new HashMap<>();
  }

  public Board(Map<Posn, Piece> pieces, Map<Side, Posn> kingPos) {
    this.pieces = pieces;
    this.kingPos = kingPos;
  }

  public Optional<Piece> getPiece(Posn p) {
    return Optional.ofNullable(pieces.get(p));
  }

  public Piece getPieceSimple(Posn p) {
    return pieces.get(p);
  }

  public boolean hasPiece(Posn p) {
    return pieces.containsKey(p);
  }

  public boolean noPieces(Posn... posns) {
    for (Posn p : posns) {
      if (pieces.containsKey(p)) {
        return false;
      }
    }
    return true;
  }

  public void placePiece(Piece piece) {
    pieces.put(piece.getPos(), piece);
  }

  public void removePiece(Posn p) {
    pieces.remove(p);
  }

  public boolean inBounds(Posn p) {
    return p.x >= 0 && p.y >= 0 && p.x < WIDTH && p.y < HEIGHT;
  }

  public int pixelWidth() {
    return HEIGHT * SQUARE_SL;
  }

  public int pixelHeight() {
    return WIDTH * SQUARE_SL;
  }

  // reset the board to the starting state
  public void reset() {
    pieces = new HashMap<>(WIDTH * 4);
    kingPos = Map.of(Side.BLACK, new Posn(4, 0), Side.WHITE, new Posn(4, 7));
    placeRow1(0, Side.BLACK);
    placeRow1(7, Side.WHITE);
    placePawns(1, Side.BLACK);
    placePawns(6, Side.WHITE);
  }

  // place the starting pieces at the given row, with the given color
  private void placeRow1(int row, Side color) {
    for (int i = 0; i < WIDTH; i++) {
      placePiece(ROW_1.get(i).makePiece(color, new Posn(i, row)));
    }
  }

  // place pawns of the given color at the given row
  private void placePawns(int row, Side color) {
    for (int i = 0; i < WIDTH; i++) {
      placePiece(Type.PAWN.makePiece(color, new Posn(i, row)));
    }
  }

  // draw the board on the given scene
  public void draw(WorldScene scene) {
    Posn kingB = kingPos.get(Side.BLACK);
    Posn kingW = kingPos.get(Side.WHITE);
    for (int x = 0; x < WIDTH; x++) {
      for (int y = 0; y < HEIGHT; y++) {
        Posn current = new Posn(x, y);
        placeSquare(scene, current, false, false, false, (current.equals(kingB) &&
            kingTargeted(Side.BLACK)) || (current.equals(kingW) && kingTargeted(Side.WHITE)));
      }
    }
  }

  public void draw(WorldScene scene, Posn selection, Posn hover, Map<Posn, Action> actions) {
    Posn kingB = kingPos.get(Side.BLACK);
    Posn kingW = kingPos.get(Side.WHITE);
    for (int x = 0; x < WIDTH; x++) {
      for (int y = 0; y < HEIGHT; y++) {
        Posn current = new Posn(x, y);
        placeSquare(scene, current, selection.equals(current), actions.containsKey(current)
            || selection.equals(current), hover.equals(current), (current.equals(kingB) &&
            kingTargeted(Side.BLACK)) || (current.equals(kingW) && kingTargeted(Side.WHITE)));
      }
    }
  }

  // place a chess square (with piece) at the given coordinate
  private void placeSquare(WorldScene scene, Posn current, boolean selected, boolean moveOption,
                           boolean hover, boolean checked) {
    int currentX = current.x * SQUARE_SL + SQUARE_SL / 2;
    int currentY = current.y * SQUARE_SL + SQUARE_SL / 2;
    Color c = getSquareColor(current, (selected || hover) && moveOption, checked);
    scene.placeImageXY(new RectangleImage(SQUARE_SL, SQUARE_SL, OutlineMode.SOLID, c),
        currentX, currentY);
    getPiece(current).ifPresent(piece -> piece.draw(scene, currentX, currentY));
    if (moveOption && !selected) {
      scene.placeImageXY(new CircleImage(DOT_RADIUS, OutlineMode.SOLID,
          getSquareColor(current, true, false)), currentX, currentY);
    }
  }

  // get the color of the square at the given posn
  public Color getSquareColor(Posn p, boolean selected, boolean checked) {
    if (selected) {
      return whiteSquare(p) ? SELECT_COLOR_1 : SELECT_COLOR_2;
    } else if (checked) {
      return whiteSquare(p) ? CHECK_COLOR_1 : CHECK_COLOR_2;
    } else {
      return whiteSquare(p) ? COLOR_1 : COLOR_2;
    }
  }

  // does the given posn represent a white square
  private boolean whiteSquare(Posn p) {
    return ((p.y % 2) + p.x) % 2 == 0;
  }

  // return the result of moving the given piece to the given destination
  public Board moveResult(Piece piece, Posn dest) {
    Map<Posn, Piece> updated = new HashMap<>(this.pieces);
    updated.remove(piece.getPos());
    updated.put(dest, piece.afterMove(dest));
    Map<Side, Posn> kingUpdated = new HashMap<>(this.kingPos);
    piece.updateKing(kingUpdated, dest);
    return new Board(updated, kingUpdated);
  }

  // return the result of capturing the piece at the given posn with the given piece
  public Board captureResult(Piece piece, Posn dest) {
    return moveResult(piece, dest);
  }

  // return the result of promoting the piece at the given posn
  public Board promotionResult(Piece target, Posn dest) {
    Map<Posn, Piece> updated = new HashMap<>(this.pieces);
    updated.put(dest, new Queen(target.getColor(), dest));
    return new Board(updated, new HashMap<>(this.kingPos));
  }

  public Board castleResult(Piece targetKing, Piece targetRook, Posn kingDest, Posn rookDest) {
    Board updated = moveResult(targetKing, kingDest);
    updated.pieces.remove(targetRook.getPos());
    updated.pieces.put(rookDest, targetRook.afterMove(rookDest));
    return updated;
  }

  public Board enPassantResult(Piece target, Posn dest, Posn toCapture) {
    Board updated = moveResult(target, dest);
    updated.pieces.remove(toCapture);
    return updated;
  }

  // convert the given pixel posn to a board coordinate
  public Posn toCoord(Posn p) {
    return new Posn(p.x / SQUARE_SL, p.y / SQUARE_SL);
  }

  // can the piece at the given posn be captured by a piece from the given side
  public boolean isTargeted(Posn posn, Side side) {
    for (Map.Entry<Posn, Piece> entry : pieces.entrySet()) {
      Piece piece = entry.getValue();
      if (piece.getColor().equals(side) && piece.canCapture(this, posn)) {
        return true;
      }
    }
    return false;
  }

  public boolean noneTargeted(Side side, Posn... posns) {
    for (Posn p : posns) {
      if (isTargeted(p, side)) {
        return false;
      }
    }
    return true;
  }

  // is the king of the given side targeted
  public boolean kingTargeted(Side side) {
    return isTargeted(kingPos.get(side), side.invert());
  }

  // does any piece from the given side have a legal action
  public boolean hasAction(Side side) {
    for (Map.Entry<Posn, Piece> entry : pieces.entrySet()) {
      Piece piece = entry.getValue();
      if (piece.getColor().equals(side)) {
        if (piece.hasAction(this)) {
          return true;
        }
      }
    }
    return false;
  }
}
