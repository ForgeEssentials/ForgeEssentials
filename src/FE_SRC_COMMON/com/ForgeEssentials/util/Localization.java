package com.ForgeEssentials.util;

import java.io.File;
import java.io.FileOutputStream;
import java.net.URL;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;

import net.minecraftforge.common.Configuration;

import com.ForgeEssentials.core.ForgeEssentials;

import cpw.mods.fml.common.registry.LanguageRegistry;

public class Localization
{
	private String[]			langFiles						=
																{ "en_US.xml", "en_UK.xml" };

	/*
	 * Command stuff
	 */

	public static final String	BUTCHERED						= "message.butchered";
	public static final String	REMOVED							= "message.removed";
	public static final String	KILLED							= "message.killed";
	public static final String	SMITE_SELF						= "message.smite.self";
	public static final String	SMITE_PLAYER					= "message.smite.player";
	public static final String	SMITE_GROUND					= "message.smite.ground";
	public static final String	BURN_SELF						= "message.burn.self";
	public static final String	BURN_PLAYER						= "message.burn.player";
	public static final String	HEALED							= "message.healed";
	public static final String	SPAWNED							= "message.spawned";
	public static final String	SPAWNSET						= "message.spawnset";
	public static final String	POTIONEFFECTNOTFOUND			= "command.potion.effectnotfound";

	/*
	 * Kit command & tickHandler
	 */

	public static final String	KIT_LIST						= "command.kit.list";
	public static final String	KIT_NOTEXISTS					= "command.kit.noExists";
	public static final String	KIT_MADE						= "command.kit.made";
	public static final String	KIT_ALREADYEXISTS				= "command.kit.alreadyExists";
	public static final String	KIT_REMOVED						= "command.kit.removed";
	public static final String	KIT_STILLINCOOLDOWN				= "command.kit.stillInCooldown";
	public static final String	KIT_DONE						= "command.kit.done";

	/*
	 * Ego boosts
	 */

	public static final String	CREDITS_ABRARSYED				= "message.credits.AbrarSyed";
	public static final String	CREDITS_BOBAREDDINO				= "message.credits.Bob_A_Red_Dino";
	public static final String	CREDITS_BSPKRS					= "message.credits.bspkrs";
	public static final String	CREDITS_MYSTERIOUSAGES			= "message.credits.MysteriousAges";
	public static final String	CREDITS_LUACS1998				= "message.credits.luacs1998";
	public static final String	CREDITS_DRIES007				= "message.credits.Dries007";
	public static final String	CREDITS_MALKIERIAN				= "message.credits.Malkierian";

	/*
	 * Errors & general messages
	 */

	public static final String	ERROR_NOPLAYER					= "message.error.noPlayerX";
	public static final String	ERROR_BADSYNTAX					= "message.error.badsyntax";
	public static final String	ERROR_NAN						= "message.error.nan";
	public static final String	ERROR_NODEATHPOINT				= "message.error.nodeathpoint";
	public static final String	ERROR_NOHOME					= "message.error.nohome";
	public static final String	ERROR_NOSELECTION				= "message.error.noselection";
	public static final String	ERROR_TARGET					= "message.error.target";
	public static final String	ERROR_NOPAGE					= "message.error.nopage";
	public static final String	ERROR_PERMDENIED				= "message.error.permdenied";
	public static final String  ERROR_NOUSEWAND					= "message.error.cantUseWand";
	public static final String	ERROR_NOITEMPLAYER				= "message.error.noItemPlayer";
	public static final String	ERROR_NOITEMTARGET				= "message.error.noItemTarget";
	public static final String	ERROR_NOMOB						= "message.error.noMobX";
	public static final String	ERROR_PERM_SQL					= "message.error.permission.sql";

	public static final String	DONE							= "message.done";

