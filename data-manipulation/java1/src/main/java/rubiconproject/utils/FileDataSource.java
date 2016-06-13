package rubiconproject.utils;

import java.io.FileReader;
import java.io.Reader;

public class FileDataSource implements DataSource {

    private String id;

    private FileReader reader;

    private InputDataFormat inputDataFormat;

    public FileDataSource(String id, FileReader reader, InputDataFormat inputDataFormat) {
        this.id = id;
        this.reader = reader;
        this.inputDataFormat = inputDataFormat;
    }

    @Override
    public InputDataFormat getInputDataFormat() {
        return inputDataFormat;
    }

    @Override
    public Reader getDataReader() {
        return reader;
    }

    @Override
    public String getId() {
        return id;
    }

}
