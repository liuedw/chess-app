package demo;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.HashSet;

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;


public class ChessPositionTest {

    ChessPosition chess;

    @Before
    public void init() {
        chess = new ChessPosition();
    }

    @Test
    public void constructorTest() {
        chess = new ChessPosition();
        assertEquals(Player.WHITE, chess.getPlayer());
        chess = new ChessPosition(Player.BLACK);
        assertEquals(Player.BLACK, chess.getPlayer());
    }

    @Test
    public void populateTest() {
        chess.populate(Piece.BKING, 0, 4);
        chess.populate(Piece.WPAWN, 6, 3);
        chess.populate(Piece.BPAWN, 0, 0);
        chess.populate(Piece.WQUEEN, 4, 5);
        assertEquals(Piece.BKING, chess.getBoard().getPiece(0, 4));
        assertEquals(Piece.WPAWN, chess.getBoard().getPiece(6, 3));
        assertEquals(Piece.BPAWN, chess.getBoard().getPiece(0, 0));
    }

    @Test
    public void replaceTest() {
        chess.populate(Piece.BKING, 0, 4);
        chess.populate(Piece.WKNIGHT, 0, 4);
        assertEquals(Piece.WKNIGHT, chess.getBoard().getPiece(0, 4));
        chess.populate(null, 0, 4);
        assertEquals(null, chess.getBoard().getPiece(0, 4));
    }

    @Test
    public void moveTest() {
        chess.populate(Piece.BKING, 0, 4);
        chess.populate(Piece.WPAWN, 6, 3);
        chess.populate(Piece.BPAWN, 0, 0);
        chess.populate(Piece.WQUEEN, 4, 5);

        Player player1 = chess.getPlayer();

        assertEquals(Piece.BPAWN, chess.getBoard().getPiece(0, 0));

        Piece piece1 = chess.getBoard().getPiece(0, 0);
        ChessPosition chess2 = chess.move(0, 0, 4, 5);
        assertEquals(Piece.WPAWN, chess2.getBoard().getPiece(6, 3));
        assertEquals(Piece.BKING, chess2.getBoard().getPiece(0, 4));

        assertNotEquals(player1, chess2.getPlayer());
        assertEquals(player1, chess.getPlayer());

        assertEquals(piece1, chess2.getBoard().getPiece(4, 5));
        assertEquals(null, chess2.getBoard().getPiece(0, 0));

        assertEquals(Piece.WQUEEN, chess.getBoard().getPiece(4, 5));
        assertEquals(piece1, chess.getBoard().getPiece(0, 0));
    }

    @Test
    public void normallyNotInCheck() {
        chess.populate(Piece.WKING, 0, 7);
        assertEquals(false, chess.isInCheck(Player.WHITE));

        /*   R   #   #   #
         * #   # K #   # Q
         * B #   #   #   #
         * #   #   #   # P
         *   #   #   #   #
         * #   #   #   #  
         *   #   #   #   #
         * K   #   # N #  
         * White king is bottom left
         * Other K is black king
         */
        chess.populate(Piece.BPAWN, 7, 3);
        chess.populate(Piece.BKNIGHT, 5, 7);
        chess.populate(Piece.BBISHOP, 0, 2);
        chess.populate(Piece.BROOK, 1, 0);
        chess.populate(Piece.BQUEEN, 7, 1);
        chess.populate(Piece.BKING, 3, 1);
        assertEquals(false, chess.isInCheck(Player.WHITE));
        assertEquals(false, chess.isInCheck(Player.BLACK));
    }

    @Test
    public void enemyPiecesCanCheck() {
        /*   #   #   #   #
         * #   #   #   #  
         *   #   #   #   #
         * #   #   #   #  
         *   #   #   #   #
         * #   #   #   #  
         *   #   #   #   #
         * K   #   #   # R
         */
        chess.populate(Piece.WKING, 0, 7);
        chess.populate(Piece.BROOK, 7, 7);
        assertEquals(true, chess.isInCheck(Player.WHITE));

        /*   #   #   #   #
         * #   #   #   #  
         *   #   #   #   #
         * #   #   #   #  
         *   #   #   #   #
         * # N #   #   #  
         *   #   #   #   #
         * K   #   #   #  
         */
        chess.populate(null, 7, 7);
        chess.populate(Piece.BKING, 0, 7);
        chess.populate(Piece.WKNIGHT, 1, 5);
        assertEquals(true, chess.isInCheck(Player.BLACK));
    }

