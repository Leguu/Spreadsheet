package data.exceptions;

/**
 * Error type for errors related to the files given by the user.
 * This could mean files with insufficient entries, or a non-existent file.
 */
public class InputException extends Exception {
    public InputException(String message) {
        super(message);
    }
}
