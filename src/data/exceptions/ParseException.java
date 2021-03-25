package data.exceptions;

/**
 * Error for failure in parsing. I.e., malformed CSV.
 */
public class ParseException extends Exception{
    public ParseException(String message) {
        super(message);
    }
}
