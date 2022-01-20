package cowin.exceptions;

import java.io.IOException;

/**
 * Thrown when server sends invalid response.
 *
 * @author Kumar Pranjal
 */
public class InvalidResponseException extends IOException {

    /**
     * Constructs an {@code InvalidResponseException} with {@code null} as its error
     * detail message.
     */
    public InvalidResponseException() {
        super();
    }

}
