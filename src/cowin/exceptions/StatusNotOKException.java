package cowin.exceptions;

import java.io.IOException;

/**
 * Thrown when server does not reply with HTTP status code 200.
 *
 * @author Kumar Pranjal
 */
public class StatusNotOKException extends IOException {

    /**
     * Constructs a {@code StatusNotOKException} with the specified detail message.
     *
     * @param message the detail message
     */
    public StatusNotOKException(final String message) {
        super(message);
    }

}
