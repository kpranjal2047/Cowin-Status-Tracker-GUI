package cowinjava.exceptions;

import java.io.IOException;

/**
 * Thrown to indicate miscommunication between app and server.
 *
 * @author Kumar Pranjal
 */
public class DataCommunicationException extends IOException {

    /**
     * Constructs a new DataCommunicationException with no detail message.
     */
    public DataCommunicationException() {
        super();
    }

    /**
     * Constructs a new DataCommunicationException with the specified detail message.
     *
     * @param message the detail message
     */
    public DataCommunicationException(String message) {
        super(message);
    }

    /**
     * Constructs a new DataCommunicationException with the specified cause and a detail
     * message of <tt>(cause==null ? null : cause.toString())</tt> (which typically
     * contains the class and detail message of <tt>cause</tt>).
     *
     * @param cause the cause (A <tt>null</tt> value is permitted, and indicates
     *              that the cause is nonexistent or unknown)
     */
    public DataCommunicationException(Throwable cause) {
        super(cause);
    }

    /**
     * Constructs a new DataCommunicationException with the specified detail message and
     * cause.
     *
     * @param message the detail message
     * @param cause   the cause (A <tt>null</tt> value is permitted, and indicates
     *                that the cause is nonexistent or unknown)
     */
    public DataCommunicationException(String message, Throwable cause) {
        super(message, cause);
    }
}
