package cowinjava.exceptions;

import java.io.IOException;

/**
 * Thrown when server does not reply with HTTP status code 200.
 *
 * @author Kumar Pranjal
 */
public class StatusNotOKException extends IOException {

    /**
     * Constructs a {@code StatusNotOKException} with no detail message.
     */
    public StatusNotOKException() {
        super();
    }

    /**
     * Constructs a {@code StatusNotOKException} with the specified detail
     * message.
     *
     * @param message the detail message
     */
    public StatusNotOKException(final String message) {
        super(message);
    }

    /**
     * Constructs a {@code StatusNotOKException} with the specified cause and
     * a detail message of {@code (cause==null ? null : cause.toString())} (which
     * typically contains the class and detail message of {@code cause}).
     *
     * @param cause the cause (A {@code null} value is permitted, and indicates that
     *              the cause is nonexistent or unknown)
     */
    public StatusNotOKException(final Throwable cause) {
        super(cause);
    }

    /**
     * Constructs a {@code StatusNotOKException} with the specified detail
     * message and cause.
     *
     * @param message the detail message
     * @param cause   the cause (A {@code null} value is permitted, and indicates
     *                that the cause is nonexistent or unknown)
     */
    public StatusNotOKException(final String message, final Throwable cause) {
        super(message, cause);
    }
}
