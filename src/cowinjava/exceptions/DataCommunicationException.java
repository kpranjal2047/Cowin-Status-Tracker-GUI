package cowinjava.exceptions;

import java.io.IOException;

/**
 * Thrown to indicate a miscommunication between the app and the server.
 *
 * @author Kumar Pranjal
 */
public class DataCommunicationException extends IOException {

    /**
     * Constructs a {@code DataCommunicationException} with no detail message.
     */
    public DataCommunicationException() {
        super();
    }

    /**
     * Constructs a {@code DataCommunicationException} with the specified detail
     * message.
     *
     * @param message the detail message
     */
    public DataCommunicationException(final String message) {
        super(message);
    }

    /**
     * Constructs a {@code DataCommunicationException} with the specified cause and
     * a detail message of {@code (cause==null ? null : cause.toString())} (which
     * typically contains the class and detail message of {@code cause}).
     *
     * @param cause the cause (A {@code null} value is permitted, and indicates that
     *              the cause is nonexistent or unknown)
     */
    public DataCommunicationException(final Throwable cause) {
        super(cause);
    }

    /**
     * Constructs a {@code DataCommunicationException} with the specified detail
     * message and cause.
     *
     * @param message the detail message
     * @param cause   the cause (A {@code null} value is permitted, and indicates
     *                that the cause is nonexistent or unknown)
     */
    public DataCommunicationException(final String message, final Throwable cause) {
        super(message, cause);
    }
}
