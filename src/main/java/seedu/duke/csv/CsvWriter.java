package seedu.duke.csv;

import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;

import com.opencsv.CSVWriter;
import seedu.duke.exception.DukeException;

public class CsvWriter {

    private CSVWriter writer;

    public CsvWriter(String fullPath) throws DukeException {
        this(fullPath, false);
    }

    public CsvWriter(String fullPath, boolean isAppend) throws DukeException {
        try {
            Writer fileWriter = new FileWriter(fullPath, isAppend);
            this.writer = new CSVWriter(fileWriter, CSVWriter.DEFAULT_SEPARATOR,
                                        CSVWriter.DEFAULT_QUOTE_CHARACTER,
                                        CSVWriter.DEFAULT_ESCAPE_CHARACTER,
                                        CSVWriter.RFC4180_LINE_END);
        } catch (IOException e) {
            throw new DukeException("Cannot create file");
        }
    }

    public void write(String[] data) {
        assert writer != null;
        writer.writeNext(data);
    }

    public void close() throws DukeException {
        try {
            writer.close();
        } catch (IOException e) {
            throw new DukeException("Error Closing File");
        }
    }
}
