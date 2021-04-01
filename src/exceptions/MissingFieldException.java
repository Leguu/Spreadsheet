package exceptions;

/**
 * Error for missing fields.
 */
public class MissingFieldException extends Exception {
    public String line;
    public int missing;
    public int expected;

    public MissingFieldException(String line, int missing, int expected) {
        super("There are missing fields!");
        this.line = line;
        this.missing = missing;
        this.expected = expected;
    }
}
