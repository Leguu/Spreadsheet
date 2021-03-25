package data;

import data.exceptions.ParseException;

/**
 * Custom tokenizer class that tokenizes and respects quotes.
 */
public class Tokenizer {
    final String[] buf;
    int index = 0;

    public Tokenizer(String line) {
        buf = line.split(",");
    }

    public boolean hasToken() {
        return index < buf.length;
    }

    public String nextToken() throws ParseException {
        if (buf[index].isBlank()) {
            index += 1;
            return null;
        }

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

    public int length() throws ParseException {
        var prev = index;
        var i = 0;
        while (hasToken()) {
            nextToken();
            i += 1;
        }
        index = prev;
        return i;
    }
}
