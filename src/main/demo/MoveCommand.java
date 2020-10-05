package demo;

/**
 * A class representing a To and From of a move object
 */
public class MoveCommand {
    public static String source = null;
    public static String dest = null;

    /**
     * Returns a value indicating whether or not a "To" and "From" value of a move has been chosen
     * @return A boolean indicating whether or not a valid move can be made with 2 selected tiles
     */
    public static boolean bothSelected() {
        return source != null && dest != null;
    }

    /**
     * Clears the selected moves from this object
     */
    public static void clear() {
        source = dest = null;
    }

    /**
     * Returns an array representing the (x, y) position of a square in the chess board
     * @param s A string representing a position (i.e. A1 or H5)
     * @return An array of size 2 representing the position in the form of an integer pair
     */
    public static int[] toCoord(String s) {
        int[] coordinates = new int[2];
        coordinates[0] = s.toLowerCase().charAt(0) - 'a';
        coordinates[1] = 8 - Integer.parseInt(s.charAt(1) + "");
        return coordinates;
    }
}
