package rubiconproject.utils;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.Writer;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import rubiconproject.model.FinishMark;
import rubiconproject.model.SiteCollection;

public class OutputWriter {

    private final Logger logger = LoggerFactory.getLogger(getClass());

    private final ObjectMapper jsonMapper = new ObjectMapper();

    private Writer output;

    private boolean started;

    private BlockingQueue<SiteCollection> siteCollections = new ArrayBlockingQueue<>(1024);

    public OutputWriter(Writer output) {
        this.output = output;
    }

    private void start() {
        new Thread(() -> {
            try (BufferedWriter writer = new BufferedWriter(output)) {
                while (true) {
                    try {
                        SiteCollection siteCollection = siteCollections.take();
                        if (siteCollection instanceof FinishMark) {
                            break;
                        }
                        writer.append(jsonMapper.writeValueAsString(siteCollection));
                        writer.newLine();
                    } catch (InterruptedException e) {
                        logger.error("Output writing has been interrupted", e);
                    } catch (IOException e) {
                        logger.debug("Failed to write output", e);
                    }
                }
            } catch (IOException e) {
                logger.error("Failed to close output writer", e);
            }
        }).start();
    }

    public void add(SiteCollection siteCollection) {
        try {
            siteCollections.put(siteCollection);
            if (!started) {
                start();
                started = true;
            }
        } catch (InterruptedException e) {
            logger.error("Output writing has been interrupted", e);
        }
    }

}