    @Test
    public void friendlyPiecesDontCheck() {
        /*   #   #   #   #
         * #   #   #   #  
         *   #   #   #   #
         * #   #   #   #  
         *   #   #   #   #
         * #   #   #   #  
         *   #   #   #   #
         * K   #   #   # R
         */
        chess.populate(Piece.WKING, 0, 7);
        chess.populate(Piece.WROOK, 7, 7);
        assertEquals(false, chess.isInCheck(Player.WHITE));

        /*   #   #   #   #
         * #   #   #   #  
         *   #   #   #   #
         * #   #   #   #  
         *   #   #   #   #
         * # N #   #   #  
         *   #   #   #   #
         * K   #   #   #  
         */
        chess.populate(null, 7, 7);
        chess.populate(Piece.BKING, 0, 7);
        chess.populate(Piece.BKNIGHT, 1, 5);
        assertEquals(false, chess.isInCheck(Player.BLACK));
    }

    @Test
    public void checksCanBeBlocked() {
        /*   #   #   #   #
         * #   #   #   #  
         *   #   #   #   #
         * #   #   #   #  
         *   #   #   #   #
         * #   #   #   #  
         *   #   #   #   #
         * K   # N #   # R
         */
        chess.populate(Piece.WKING, 0, 7);
        chess.populate(Piece.BROOK, 7, 7);
        // Blocked by friendly piece
        chess.populate(Piece.WKNIGHT, 3, 7);
        assertEquals(false, chess.isInCheck(Player.WHITE));

        // Blocked by enemy piece
        chess.populate(Piece.BKING, 0, 7);
        chess.populate(Piece.WROOK, 7, 7);
        assertEquals(false, chess.isInCheck(Player.BLACK));
    }

    @Test
    public void possibleMovesTest() {
        /* Q #   #   #   #
         * #   #   #   #  
         *   #   #   #   #
         * #   #   #   P  
         *   #   #   #   #
         * #   #   #   #  
         *   #   #   #   #
         * #   # N #   #  
         */
        chess.populate(Piece.WQUEEN, 0, 0);
        chess.populate(Piece.WKNIGHT, 3, 7);
        chess.populate(Piece.WPAWN, 6, 3);

        Set<Move> expected = new HashSet<>();
        // Queen
        for (int i = 1; i < 8; i++) {
            expected.add(new Move(0, 0, i, 0));
            expected.add(new Move(0, 0, 0, i));
            expected.add(new Move(0, 0, i, i));
        }
        // Knight
        expected.add(new Move(3, 7, 1, 6));
        expected.add(new Move(3, 7, 2, 5));
        expected.add(new Move(3, 7, 4, 5));
        expected.add(new Move(3, 7, 5, 6));
        // Pawn
        expected.add(new Move(6, 3, 6, 2));

        Set<Move> actual = chess.possibleMoves();

        assertEquals(expected, actual);
    }

    @Test
    public void friendlyPiecesBlockMoves() {
        /* Q P   #   #   #
         * #   #   #   #  
         * P #   #   #   #
         * #   # P #   #  
         *   #   #   #   #
         * #   #   #   #  
         *   #   #   #   #
         * #   #   #   #  
         */
        chess.populate(Piece.WQUEEN, 0, 0);
        chess.populate(Piece.WPAWN, 0, 2);
        chess.populate(Piece.WPAWN, 1, 0);
        chess.populate(Piece.WPAWN, 3, 3);

        Set<Move> expected = new HashSet<>();
        // Queen
        expected.add(new Move(0, 0, 0, 1));
        expected.add(new Move(0, 0, 1, 1));
        expected.add(new Move(0, 0, 2, 2));
        // Pawns
        expected.add(new Move(0, 2, 0, 1));
        expected.add(new Move(3, 3, 3, 2));

        Set<Move> actual = chess.possibleMoves();

        assertEquals(expected, actual);
    }

    @Test
    public void enemyPiecesBlockMovesButCanBeCaptured() {
        /* Q P   #   #   #
         * #   #   #   #  
         * P #   #   #   #
         * #   # P #   #  
         *   #   #   #   #
         * #   #   #   #  
         *   #   #   #   #
         * #   #   #   #  
         */
        chess.populate(Piece.WQUEEN, 0, 0);
        chess.populate(Piece.BPAWN, 0, 2);
        chess.populate(Piece.BPAWN, 1, 0);
        chess.populate(Piece.BPAWN, 3, 3);

        Set<Move> expected = new HashSet<>();
        // Queen
        expected.add(new Move(0, 0, 0, 1));
        expected.add(new Move(0, 0, 0, 2));
        expected.add(new Move(0, 0, 1, 0));
        expected.add(new Move(0, 0, 1, 1));
        expected.add(new Move(0, 0, 2, 2));
        expected.add(new Move(0, 0, 3, 3));
        // Can't move enemy pawns

        Set<Move> actual = chess.possibleMoves();

        assertEquals(expected, actual);
    }

