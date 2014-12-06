package com.forgeessentials.playerlogger;

import com.forgeessentials.core.moduleLauncher.config.IConfigLoader.ConfigLoaderBase;
import net.minecraftforge.common.config.Configuration;

import java.util.Arrays;

public class ConfigPlayerLogger extends ConfigLoaderBase {
    
    public static final String[] exemptDefPlayers = { "\"[Forestry]\"", "\"[Buildcraft]\"" };
    private static final String cat = "playerLogger";

    @Override
    public void load(Configuration config, boolean isReload)
    {

        String subcat = cat + ".DB";
        config.addCustomCategoryComment(subcat, "Database settings. Look here if something broke. This MUST be changed to load the PlayerLogger module.");

        ModulePlayerLogger.url = config.get(subcat, "url", ModulePlayerLogger.DEFAULT_URL, "jdbc url").getString();
        ModulePlayerLogger.username = config.get(subcat, "username", ModulePlayerLogger.DEFAULT_USER).getString();
        ModulePlayerLogger.password = config.get(subcat, "password", ModulePlayerLogger.DEFAULT_PASS).getString();
        ModulePlayerLogger.ragequitOn = config.get(subcat, "ragequit", false, "Stop the server when the logging fails").getBoolean(false);

        subcat = cat + ".exempt";
        config.addCustomCategoryComment(subcat,
                "Don't log stuff from these players/group.\nCase sensitive.\nMods should not be using fake players. But if they do, you can add them here if you don't logs from them.");

        EventLogger.exempt_players = Arrays.asList(config.get(subcat, "players", exemptDefPlayers).getStringList());
        EventLogger.exempt_groups = Arrays.asList(config.get(subcat, "groups", new String[] { }).getStringList());

        subcat = cat + ".events";
        config.addCustomCategoryComment(subcat, "Toggle events to log here.");

        EventLogger.logPlayerLoginLogout = config.get(subcat, "playerLoginLogout", true).getBoolean(true);
        EventLogger.logPlayerChangedDimension = config.get(subcat, "playerChangeDimension", true).getBoolean(true);
        EventLogger.logPlayerRespawn = config.get(subcat, "playerRespawn", true).getBoolean(true);

        String subcat2 = subcat + ".commands";

        EventLogger.logCommands_Player = config.get(subcat2, "Enable_Player", true).getBoolean(true);
        EventLogger.logCommands_Block = config.get(subcat2, "Enable_CmdBlock", true).getBoolean(true);
        EventLogger.logCommands_rest = config.get(subcat2, "Enable_Rest", true).getBoolean(true);

        subcat2 = subcat + ".blockChanges";

        EventLogger.logBlockChanges = config.get(subcat2, "Enable", true).getBoolean(true);
        EventLogger.BlockChange_WhiteList_Use = config.get(subcat2, "UseWhitelist", false, "If true, only log in dimensions that are in the whitelist.")
                .getBoolean(false);

        int[] intArray1 = config.get(subcat2, "WhiteList", new int[]
                { 0, 1, -1 }, "WhiteList overrides blacklist!").getIntList();
        for (int i : intArray1)
        {
            EventLogger.BlockChange_WhiteList.add(i);
        }

        int[] intArray2 = config.get(subcat2, "BlackList", new int[] { }, "Don't make logs in these dimensions.").getIntList();
        for (int i : intArray2)
        {
            EventLogger.BlockChange_BlackList.add(i);
        }
    }

}
