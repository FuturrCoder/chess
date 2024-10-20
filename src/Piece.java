import javalib.impworld.WorldScene;
import javalib.worldimages.FromFileImage;
import javalib.worldimages.Posn;
import javalib.worldimages.ScaleImage;
import javalib.worldimages.WorldImage;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.stream.Collectors;

@SuppressWarnings("SameReturnValue")
public abstract class Piece {
  protected final Side color;
  protected final Posn pos;
  protected final String imageName; // first letter should be capitalized

  public Piece(Side color, Posn pos, String imageName) {
    this.color = color;
    this.pos = pos;
    this.imageName = imageName;
  }

  // get all actions the piece can make given the current board state
  public Set<Action> getActions(Board board) {
    Set<Action> toReturn = new HashSet<>();
    toReturn.addAll(getMovesAndCaptures(board));
    toReturn.addAll(getCastles(board));
    toReturn.addAll(getEnPassants(board));
    return toReturn;
  }

  // map of action from destination to action
  public Map<Posn, Action> getActionsMap(Board board) {
    Set<Action> actions = getActions(board);
    return actions.stream().collect(Collectors.toMap(Action::getDest, Function.identity()));
  }

  // create a consumer that adds the given action to the given set if it does not allow the king to be taken
  protected <X extends Action> void addIfSafe(Board board, Set<X> actions, X toAdd) {
    if (safe(board, toAdd)) {
      actions.add(toAdd);
    }
  }

  // returns whether the given action does not allow the king to be taken
  protected boolean safe(Board board, Action action) {
    return !action.apply(board).kingTargeted(color);
  }

  // all the movements (not captures) the piece can make given the current board state
  public Set<Move> getMoves(Board board) {
    Set<Move> soFar = new HashSet<>();
    iterateMoves(board, p -> addIfSafe(board, soFar, new Move(this, p)), p -> {});
    return soFar;
  }

  // all the captures the piece can make given the current board state
  public Set<Capture> getCaptures(Board board) {
    Set<Capture> soFar = new HashSet<>();
    iterateMoves(board, p -> {}, p -> addIfSafe(board, soFar, new Capture(this, p)));
    return soFar;
  }

  public Set<Move> getMovesAndCaptures(Board board) {
    Set<Move> soFar = new HashSet<>();
    iterateMoves(board, p -> addIfSafe(board, soFar, new Move(this, p)),
        p -> addIfSafe(board, soFar, new Capture(this, p)));
    return soFar;
  }

  // use the given consumers on free and occupied squares the piece can move to
  protected void iterateMoves(Board board, Consumer<Posn> forFrees, Consumer<Posn> forPieces) {
    iterateMovesBreak(board, p -> {
      forFrees.accept(p);
      return false;
    }, forPieces);
  }

  // forFrees returns whether to end (return) the method
  protected abstract void iterateMovesBreak(Board board, Function<Posn, Boolean> forFrees, Consumer<Posn> forPieces);

  // all the promotions the piece can make given the current board state
  public Set<Promotion> getPromotions(Board board) {
    return Set.of();
  }

  // all the castles the piece can make given the current board state
  public Set<Castle> getCastles(Board board) {
    return Set.of();
  }

  // all the en passants the piece can make given the current board state
  public Set<EnPassant> getEnPassants(Board board) {
    return Set.of();
  }

  // get all the deltas by which the piece can capture
  protected abstract Set<Posn> getAllDeltas();

  // can the piece capture the piece at the given posn
  public abstract boolean canCapture(Board board, Posn p);

  public Posn getPos() {
    return pos;
  }

  public Side getColor() {
    return color;
  }

  public void draw(WorldScene scene, int currentX, int currentY) {
    WorldImage pieceImage = new FromFileImage("resources/" + color.name().toLowerCase()
        + imageName + ".png");
    double sideLength = Math.max(pieceImage.getWidth(), pieceImage.getHeight());
    pieceImage = new ScaleImage(pieceImage, Board.SQUARE_SL / sideLength);
    scene.placeImageXY(pieceImage, currentX, currentY);
  }

  // create a copy of this after moving to the given posn
  public abstract Piece afterMove(Posn dest);

  // update the map of king positions if this is a king
  public void updateKing(Map<Side, Posn> kingUpdated, Posn dest) {}

  public boolean hasAction(Board board) {
    return hasMoveOrCapture(board) || hasEnPassant(board);
  }

  protected boolean hasMoveOrCapture(Board board) {
    AtomicBoolean toReturn = new AtomicBoolean(false);
    Function<Posn, Boolean> forFrees = p -> {
      if (safe(board, new Move(this, p))) {
        toReturn.set(true);
        return true;
      }
      return false;
    };
    Consumer<Posn> forPieces = p -> {
      if (safe(board, new Capture(this, p))) {
        toReturn.set(true);
      }
    };
    iterateMovesBreak(board, forFrees, forPieces);
    return toReturn.get();
  }

  protected boolean hasEnPassant(Board board) {
    return false;
  }

  // is the piece a Rook and can it participate in a castle
  public boolean canCastle() {
    return false;
  }

  // is the piece a Pawn and can it be taken in en passant by the piece at the given posn
  public boolean canBeEnPassanted(Posn p) {
    return false;
  }
}