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

	public static final String WB_HITBORDER = "message.wb.hitborder";
	
	public static final String WB_STATUS_HEADER = "message.wb.status.header";	
	public static final String WB_STATUS_BORDERSET = "message.wb.status.borderset";
	public static final String WB_STATUS_BORDERNOTSET = "message.wb.status.bordernotset";
	
	public static final String WB_LAGWARING = "message.wb.lagwarning";
	
	public static final String WB_FILL_INFO = "message.wb.fill.info";
	public static final String WB_FILL_CONFIRM = "message.wb.fill.confirm";
	public static final String WB_FILL_ONLYONCE = "message.wb.fill.onlyonce";
	public static final String WB_FILL_CONSOLENEEDSDIM = "message.wb.fill.consoleneedsdim";
	public static final String WB_FILL_START = "message.wb.fill.start";
	
	public static final String WB_TURBO_INFO = "message.wb.turbo.info";
	public static final String WB_TURBO_CONFIRM = "message.wb.turbo.confirm";
	public static final String WB_TURBO_NOTHINGTODO = "message.wb.turbo.nothingtodo";
	public static final String WB_TURBO_ON = "message.wb.turbo.on";
	public static final String WB_TURBO_OFF = "message.wb.turbo.off";
	
	public static final String WB_SET = "message.wb.set";
	
	public static final String UNIT_SECONDS = "unit.seconds";
	
	//public static final String WB_ = "message.wb.";
	
	public static final String WALLET = "message.wallet.walletname";
	public static final String WALLET_CURRENCY_SINGULAR = "message.wallet.currencysingular";
	public static final String WALLET_CURRENCY_PLURAL = "message.wallet.currencyplural";
	public static final String WALLET_SET = "message.wallet.walletset";
	public static final String WALLET_SET_SELF = "message.wallet.walletsetself";
	public static final String WALLET_SET_TARGET = "message.wallet.walletsettarget";
	public static final String WALLET_ADD = "message.wallet.walletadd";
	public static final String WALLET_ADD_SELF = "message.wallet.walletaddself";
	public static final String WALLET_ADD_TARGET = "message.wallet.walletaddtarget";
	public static final String WALLET_REMOVE = "message.wallet.walletremove";
	public static final String WALLET_REMOVE_SELF = "message.wallet.walletremoveself";
	public static final String WALLET_REMOVE_TARGET = "message.wallet.walletremovetarget";
	public static final String WALLET_GET = "message.wallet.walletget";
	public static final String WALLET_GET_SELF = "message.wallet.walletgetself";
	public static final String WALLET_GET_TARGET = "message.wallet.walletgettarget";

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
