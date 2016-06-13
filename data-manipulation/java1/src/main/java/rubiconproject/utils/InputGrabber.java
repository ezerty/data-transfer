package rubiconproject.utils;

import com.fasterxml.jackson.databind.MappingIterator;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.csv.CsvMapper;
import com.fasterxml.jackson.dataformat.csv.CsvSchema;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.Reader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import rubiconproject.model.FinishMark;
import rubiconproject.model.Site;
import rubiconproject.model.SiteCollection;

public class InputGrabber implements InputReader, DataProvider<SiteCollection> {

    private static volatile InputGrabber instance;

    private InputGrabber() {
    }

    public static InputGrabber getInstance() {
        if (instance == null) {
            synchronized (InputGrabber.class) {
                if (instance == null) {
                    instance = new InputGrabber();
                }
            }
        }
        return instance;
    }

    private final Logger logger = LoggerFactory.getLogger(getClass());

    private SiteParser jsonParser = new SiteParser() {
        private ObjectMapper objectMapper = new ObjectMapper();

        @Override
        public List<Site> parse(Reader reader) throws IOException {
            return objectMapper.readValue(reader, objectMapper.getTypeFactory().constructCollectionType(List.class, Site.class));
        }
    };

    private SiteParser csvParser = new SiteParser() {

        CsvMapper mapper = new CsvMapper();

        CsvSchema schema = CsvSchema.emptySchema().withHeader();

        @Override
        public List<Site> parse(Reader reader) throws IOException {
            CsvSchema schema = CsvSchema.emptySchema().withHeader();
            MappingIterator<Map<String, String>> it = mapper.readerFor(Map.class).with(schema).readValues(reader);
            List<Site> sites = new ArrayList<>();
            while (it.hasNext()) {
                Map<String, String> siteFields = it.next();
                logger.info(siteFields.toString());
                Site site = new Site();
                site.setId(Integer.valueOf(siteFields.get("id")));
                site.setName(siteFields.get("name"));
                site.setMobile(siteFields.get("is mobile").equals("true"));
                site.setScore(Integer.valueOf(siteFields.get("score")));
                sites.add(site);
            }
            return sites;
        }
    };

    private ExecutorService executor = Executors.newSingleThreadExecutor();

    private BlockingQueue<SiteCollection> parsedObjects = new ArrayBlockingQueue<>(1024);

    @Override
    public void addSources(Iterable<DataSource> dataSources) {
        executor.submit(() -> {

            dataSources.forEach((DataSource dataSource) -> {

                SiteParser parser = dataSource.getInputDataFormat() == InputDataFormat.JSON
                        ? jsonParser : csvParser;
                try (BufferedReader reader = new BufferedReader(dataSource.getDataReader())) {
                    List<Site> sites = Collections.EMPTY_LIST;
                    try {
                        sites = parser.parse(reader);
                    } catch (IOException e) {
                        logger.warn(String.format("Failed to parse file: [%s] with [%s] mapper",
                                dataSource.getId(), dataSource.getInputDataFormat().name()), e);
                    }
                    parsedObjects.put(new SiteCollection(dataSource.getId(), sites));
                } catch (IOException e) {
                    logger.error("Failed to close input reader", e);
                } catch (InterruptedException e) {
                    logger.error("Failed to grab collection, interrupted", e);
                }
            });
            while (true) {
                try {
                    parsedObjects.put(new FinishMark());
                    break;
                } catch (InterruptedException e) {
                    logger.warn("Failed to send finish mark, interrupted");
                }
            }

        }
        );
    }

    @Override
    public SiteCollection getNext() throws InterruptedException {
        return parsedObjects.take();
    }

    public void close() {
        executor.shutdown();

    }

    private interface SiteParser {

        public List<Site> parse(Reader reader) throws IOException;
    }

}
