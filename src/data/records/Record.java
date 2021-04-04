package data.records;

import data.Tokenizer;
import exceptions.InputException;
import exceptions.MissingFieldException;
import exceptions.ParseException;

import java.util.ArrayList;

/**
 * A record represents a single line of a CSV document.
 */
public class Record {
    private final ArrayList<String> data = new ArrayList<>();

    /**
     * Create a new Record with a length equal to the number of tokens present.
     * Only use to create an attribute record.
     *
     * @param line the String to convert.
     */
    public Record(String line) throws MissingFieldException, ParseException {
        var tokenizer = new Tokenizer(line);

        var missing = 0;
        while (tokenizer.hasToken()) {
            var token = tokenizer.nextToken();

            if (token == null) {
                data.add("*****");
                missing += 1;
                continue;
            }

            token = token.trim();

            // If it's a number don't add quotes around it.
            // If it is, but has quotes already, leave it as it is.
            // If it is, and doesn't have quotes, add quotes.
            try {
                Double.parseDouble(token);
                data.add(token);
            } catch (NumberFormatException ignored) {
                if (token.startsWith("\"")) data.add(token);
                else data.add('"' + token + '"');
            }
        }

        if (missing != 0) throw new MissingFieldException(
                String.join(", ", data),
                missing,
                data.size()
        );
    }

    /**
     *The length method simply returns the length of record's data.
     * @return The data size
     */
    public int length() {
        return data.size();
    }

    /**
     *
     * This methods prints out the attributes and data to JSON
     * @param attributes The record representing the attribute
     * @throws InputException Will throw whenever there are mroe attributes than fields. No JSON will be created
     */
    public String toJSON(Record attributes) throws InputException {
        if (attributes.length() > data.size())
            throw new InputException("There are more attributes than fields!");
        var sb = new StringBuilder("{\n");
        for (int i = 0; i < attributes.length(); i++) {
            sb.append('\t');
            sb.append(attributes.data.get(i)).append(": ");
            sb.append(data.get(i));
            sb.append(i == attributes.length() - 1 ? "\n" : ",\n");
        }
        sb.append("}");
        return sb.toString();
    }
}
