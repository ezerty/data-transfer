package rubiconproject.utils;

import java.io.Reader;

public interface DataSource {

    String getId();
    
    InputDataFormat getInputDataFormat();

    Reader getDataReader();
}
