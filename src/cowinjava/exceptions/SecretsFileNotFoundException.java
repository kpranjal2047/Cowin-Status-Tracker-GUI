package cowinjava.exceptions;

import java.io.IOException;

/**
 * Thrown when Twilio secrets file cannot be read.
 *
 * @author Kumar Pranjal
 */
public class SecretsFileNotFoundException extends IOException {

    /**
     * Constructs a {@code SecretsFileNotFoundException} with no detail message.
     */
    public SecretsFileNotFoundException() {
        super();
    }

    /**
     * Constructs a {@code SecretsFileNotFoundException} with the specified detail
     * message.
     *
     * @param message the detail message
     */
    public SecretsFileNotFoundException(final String message) {
        super(message);
    }

    /**
     * Constructs a {@code SecretsFileNotFoundException} with the specified cause and
     * a detail message of {@code (cause==null ? null : cause.toString())} (which
     * typically contains the class and detail message of {@code cause}).
     *
     * @param cause the cause (A {@code null} value is permitted, and indicates that
     *              the cause is nonexistent or unknown)
     */
    public SecretsFileNotFoundException(final Throwable cause) {
        super(cause);
    }

    /**
     * Constructs a {@code SecretsFileNotFoundException} with the specified detail
     * message and cause.
     *
     * @param message the detail message
     * @param cause   the cause (A {@code null} value is permitted, and indicates
     *                that the cause is nonexistent or unknown)
     */
    public SecretsFileNotFoundException(final String message, final Throwable cause) {
        super(message, cause);
    }
}
