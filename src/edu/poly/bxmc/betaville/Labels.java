package edu.poly.bxmc.betaville;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Locale;
import java.util.MissingResourceException;
import java.util.Properties;

import org.apache.log4j.Logger;

/**
 * 
 * @author Skye Book
 *
 */
public class Labels {

	private static Logger logger = Logger.getLogger(Labels.class);

	private static Properties languageBundle;

	static{
		logger.info("System Default Locale\t"+Locale.getDefault());
		logger.info("JVM: user.language\t"+System.getProperty("user.language"));
		
		URL defaultBundle = ResourceLoader.loadResource("/data/localization/Labels.properties");
		URL bundleToUse = defaultBundle;
		
		// Use normal 
		String lang = Locale.getDefault().getLanguage().toLowerCase();
		if(!lang.equals("en")){
			bundleToUse = ResourceLoader.loadResource("/data/localization/Labels_"+lang+".properties");
		}
		
		InputStream is = null;
		try {
			is = bundleToUse.openStream();
		} catch (IOException e) {
			try {
				is = defaultBundle.openStream();
			} catch (IOException e1) {
				logger.fatal("Unable to open default language bundle");
			}
		}
		
		languageBundle = new Properties();
		try {
			languageBundle.load(is);
			is.close();
		} catch (IOException e) {
			logger.fatal("Failure while loading labels for localization");
			e.printStackTrace();
		}
	}

	public static String get(String key){
		try{
			return languageBundle.getProperty(key);
		}catch(MissingResourceException e){
			logger.error("Localization string not found for key:\t"+key);
			return key;
		}
	}
	
	public static String get(Class<?> clazz, String key){
		try{
			return languageBundle.getProperty(clazz.getSimpleName()+"."+key);
		}catch(MissingResourceException e){
			logger.error("Localization string not found for key:\t"+key);
			return key;
		}
	}
	
	public static String generic(String key){
		try{
			return languageBundle.getProperty("Generic."+key);
		}catch(MissingResourceException e){
			logger.error("Localization string not found for key:\t"+key);
			return key;
		}
	}
	
	public static String compound(String compound){
		String[] keys = compound.split(" ");
		StringBuilder sb = new StringBuilder();
		for(String key : keys){
			sb.append(get(key)).append(" ");
		}
		
		// Remove trailing whitespace from last append operation
		return sb.toString().trim();
	}
}
