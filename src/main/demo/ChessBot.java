package demo;

import java.util.Set;

/**
 * Represents an autonomous chess player. Uses alpha beta pruning to assist its search, but still unable
 * to reach deeper than 3 ply. Even 3 is a bit of a stretch, so if you don't feel like waiting several
 * seconds for the bot to move, adjust the ply using the commands provided when running the
 * command line application. The board's function for generating moves has bad performance, and copying
 * the board seems like a bad design decision because it's unnecessary unless we're doing a parallel
 * search, so I think this is why we can't get any deeper.
 */
public class ChessBot {

    /**
     * how many moves ahead to look. Really was hoping to get at least 4 but after plugging the bot in to
     * the command line interface, even 3 is a bit of a stretch on a slower computer.
     */
    private int ply = 3;

    /**
     * gets the best move for whoever's turn it is
     *
     * @param position the position to analyze
     * @return the best move available to the current player
     */
    public Move getBestMove(ChessPosition position) {
        return this.alphabeta(position, this.ply, -BoardEval.INFINITY, BoardEval.INFINITY).choice;
    }

    /**
     * performs a recursive alpha beta search through the game tree
     *
     * @param position the position to be analyzed by this call
     * @param depth the number of levels left to search
     * @param alpha current alpha value
     * @param beta current beta value
     * @return a MoveChoice consisting of the best move from this position, along with its value
     */
    private MoveChoice alphabeta(ChessPosition position, int depth, int alpha, int beta) {
        Set<Move> availableMoves = position.possibleMoves();

        if (availableMoves.isEmpty()) {
            if (position.isInCheck(position.getPlayer())) {
                return new MoveChoice(null, -BoardEval.MATE_VALUE);
            } else {
                return new MoveChoice(null, BoardEval.STALEMATE_VALUE);
            }
        } else if (depth <= 0) {
            return new MoveChoice(null, BoardEval.evaluate(position));
        }

        MoveChoice result = new MoveChoice(null, 0);
        for (Move move : availableMoves) {
            ChessPosition nextPos = position.move(move);
            MoveChoice opponentBest = this.alphabeta(nextPos, depth - 1, -beta, -alpha);
            //negate the value since the best move for them is the worst for us
            opponentBest.value = -opponentBest.value;
            if (opponentBest.value > alpha) {
                alpha = opponentBest.value;
                result.choice = move;
            }

            if (alpha >= beta) {
                result.value = alpha;
            }
        }

        result.value = alpha;
        return result;
    }

    /**
     * sets the search depth for this bot (how many moves ahead it looks)
     *
     * @param ply the new depth to search to
     */
    public void setPly(int ply) {
        this.ply = ply;
    }

    /**
     * getter for the current search depth
     */
    public int getPly() {
        return this.ply;
    }
}