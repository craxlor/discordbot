package com.github.craxlor.discordbot;

import java.io.IOException;
import java.io.InputStream;

import javax.annotation.Nullable;

import org.slf4j.LoggerFactory;
import org.slf4j.MDC;

public class Properties {

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
            MDC.put("filename", "properties");
            LoggerFactory.getLogger("sift").warn("""
                    gradle.properties file cannot be found or the given key cannot be found inside gradle.properties
                    ---------------------
                    """ + e.getMessage());
            return null;
        }
    }
}
