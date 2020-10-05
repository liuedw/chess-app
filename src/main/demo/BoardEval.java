package demo;

/**
 * provides static functionality for evaluating a board state (an instance of ChessPosition).
 * Note: this is heavily inspired by the method of evaluating a board state in the 332 chess
 * assignment. I took that class last quarter and made a pretty good bot.
 */
public class BoardEval {


    /**
     * these 8 x 8 arrays represent the bonus evaluation score for each grid square
     * when a piece of the specified type is on that square. The first row represents the
     * bottom of the board, i.e. where white's pieces start. The arrays for black pieces
     * have the same values, but are flipped vertically and all the values are negated.
     */
    private static int[][] whitePawnBonuses = { { 0, 0, 0, 0, 0, 0, 0, 0 },
            { 0, 0, 0, -5, -5, 0, 3, 0 }, { 0, 2, 3, 4, 4, 3, 2, 0 },
            { 0, 4, 6, 10, 10, 6, 4, 0 }, { 0, 4, 6, 8, 8, 6, 4, 0 },
            { 4, 8, 8, 12, 12, 8, 8, 4 }, { 5, 10, 15, 20, 20, 15, 10, 5 },
            { 0, 0, 0, 0, 0, 0, 0, 0 } };
    private static int[][] blackPawnBonuses;

    private static int[][] whiteKnightBonuses = { { -10, -10, -5, -5, -5, -5, -10, -10 },
            { -8, 0, 0, 3, 3, 0, 0, -8 }, { -8, 0, 10, 8, 8, 10, 0, -8 },
            { -8, 0, 8, 10, 10, 8, 0, -8 }, { -8, 0, 8, 10, 10, 8, 0, -8 },
            { -8, 0, 10, 8, 8, 10, 0, -8 }, { -8, 0, 0, 3, 3, 0, 0, -8 },
            { -10, -5, -5, -5, -5, -5, -5, -10 } };
    private static int[][] blackKnightBonuses;

    private static int[][] whiteBishopBonuses = { { -5, -5, -5, -5, -5, -5, -5, -5 },
            { -5, 10, 5, 8, 8, 5, 10, -5 }, { -5, 5, 3, 8, 8, 3, 5, -5 },
            { -5, 3, 10, 3, 3, 10, 3, -5 }, { -5, 3, 10, 3, 3, 10, 3, -5 },
            { -5, 5, 3, 8, 8, 3, 5, -4 }, { -5, 10, 5, 8, 8, 5, 10, -5 },
            { -5, -5, -5, -5, -5, -5, -5, -5 } };
    private static  int[][] blackBishopBonuses;

    private static int[][] whiteKingBonuses = { { 7, 4, 0, 0, 0, 2, 6, 10 },
            { 0, 0, 0, 0, 0, 0, 0, 0 },{ -2, -2, -2, -2, -2, -2, -2, -2},
            { -4, -4, -4, -4, -4, -4, -4, -4}, { -5, -5, -5, -5, -5, -5, -5, -5},
            { -5, -5, -5, -5, -5, -5, -5, -5}, { -5, -5, -5, -5, -5, -5, -5, -5},
            { -5, -5, -5, -5, -5, -5, -5, -5}};
    private static int[][] blackKingBonuses;

    private static int[][] whiteRookBonuses = { { 0, 0, 10, 15, 15, 10, 0, 0 },
            { 0, 0, 10, 15, 15, 10, 0, 0 },{ 0, 0, 0, 0, 0, 0, 0, 0 },
            { 0, 0, 0, 0, 0, 0, 0, 0 }, { 0, 0, 0, 0, 0, 0, 0, 0 },
            { 0, 0, 0, 0, 0, 0, 0, 0 }, { 0, 0, 6, 8, 8, 6, 0, 0 },
            { 0, 0, 6, 8, 8, 6, 0, 0 }};
    private static int[][] blackRookBonuses;

    private static int[][] whiteQueenBonuses = { { 0, 5, 10, 15, 10, 4, 2, 0 },
            { 0, 3, 5, 5, 5, 5, 3, 0 },{ 0, 2, 4, 4, 4, 4, 2, 0 },
            { 0, 0, 0, 0, 0, 0, 0, 0 }, { 0, 0, 0, 0, 0, 0, 0, 0 },
            { 0, 0, 0, 0, 0, 0, 0, 0 }, { 0, 0, 0, 0, 0, 0, 0, 0 },
            { 0, 0, 0, 0, 0, 0, 0, 0 }};
    private static int[][] blackQueenBonuses;

    /**
     * composed of the above arrays, indexed by Piece.getPositionBonusIndex()
     */
    private static int[][][] positionBonuses;

    /**
     * initializes black piece arrays as well as positionBonuses
     */
    static {
        blackPawnBonuses = invertValues(whitePawnBonuses);
        blackKnightBonuses = invertValues(whiteKnightBonuses);
        blackBishopBonuses = invertValues(whiteBishopBonuses);
        blackKingBonuses = invertValues(whiteKingBonuses);
        blackRookBonuses = invertValues(whiteRookBonuses);
        blackQueenBonuses = invertValues(whiteQueenBonuses);
        positionBonuses = new int[][][] {whitePawnBonuses, blackPawnBonuses, whiteKnightBonuses, blackKnightBonuses,
                            whiteBishopBonuses, blackBishopBonuses, whiteKingBonuses, blackKingBonuses,
                            whiteRookBonuses, blackRookBonuses, whiteQueenBonuses, blackQueenBonuses};
    }

    /**
     * the values for infinity and mate are fairly arbitrary, but no need to use Integer.MAX_VALUE
     */
    public static final int INFINITY = 1000000;
    public static final int MATE_VALUE = INFINITY / 2;
    public static final int STALEMATE_VALUE = 0;


    /**
     * flips a 2-d array upside down and negates all the values in it
     *
     * @param orig the array to flip
     * @return the flipped array
     */
    private static int[][] invertValues(int[][] orig) {
        try {
            int[][] result = new int[orig.length][orig[0].length];
            for (int i = 0; i < orig.length; i++) {
                int[] row = orig[i];
                int[] negatedRow = new int[row.length];
                for (int j = 0; j < negatedRow.length; j++) {
                    negatedRow[j] = -row[j];
                }
                result[orig.length - 1 - i] = negatedRow;
            }
            return result;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * evaluates given position and returns its utility for the current player. The magnitude of typical evaluation
     * scores can range from 0 to a few thousand (positive means good for current player, negative means bad), but checkmate
     * produces a magnitude of 500,000. Note that this function is not responsible for handling checkmates; those should
     * be handled elsewhere in the alpha beta search
     *
     * @param position the position to evaluate
     * @return an evaluation score for this position's utility to the current player
     */
    public static int evaluate(ChessPosition position) {
        Board board = position.getBoard();
        int value = 0;
        for (int y = 0; y < 8; y++) {
            for (int x = 0; x < 8; x++) {
                Piece piece = board.getPiece(x, y);
                if (piece != null) {
                    value += piece.getValue();
                    value += positionBonuses[piece.getPositionBonusIndex()][7 - y][x];
                }
            }
        }

        return position.getPlayer() == Player.WHITE ? value : -value;
    }
}