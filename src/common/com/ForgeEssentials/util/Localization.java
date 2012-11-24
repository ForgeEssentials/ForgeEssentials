package com.ForgeEssentials.util;


import cpw.mods.fml.common.registry.LanguageRegistry;

public class Localization
{

	private String[] langFiles = { "en_US.xml" };

	public void load()
	{
		OutputHandler.SOP("Loading languages");
		for (String langFile : langFiles)
		{
			try
			{
				LanguageRegistry.instance().loadLocalization(getClass().getResource("lang/" + langFile), langFile.substring(langFile.lastIndexOf('/') + 1, langFile.lastIndexOf('.')), true);
				OutputHandler.SOP("Loaded language file " + langFile);
			} catch (Exception e)
			{
				OutputHandler.SOP("Could not load language file " + langFile);
			}
		}
	}

	public static String get(String key)
	{
		return LanguageRegistry.instance().getStringLocalization(key);
	}
}
