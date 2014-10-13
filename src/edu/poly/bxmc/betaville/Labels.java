package edu.poly.bxmc.betaville;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.Locale;
import java.util.MissingResourceException;
import java.util.ResourceBundle;

import org.apache.log4j.Logger;

/**
 * 
 * @author Skye Book
 *
 */
public class Labels {

	private static Logger logger = Logger.getLogger(Labels.class);

	private static ResourceBundle bundle;

	static{
		File file = new File("data/localization");
		try {
			ClassLoader bundleLoader = new URLClassLoader(new URL[]{file.toURI().toURL()});
			bundle = ResourceBundle.getBundle("Labels", Locale.getDefault(), bundleLoader);
		} catch (MalformedURLException e) {
			e.printStackTrace();
		}
	}

	public static String get(String key){
		try{
			return bundle.getString(key);
		}catch(MissingResourceException e){
			logger.error("Localization string not found for key:\t"+key);
			return key;
		}
	}
	
	public static String get(Class<?> clazz, String key){
		try{
			return bundle.getString(clazz.getSimpleName()+"."+key);
		}catch(MissingResourceException e){
			logger.error("Localization string not found for key:\t"+key);
			return key;
		}
	}
	
	public static String generic(String key){
		try{
			return bundle.getString("Generic."+key);
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
