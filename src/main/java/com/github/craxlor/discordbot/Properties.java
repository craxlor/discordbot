package com.github.craxlor.discordbot;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.logging.Logger;

import javax.annotation.Nullable;

public class Properties {

    private static final Logger logger = com.github.craxlor.utilities.Logger.getLogger("properties");

    @Nullable
    public static String get(String key) {
        try {
            java.util.Properties properties = new java.util.Properties();
            properties.load(new FileInputStream("gradle.properties"));
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
