package demo;

/**
 * An exception thrown when ChessSereializer meets a ill-formatted file. 
 */
public class SerializationException extends Exception {
    public SerializationException() {
        super();
    }

    public SerializationException(String msg) {
        super(msg);
    }
}