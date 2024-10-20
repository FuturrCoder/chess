import javalib.impworld.World;
import javalib.impworld.WorldScene;
import javalib.worldimages.OutlineMode;
import javalib.worldimages.Posn;
import javalib.worldimages.RectangleImage;
import javalib.worldimages.TextImage;

import java.awt.*;
import java.util.*;
import java.util.List;

public class Chess extends World {
//  public static final Color MSG_COLOR = new Color(53, 167, 255);
  private Board board;
  private Player current;
  private Player next;
  private boolean hasSelection = false; // is a piece selected
  private Posn selection;
  private Posn hover = new Posn(0, 0);
  private Map<Posn, Action> actions = new HashMap<>();

  private Chess() {
    this.board = new Board();
  }

  public void launchGame() {
    this.bigBang(this.pixelWidth(), this.pixelHeight());
  }

  // switch the current and next player
  private void nextTurn() {
    Player temp = current;
    current = next;
    next = temp;
  }

  private int pixelWidth() {
    return board.pixelWidth();
  }

  private int pixelHeight() {
    return board.pixelHeight();
  }

  // convert the given pixel posn to a board coordinate
  public Posn toCoord(Posn p) {
    return board.toCoord(p);
  }

  // reset the game to the starting state
  private void reset() {
    board.reset();
    actions = new HashMap<>();
    hasSelection = false;
  }

  // select the piece (if it exists) at the given posn (in square coordinates)
  private void select(Posn p) {
    board.getPiece(p).ifPresent(piece -> {
      if (piece.getColor().equals(current.getSide())) {
        hasSelection = true;
        selection = p;
        actions = board.getPieceSimple(p).getActionsMap(board);
      }
    });
  }

  public WorldScene makeScene() {
    WorldScene scene = getEmptyScene();
    if (hasSelection) {
      board.draw(scene, selection, hover, actions);
    } else {
      board.draw(scene);
    }
    return scene;
  }

  public void onMousePressed(Posn p, String buttonName) {
    if (!buttonName.equals("LeftButton")) {
      return;
    }
    Posn coord = toCoord(p);
    Action action = actions.get(coord);
    if (hasSelection && action != null) {
      board = action.apply(board);
      hasSelection = false;
      nextTurn();
      if (!board.hasAction(current.getSide())) {
        endOfWorld(next.getSide() + " wins!");
      }
    } else {
      hasSelection = false;
      select(coord);
    }
  }

  public void onKeyEvent(String name) {
    if (name.equals("escape")) {
      hasSelection = false;
    }
  }

  public void onMouseMoved(Posn mouse, String buttonName) {
    if (hasSelection) {
      hover = toCoord(mouse);
    }
  }

  public void onTick() {}

  @SuppressWarnings("WrapperTypeMayBePrimitive")
  public WorldScene lastScene(String msg) {
    WorldScene scene = makeScene();
    Side winning = next.getSide();
    Color bgColor = board.getSquareColor(new Posn(0, winning.id()), false, false);
    Color textColor = board.getSquareColor(new Posn(0, winning.invert().id()), true, false);
    textColor = winning.equals(Side.WHITE) ? textColor.darker() : textColor.brighter();
    TextImage textImage = new TextImage(msg, 24, textColor);
    Double width = textImage.getWidth();
    Double height = textImage.getHeight();
    scene.placeImageXY(new RectangleImage(width.intValue() + 20, height.intValue() + 10,
        OutlineMode.SOLID, bgColor), pixelWidth() / 2, pixelHeight() / 2);
    scene.placeImageXY(textImage, pixelWidth() / 2 - 3, pixelHeight() / 2);
    return scene;
  }

  public Board getBoard() {
    return board;
  }

  public static class Builder {
    private final Chess chess;

    public Builder() {
      this.chess = new Chess();
    }

    public Builder setFirstPlayer(Player player) {
      if (chess.current != null) {
        throw new IllegalArgumentException("first player is already set");
      }
      chess.current = player;
      return this;
    }

    public Builder setSecondPlayer(Player player) {
      if (chess.next != null) {
        throw new IllegalArgumentException("second player is already set");
      }
      chess.next = player;
      return this;
    }

    public Builder addPiece(Type type, Side color, Posn p) {
      chess.board.placePiece(type.makePiece(color, p));
      return this;
    }

    public Builder addPieces(List<Piece> pieces) {
      for (Piece piece : pieces) {
        chess.board.placePiece(piece);
      }
      return this;
    }

    // make all added pieces the given color
    public Builder addPieces(List<Type> pieces, List<Posn> posns, Side color) {
      if (pieces.size() != posns.size()) {
        throw new IllegalArgumentException("number of pieces must equal number of posns");
      }
      for (int i = 0; i < pieces.size(); i++) {
        chess.board.placePiece(pieces.get(i).makePiece(color, posns.get(i)));
      }
      return this;
    }

    public Builder startingBoard() {
      chess.reset();
      return this;
    }

    public Chess build() {
      return this.chess;
    }
  }
}