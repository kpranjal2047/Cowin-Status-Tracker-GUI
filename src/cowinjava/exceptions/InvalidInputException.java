package cowinjava.exceptions;

import java.io.IOException;

/**
 * Thrown to indicate that some input has got illegal or inappropriate value.
 *
 * @author Kumar Pranjal
 */
public class InvalidInputException extends IOException {

    /**
     * Constructs an {@code InvalidInputException} with no detail message.
     */
    public InvalidInputException() {
        super();
    }

    /**
     * Constructs an {@code InvalidInputException} with the specified detail
     * message.
     *
     * @param message the detail message
     */
    public InvalidInputException(final String message) {
        super(message);
    }

    /**
     * Constructs an {@code InvalidInputException} with the specified cause and a
     * detail message of {@code (cause==null ? null : cause.toString())} (which
     * typically contains the class and detail message of {@code cause}).
     *
     * @param cause the cause (A {@code null} value is permitted, and indicates that
     *              the cause is nonexistent or unknown)
     */
    public InvalidInputException(final Throwable cause) {
        super(cause);
    }

    /**
     * Constructs an {@code InvalidInputException} with the specified detail message
     * and cause.
     *
     * @param message the detail message
     * @param cause   the cause (A {@code null} value is permitted, and indicates
     *                that the cause is nonexistent or unknown)
     */
    public InvalidInputException(final String message, final Throwable cause) {
        super(message, cause);
    }
}
