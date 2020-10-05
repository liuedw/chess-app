package demo;

import org.junit.*;
import static org.junit.Assert.*;

public class FeedbackTests {
    @Test
    public void complaint1() {
        ChessPosition cp = new ChessPosition();
        BoardCoord.populate(cp, Piece.WKING, "e5");
        BoardCoord.populate(cp, Piece.BPAWN, "f6");
        assertTrue(cp.isInCheck(Player.WHITE));
    }

    @Test
    public void complaint2() {
        ChessPosition cp = new ChessPosition();
        BoardCoord.populate(cp, Piece.WQUEEN, "a1");
        BoardCoord.populate(cp, Piece.WPAWN, "c3");
        BoardCoord.populate(cp, Piece.BPAWN, "a2");
        BoardCoord.populate(cp, Piece.BPAWN, "b1");
        BoardCoord.populate(cp, Piece.BPAWN, "c4");
        assertEquals(3, cp.possibleMoves().size());
    }

    @Test
    public void complaint3() {
        ChessPosition cp = new ChessPosition();
        BoardCoord.populate(cp, Piece.BPAWN, "a7");
        BoardCoord.populate(cp, Piece.WPAWN, "b5");
        assertTrue(cp.possibleMoves().contains(BoardCoord.move("b5", "b6")));
    }

    @Test
    public void complaint4() {
        ChessPosition cp = new ChessPosition();
        BoardCoord.populate(cp, Piece.WBISHOP, "c3");
        BoardCoord.populate(cp, Piece.WPAWN, "e5");
        BoardCoord.populate(cp, Piece.BPAWN, "b2");
        BoardCoord.populate(cp, Piece.BPAWN, "e6");
        assertEquals(6, cp.possibleMoves().size());
    }

    @Test
    public void complaint5() {
        ChessPosition cp = new ChessPosition();
        BoardCoord.populate(cp, Piece.BPAWN, "a4");
        BoardCoord.populate(cp, Piece.WPAWN, "a2");
        BoardCoord.populate(cp, Piece.BPAWN, "a7");
        BoardCoord.populate(cp, Piece.BKNIGHT, "a6");
        BoardCoord.populate(cp, Piece.BQUEEN, "b6");
        assertTrue(cp.possibleMoves().contains(BoardCoord.move("a2", "a3")));
    }

    @Test
    public void complaint6() {
        ChessPosition cp = new ChessPosition();
        BoardCoord.populate(cp, Piece.WROOK, "b2");
        BoardCoord.populate(cp, Piece.WPAWN, "d2");
        BoardCoord.populate(cp, Piece.BPAWN, "b3");
        BoardCoord.populate(cp, Piece.BPAWN, "d3");
        assertEquals(4, cp.possibleMoves().size());
    }
}