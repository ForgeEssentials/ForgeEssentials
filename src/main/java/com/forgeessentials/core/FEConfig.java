package com.forgeessentials.core;

import java.text.SimpleDateFormat;

import net.minecraftforge.common.config.Configuration;

import com.forgeessentials.core.moduleLauncher.config.ConfigLoaderBase;

public class FEConfig extends ConfigLoaderBase
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

    @Override
    public void load(Configuration config, boolean isReload)
    {
        config.addCustomCategoryComment(CONFIG_CAT, "Configure ForgeEssentials Core.");
        config.addCustomCategoryComment(CONFIG_CAT_MODULES, "Enable/disable modules here.");

        FORMAT_DATE = new SimpleDateFormat(config.get(CONFIG_CAT, "format_date", "yyyy-MM-dd", "Date-only format").getString());
        FORMAT_DATE_TIME = new SimpleDateFormat(config.get(CONFIG_CAT, "format_date_time", "dd.MM HH:mm", "Date and time format").getString());
        FORMAT_DATE_TIME_SECONDS = new SimpleDateFormat(config.get(CONFIG_CAT, "format_date_time_seconds", "dd.MM HH:mm:ss",
                "Date and time format with seconds").getString());
        FORMAT_TIME = new SimpleDateFormat(config.get(CONFIG_CAT, "format_time", "HH:mm", "Time-only format").getString());
        FORMAT_TIME_SECONDS = new SimpleDateFormat(config.get(CONFIG_CAT, "format_time", "HH:mm:ss", "Time-only format with seconds").getString());

        modlistLocation = config.get(CONFIG_CAT, "modlistLocation", "modlist.txt",
                "Specify the file where the modlist will be written to. This path is relative to the ForgeEssentials folder.").getString();

        majoritySleep = config.get(CONFIG_CAT_MISC, "MajoritySleepThreshold", 50, //
                "Once this percent of player sleeps, allow the night to pass. Set to 100 to disable.").getInt(50) / 100.0f;
        checkSpacesInNames = config.get(CONFIG_CAT_MISC, "CheckSpacesInNames", true, //
                "Check if a player's name contains spaces (can gum up some things in FE)").getBoolean();
    }

}
