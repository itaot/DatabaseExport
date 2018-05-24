package me.itaot.database.tools;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

import org.apache.log4j.Logger;

public class ConfigTools {
	private static final Logger log = Logger.getLogger(ConfigTools.class);
	private static Properties properties = new Properties();
	private static String configPath = "";
	
	static{
		try {
			String projectPath = new File("").getCanonicalPath();
			String configPath = projectPath + "\\config.properties";
			properties.load(new FileInputStream(new File(configPath)));
			log.info("config.properties配置文件加載成功...");
		} catch (IOException e) {
			e.printStackTrace();
				log.error("無法打開配置文件[" + configPath + "]");
		}
	}
	
	public static String get(String key){
		return properties.getProperty(key);
	}
}
