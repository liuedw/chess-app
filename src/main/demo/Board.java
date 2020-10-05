package demo;

/**
 * A class representing a 8*8 chess board that holds pieces
 * The index of the board increases from left to right and from top to bottom.
 * For example, if one wants to access the upper-left corner on the board,
 * then x equals 0 and y equals 0 is the right place to look for.
 */
public class Board {
    private int x;
    private Piece[][] board;

    /**
     * Construct a new empty board with width and height of 8
     */
    public Board() {
        board = new Piece[8][8];
    }

    /**
     * Determines whether or not a position is on the board.
     * @param x the x coordinate of the position to check
     * @param y the y coordinate of the position to check
     * @return Whether the position (x, y) is on the board.
     *       i.e. x &lt; 0, x &gt; 7, y &lt; 0, or y &gt; 7.
     */
    public boolean isValidPosition(int x, int y) {
        return 0 <= x && x < 8 && 0 <= y && y < 8;
    }

    /**
     * Place piece on the board at assigned position.
     * @param x the x coordinate of the desired position to place the piece
     * @param y the y coordinate of the desired position to place the piece
     * @param piece the desired type of piece to be placed
     * @throws IllegalArgumentException if the position is not on the board
     * @effects Replaces whatever is at the given position with the given piece.
     * @modifies <code>this</code>
     */
    public void setPiece(int x, int y, Piece piece) {
        if (!isValidPosition(x, y)) {
            throw new IllegalArgumentException();
        }
        board[x][y] = piece;
    }

    /**
     * Get the type of the piece at the specific position.
     * @param x the x coordinate of the position
     * @param y the y coordinate of the position
     * @return The type of piece at the position, or null if it is empty.
     *
     * @throws IllegalArgumentException if the position is not on the board
     */
    public Piece getPiece(int x, int y) {
        if (!isValidPosition(x, y)) {
            throw new IllegalArgumentException();
        }

        return board[x][y];
    }

}