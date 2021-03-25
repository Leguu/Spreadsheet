package data.records;

import data.Tokenizer;
import data.exceptions.InputException;
import data.exceptions.MissingFieldException;
import data.exceptions.ParseException;

import java.util.Arrays;
import java.util.Objects;

/**
 * A record represents a single line of a CSV document.
 */
public class Record {
    public final String[] data;
    private final Tokenizer tokenizer;

    /**
     * Create a new Record with a length equal to the number of tokens present.
     * Only use to create an attribute record.
     *
     * @param line the line to convert.
     * @throws MissingFieldException
     * @throws ParseException
     */
    public Record(String line) throws MissingFieldException, ParseException {
        tokenizer = new Tokenizer(line);
        this.data = new String[tokenizer.length()];
        parseCSV();
    }

    /**
     * Creates a new Record with pre-set length.
     * Use this for creating non-attribute records.
     *
     * @param line
     * @param len
     * @throws MissingFieldException
     * @throws ParseException
     */
    public Record(String line, int len) throws MissingFieldException, ParseException {
        tokenizer = new Tokenizer(line);
        this.data = new String[len];
        parseCSV();
    }

    public void parseCSV() throws MissingFieldException, ParseException {
        // Counts the number of missing fields.
        var missing = 0;

        for (int i = 0; tokenizer.hasToken(); i += 1) {
            if (i >= data.length)
                throw new ParseException("There are more entries than attributes!");
            var token = tokenizer.nextToken();

            if (token == null) {
                data[i] = "*****";
                missing += 1;
                continue;
            }
            
            token = token.trim();

            // If it's a number don't add quotes around it.
            // If it is, but has quotes already, leave it as it is.
            // If it is, and doesn't have quotes, add quotes.
            try {
                Double.parseDouble(token);
                data[i] = token;
            } catch (NumberFormatException ignored) {
                if (token.startsWith("\"")) data[i] = token;
                else data[i] = '"' + token + '"';
            }

        }

        if (missing != 0) throw new MissingFieldException(
                String.join(", ", data),
                missing,
                data.length
        );
    }

    @Override
    public String toString() {
        return "Record{" +
                "data=" + Arrays.toString(data) +
                '}';
    }

    public String toJSON(String[] attributes) throws InputException {
        if (attributes.length > Arrays.stream(data).filter(Objects::nonNull).count()) throw new InputException("There are more attributes than fields!");
        var sb = new StringBuilder("{\n");
        for (int i = 0; i < attributes.length; i++) {
            sb.append('\t');
            sb.append(attributes[i]).append(": ");
            sb.append(data[i]);
            sb.append(i == attributes.length - 1 ? "\n" : ",\n");
        }
        sb.append("}");
        return sb.toString();
    }
}
