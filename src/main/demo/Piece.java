package demo;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

/**
 * All pieces needed for a chess game.
 */
public enum Piece {
    /**
     * White King
     */
    WKING,
    /**
     * White Knight
     */
    WKNIGHT,
    /**
     * White Rook
     */
    WROOK,
    /**
     * White Queen
     */
    WQUEEN,
    /**
     * White Bishop
     */
    WBISHOP,
    /**
     * White Pawn
     */
    WPAWN,
    /**
     * Black King
     */
    BKING,
    /**
     * Black Knight
     */
    BKNIGHT,
    /**
     * Black Rook
     */
    BROOK,
    /**
     * Black Queen
     */
    BQUEEN,
    /**
     * Black Bishop
     */
    BBISHOP,
    /**
     * Black Pawn
     */
    BPAWN;

    /**
     * Returns the player that owns this piece.
     * @return The player that this piece belongs to.
     */
    public Player getPlayer() {
        switch (this) {
            case WPAWN:
            case WKNIGHT:
            case WBISHOP:
            case WROOK:
            case WQUEEN:
            case WKING:
                return Player.WHITE;
            case BPAWN:
            case BKNIGHT:
            case BBISHOP:
            case BROOK:
            case BQUEEN:
            case BKING:
                return Player.BLACK;
            default:
                throw new IllegalStateException();
        }
    }

    /**
     * gets the value of this piece from white's perspective
     * (weights borrowed from 332 SimpleEvaluator)
     *
     * @return the value of this piece from white's perspective
     */
    public int getValue() {
        switch (this) {
            case WPAWN:
                return 100;
            case BPAWN:
                return -100;
            case WKNIGHT:
                return 300;
            case BKNIGHT:
                return -300;
            case WBISHOP:
                return 300;
            case BBISHOP:
                return -300;
            case WKING:
                return 350;
            case BKING:
                return -350;
            case WROOK:
                return 500;
            case BROOK:
                return -500;
            case WQUEEN:
                return 900;
            case BQUEEN:
                return -900;
            default:
                throw new IllegalStateException();
        }
    }

    /**
     * gets an index from 0-11 depending on what type of piece this is. Used to index
     * into a position value array by BoardEval
     *
     * @return an index from 0-11 based on this piece type
     */
    public int getPositionBonusIndex() {
        switch (this) {
            case WPAWN:
                return 0;
            case BPAWN:
                return 1;
            case WKNIGHT:
                return 2;
            case BKNIGHT:
                return 3;
            case WBISHOP:
                return 4;
            case BBISHOP:
                return 5;
            case WKING:
                return 6;
            case BKING:
                return 7;
            case WROOK:
                return 8;
            case BROOK:
                return 9;
            case WQUEEN:
                return 10;
            case BQUEEN:
                return 11;
            default:
                throw new IllegalStateException();
        }
    }

