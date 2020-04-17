package application;

import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

/**
 * Manages resources urls
 * resources.properties
 */
public class PropertyManager {
    final static String root = "src/" ;
    final static String resourceFile = "resources.properties";
    static Properties resourceProperties;

    public PropertyManager() {
        resourceProperties = new Properties();
        try {
            InputStream resourceStream = new BufferedInputStream(new FileInputStream(root + resourceFile));
            resourceProperties.load(resourceStream);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static String getResourceProperty(String key) {
        return resourceProperties.getProperty(key, "");
    }

    public static String getResourceProperty(String key, String defaultValue) {
        return resourceProperties.getProperty(key, defaultValue);
    }
}