    @Test
    public void pawnsMoveProperly() {
        /* All pieces are pawns, W/B indicate color
         *   W   #   #   #
         * B   #   #   #  
         *   #   #   #   #
         * #   # B #   #  
         *   #   W   #   #
         * #   #   #   B  
         *   #   #   W   #
         * #   #   #   #  
         */
        chess.populate(Piece.WPAWN, 1, 0);
        chess.populate(Piece.WPAWN, 3, 4);
        chess.populate(Piece.WPAWN, 5, 6);
        chess.populate(Piece.BPAWN, 0, 1);
        chess.populate(Piece.BPAWN, 3, 3);
        chess.populate(Piece.BPAWN, 6, 5);

        Set<Move> expected = new HashSet<>();
        // Top white pawn can't move
        // Middle white pawn can't move
        // Bottom white pawn can capture, advance 1, or advance 2
        expected.add(new Move(5, 6, 6, 5));
        expected.add(new Move(5, 6, 5, 5));
        expected.add(new Move(5, 6, 5, 4));

        Set<Move> actual = chess.possibleMoves();

        assertEquals(expected, actual);

        // Check from black's perspective
        chess.setPlayer(Player.BLACK);

        expected = new HashSet<>();
        // Top black pawn can advance 1 or 2
        expected.add(new Move(0, 1, 0, 2));
        expected.add(new Move(0, 1, 0, 3));
        // Middle black pawn can't move
        // Bottom black pawn can capture or advance 1
        expected.add(new Move(6, 5, 5, 6));
        expected.add(new Move(6, 5, 6, 6));

        actual = chess.possibleMoves();

        assertEquals(expected, actual);
    }

    @Test
    public void cannotMoveKingIntoCheck() {
        /*   #   #   #   #
         * #   #   #   #  
         *   #   #   #   #
         * # B #   #   #  
         *   #   #   #   #
         * #   #   #   #  
         *   #   #   P P P
         * #   #   #   K  
         */
        chess.populate(Piece.WKING, 6, 7);
        chess.populate(Piece.WPAWN, 5, 6);
        chess.populate(Piece.WPAWN, 6, 6);
        chess.populate(Piece.WPAWN, 7, 6);
        chess.populate(Piece.BBISHOP, 1, 3);

        Set<Move> moves = chess.possibleMoves();
        // Shouldn't be able to move king left
        assertEquals(false, moves.contains(new Move(6, 7, 5, 7)));
    }

    @Test
    public void cannotMovePieceBlockingCheck() {
        /*   #   #   #   #
         * #   #   #   #  
         *   #   #   #   #
         * #   B   #   #  
         *   #   #   #   #
         * #   #   #   #  
         *   #   #   P P P
         * #   #   #   K  
         */
        chess.populate(Piece.WKING, 6, 7);
        chess.populate(Piece.WPAWN, 5, 6);
        chess.populate(Piece.WPAWN, 6, 6);
        chess.populate(Piece.WPAWN, 7, 6);
        chess.populate(Piece.BBISHOP, 2, 3);

        Set<Move> moves = chess.possibleMoves();
        // Shouldn't be able to move left pawn
        assertEquals(false, moves.contains(new Move(5, 6, 5, 5)));
        assertEquals(false, moves.contains(new Move(5, 6, 5, 4)));
    }

    @Test
    public void cannotLeaveKingInCheck() {
        /*   R   #   #   #
         * #   #   #   #  
         *   #   #   #   #
         * #   #   #   #  
         *   #   #   #   #
         * #   #   #   #  
         *   #   #   #   P
         * # K #   #   #  
         */
        chess.populate(Piece.WKING, 1, 7);
        chess.populate(Piece.BROOK, 1, 0);
        chess.populate(Piece.WPAWN, 7, 6);

        Set<Move> moves = chess.possibleMoves();
        // Shouldn't be able to move pawn
        assertEquals(false, moves.contains(new Move(7, 6, 7, 5)));
        assertEquals(false, moves.contains(new Move(7, 6, 7, 4)));
    }
    
    @Test
    public void canMoveKingOutOfCheck() {
        /*   R   #   #   #
         * #   #   #   #  
         *   #   #   #   #
         * #   #   #   #  
         *   #   #   #   #
         * #   #   #   #  
         *   #   #   #   #
         * # K #   #   #  
         */
        chess.populate(Piece.WKING, 1, 7);
        chess.populate(Piece.BROOK, 1, 0);

        Set<Move> moves = chess.possibleMoves();
        // King can move left, one test is probably enough
        assertEquals(true, moves.contains(new Move(1, 7, 0, 7)));
    }

