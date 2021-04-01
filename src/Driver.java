import data.Table;
import exceptions.InputException;
import exceptions.MissingFieldException;
import exceptions.ParseException;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.nio.file.Path;
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

        for (String arg : args) {
            if (Files.notExists(Path.of(arg))) {
                System.err.println("File \"" + arg + "\" does not exist. Closing all files and exiting...");
                return;
            }
        }

        Driver driver = new Driver();

        driver.csvToJson(args);

        driver.log.close();
    }

    void csvToJson(String[] args) {
        for (String input : args) {
            try {
                var reader = new Scanner(new File(input));
                var writer = new PrintWriter(input.replace(".txt", ".json"));

                var table = new Table(reader, log, input);
                table.writeJSON(writer);

                reader.close();
                writer.close();

                System.err.println("INFO: File \"" + input + "\" converted successfully and saved as JSON.");
            } catch (IOException e) {
                // If an IOException of any kind occurs, we need to delete all the previously created files.
                System.err.println("SEVERE: Unrecoverable error occurred for file \"" + input + "\": " + e.getMessage());
                System.err.println("\tExiting application...");
                return;
            } catch (InputException e) {
                System.err.println("ERROR: Could not open file \"" + input + "\" for reading: " + e.getMessage());
                System.err.println("\tSkipping file...");
            } catch (MissingFieldException e) {
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
