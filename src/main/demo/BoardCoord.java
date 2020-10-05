package demo;

/**
 * Represents a coordinate on a board, (x, y) where both x and y starts from the
 *   top left corner. 
 */
public class BoardCoord {
    public final int x;
    public final int y;

    /**
     * Constructs a <code>BoardCoord</code>
     * @requires 0 &le x &le 7 && 0 &le y &le 7
     */
    public BoardCoord(int x, int y) {
        if (x < 0 || x > 7 || y < 0 || y > 7) {
            throw new IllegalArgumentException();
        }

        this.x = x;
        this.y = y;
    }

    /**
     * Constructs a <code>BoardCoord</code>
     * @requires coord != null and coord must have a length of two,
     *   with one character from [1-8], the other one from [A-H]
     */
    public BoardCoord(String coord) {
        if (coord == null || coord.length() != 2) {
            throw new IllegalArgumentException();
        }
        if (Character.isLetter(coord.charAt(0)) && Character.isDigit(coord.charAt(1))) {
            x = getColNum(coord.charAt(0));
            y = getRowNum(coord.charAt(1));
        } else if (Character.isDigit(coord.charAt(0)) && Character.isLetter(coord.charAt(1))) {
            x = getColNum(coord.charAt(1));
            y = getRowNum(coord.charAt(0));
        } else {
            throw new IllegalArgumentException();
        }
    }

    @Override
    public String toString() {
        return new Character((char)('A' + x)).toString() + ((char)(7 - y + '1'));
    }

    @Override
    public int hashCode() {
        return (x << 4) + y;
    }

    @Override
    public boolean equals(Object o) {
        if (o instanceof BoardCoord) {
            BoardCoord other = (BoardCoord)o;
            return this.x == other.x && this.y == other.y;
        }
        return false;
    }

    /**
     * Place the given piece on a given coordinate on the given board,
     *   where the coordinate is represented by two characters. 
     * @requires cp != null AND
     *   coord != null and coord must have a length of two,
     *   with one character from [1-8], the other one from [A-H]
     */
    public static void populate(ChessPosition cp, Piece p, String coord) {
        if (cp == null) {
            throw new IllegalArgumentException();
        }

        BoardCoord bc = new BoardCoord(coord);
        cp.populate(p, bc.x, bc.y);
    }

    /**
     * Construct a new <code>Move</move> from one coordinate to another
     *   coordinate, both are represented by two characters. 
     * @requires src != null AND dst != null AND
     *   both src and dst must have a length of two,
     *   with one character from [1-8], the other one from [A-H]
     * @param src the source of the move, represented as chess coordinate
     * @param dst the destination of the move, represented as chess coordinate
     */
    public static Move move(String src, String dst) {
        BoardCoord bc1 = new BoardCoord(src);
        BoardCoord bc2 = new BoardCoord(dst);

        return new Move(bc1.x, bc1.y, bc2.x, bc2.y);
    }

    private static void throwIfOutOfBound(int v) {
        if (v < 0 || v > 7) {
            throw new IllegalArgumentException("" + v);
        }
    }

    private static int getColNum(char c) {
        int res = c - (Character.isUpperCase(c) ? 'A' : 'a');
        throwIfOutOfBound(res);
        return res;
    }

    private static int getRowNum(char c) {
        int res = 7 - (c - '1');
        throwIfOutOfBound(res);
        return res;
    }
}