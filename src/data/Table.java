package data;

import data.records.Record;
import exceptions.InputException;
import exceptions.MissingFieldException;
import exceptions.ParseException;

import java.io.IOException;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Scanner;

/**
 * The Table class represents a CSV file. It contains both attributes and entries of the file.
 */
public class Table {
    /**
     * The attributes of the file
     */
    private final Record attributes;
    /**
     * An arraylist of entries (data)
     */
    private final ArrayList<Record> entries = new ArrayList<>();

    /**
     * This method is the constructor of the Table Class. It will write to the Log file any errors/missing values. It will also record valid attributes and entries.
     * @param reader This is where we read the csv file
     * @param log This is where we log all of out errors/missing values
     * @param name The name of the file we read from
     * @throws InputException Throws InputException if we have insufficient entries, or a non-existent file.
     * @throws ParseException Throws ParseException if we have failure in parsing
     * @throws IOException Throws IOException if we have a FileNotFoundException or NoSuchElementException
     * @throws MissingFieldException Throws MissingFieldException when we have missing fields in CSV
     */
    public Table(Scanner reader, Writer log, String name) throws InputException, ParseException, IOException, MissingFieldException {
        var line = "";

        // Skip all the blank lines
        while (reader.hasNextLine() && line.isBlank())
            line = reader.nextLine();

        // If the final line is blank, then we know the file is empty.
        if (line.isBlank()) throw new InputException("File \"" + name + "\" has no entries!");

        // If this throws an error, we need to stop the creation of the Table.
        attributes = new Record(line);

        var missingFields = 0;
        for (var i = 0; reader.hasNextLine(); i++) {
            line = reader.nextLine();
            // Skip blank lines
            if (line.isBlank()) continue;

            // If this throws an error, we need to handle it here and skip the entry.
            try {
                entries.add(new Record(line));
            } catch (MissingFieldException e) {
                System.err.print("WARN: In file \"" + name + "\", line " + (i + 1) + ": ");
                System.err.println(e.getMessage() + " Skipping entry...");
                log.append("Line ").append(String.valueOf(i)).append(": ").append(e.line).append("\n");
                missingFields += e.missing;
            }
        }

        // If we have missing fields, we need to write them to log.
        if (missingFields > 0) {
            log.append("Errors in file \"").append(name).append("\".\n");
            log.append("Total number of missing fields: ")
                    .append(String.valueOf(missingFields))
                    .append(". Expected number of fields per line: ")
                    .append(String.valueOf(attributes.length()))
                    .append(".\n");
            log.append("-----\n");
        }
    }

    /**
     * This method will write the attributes and entries to JSON only if all attributes are available
     * @param writer This parameter is where the JSON file we write to
     * @throws IOException Throws IOException if we have a FileNotFoundException or NoSuchElementException
     * @throws InputException Throws InputException if we have insufficient entries, or a non-existent file.
     */
    public void writeJSON(Writer writer) throws IOException, InputException {
        writer.write("[\n");
        for (int i = 0; i < entries.size(); i++) {
            if (entries.get(i) == null) continue;
            writer.write('\t');
            // Replace newlines with newlines and tab, in order to provide proper indenting.
            writer.write(entries.get(i).toJSON(attributes).replaceAll("\n", "\n\t"));
            writer.write(i != entries.size() - 1 ? ",\n" : "\n");
        }
        writer.write("]\n");
    }
}
