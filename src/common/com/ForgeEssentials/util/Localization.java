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

	/**
	 * Fetches a localized format string, and inserts any provided arguments into it.
	 * A wrapper for all the "String.format(Localization.get(key), ...)" calls in commands.
	 * @param localizationKey Key to get the appropriate entry in the current localization file.
	 * @param args Arguments required to populate the localized string
	 * @return String String containing the localized, formatted string.
	 */
	public static String formatLocalizedString(String localizationKey, Object ...args)
	{
		return String.format(get(localizationKey), args);
	}
}
