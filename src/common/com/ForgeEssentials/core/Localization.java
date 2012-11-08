package com.ForgeEssentials.core;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.LinkedList;
import java.util.Properties;

public class Localization {
	
	/**
	 * Based on BC's.
	 */
	
	private static class modInfo{
		final String modName, defaultLang;
		
		public modInfo(String modName, String defaultLang){
			this.modName = modName;
			this.defaultLang = defaultLang;
		}
	}
	
	private static String loadedLang = getCurrentLang();
	private static Properties defaultMap = new Properties();
	private static Properties map = new Properties();
	private static LinkedList<modInfo> mods = new LinkedList<modInfo>();
	
	public static void addLocale(String path, String defaultLang){
		mods.add(new modInfo(path, defaultLang));
		load(path, defaultLang);
	}
	public static synchronized String get(String key){
		if (!getCurrentLang().equals(loadedLang)){
			map.clear();
			for (modInfo mInfo : mods){
				load (mInfo.modName, mInfo.defaultLang);
			}
			loadedLang = getCurrentLang();
			
		}
		return map.getProperty(key,defaultMap.getProperty(key, key));
	}
	private static void load(String path, String default_lang){
		InputStream langstrm = null;
		Properties modMap = new Properties();
		
		try{
			langstrm = Localization.class.getResourceAsStream(path + default_lang + ".lang");
			modMap.load(langstrm);
			defaultMap.putAll(modMap);
			langstrm.close();
			
			langstrm = Localization.class.getResourceAsStream(path + getCurrentLang() + ".lang");
			if (langstrm != null){
				modMap.clear();
				modMap.load(langstrm);
			}
			map.putAll(modMap);
		} catch (Exception e){
			e.printStackTrace();
		} finally {
			try{
				if (langstrm != null)
					langstrm.close();
			} catch (IOException ex) {
				ex.printStackTrace();
			}
		}
	}
	
	private static String getCurrentLang(){
		return null;
	}

}