    @Test
    public void canBlockCheckingPiece() {
        /*   R   #   #   #
         * #   #   #   #  
         *   #   #   #   #
         * #   # B #   #  
         *   #   #   #   #
         * #   #   #   #  
         *   #   #   #   #
         * # K #   #   #  
         */
        chess.populate(Piece.WKING, 1, 7);
        chess.populate(Piece.BROOK, 1, 0);
        chess.populate(Piece.WBISHOP, 3, 3);

        Set<Move> moves = chess.possibleMoves();
        // Bishop can block the rook at two positions
        assertEquals(true, moves.contains(new Move(3, 3, 1, 1)));
        assertEquals(true, moves.contains(new Move(3, 3, 1, 5)));
    }

    @Test
    public void canCaptureCheckingPiece() {
        /*   R   #   #   #
         * #   P   #   #  
         *   #   #   #   #
         * #   #   #   #  
         *   #   #   #   #
         * #   #   #   #  
         *   #   #   #   #
         * # K #   #   #  
         */
        chess.populate(Piece.WKING, 1, 7);
        chess.populate(Piece.BROOK, 1, 0);
        chess.populate(Piece.WPAWN, 2, 1);

        Set<Move> moves = chess.possibleMoves();
        // Pawn can capture rook
        assertEquals(true, moves.contains(new Move(2, 1, 1, 0)));
    }

    @Test
    public void canCheckEnemyKing() {
        /*   K   #   #   #
         * P P P   #   #  
         *   #   #   #   #
         * #   #   #   #  
         *   #   #   # R #
         * #   #   #   #  
         *   #   #   #   #
         * # K #   #   #  
         */
        chess.populate(Piece.WKING, 1, 7);
        chess.populate(Piece.BKING, 1, 0);
        chess.populate(Piece.BPAWN, 0, 1);
        chess.populate(Piece.BPAWN, 1, 1);
        chess.populate(Piece.BPAWN, 2, 1);
        chess.populate(Piece.WROOK, 6, 4);

        Set<Move> moves = chess.possibleMoves();
        // Rook can check enemy king
        assertEquals(true, moves.contains(new Move(6, 4, 6, 0)));
    }

    @Test
    public void fourMoveCheckmate() {
        /* Starting position
         * R N B Q K B N R
         * P P P P P P P P
         *   #   #   #   #
         * #   #   #   #  
         *   #   #   #   #
         * #   #   #   #  
         * P P P P P P P P
         * R N B Q K B N R
         */
        // White
        chess.populate(Piece.WROOK, 0, 7);
        chess.populate(Piece.WKNIGHT, 1, 7);
        chess.populate(Piece.WBISHOP, 2, 7);
        chess.populate(Piece.WQUEEN, 3, 7);
        chess.populate(Piece.WKING, 4, 7);
        chess.populate(Piece.WBISHOP, 5, 7);
        chess.populate(Piece.WKNIGHT, 6, 7);
        chess.populate(Piece.WROOK, 7, 7);
        for (int i = 0; i < 8; i++) {
            chess.populate(Piece.WPAWN, i, 6);
        }
        // Black
        chess.populate(Piece.BROOK, 0, 0);
        chess.populate(Piece.BKNIGHT, 1, 0);
        chess.populate(Piece.BBISHOP, 2, 0);
        chess.populate(Piece.BQUEEN, 3, 0);
        chess.populate(Piece.BKING, 4, 0);
        chess.populate(Piece.BBISHOP, 5, 0);
        chess.populate(Piece.BKNIGHT, 6, 0);
        chess.populate(Piece.BROOK, 7, 0);
        for (int i = 0; i < 8; i++) {
            chess.populate(Piece.BPAWN, i, 1);
        }

        /* 1. e4 e5
         * 2. Bc4 Nc6
         * 3. Qh5 Bc5
         * 4. Qxf7#
         * R # B Q K # N R
         * P P P P # Q P P
         *   # N #   #   #
         * #   B   P   #  
         *   # B # P #   #
         * #   #   #   #  
         * P P P P   P P P
         * R N B   K   N R
         */
        List<Move> moves = new ArrayList<>();
        moves.add(new Move(4, 6, 4, 4));  // e4
        moves.add(new Move(4, 1, 4, 3));  // e5
        moves.add(new Move(5, 7, 2, 4));  // Bc4
        moves.add(new Move(1, 0, 2, 2));  // Nc6
        moves.add(new Move(3, 7, 7, 3));  // Qh5
        moves.add(new Move(5, 0, 2, 3));  // Bc5
        moves.add(new Move(7, 3, 5, 1));  // Qxf7#

        for (Move move : moves) {
            Set<Move> possibleMoves = chess.possibleMoves();
            assertEquals(true, possibleMoves.contains(move));
            chess = chess.move(move);
        }
        assertEquals(Player.BLACK, chess.getPlayer());
        assertEquals(true, chess.isInCheck(Player.BLACK));
        assertEquals(true, chess.possibleMoves().isEmpty());
    }

}