package data.exceptions;

/**
 * Error for missing attributes.
 */
public class MissingAttributeException extends Exception {
    public String line;
    public int missing;
    public int expected;

    public MissingAttributeException(String line, int missing) {
        super("There are missing fields!");
        this.line = line;
        this.missing = missing;
        this.expected = expected;
    }
}
