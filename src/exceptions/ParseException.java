package exceptions;

/**
 * Error for failure in parsing. I.e., malformed CSV.
 */
public class ParseException extends Exception{
    /**
     * Parse Exception will print out the message in parameter
     * @param message This parameter is the desired message to be printed out
     */
    public ParseException(String message) {
        super(message);
    }
}
