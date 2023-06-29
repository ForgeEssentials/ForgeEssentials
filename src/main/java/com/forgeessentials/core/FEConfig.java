package com.forgeessentials.core;

import java.text.SimpleDateFormat;

import com.forgeessentials.core.config.ConfigData;
import com.forgeessentials.core.config.ConfigLoaderBase;
import com.forgeessentials.util.output.ChatOutputHandler;

import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.common.ForgeConfigSpec.Builder;

public class FEConfig extends ConfigLoaderBase {
	private static ForgeConfigSpec CORE_CONFIG;
	public static final ConfigData data = new ConfigData("main", CORE_CONFIG, new ForgeConfigSpec.Builder());

	public static final String CONFIG_MAIN_CORE = "Core";
	public static final String CONFIG_MAIN_MISC = "Misc";

	public static boolean mcStats;

	public static String modlistLocation = "modlist.txt";

	public static float majoritySleep;

	public static boolean checkSpacesInNames;

	public static boolean enableCommandAliases;

	public static SimpleDateFormat FORMAT_DATE = new SimpleDateFormat("yyyy-MM-dd");

	public static SimpleDateFormat FORMAT_DATE_TIME = new SimpleDateFormat("dd.MM HH:mm");

	public static SimpleDateFormat FORMAT_DATE_TIME_SECONDS = new SimpleDateFormat("dd.MM HH:mm:ss");

	public static SimpleDateFormat FORMAT_TIME = new SimpleDateFormat("HH:mm");

	public static SimpleDateFormat FORMAT_TIME_SECONDS = new SimpleDateFormat("HH:mm:ss");

	public static SimpleDateFormat FORMAT_GSON_COMPAT = new SimpleDateFormat("MMM d, yyyy h:mm:ss aa");

	static ForgeConfigSpec.ConfigValue<String> FEFORMAT_DATE;
	static ForgeConfigSpec.ConfigValue<String> FEFORMAT_DATE_TIME;
	static ForgeConfigSpec.ConfigValue<String> FEFORMAT_DATE_TIME_SECONDS;
	static ForgeConfigSpec.ConfigValue<String> FEFORMAT_TIME;
	static ForgeConfigSpec.ConfigValue<String> FEFORMAT_TIME_SECONDS;
	static ForgeConfigSpec.ConfigValue<String> FEFORMAT_GSON_COMPAT;
	static ForgeConfigSpec.ConfigValue<String> FEmodlistLocation;
	static ForgeConfigSpec.IntValue FEmajoritySleep;
	static ForgeConfigSpec.BooleanValue FEcheckSpacesInNames;
	static ForgeConfigSpec.BooleanValue FEenableCommandAliases;

	@Override
	public void load(Builder BUILDER, boolean isReload) {
		BUILDER.comment("Configure ForgeEssentials Core.").push(CONFIG_MAIN_CORE);
		FEFORMAT_DATE = BUILDER.comment("Date-only format").define("format_date", "yyyy-MM-dd");
		FEFORMAT_DATE_TIME = BUILDER.comment("Date and time format").define("format_date_time", "dd.MM HH:mm");
		FEFORMAT_DATE_TIME_SECONDS = BUILDER.comment("Date and time format with seconds")
				.define("format_date_time_seconds", "dd.MM HH:mm:ss");
		FEFORMAT_TIME = BUILDER.comment("Time-only format").define("format_time", "HH:mm");
		FEFORMAT_TIME_SECONDS = BUILDER.comment("Time-only format with seconds").define("format_time_seconds",
				"HH:mm:ss");
		FEFORMAT_GSON_COMPAT = BUILDER
				.comment("Extra Time format to Load GSON data from a different locale and convert it!")
				.define("format_gson_compat", "MMM d, yyyy h:mm:ss aa");
		FEmodlistLocation = BUILDER.comment(
				"Specify the file where the modlist will be written to. This path is relative to the ForgeEssentials folder.")
				.define("modlistLocation", "modlist.txt");
		FEenableCommandAliases = BUILDER.comment("Enable Command Ailases for FE commands").define("enableAilases",
				true);
		BUILDER = ForgeEssentials.load(BUILDER, isReload);
		BUILDER.pop();

		BUILDER.push(CONFIG_MAIN_MISC);
		FEmajoritySleep = BUILDER
				.comment("Once this percent of player sleeps, allow the night to pass. Set to 100 to disable.")
				.defineInRange("MajoritySleepThreshold", 50, 0, 100);
		FEcheckSpacesInNames = BUILDER
				.comment("Check if a player's name contains spaces (can gum up some things in FE)")
				.define("CheckSpacesInNames", true);
		BUILDER.pop();
		BUILDER = ChatOutputHandler.load(BUILDER, isReload);
	}

	@Override
	public void bakeConfig(boolean reload) {
		FORMAT_DATE = new SimpleDateFormat(FEFORMAT_DATE.get());
		FORMAT_DATE_TIME = new SimpleDateFormat(FEFORMAT_DATE_TIME.get());
		FORMAT_DATE_TIME_SECONDS = new SimpleDateFormat(FEFORMAT_DATE_TIME_SECONDS.get());
		FORMAT_TIME = new SimpleDateFormat(FEFORMAT_TIME.get());
		FORMAT_TIME_SECONDS = new SimpleDateFormat(FEFORMAT_TIME_SECONDS.get());
		modlistLocation = FEmodlistLocation.get();

		majoritySleep = FEmajoritySleep.get();
		checkSpacesInNames = FEcheckSpacesInNames.get();
		enableCommandAliases = FEenableCommandAliases.get();
		ForgeEssentials.bakeConfig(reload);
		ChatOutputHandler.bakeConfig(reload);
	}

	@Override
	public ConfigData returnData() {
		return data;
	}

}
