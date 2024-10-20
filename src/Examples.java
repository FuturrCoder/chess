import javalib.worldimages.Posn;
import org.apache.commons.lang3.reflect.FieldUtils;
import tester.Tester;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

public class Examples {
  void testChess(Tester t) {
    new Chess.Builder()
        .startingBoard()
        .setFirstPlayer(new Player("Player 1", Side.WHITE))
        .setSecondPlayer(new Player("Player 2", Side.BLACK))
        .build()
        .launchGame();
  }

  void testBuilder(Tester t) throws IllegalAccessException {
    Chess chess = new Chess.Builder()
        .addPieces(List.of(Type.PAWN, Type.ROOK, Type.QUEEN), List.of(new Posn(1, 3), new Posn(4, 2),
            new Posn(3, 3)), Side.BLACK)
        .build();
    t.checkExpect(FieldUtils.readField(chess.getBoard(), "pieces", true),
        Map.of(new Posn(1, 3), new Pawn(Side.BLACK, new Posn(1, 3)), new Posn(4, 2),
            new Rook(Side.BLACK, new Posn(4, 2)), new Posn(3, 3), new Queen(Side.BLACK, new Posn(3, 3))));
  }

  void testMove(Tester t) {
    Chess start = new Chess.Builder()
        .startingBoard()
        .build();
    Board board = start.getBoard();
    checkMoves(board, new Posn(0, 0), Set.of(), t); // black rook
    checkMoves(board, new Posn(3, 7), Set.of(), t); // white queen
    checkMoves(board, new Posn(4, 7), Set.of(), t); // white king
    checkMoves(board, new Posn(1, 0), Set.of(new Posn(0, 2), new Posn(2, 2)), t); // black knight
    checkMoves(board, new Posn(2, 6), Set.of(new Posn(2, 5), new Posn(2, 4)), t); // white pawn
    board.placePiece(new Bishop(Side.BLACK, new Posn(1, 4)));
    checkMoves(board, new Posn(1, 4), Set.of(new Posn(0, 3), new Posn(0, 5), new Posn(3, 2),
        new Posn(2, 3), new Posn(2, 5)), t);
    board.placePiece(new Queen(Side.BLACK, new Posn(1, 4)));
    checkMoves(board, new Posn(1, 4), Set.of(new Posn(0, 3), new Posn(0, 5), new Posn(3, 2),
        new Posn(2, 3), new Posn(2, 5), new Posn(1, 2), new Posn(1, 3), new Posn(1, 5), new Posn(0, 4),
        new Posn(2, 4), new Posn(3, 4), new Posn(4, 4), new Posn(5, 4), new Posn(6, 4), new Posn(7, 4)), t);
    board.placePiece(new King(Side.BLACK, new Posn(2, 3)));
    checkMoves(board, new Posn(2, 3), Set.of(new Posn(1, 2), new Posn(2, 2), new Posn(3, 2),
        new Posn(1, 3), new Posn(3, 3), new Posn(2, 4), new Posn(3, 4)), t);
    Board board1 = start.getBoard();
    board1.placePiece(new Bishop(Side.BLACK, new Posn(1, 4)));
    board1.placePiece(new King(Side.BLACK, new Posn(2, 3)));

    new ArrayList<>(board.getPieceSimple(new Posn(2, 3)).getMoves(board)).get(0).apply(board);
    t.checkExpect(board, board1);
  }

  void checkMoves(Board b, Posn p, Set<Posn> expected, Tester t) {
    t.checkExpect(b.getPieceSimple(p).getMoves(b).stream()
        .map(Move::getDest).collect(Collectors.toSet()), expected);
  }

  void testToCoord(Tester t) {
    Chess start = new Chess.Builder()
        .startingBoard()
        .build();
    t.checkExpect(start.toCoord(new Posn(0, 0)), new Posn(0, 0));
    t.checkExpect(start.toCoord(new Posn(Board.SQUARE_SL, Board.SQUARE_SL * 2 - 1)), new Posn(1, 1));
  }

  void testCanCapture(Tester t) {
    Chess game = new Chess.Builder()
        .startingBoard()
        .setFirstPlayer(new Player("Player 1", Side.WHITE))
        .setSecondPlayer(new Player("Player 2", Side.BLACK))
        .build();
    Board b = game.getBoard();
    t.checkExpect(canCapture(b, new Posn(3, 0), new Posn(2, 0)), false);
    b.placePiece(new Bishop(Side.WHITE, new Posn(1, 3)));
    t.checkExpect(canCapture(b, new Posn(1, 3), new Posn(3, 1)), true);
    t.checkExpect(canCapture(b, new Posn(1, 3), new Posn(4, 0)), false);
    b.removePiece(new Posn(3, 1));
    t.checkExpect(canCapture(b, new Posn(1, 3), new Posn(4, 0)), true);
    Chess game2 = new Chess.Builder()
        .startingBoard()
        .setFirstPlayer(new Player("Player 1", Side.WHITE))
        .setSecondPlayer(new Player("Player 2", Side.BLACK))
        .build();
    Board b2 = game2.getBoard();
    b2.placePiece(new Knight(Side.BLACK, new Posn(3, 5)));
    t.checkExpect(canCapture(b2, new Posn(3, 5), new Posn(4, 7)), true);
    t.checkExpect(canCapture(b2, new Posn(4, 6), new Posn(3, 5)), true);
    t.checkExpect(canCapture(b2, new Posn(3, 6), new Posn(3, 5)), false);
    b2.removePiece(new Posn(3, 5));
    b2.placePiece(new Rook(Side.WHITE, new Posn(4, 1)));
    t.checkExpect(canCapture(b2, new Posn(4, 0), new Posn(4, 1)), true);
    t.checkExpect(canCapture(b2, new Posn(3, 0), new Posn(4, 1)), true);
    t.checkExpect(canCapture(b2, new Posn(4, 1), new Posn(4, 0)), true);
    t.checkExpect(canCapture(b2, new Posn(4, 1), new Posn(6, 1)), false);
  }

  boolean canCapture(Board board, Posn from, Posn to) {
    return board.getPieceSimple(from).canCapture(board, to);
  }
}
