package cowinjava.exceptions;

import java.io.IOException;

/**
 * Thrown when Twilio secrets file cannot be read.
 *
 * @author Kumar Pranjal
 */
public class SecretsFileNotFoundException extends IOException {

    /**
     * Constructs a {@code SecretsFileNotFoundException} with the specified detail
     * message.
     *
     * @param message the detail message
     */
    public SecretsFileNotFoundException(final String message) {
        super(message);
    }

}
