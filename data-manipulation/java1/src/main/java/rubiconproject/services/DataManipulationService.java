package rubiconproject.services;

import rubiconproject.exceptions.InvalidArgumentException;

public interface DataManipulationService {

    void manipulate(String pathToDirectory, String outputFile) throws InvalidArgumentException;
}
