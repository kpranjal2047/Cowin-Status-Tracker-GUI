package cowin.java.exceptions;

import java.io.IOException;

/**
 * Thrown when server sends invalid response.
 *
 * @author Kumar Pranjal
 */
public class InvalidResponseException extends IOException {

    /**
     * Constructs a {@code InvalidResponseException} with the specified detail
     * message.
     *
     * @param message the detail message
     */
    public InvalidResponseException(final String message) {
        super(message);
    }

}
