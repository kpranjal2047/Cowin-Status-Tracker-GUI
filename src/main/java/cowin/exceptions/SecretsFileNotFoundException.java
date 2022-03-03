package cowin.exceptions;

import java.io.IOException;

/**
 * Thrown when Twilio secrets file cannot be read.
 *
 * @author Kumar Pranjal
 */
public class SecretsFileNotFoundException extends IOException {

    /**
     * Constructs a {@code SecretsFileNotFoundException} with {@code null} as its error detail
     * message.
     */
    public SecretsFileNotFoundException() {
        super();
    }

}
