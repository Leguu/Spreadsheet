import data.exceptions.InputException;
import data.exceptions.MissingAttributeException;
import data.exceptions.MissingFieldException;
import data.exceptions.ParseException;
import data.records.Record;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.util.Arrays;
import java.util.Scanner;

public class Driver {
    PrintWriter log;

    public Driver() {
        try {
            var file = new File("errors.txt");
            this.log = new PrintWriter(file);
        } catch (FileNotFoundException | SecurityException e) {
            System.err.println("SEVERE: Log file cannot be created or accessed. Exiting program...");
            System.exit(1);
        }
    }

    public static void main(String[] args) {
        if (args.length < 1) {
            System.out.println("Please input at least 1 file name.");
            return;
        }

        Driver driver = new Driver();

        driver.csvToJson(args);

        driver.log.close();
    }

    static void writeToJson(String name, Record[] records) throws IOException, InputException {
        var file = new File(name);
        var output = new PrintWriter(file);
        

        output.println("[");
        var attributes = records[0].data;
        for (int i = 1; i < records.length; i++) {
            if (records[i] == null) continue;
            output.print('\t');
            // Replace newlines with newlines and tab, in order to provide proper indenting.
            output.print(records[i].toJSON(attributes).replaceAll("\n", "\n\t"));
            output.println(i != records.length - 1 ? "," : "");
        }
        output.println("]");

        output.close();
    }

    static String fileType(String name) {
        return name.substring(name.lastIndexOf(".") + 1);
    }

    /**
     * Reads a CSV file and returns an array of records.
     *
     * @param name The name of the file to read.
     * @return An array with Records or null values where a field is missing.
     * @throws IOException
     * @throws InputException
     * @throws MissingAttributeException
     * @throws ParseException
     */
    Record[] readCSV(String name) throws IOException, InputException, MissingAttributeException, ParseException {
        var input = new File(name);
        if (!input.isFile() || !input.exists())
            throw new InputException("This is not a file, or the file does not exist.");

        // The number of non-empty lines in the file.
        // These are the records.
        var lines = Files.lines(input.toPath()).filter(s -> !s.isEmpty()).count();
        if (lines < 2) throw new InputException("The file doesn't have any entries!");

        // Time to read the CSV
        Scanner reader = new Scanner(input);

        // Total number of missing fields in the entire file.
        // Also keeps track of whether an error is discovered.
        var missingFields = 0;
        var records = new Record[(int) lines];

        // The first Record (attributes) need to be treated separately.
        // This is because a missing field in the attributes needs to stop execution of this function.
        // Normal records with a missing field can simply be skipped.
        try {
            records[0] = new Record(reader.nextLine());
        } catch (MissingFieldException e) {
            throw new MissingAttributeException(e.line, e.missing);
        }

        // For each line in the reader, we try and create a Record object.
        // If the line has missing fields, then we print it out and skip.
        for (int i = 1; i < lines; i++) {
            try {
                records[i] = new Record(reader.nextLine(), records[0].data.length);
            } catch (MissingFieldException e) {
                System.err.print("WARN: In file \"" + name + "\", line " + (i + 1) + ": ");
                System.err.println(e.getMessage() + " Skipping entry...");
                log.append("Line " + i + ": " + e.line).append("\n");
                missingFields += e.missing;
            }
        }

        // If there were errors, we need to append to the log the name of the file,
        // the number of fields missing, the expected number of fields per line,
        // and a divisor.
        if (missingFields > 0) {
            log.append("Errors in file \"").append(name).append("\".\n");
            log.append("Total number of missing fields: ")
                    .append(String.valueOf(missingFields))
                    .append(". Expected number of fields per line: ")
                    .append(String.valueOf(records[0].data.length))
                    .append(".\n");
            log.append("-----\n");
        }

        return records;
    }

    void csvToJson(String[] args) {
        for (String input : args) {
            try {
                var filetype = fileType(input);
                var output = input.replace(filetype, "json");
                var records = readCSV(input);
                writeToJson(output, records);
                System.err.println("INFO: File \"" + input + "\" converted successfully and saved as JSON.");
            } catch (IOException e) {
                // If an IOException of any kind occurs, we need to delete all the previously created files.
                System.err.println("SEVERE: Unrecoverable error occurred for file \"" + input + "\": " + e.getMessage());
                System.err.println("\tDeleting all working files...");
                // Rename all input files to their output names.
                var outputs = Arrays.stream(args).map(s -> {
                    var filetype = fileType(s);
                    return s.replace(filetype, "json");
                }).toArray(String[]::new);
                for (String output : outputs) {
                    if (input.equals(output)) continue;
                    var file = new File(output);
                    file.delete();
                }
                break;
            } catch (InputException e) {
                System.err.println("ERROR: Could not open file \"" + input + "\" for reading: " + e.getMessage());
                System.err.println("\tSkipping file...");
            } catch (MissingAttributeException e) {
                System.err.println("ERROR: Attribute(s) missing in file \"" + input + "\". File failed to convert!");
                System.err.println("\tSkipping file...");
                log.append(e.line).append("\n");
                log.append("Attributes missing in file \"").append(input).append("\".\n");
                log.append("Missing ").append(String.valueOf(e.missing)).append(" attribute(s).\n");
                log.append("-----\n");
            } catch (ParseException e) {
                System.err.print("ERROR: File \"" + input + "\" is invalid: ");
                System.err.println(e.getMessage());
            }
        }
    }
}
