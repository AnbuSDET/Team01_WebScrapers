package com.utilities;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

public class PropertyFileReader {

    private final static String propertyFilePath = "./src/test/resources/Global.properties";
    
    // Properties object to load the property file once
    private static Properties prop = new Properties();

    // Method to get the value from properties file for a specific key
    public static String getGlobalValue(String key) throws Throwable {
        FileInputStream fis = null;
        try {
            // Open the properties file if it's not loaded already
            fis = new FileInputStream(propertyFilePath);
            prop.load(fis);
        } catch (IOException e) {
            // Log the error and rethrow the exception with a detailed message
            e.printStackTrace();
            throw new RuntimeException("Global.properties file not found or could not be loaded at " + propertyFilePath);
        } finally {
            // Ensure FileInputStream is closed to prevent resource leak
            if (fis != null) {
                try {
                    fis.close();
                } catch (IOException e) {
                    e.printStackTrace(); // Log if closing the input stream fails
                }
            }
        }

        // Fetch and return the property value for the given key
        String value = prop.getProperty(key);
        if (value == null) {
            throw new RuntimeException("Key '" + key + "' not found in the properties file.");
        }
        return value;
    }
}
