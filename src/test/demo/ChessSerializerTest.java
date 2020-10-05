package demo;

import java.io.*;

import org.junit.*;
import static org.junit.Assert.*;

public class ChessSerializerTest {
    private void checkChessPosition(ChessPosition cp) {
        try {
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            ChessSerializer.serialize(out, cp);
            ByteArrayInputStream in = new ByteArrayInputStream(out.toByteArray());
            ChessPosition result = ChessSerializer.deserialize(in);
            assertEquals(cp.getPlayer(), result.getPlayer());
            Board expectB = cp.getBoard();
            Board actualB = result.getBoard();
            for (int x = 0; x < 8; x++) {
                for (int y = 0; y < 8; y++) {
                    assertEquals("piece mismatch at " + x + ", " + y, expectB.getPiece(x, y), actualB.getPiece(x, y));
                }
            }
        } catch (IOException | SerializationException e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    public void standardBoardTest() {
        ChessPosition chess = new ChessPosition();
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

        checkChessPosition(chess);
    }
}