	/*
	 * Permissions stuff
	 */
	public static final String	ERROR_NOPERMISSION				= "message.error.nopermission";
	public static final String	ERROR_ZONE_NOZONE				= "message.error.nozone";
	public static final String	ERROR_ZONE_YESZONE				= "message.error.yeszone";
	public static final String	CONFIRM_ZONE_REMOVE				= "message.confirm.zone.remove";
	public static final String	CONFIRM_ZONE_DEFINE				= "message.confirm.zone.define";
	public static final String	CONFIRM_ZONE_REDEFINE			= "message.confirm.zone.redefine";
	public static final String	CONFIRM_ZONE_SETPARENT			= "message.confirm.zone.setparent";
	public static final String	ERROR_ILLEGAL_STATE				= "message.error.illegalState";
	public static final String	ERROR_ILLEGAL_ENTITY			= "message.error.illegalState";

	/*
	 * WorldControl
	 */

	public static final String	WC_SETCONFIRMBLOCKSCHANGED		= "message.wc.setConfirmBlocksChanged";
	public static final String	WC_REPLACECONFIRMBLOCKSCHANGED	= "message.wc.replaceConfirmBlocksChanged";
	public static final String	WC_THAWCONFIRM					= "message.wc.thawConfirm";
	public static final String	WC_FREEZECONFIRM				= "message.wc.freezeConfirm";
	public static final String	WC_SNOWCONFIRM					= "message.wc.snowConfirm";
	public static final String	WC_TILLCONFIRM					= "message.wc.tillConfirm";
	public static final String	WC_UNTILLCONFIRM				= "message.wc.untillConfirm";
	public static final String	WC_INVALIDBLOCKID				= "message.wc.invalidBlockId";
	public static final String	WC_BLOCKIDOUTOFRANGE			= "message.wc.blockIdOutOfRange";
	public static final String	WC_NOUNDO						= "message.wc.noUndo";
	public static final String	WC_NOREDO						= "message.wc.noRedo";

	/*
	 * TeleportCenter
	 */

	public static final String	TC_COOLDOWN						= "message.tc.cooldown";
	public static final String	TC_WARMUP						= "message.tc.warmup";
	public static final String	TC_ABORTED						= "message.tc.aborted";
	public static final String	TC_DONE							= "message.tc.done";

	/*
	 * WorldBorder
	 */

	public static final String	WB_HITBORDER					= "message.wb.hitborder";

	public static final String	WB_STATUS_HEADER				= "message.wb.status.header";
	public static final String	WB_STATUS_BORDERSET				= "message.wb.status.borderset";
	public static final String	WB_STATUS_BORDERNOTSET			= "message.wb.status.bordernotset";

	public static final String	WB_LAGWARING					= "message.wb.lagwarning";

	public static final String	WB_FILL_INFO					= "message.wb.fill.info";
	public static final String	WB_FILL_CONFIRM					= "message.wb.fill.confirm";
	public static final String	WB_FILL_ONLYONCE				= "message.wb.fill.onlyonce";
	public static final String	WB_FILL_CONSOLENEEDSDIM			= "message.wb.fill.consoleneedsdim";
	public static final String	WB_FILL_START					= "message.wb.fill.start";
	public static final String	WB_FILL_STILLGOING				= "message.wb.fill.stillgoing";
	public static final String	WB_FILL_DONE					= "message.wb.fill.done";
	public static final String	WB_FILL_ETA						= "message.wb.fill.eta";
	public static final String	WB_FILL_ABORTED					= "message.wb.fill.aborted";
	public static final String	WB_FILL_FINISHED				= "message.wb.fill.finished";
	public static final String	WB_FILL_UNLOADEDWOLD			= "message.wb.fill.worldunloaded";

	public static final String	WB_SAVING_FAILED				= "message.wb.saving.failed";

	public static final String	WB_TURBO_INFO					= "message.wb.turbo.info";
	public static final String	WB_TURBO_CONFIRM				= "message.wb.turbo.confirm";
	public static final String	WB_NOTHINGTODO					= "message.wb.nothingtodo";
	public static final String	WB_TURBO_ON						= "message.wb.turbo.on";
	public static final String	WB_TURBO_OFF					= "message.wb.turbo.off";

