package demo;

import java.io.*;

/**
 * This class provides utility functions to present a <code>Board</code> in CLI.
 */
public class BoardPresent {
    private static final String WShort = "   ";
    private static final String BShort = "\u2588\u2588\u2588"; // filled block
    private static final String WLong = "       ";
    private static final String BLong = "\u2588\u2588\u2588\u2588\u2588\u2588\u2588";
    
    /**
     * Write the presentation of the given <code>Board</code> to a <code>PrintStream</code>.
     * @modifies output
     * @requires output != null && board != null
     */
    public static void present(PrintStream output, Board board) {
        if (output == null || board == null) {
            throw new IllegalArgumentException();
        }

        boolean isWhite = true;
        for (int i = 0; i < 8; i++) {
            // write number column
            output.print(WLong);
            presentLineNoPiece(output, isWhite);

            output.print(WShort);
            output.print(8 - i);
            output.print(WShort);
            presentLineWithPiece(output, isWhite, board, i);

            output.print(WLong);
            presentLineNoPiece(output, isWhite);
            isWhite = !isWhite;
        }

        // write character row
        output.println();
        output.print(WLong);
        for (int i = 0; i < 8; i++) {
            output.print(WShort);
            output.print((char)('A' + i));
            output.print(WShort);
        }
        output.println();
        output.println();
    }

    private static void presentLineNoPiece(PrintStream output, boolean startWithWhite) {
        boolean isWhite = startWithWhite;
        for (int i = 0; i < 8; i++) {
            output.print(isWhite ? WLong : BLong);
            isWhite = !isWhite;
        }
        output.println();
    }

    private static void presentLineWithPiece(PrintStream output, boolean startWithWhite, Board board, int line) {
        boolean isWhite = startWithWhite;
        for (int i = 0; i < 8; i++) {
            Piece p = board.getPiece(i, line);
            if (p == null) {
                output.print(isWhite ? WLong : BLong);
            } else {
                if (isWhite) {
                    output.print(WShort);
                    output.print(presentPiece(p));
                    output.print(WShort);
                } else {
                    output.print(BShort);
                    output.print(presentPiece(p));
                    output.print(BShort);
                }
            }
            isWhite = !isWhite;
        }
        output.println();
    }

    /**
     * Get the <code>Piece</code> represented by the given character.
     * @return the <code>Piece</code> represented by the given character.
     */
    public static Piece pieceFrom(char c) {
        switch (c) {
            case 'K':
                return Piece.WKING;
            case 'N':
                return Piece.WKNIGHT;
            case 'R':
                return Piece.WROOK;
            case 'Q':
                return Piece.WQUEEN;
            case 'B':
                return Piece.WBISHOP;
            case 'P':
                return Piece.WPAWN;
            case 'k':
                return Piece.BKING;
            case 'n':
                return Piece.BKNIGHT;
            case 'r':
                return Piece.BROOK;
            case 'q':
                return Piece.BQUEEN;
            case 'b':
                return Piece.BBISHOP;
            case 'p':
                return Piece.BPAWN;
            default:
                return null;
        }
    }

    /**
     * Get the character representing the given <code>Piece</code>.
     * @return the character representing the given <code>Piece</code>.
     */
    public static char presentPiece(Piece p) {
        switch (p) {
            case WKING:
                return 'K';
            case WKNIGHT:
                return 'N';
            case WROOK:
                return 'R';
            case WQUEEN:
                return 'Q';
            case WBISHOP:
                return 'B';
            case WPAWN:
                return 'P';
            case BKING:
                return 'k';
            case BKNIGHT:
                return 'n';
            case BROOK:
                return 'r';
            case BQUEEN:
                return 'q';
            case BBISHOP:
                return 'b';
            case BPAWN:
                return 'p';
            default:
                return '?';
        }
    }
}