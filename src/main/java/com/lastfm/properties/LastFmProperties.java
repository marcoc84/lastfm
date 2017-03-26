package com.lastfm.properties;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;
import org.apache.log4j.Logger;

public class LastFmProperties {

    private final static Logger logger = Logger.getLogger(LastFmProperties.class);

    private static LastFmProperties selfInstance;

    private Properties properties;

    private LastFmProperties() {}

    public static LastFmProperties getInstance() {
        synchronized (LastFmProperties.class) {
            if (selfInstance == null) {
                selfInstance = new LastFmProperties();
                selfInstance.loadProperties();
            }
        }

        return selfInstance;
    }

    public String get(String key) {
        Object value = properties.get(key);

        if(value != null) {
            return String.valueOf(value);
        } else {
            return "";
        }
    }

    private void loadProperties() {
        try {
            ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
            InputStream input = classLoader.getResourceAsStream("lastfm.properties");
            properties = new Properties();
            properties.load(input);
        } catch (FileNotFoundException e) {
            logger.error("FileNotFoundException error:" + e.getMessage(), e);
        } catch (IOException e) {
            logger.error("IOException error:" + e.getMessage(), e);
        }
    }
}
