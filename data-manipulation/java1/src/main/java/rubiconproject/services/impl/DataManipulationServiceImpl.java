package rubiconproject.services.impl;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import rubiconproject.exceptions.InvalidArgumentException;
import rubiconproject.model.FinishMark;
import rubiconproject.model.Site;
import rubiconproject.model.SiteCollection;
import rubiconproject.services.DataManipulationService;
import rubiconproject.services.KeywordService;
import rubiconproject.utils.DataSource;
import rubiconproject.utils.FileDataSource;
import rubiconproject.utils.InputDataFormat;
import rubiconproject.utils.InputGrabber;
import rubiconproject.utils.OutputWriter;

public class DataManipulationServiceImpl implements DataManipulationService {

    private final Logger logger = LoggerFactory.getLogger(getClass());

    private KeywordService keywordService = KeywordServiceImpl.getInstance();

    private InputGrabber grabber = InputGrabber.getInstance();

    private static volatile DataManipulationServiceImpl instance;

    private DataManipulationServiceImpl() {
    }

    public static DataManipulationServiceImpl getInstance() {
        if (instance == null) {
            synchronized (DataManipulationServiceImpl.class) {
                if (instance == null) {
                    instance = new DataManipulationServiceImpl();
                }
            }
        }
        return instance;
    }

    @Override
    public void manipulate(String pathToDirectory, String outputFile) throws InvalidArgumentException {
        if(pathToDirectory == null || outputFile == null || pathToDirectory.trim().isEmpty() || outputFile.trim().isEmpty()) {
            throw new InvalidArgumentException("Invalid arguments specified", null);
        }
        try {
            List<DataSource> inputFiles = new ArrayList<>();
            Files.walk(Paths.get(pathToDirectory)).forEach((Path path) -> {
                try {
                    if (Files.isRegularFile(path)) {
                        inputFiles.add(new FileDataSource(path.getFileName().toString(), new FileReader(path.toFile()),
                                path.toString().endsWith(".json") ? InputDataFormat.JSON : InputDataFormat.CSV));
                    }
                } catch (FileNotFoundException e) {
                    throw new InvalidArgumentException(String.format("Failed to read files from: %s", pathToDirectory), e);
                }
            });

            grabber.addSources(inputFiles);

            OutputWriter outputWriter = new OutputWriter(new FileWriter(outputFile));

            SiteCollection siteCollection = null;
            while (!(siteCollection instanceof FinishMark)) {
                try {
                    siteCollection = grabber.getNext();
                    if (!(siteCollection instanceof FinishMark)) {
                        siteCollection.getSites().parallelStream().forEach((Site site) -> {
                            site.setKeywords(keywordService.resolveKeywords(site));
                        });
                    }
                    outputWriter.add(siteCollection);
                } catch (InterruptedException e) {
                    logger.error("Input reading has been interrupted", e);
                }
            }
            grabber.close();

        } catch (IOException e) {
            throw new InvalidArgumentException(String.format("Failed to read files from: %s", pathToDirectory), e);
        }

    }

}
