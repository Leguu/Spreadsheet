package data;

import exceptions.ParseException;

/**
 * Custom tokenizer class that tokenizes and respects quotes.
 */
public class Tokenizer {
    /**
     * The record
     */
    final String[] buf;
    int index = 0;

    /**
     * The Constructor of Tokenizer, will split the line in commas
     * @param line The String to split
     */
    public Tokenizer(String line) {
        buf = line.split(",", -1);
    }

    /**
     * This method looks if theres another token in the record
     * @return Returns a boolean weather or not the index is smaller than length of buf
     */
    public boolean hasToken() {
        return index < buf.length;
    }

    /**
     * This methods returns the String of the next token in the record
     * @return A String of the next token
     * @throws ParseException Looks if there's a missing quote and throws a ParseException
     */
    public String nextToken() throws ParseException {
        // If the token is blank, then we know a field is missing.
        if (buf[index].isBlank()) {
            index += 1;
            return null;
        }

        // If the token does not start with a quote, or if it ends with a quote, then we know we can return it as-is.
        // The second condition here is for when there's a single-word token surrounded by quotations.
        if (!buf[index].startsWith("\"") || buf[index].endsWith("\"")) {
            return buf[index++];
        }

        var sb = new StringBuilder(buf[index++]);
        while (index < buf.length) {
            sb.append(",").append(buf[index]);
            if (buf[index++].endsWith("\""))
                return sb.toString();
        }

        throw new ParseException("There is a missing closing quote!");
    }
}
