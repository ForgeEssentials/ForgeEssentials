package com.forgeessentials.core;

import java.text.SimpleDateFormat;

import net.minecraftforge.common.ForgeConfigSpec;

public class FEConfig
{

    public static final String CONFIG_MAIN_CORE = "Core";
    public static final String CONFIG_MAIN_MISC = "Misc";
    public static final String CONFIG_MAIN_MODULES = "Modules";
    

    public static boolean mcStats;

    public static String modlistLocation;

    public static float majoritySleep;

    public static boolean checkSpacesInNames;

    public static SimpleDateFormat FORMAT_DATE = new SimpleDateFormat("yyyy-MM-dd");

    public static SimpleDateFormat FORMAT_DATE_TIME = new SimpleDateFormat("dd.MM HH:mm");

    public static SimpleDateFormat FORMAT_DATE_TIME_SECONDS = new SimpleDateFormat("dd.MM HH:mm:ss");

    public static SimpleDateFormat FORMAT_TIME = new SimpleDateFormat("HH:mm");

    public static SimpleDateFormat FORMAT_TIME_SECONDS = new SimpleDateFormat("HH:mm:ss");

    static ForgeConfigSpec.ConfigValue<String> FEFORMAT_DATE;
    static ForgeConfigSpec.ConfigValue<String> FEFORMAT_DATE_TIME;
    static ForgeConfigSpec.ConfigValue<String> FEFORMAT_DATE_TIME_SECONDS;
    static ForgeConfigSpec.ConfigValue<String> FEFORMAT_TIME;
    static ForgeConfigSpec.ConfigValue<String> FEFORMAT_TIME_SECONDS;
    static ForgeConfigSpec.ConfigValue<String> FEmodlistLocation;
    static ForgeConfigSpec.IntValue FEmajoritySleep;
    static ForgeConfigSpec.BooleanValue FEcheckSpacesInNames;
    
    public static void load(ForgeConfigSpec.Builder BUILDER)
    {
    	BUILDER.comment("Configure ForgeEssentials Core.").push(CONFIG_MAIN_CORE);
    	FEFORMAT_DATE = BUILDER.comment("Date-only format")
    			.define("format_date", "yyyy-MM-dd"); 
    	FEFORMAT_DATE_TIME = BUILDER.comment("Date and time format")
    			.define("format_date_time", "dd.MM HH:mm"); 
    	FEFORMAT_DATE_TIME_SECONDS = BUILDER.comment("Date and time format with seconds")
    			.define("format_date_time_seconds", "dd.MM HH:mm:ss"); 
    	FEFORMAT_TIME = BUILDER.comment("Time-only format")
    			.define("format_time", "HH:mm");
    	FEFORMAT_TIME_SECONDS = BUILDER.comment("Time-only format with seconds")
    			.define("format_time_seconds", "HH:mm:ss");
    	FEmodlistLocation = BUILDER.comment("Specify the file where the modlist will be written to. This path is relative to the ForgeEssentials folder.")
    			.define("modlistLocation", "modlist.txt");
    	BUILDER.pop();
        
        BUILDER.comment("Enable/disable modules here.").push(CONFIG_MAIN_MODULES);
        BUILDER.pop();
        
        BUILDER.push(CONFIG_MAIN_MISC);
        FEmajoritySleep = BUILDER.comment("Once this percent of player sleeps, allow the night to pass. Set to 100 to disable.")
    			.defineInRange("MajoritySleepThreshold",50, 0, 100);
        FEcheckSpacesInNames = BUILDER.comment("Check if a player's name contains spaces (can gum up some things in FE)")
                .define("CheckSpacesInNames", true);
        
        BUILDER.pop();
    }

	public static void bakeConfig(boolean reload) {
    	FORMAT_DATE = new SimpleDateFormat(FEFORMAT_DATE.get()); 
    	FORMAT_DATE_TIME = new SimpleDateFormat(FEFORMAT_DATE_TIME.get()); 
    	FORMAT_DATE_TIME_SECONDS = new SimpleDateFormat(FEFORMAT_DATE_TIME_SECONDS.get()); 
    	FORMAT_TIME = new SimpleDateFormat(FEFORMAT_TIME.get());
    	FORMAT_TIME_SECONDS = new SimpleDateFormat(FEFORMAT_TIME_SECONDS.get());
    	modlistLocation = FEmodlistLocation.get();

        majoritySleep = FEmajoritySleep.get();
        checkSpacesInNames = FEcheckSpacesInNames.get();
	}

}
