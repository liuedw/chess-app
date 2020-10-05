package demo;

import java.util.HashSet;
import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.Set;

/**
 * A class allows client to play a chess game and stores information
 * about the chess board and player. Board positions are indexed
 * from 0 to 7, with (0, 0) at the top left corner.
 * <pre>
 *   x -&gt;
 * y   0 1 2 3 4 5 6 7
 * | 0   #   #   #   #
 * v 1 #   #   #   #  
 *   2   #   #   #   #
 *   3 #   #   #   #  
 *   4   #   #   #   #
 *   5 #   #   #   #  
 *   6   #   #   #   #
 *   7 #   #   #   #  
 * </pre>
 */
public class ChessPosition {

    private Board board;
    private Player player;

    /**
     * Construct a new empty board on white's turn.
     */
    public ChessPosition() {
        board = new Board();
        player = Player.WHITE; // start at white's turn
    }

    /**
     * Construct a new empty board with the given the player's turn.
     * @param player the player who has the first turn
     * @throws IllegalArgumentException if the given player is null
     */
    public ChessPosition(Player player) {
        if (player == null) {
            throw new IllegalArgumentException();
        }
        board = new Board();
        this.player = player;
    }

    /**
     * Place the given piece at the specified position.
     * @param piece the type of piece to be placed
     * @param x the x coordinate to place the piece at
     * @param y the y coordinate to place the piece at
     * @throws IllegalArgumentException if the piece is placed to a position not on the board
     * @effects Replaces whatever is at the given position with the given piece.
     * @modifies <code>this</code>
     */
    public void populate(Piece piece, int x, int y) {
        if (!board.isValidPosition(x, y)) {
            throw new IllegalArgumentException();
        }
        board.setPiece(x, y, piece);
    }

    /**
     * Move a piece from a starting position to a new position on a copy of the board.
     * The starting position will become empty and whatever was on the new position will
     * be replaced with the moved piece. Does not validate the move according to chess rules.
     * @param prevX the x coordinate of the starting position
     * @param prevY the y coordinate of the starting position
     * @param moveToX the x coordinate of the new position
     * @param moveToY the y coordinate of the new position
     * @return A copy of the game after moving.
     *
     * @throws IllegalArgumentException if either the starting position or the new position is not valid
     * @throws NoSuchElementException if there is no piece on the starting position
     */
    public ChessPosition move(int prevX, int prevY, int moveToX, int moveToY) {
        if (!board.isValidPosition(prevX, prevY)) {
            throw new IllegalArgumentException();
        }
        if (!board.isValidPosition(moveToX, moveToY)) {
            throw new IllegalArgumentException();
        }

        if (board.getPiece(prevX, prevY) == null) {
            throw new NoSuchElementException();
        }

        // make a copy of the chess game
        ChessPosition copy = new ChessPosition(this.player);
        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
                Piece p = this.board.getPiece(i, j);
                copy.getBoard().setPiece(i, j, p);
            }
        }

        // toggle player's turn
        if (copy.getPlayer() == Player.WHITE) {
            copy.setPlayer(Player.BLACK);
        } else {
            copy.setPlayer(Player.WHITE);
        }

        // set the origin position to be null and set the piece to the new position
        Piece pieceToMove = copy.getBoard().getPiece(prevX, prevY);
        copy.getBoard().setPiece(prevX, prevY, null);
        copy.getBoard().setPiece(moveToX, moveToY, pieceToMove);

        // return the board copy
        return copy;

    }

    /**
     * Same as the other move method, but using the fields of an instance of Move.
     * @param move the move to make
     * @return A copy of the game after moving.
     */
    public ChessPosition move(Move move) {
        return move(move.prevX, move.prevY, move.moveToX, move.moveToY);
    }

    /**
     * Determines whether the king of the given player is in check. If they
     * have no kings on the board, they are considered not in check. If they
     * have multiple kings on the board, they are considered in check if any
     * of their kings is in check.
     * @param player the player whose king to test
     * @return True if the king is in check, false otherwise.
     * @throws IllegalArgumentException if the given player is null
     */
    public boolean isInCheck(Player player) {
        if (player == null) {
            throw new IllegalArgumentException();
        }
        
        // The king to test for
        Piece king;
        if (player == Player.WHITE) {
            king = Piece.WKING;
        } else if (player == Player.BLACK) {
            king = Piece.BKING;
        } else {
            // Not in the spec in case someone wants to extend this for more players or something
            throw new IllegalArgumentException();
        }

        // Loop through each square on the board to see if they are checking the king
        for (int x = 0; x < 8; x++) {
            for (int y = 0; y < 8; y++) {
                Piece piece = board.getPiece(x, y);
                // No piece or friendly piece
                if (piece == null || piece.getPlayer() == player) {
                    continue;
                }
                // Enemy piece, see if it can move to the king
                Set<Move> moves = piece.getMoves(board, x, y);
                for (Move move : moves) {
                    if (board.getPiece(move.moveToX, move.moveToY) == king) {
                        return true;
                    }
                }
            }
        }

        return false;
    }

    /**
     * Gets all possible moves the current player could make from this board state.
     * Moves that would leave their king in check are not allowed. Does not support
     * castling or en passant.
     * @return A set of all legal moves the current player can make.
     */
    public Set<Move> possibleMoves() {
        // Set to accumulate all possible moves
        Set<Move> moves = new HashSet<>();
        // Find all pieces belonging to the current player
        for (int x = 0; x < 8; x++) {
            for (int y = 0; y < 8; y++) {
                Piece piece = board.getPiece(x, y);
                // No piece or not my piece
                if (piece == null || piece.getPlayer() != player) {
                    continue;
                }
                // My piece, add its moves to the set
                moves.addAll(piece.getMoves(board, x, y));
            }
        }

        // Remove the moves that would leave me in check
        Iterator<Move> iter = moves.iterator();
        while (iter.hasNext()) {
            Move move = iter.next();
            ChessPosition next = move(move);
            if (next.isInCheck(player)) {
                iter.remove();
            }
        }

        return moves;
    }

    /**
     * View current chess board.
     * @return The current chess board.
     */
    public Board getBoard() {
        return board;
    }


    /**
     * Get the current player.
     * @return The player whose turn it is.
     */
    public Player getPlayer() {
        return this.player;
    }

    /**
     * Set player to the desired one.
     * @param player the player to set the turn to
     * @throws IllegalArgumentException if player is null
     * @effects The next move will be the given player's turn.
     * @modifies <code>this</code>
     */
    public void setPlayer(Player player) {
        if (player == null) {
            throw new IllegalArgumentException();
        }
        this.player = player;
    }

}
