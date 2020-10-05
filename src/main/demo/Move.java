package demo;

/**
 * A class representing a chess move from a starting position to an ending position.
 */
public class Move {
    /**
     * The position to start at.
     */
    public int prevX;
    public int prevY;
    /**
     * The position to end on.
     */
    public int moveToX;
    public int moveToY;

    /**
     * the value of this move for white
     */
    public int value;

    public Move(int prevX, int prevY, int moveToX, int moveToY) {
        this.prevX = prevX;
        this.prevY = prevY;
        this.moveToX = moveToX;
        this.moveToY = moveToY;
    }

    /**
     * Implemented so Set comparison works in the tests. Two moves are equal
     * if they start at the same coordinates and end at the same coordinates.
     */
    @Override
    public boolean equals(Object other) {
        if (!(other instanceof Move)) {
            return false;
        }

        Move move = (Move) other;
        if (
               move.prevX != this.prevX
            || move.prevY != this.prevY
            || move.moveToX != this.moveToX
            || move.moveToY != this.moveToY
        ) {
            return false;
        }

        return true;
    }

    /**
     * Implemented so Set comparison works in the tests. Just the xor of the hashes
     * of all the fields.
     */
    @Override
    public int hashCode() {
        return Integer.hashCode(prevX) ^ Integer.hashCode(prevY) ^ Integer.hashCode(moveToX) ^ Integer.hashCode(moveToY);
    }

    /**
     * For printing moves for debugging.
     */
    @Override
    public String toString() {
        String[] xLabels = {"A", "B", "C", "D", "E", "F", "G", "H"};
        int[] yLabels = {8, 7, 6, 5, 4, 3, 2, 1};
        return (xLabels[prevX] + yLabels[prevY]) + " to " + xLabels[moveToX] + yLabels[moveToY];
    }


}