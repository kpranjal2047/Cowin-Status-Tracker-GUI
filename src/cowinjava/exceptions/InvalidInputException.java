package cowinjava.exceptions;

/**
 * Thrown to indicate that some input has got illegal or inappropriate value.
 *
 * @author Kumar Pranjal
 */
public class InvalidInputException extends RuntimeException {

    /**
     * Constructs a new InvalidInputException with no detail message.
     */
    public InvalidInputException() {
        super();
    }

    /**
     * Constructs a new InvalidInputException with the specified detail message.
     *
     * @param message the detail message
     */
    public InvalidInputException(String message) {
        super(message);
    }

    /**
     * Constructs a new InvalidInputException with the specified cause and a detail
     * message of <tt>(cause==null ? null : cause.toString())</tt> (which typically
     * contains the class and detail message of <tt>cause</tt>).
     *
     * @param cause the cause (A <tt>null</tt> value is permitted, and indicates
     *              that the cause is nonexistent or unknown)
     */
    public InvalidInputException(Throwable cause) {
        super(cause);
    }

    /**
     * Constructs a new InvalidInputException with the specified detail message and
     * cause.
     *
     * @param message the detail message
     * @param cause   the cause (A <tt>null</tt> value is permitted, and indicates
     *                that the cause is nonexistent or unknown)
     */
    public InvalidInputException(String message, Throwable cause) {
        super(message, cause);
    }
}
