package com.github.craxlor.discordbot;

import java.io.IOException;
import java.io.InputStream;
import java.util.logging.Logger;

import javax.annotation.Nullable;

public class Properties {

    private static final Logger logger = com.github.craxlor.utilities.Logger.getLogger("properties");

    @Nullable
    public static String get(String key) {
        try {
            java.util.Properties properties = new java.util.Properties();
            InputStream is = Properties.class.getClassLoader().getResourceAsStream("gradle.properties");
            properties.load(is);
            is.close();
            return properties.getProperty(key);
        } catch (IOException e) {
            e.printStackTrace();
            logger.warning("""
                    gradle.properties file cannot be found or the given key cannot be found inside gradle.properties
                    ---------------------
                    """ + e.getMessage());
            return null;
        }
    }
}
