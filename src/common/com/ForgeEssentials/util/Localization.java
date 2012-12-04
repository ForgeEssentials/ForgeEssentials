package com.ForgeEssentials.util;

import cpw.mods.fml.common.registry.LanguageRegistry;

public class Localization
{
	private String[] langFiles = { "en_US.xml", "en_UK.xml" };

	// Constants for localization messages, so we don't get duplicates or errors
	public static final String BUTCHERED = "message.butchered";
	public static final String REMOVED = "message.removed";
	public static final String KILLED = "message.killed";
	public static final String SMITE_SELF = "message.smite.self";
	public static final String SMITE_PLAYER = "message.smite.player";
	public static final String SMITE_GROUND = "message.smite.ground";
	public static final String BURN_SELF = "message.burn.self";
	public static final String BURN_PLAYER = "message.burn.player";
	public static final String HEALED = "message.healed";
	public static final String SPAWNED = "message.spawned";

	public static final String CREDITS_ABRARSYED = "message.credits.AbrarSyed";
	public static final String CREDITS_BOBAREDDINO = "message.credits.Bob_A_Red_Dino";
	public static final String CREDITS_BSPKRS = "message.credits.bspkrs";
	public static final String CREDITS_MYSTERIOUSAGES = "message.credits.MysteriousAges";
	public static final String CREDITS_LUACS1998 = "message.credits.luacs1998";
	public static final String CREDITS_DRIES007 = "message.credits.Dries007";

	public static final String ERROR_NOPLAYER = "message.error.noPlayerX";
	public static final String ERROR_BADSYNTAX = "message.error.badsyntax";
	public static final String ERROR_NAN = "message.error.nan";
	public static final String ERROR_NODEATHPOINT = "message.error.nodeathpoint";
	public static final String ERROR_NOHOME = "message.error.nohome";
	public static final String ERROR_TARGET = "message.error.target";
	public static final String ERROR_NOPAGE = "message.error.nopage";
	public static final String ERROR_NOZONE = "message.error.nozone";
	public static final String ERROR_YESZONE = "message.error.yeszone";
	public static final String ERROR_PERMDENIED = "message.error.permdenied";
	public static final String ERROR_NOITEMPLAYER = "message.error.noItemPlayer";
	public static final String ERROR_NOITEMTARGET = "message.error.noItemTarget";
	public static final String ERROR_NOMOB = "message.error.noMobX";

	public static final String DONE = "message.done";

	public static final String WC_SETCONFIRMBLOCKSCHANGED = "message.wc.setConfirmBlocksChanged";
	public static final String WC_REPLACECONFIRMBLOCKSCHANGED = "message.wc.replaceConfirmBlocksChanged";
	public static final String WC_THAWCONFIRM = "message.wc.thawConfirm";
	public static final String WC_FREEZECONFIRM = "message.wc.freezeConfirm";
	public static final String WC_SNOWCONFIRM = "message.wc.snowConfirm";
	public static final String WC_TILLCONFIRM = "message.wc.tillConfirm";
	public static final String WC_UNTILLCONFIRM = "message.wc.untillConfirm";
	public static final String WC_INVALIDBLOCKID = "message.wc.invalidBlockId";
	public static final String WC_BLOCKIDOUTOFRANGE = "message.wc.blockIdOutOfRange";

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
	 * Fetches a localized format string, and inserts any provided arguments into it. A wrapper for all the "String.format(Localization.get(key), ...)" calls in commands.
	 * 
	 * @param localizationKey
	 *            Key to get the appropriate entry in the current localization file.
	 * @param args
	 *            Arguments required to populate the localized string
	 * @return String String containing the localized, formatted string.
	 */
	public static String format(String localizationKey, Object... args)
	{
		return String.format(get(localizationKey), args);
	}
}
