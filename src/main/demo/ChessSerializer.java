package demo;

import java.io.*;
import java.util.*;

/**
 * This class provides utility functions to serialize and deserialize a <code>ChessPosition</code>.
 */
public class ChessSerializer {

    /**
     * Serializes the given <code>ChessPosition</code> and write to a file with the given file name.
     * @requires filename != null && cp != null
     * @effects writing data to a file with the given name
     * @throws FileNotFoundException if the given file does not exist
     * @throws IOException if an I/O error occurs
     */
    public static void serialize(String filename, ChessPosition cp) throws IOException {
        if (filename == null || cp == null) {
            throw new IllegalArgumentException();
        }
        serialize(new FileOutputStream(filename), cp);
    }

    /**
     * Serializes the given <code>ChessPosition</code> and write to an <code>OutputStream</code>.
     * @requires s != null && cp != null
     * @effects writing data to s
     * @throws IOException if an I/O error occurs
     */
    public static void serialize(OutputStream s, ChessPosition cp) throws IOException {
        OutputStreamWriter w = new OutputStreamWriter(s);
        w.write(cp.getPlayer() + "\n");
        Board b = cp.getBoard();
        for (int y = 0; y < 8; y++) {
            for (int x = 0; x < 8; x++) {
                Piece piece = b.getPiece(x, y);
                if (piece == null) {
                    w.write(". ");
                } else {
                    w.write(BoardPresent.presentPiece(piece));
                    w.write(' ');
                }
            }
            w.write('\n');
        }
        w.flush();
    }

    /**
     * Deserializes the file at the given path and constructs a <code>ChessPosition</code>
     *   based on it.
     * @requires filename != null
     * @effects constructs a <code>ChessPosition</code>
     * @throws SerializationException if the given file is ill-formatted
     * @throws IOException if an I/O error occurs
     */
    public static ChessPosition deserialize(String filename) throws IOException, SerializationException {
        return deserialize(new FileInputStream(filename));
    }

    /**
     * Deserializes data from the given <code>InputStream</code> and constructs a
     *   <code>ChessPosition</code> based on it.
     * @requires s != null
     * @effects constructs a <code>ChessPosition</code>
     * @throws SerializationException if data in the given stream
     * @throws IOException if an I/O error occurs
     */
    public static ChessPosition deserialize(InputStream s) throws IOException, SerializationException {
        Scanner scanner = new Scanner(s);
        ChessPosition res;

        try {
            res = new ChessPosition(Player.valueOf(scanner.next()));
            for (int y = 0; y < 8; y++) {
                for (int x = 0; x < 8; x++) {
                    String str = scanner.next();
                    if (str.length() != 1) {
                        throw new SerializationException();
                    }
                    res.populate(BoardPresent.pieceFrom(str.charAt(0)), x, y);
                }
            }
        } catch (NoSuchElementException e) {
            throw new SerializationException();
        }
        
        return res;
    }
}