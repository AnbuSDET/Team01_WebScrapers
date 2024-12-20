package com.utilities;
import java.io.FileInputStream;
import java.util.Properties;
	public class PropertyFileReader {

		private final static String propertyFilePath = "./src/test/resources/input_data/Global.properties";
		
		
		static FileInputStream fis;
		static Properties prop;

		public static String getGlobalValue(String key) throws Throwable {
			try {
				prop = new Properties();
				fis = new FileInputStream(propertyFilePath);
				prop.load(fis);
				fis.close();
			} catch (Exception e) {
				e.printStackTrace();
				throw new RuntimeException("Configuration.properties not found at " + propertyFilePath);
			}
			return prop.getProperty(key);
		}


	}