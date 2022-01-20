package cowin.exceptions;

import java.io.IOException;

/**
 * Thrown to indicate that some input has got illegal or inappropriate value.
 *
 * @author Kumar Pranjal
 */
public class InvalidInputException extends IOException {

    /**
     * Constructs an {@code InvalidInputException} with the specified detail
     * message.
     *
     * @param message The detail message (which is saved for later retrieval by the
     *                {@link #getMessage()} method)
     */
    public InvalidInputException(final String message) {
        super(message);
    }

}