	public static final String	WB_AUTO_INFO					= "message.wb.auto.info";
	public static final String	WB_AUTO_CONFIRM					= "message.wb.auto.confirm";

	public static final String	WB_SET							= "message.wb.set";

	public static final String	UNIT_SECONDS					= "unit.seconds";

	/*
	 * wallet
	 */

	public static final String	wallet							= "message.wallet.walletname";
	public static final String	wallet_CURRENCY_SINGULAR		= "message.wallet.currencysingular";
	public static final String	wallet_CURRENCY_PLURAL			= "message.wallet.currencyplural";
	public static final String	wallet_SET						= "message.wallet.walletset";
	public static final String	wallet_SET_SELF					= "message.wallet.walletsetself";
	public static final String	wallet_SET_TARGET				= "message.wallet.walletsettarget";
	public static final String	wallet_ADD						= "message.wallet.walletadd";
	public static final String	wallet_ADD_SELF					= "message.wallet.walletaddself";
	public static final String	wallet_ADD_TARGET				= "message.wallet.walletaddtarget";
	public static final String	wallet_REMOVE					= "message.wallet.walletremove";
	public static final String	wallet_REMOVE_SELF				= "message.wallet.walletremoveself";
	public static final String	wallet_REMOVE_TARGET			= "message.wallet.walletremovetarget";
	public static final String	wallet_GET						= "message.wallet.walletget";
	public static final String	wallet_GET_SELF					= "message.wallet.walletgetself";
	public static final String	wallet_GET_TARGET				= "message.wallet.walletgettarget";

	public static final String	COMMAND_DESELECT				= "message.wc.deselection";

	public void load()
	{
		OutputHandler.finer("Loading languages");

		File folder = new File(ForgeEssentials.FEDIR, "lang");
		boolean forceDl = false;
		if (!folder.exists())
		{
			forceDl = true;
			folder.mkdirs();
		}

		Configuration conf = new Configuration(new File(folder, "conf.cfg"));
		forceDl = conf.get("Lang", "AutoUpdate", true, "Leave to true unless you make changes to the lang files.").getBoolean(true);
		conf.save();

		for (String langFile : langFiles)
		{
			File file = new File(folder, langFile);
			if (!file.exists() || forceDl)
			{
				try
				{
					URL dl = new URL("https://raw.github.com/ForgeEssentials/FELocalizations/master/" + langFile);
					ReadableByteChannel rbc = Channels.newChannel(dl.openStream());
					FileOutputStream fos = new FileOutputStream(file);
					fos.getChannel().transferFrom(rbc, 0, 1 << 24);
					fos.close();
				}
				catch (Exception e)
				{
					OutputHandler.warning("Error while downloading " + langFile);
					e.printStackTrace();
				}

			}
		}

		for (String langFile : langFiles)
		{
			try
			{
				File file = new File(folder.getAbsolutePath(), langFile);
				LanguageRegistry.instance().loadLocalization(file.toURI().toURL(), file.getName().substring(0, file.getName().lastIndexOf(".")), true);
				OutputHandler.info("Loaded language file " + langFile);
			}
			catch (Exception e)
			{
				OutputHandler.info("Could not load language file " + langFile);
				e.printStackTrace();
			}
		}
	}

	public static String get(String key)
	{
		return LanguageRegistry.instance().getStringLocalization(key);
	}

	/**
	 * Fetches a localized format string, and inserts any provided arguments
	 * into it. A wrapper for all the
	 * "String.format(Localization.get(key), ...)" calls in commands.
	 * @param localizationKey
	 * Key to get the appropriate entry in the current localization
	 * file.
	 * @param args
	 * Arguments required to populate the localized string
	 * @return String String containing the localized, formatted string.
	 */
	public static String format(String localizationKey, Object... args)
	{
		return String.format(get(localizationKey), args);
	}
}
