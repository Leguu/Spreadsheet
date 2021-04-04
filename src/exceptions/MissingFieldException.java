package exceptions;

/**
 * Error for missing fields.
 */
public class MissingFieldException extends Exception {
    public String line;
    public int missing;
    public int expected;

    /**
     * Constructor of MissingFieldException. Will register the line, missing and expected values.
     * @param line The line contains the string where the field is missing
     * @param missing The index where the field is missing
     * @param expected The index which was expected
     */
    public MissingFieldException(String line, int missing, int expected) {
        super("There are missing fields!");
        this.line = line;
        this.missing = missing;
        this.expected = expected;
    }
}
