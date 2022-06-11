package com.forgeessentials.core;

import java.text.SimpleDateFormat;

import net.minecraftforge.common.ForgeConfigSpec;

public class FEConfig
{

    public static final String CONFIG_CAT = "Core";
    public static final String CONFIG_CAT_MISC = "Core.Misc";
    public static final String CONFIG_CAT_MODULES = "Core.Modules";

    public static boolean mcStats;

    public static String modlistLocation;

    public static float majoritySleep;

    public static boolean checkSpacesInNames;

    public static SimpleDateFormat FORMAT_DATE = new SimpleDateFormat("yyyy-MM-dd");

    public static SimpleDateFormat FORMAT_DATE_TIME = new SimpleDateFormat("dd.MM HH:mm");

    public static SimpleDateFormat FORMAT_DATE_TIME_SECONDS = new SimpleDateFormat("dd.MM HH:mm:ss");

    public static SimpleDateFormat FORMAT_TIME = new SimpleDateFormat("HH:mm");

    public static SimpleDateFormat FORMAT_TIME_SECONDS = new SimpleDateFormat("HH:mm:ss");

    public static void load(ForgeConfigSpec.Builder SERVER_BUILDER)
    {
        FORMAT_TIME = new SimpleDateFormat(config.get(CONFIG_CAT, "format_time", "HH:mm", "Time-only format").getString());
        FORMAT_TIME_SECONDS = new SimpleDateFormat(config.get(CONFIG_CAT, "format_time", "HH:mm:ss", "Time-only format with seconds").getString());
    	SERVER_BUILDER.comment("Configure ForgeEssentials Core.").push(CONFIG_CAT);
    	FORMAT_DATE = new SimpleDateFormat(SERVER_BUILDER.comment("Date-only format")
    			.define("format_date", "yyyy-MM-dd").get()); 
    	FORMAT_DATE_TIME = new SimpleDateFormat(SERVER_BUILDER.comment("Date and time format")
    			.define("format_date_time", "dd.MM HH:mm").get()); 
    	FORMAT_DATE_TIME_SECONDS = new SimpleDateFormat(SERVER_BUILDER.comment("Date and time format with seconds")
    			.define("format_date_time_seconds", "dd.MM HH:mm:ss").get()); 
    	FORMAT_TIME = new SimpleDateFormat(SERVER_BUILDER.comment("Time-only format")
    			.define("format_time", "HH:mm").get());
    	FORMAT_TIME_SECONDS = new SimpleDateFormat(SERVER_BUILDER.comment("Time-only format with seconds")
    			.define("format_time_seconds", "HH:mm:ss").get());
    	
    	modlistLocation = SERVER_BUILDER.comment("Specify the file where the modlist will be written to. This path is relative to the ForgeEssentials folder.")
    			.define("modlistLocation", "modlist.txt").get();
        
        
        SERVER_BUILDER.comment("Enable/disable modules here.").push(CONFIG_CAT);
        SERVER_BUILDER.push(CONFIG_CAT_MISC);
        
        majoritySleep = SERVER_BUILDER.comment("Once this percent of player sleeps, allow the night to pass. Set to 100 to disable.")
    			.defineInRange("MajoritySleepThreshold",50, 0, 100).get();
        checkSpacesInNames = SERVER_BUILDER.comment("Check if a player's name contains spaces (can gum up some things in FE)")
                .define("CheckSpacesInNames", true).get();
        SERVER_BUILDER.pop();
    }

}
