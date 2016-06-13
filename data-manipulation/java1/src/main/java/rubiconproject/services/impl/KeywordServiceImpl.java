package rubiconproject.services.impl;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;
import rubiconproject.services.KeywordService;

public class KeywordServiceImpl implements KeywordService {

    private final List<String> keywordsPool = Arrays.asList("sports,tennis,recreation", "japan,travel", "some,more,words", "foo,bar,baz");

    private static volatile KeywordServiceImpl instance;

    private KeywordServiceImpl() {
    }

    public static KeywordServiceImpl getInstance() {
        if (instance == null) {
            synchronized (KeywordServiceImpl.class) {
                if (instance == null) {
                    instance = new KeywordServiceImpl();
                }
            }
        }
        return instance;
    }

    @Override
    public String resolveKeywords(Object site) {
        return keywordsPool.get(ThreadLocalRandom.current().nextInt(0, keywordsPool.size()));
    }

}