    /**
     * Gets all possible moves that a piece of this type could make
     * from the given position on the given board. Includes moves
     * that would leave the king in check, but not other varieties
     * of illegal moves.
     * @param board the board the piece is on
     * @param prevX the x coordinate of the position the piece starts at
     * @param prevY the y coordinate of the position the piece starts at
     * @return The set of all moves that this piece can do.
     * @throws IllegalArgumentException if the board is null
     * or the position is invalid
     */
    public Set<Move> getMoves(Board board, int prevX, int prevY) {
        // I'm sorry this method is so bad
        
        if (board == null || !board.isValidPosition(prevX, prevY)) {
            throw new IllegalArgumentException();
        }

        Set<Move> moves = new HashSet<>();
        if (this == WPAWN || this == BPAWN) {
            // Vertical moves
            // Which way the pawn is going
            int dy;
            if (this == WPAWN) {
                dy = -1;
            } else {
                dy = 1;
            }
            // If it can move two squares
            int dist = 1;
            if ((this == WPAWN && prevY == 6) || (this == BPAWN && prevY == 1)) {
                dist = 2;
            }
            Set<Move> normalMoves = slidingMoves(board, prevX, prevY, 0, dy, dist);
            // Remove any moves that are capturing because pawns can't capture forwards
            Iterator<Move> iter = normalMoves.iterator();
            while (iter.hasNext()) {
                Move move = iter.next();
                if (board.getPiece(move.moveToX, move.moveToY) != null) {
                    iter.remove();
                }
            }

            // Capturing
            // dx = -1, 1, represents checking left then right
            // dy is same direction as normal movement
            // Can only capture one square away
            Set<Move> capturingMoves = new HashSet<>();
            capturingMoves.addAll(slidingMoves(board, prevX, prevY, -1, dy, 1));
            capturingMoves.addAll(slidingMoves(board, prevX, prevY, 1, dy, 1));
            // Remove any moves that aren't capturing
            iter = capturingMoves.iterator();
            while (iter.hasNext()) {
                Move move = iter.next();
                if (board.getPiece(move.moveToX, move.moveToY) == null) {
                    iter.remove();
                }
            }

            // Add the valid move options
            moves.addAll(normalMoves);
            moves.addAll(capturingMoves);
        } else if (this == WKNIGHT || this == BKNIGHT) {
            // Single jump in 8 L-shaped directions
            moves.addAll(slidingMoves(board, prevX, prevY, 2, -1, 1));
            moves.addAll(slidingMoves(board, prevX, prevY, 1, -2, 1));
            moves.addAll(slidingMoves(board, prevX, prevY, -1, -2, 1));
            moves.addAll(slidingMoves(board, prevX, prevY, -2, -1, 1));
            moves.addAll(slidingMoves(board, prevX, prevY, -2, 1, 1));
            moves.addAll(slidingMoves(board, prevX, prevY, -1, 2, 1));
            moves.addAll(slidingMoves(board, prevX, prevY, 1, 2, 1));
            moves.addAll(slidingMoves(board, prevX, prevY, 2, 1, 1));
        } else if (this == WBISHOP || this == BBISHOP) {
            // Up to 8 squares (length of the chessboard) in four diagonal directions
            moves.addAll(slidingMoves(board, prevX, prevY, 1, -1, 8));
            moves.addAll(slidingMoves(board, prevX, prevY, -1, -1, 8));
            moves.addAll(slidingMoves(board, prevX, prevY, -1, 1, 8));
            moves.addAll(slidingMoves(board, prevX, prevY, 1, 1, 8));
        } else if (this == WROOK || this == BROOK) {
            // Up to 8 squares (length of the chessboard) in four cardinal directions
            moves.addAll(slidingMoves(board, prevX, prevY, 1, 0, 8));
            moves.addAll(slidingMoves(board, prevX, prevY, 0, -1, 8));
            moves.addAll(slidingMoves(board, prevX, prevY, -1, 0, 8));
            moves.addAll(slidingMoves(board, prevX, prevY, 0, 1, 8));
        } else if (this == WQUEEN) {
            // Delegate to bishop and rook of the appropriate color
            moves.addAll(WBISHOP.getMoves(board, prevX, prevY));
            moves.addAll(WROOK.getMoves(board, prevX, prevY));
        } else if (this == BQUEEN) {
            // Delegate to bishop and rook of the appropriate color
            moves.addAll(BBISHOP.getMoves(board, prevX, prevY));
            moves.addAll(BROOK.getMoves(board, prevX, prevY));
        } else if (this == WKING || this == BKING) {
            // 1 square in 8 directions
            moves.addAll(slidingMoves(board, prevX, prevY, 1, 0, 1));
            moves.addAll(slidingMoves(board, prevX, prevY, 1, -1, 1));
            moves.addAll(slidingMoves(board, prevX, prevY, 0, -1, 1));
            moves.addAll(slidingMoves(board, prevX, prevY, -1, -1, 1));
            moves.addAll(slidingMoves(board, prevX, prevY, -1, 0, 1));
            moves.addAll(slidingMoves(board, prevX, prevY, -1, 1, 1));
            moves.addAll(slidingMoves(board, prevX, prevY, 0, 1, 1));
            moves.addAll(slidingMoves(board, prevX, prevY, 1, 1, 1));
        }

        return moves;
    }

    /**
     * Returns the moves this piece can make on the board, starting from (prevX, prevY)
     * and moving in increments of (dx, dy), up to a maximum of dist movements away
     * from the starting square, including the last movement. Is blocked by friendly
     * and enemy pieces occupying a square along the path and will not search further.
     * Includes the capture of the enemy piece as a possible move.
     */
    private Set<Move> slidingMoves(Board board, int prevX, int prevY, int dx, int dy, int dist) {
        Set<Move> moves = new HashSet<>();
        for (int i = 1; i <= dist; i++) {
            int moveToX = prevX + i * dx;
            int moveToY = prevY + i * dy;

            // Off the board
            if (!board.isValidPosition(moveToX, moveToY)) {
                break;
            }

            Piece piece = board.getPiece(moveToX, moveToY);

            // Friendly piece
            if (piece != null && piece.getPlayer() == this.getPlayer()) {
                break;
            }

            // Enemy or empty
            moves.add(new Move(prevX, prevY, moveToX, moveToY));

            // It was an enemy piece, we could capture but anything beyond is invalid
            if (piece != null) {
                break;
            }
        }
        return moves;
    }

    /**
     * Returns a shorthand representation of a piece.
     * @return A shorthand representation of a piece (i.e. Black Pawn -> bp)
     */
    public String getShortHand() {
        switch (this) {
            case WPAWN:
                return "wp";
            case BPAWN:
                return "bp";
            case WKNIGHT:
                return "wn";
            case BKNIGHT:
                return "bn";
            case WBISHOP:
                return "wb";
            case BBISHOP:
                return "bb";
            case WKING:
                return "wk";
            case BKING:
                return "bk";
            case WROOK:
                return "wr";
            case BROOK:
                return "br";
            case WQUEEN:
                return "wq";
            case BQUEEN:
                return "bq";
            default:
                throw new IllegalStateException();
        }
    }

}