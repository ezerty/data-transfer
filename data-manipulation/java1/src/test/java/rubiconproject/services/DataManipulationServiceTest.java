package rubiconproject.services;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import org.junit.Assert;
import org.junit.Test;
import rubiconproject.exceptions.InvalidArgumentException;
import rubiconproject.model.Site;
import rubiconproject.model.SiteCollection;
import rubiconproject.services.impl.DataManipulationServiceImpl;

public class DataManipulationServiceTest {

    DataManipulationService service = DataManipulationServiceImpl.getInstance();

    private ObjectMapper mapper = new ObjectMapper();

    @Test(expected = InvalidArgumentException.class)
    public void testEmptyArguments() {
        service.manipulate("", "");
    }

    @Test(expected = InvalidArgumentException.class)
    public void testEmptyInputDirectory() {
        service.manipulate("./var", "./var/output");
    }

    @Test
    public void testManipulate() throws IOException, InterruptedException {
        createFile("./tmp/input/input1.json", "[{\"site_id\": \"13000\", \"name\": \"example.com/json1\", \"mobile\": 1, \"score\": 21 },"
                + "{\"site_id\": \"13001\", \"name\": \"example.com/json2\", \"mobile\": 0, \"score\": 97 },{\"site_id\": "
                + "\"13002\", \"name\": \"example.com/json3\", \"mobile\": 0, \"score\": 311 }]");
        createFile("./tmp/input/input2.csv", "id,name,is mobile,score\n"
                + "12000,example.com/csv1,true,454\n"
                + "12001,example.com/csv2,true,128\n"
                + "12002,example.com/csv3,false,522");

        service.manipulate("./tmp/input", "./tmp/output.json");
        Thread.sleep(500);

        final List<String> expectedResults = Arrays.asList("{\"collectionId\":\"input1.json\",\"sites\":[{\"name\":\"example.com/json1\",\"mobile\":\"1\","
                + "\"keywords\":\"foo,bar,baz\",\"score\":21,\"site_id\":13000},{\"name\":\"example.com/json2\","
                + "\"mobile\":\"0\",\"keywords\":\"foo,bar,baz\",\"score\":97,\"site_id\":13001},{\"name\":"
                + "\"example.com/json3\",\"mobile\":\"0\",\"keywords\":\"some,more,words\",\"score\":311,\"site_id\":13002}]}",
                "{\"collectionId\":\"input2.csv\",\"sites\":[{\"name\":\"example.com/csv1\",\"mobile\":\"1\","
                + "\"keywords\":\"japan,travel\",\"score\":454,\"site_id\":12000},{\"name\":\"example.com/csv2\","
                + "\"mobile\":\"1\",\"keywords\":\"sports,tennis,recreation\",\"score\":128,\"site_id\":12001},"
                + "{\"name\":\"example.com/csv3\",\"mobile\":\"0\",\"keywords\":\"some,more,words\",\"score\":522,"
                + "\"site_id\":12002}]}"
        );
        BufferedReader reader = new BufferedReader(new FileReader("./tmp/output.json"));
        int count = 0;
        while(true) {
            String line = reader.readLine();
            if(line == null) {
                break;
            }
            SiteCollection actualCollection = mapper.readValue(line, SiteCollection.class);
            assertEquals(mapper.readValue(expectedResults.get(count), SiteCollection.class), actualCollection);

            count++;
        }

        Assert.assertEquals(2, count);
    }

    private void createFile(String path, String content) throws IOException {
        try (FileWriter fileWriter = new FileWriter(path)) {
            fileWriter.write(content);
        }
    }

    private void assertEquals(SiteCollection expected, SiteCollection actual) {
        Assert.assertTrue(stripKeywords(expected).equals(stripKeywords(actual)));
    }

    private SiteCollection stripKeywords(SiteCollection collection) {
        collection.getSites().forEach((Site site) -> {
            site.setKeywords(null);
        });
        return collection;
    }
}
