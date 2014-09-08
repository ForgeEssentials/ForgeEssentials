package com.forgeessentials.core;

import java.io.File;

import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.common.config.Property;

import com.forgeessentials.core.compat.CommandSetChecker;
import com.forgeessentials.util.MiscEventHandler;
import com.forgeessentials.util.OutputHandler;
import com.forgeessentials.util.TeleportCenter;

public class CoreConfig {
    public static final File mainconfig = new File(ForgeEssentials.FEDIR, "main.cfg");
    public static String largeComment_Cat_Groups, groupPrefixFormat, groupSuffixFormat, groupRankFormat;
    public final Configuration config;

    static
    {
        largeComment_Cat_Groups = "You may put enything here that you want displaed as part of the group prefixes, suffixes, or ranks.";
        largeComment_Cat_Groups += "\n {ladderName<:>Zone} will display the data for the highest priority group that the player is in that is part of the specified ladder and specified zone.";
        largeComment_Cat_Groups += "\n {...<:>...} will display the data of each group the player is in in order of priority";
        largeComment_Cat_Groups += "\n you may put contsraints with ladders or zones with {...<:>zoneName} or {ladderName<:>...}";
        largeComment_Cat_Groups += "\n you may also use the color and MCFormat codes above.";
    }

    // this is designed so it will work for any class.
    public CoreConfig()
    {
        OutputHandler.felog.finer("Loading configs");

        config = new Configuration(mainconfig, true);

        config.addCustomCategoryComment("Core", "Configure ForgeEssentials Core.");

        Property prop = config.get("Core", "versionCheck", true);
        prop.comment = "Check for newer versions of ForgeEssentials on load?";
        ForgeEssentials.verCheck = prop.getBoolean(true);

        prop = config.get("Core", "debug", false);
        prop.comment = "Activates developer debug mode. Spams your FML logs.";
        OutputHandler.debugmode = prop.getBoolean(false);

        prop = config.get("Core", "modlistLocation", "modlist.txt");
        prop.comment = "Specify the file where the modlist will be written to. This path is relative to the ForgeEssentials folder.";
        ForgeEssentials.modlistLocation = prop.getString();

        prop = config.get("Core", "removeDuplicateCommands", true);
        prop.comment = "Remove commands from the list if they already exist outside of FE.";
        CommandSetChecker.removeDuplicateCommands = prop.getBoolean(true);

        prop = config.get("Core.Misc", "tpWarmup", 5);
        prop.comment = "The amount of time you need to stand still to TP.";
        TeleportCenter.tpWarmup = prop.getInt(5);

        prop = config.get("Core.Misc", "tpCooldown", 5);
        prop.comment = "The amount of time you need to wait to TP again.";
        TeleportCenter.tpCooldown = prop.getInt(5);

        prop = config.get("Core.Misc", "MajoritySleep", true);
        prop.comment = "If +50% of players sleep, make it day.";
        MiscEventHandler.MajoritySleep = prop.getBoolean(true);

        config.addCustomCategoryComment("Core.groups", largeComment_Cat_Groups);

        groupPrefixFormat = config.get("Core.groups", "groupPrefix", "{...<:>_GLOBAL_}").getString();
        groupSuffixFormat = config.get("Core.groups", "groupSuffix", "{...<:>_GLOBAL_}").getString();
        groupRankFormat = config.get("Core.groups", "rank", "[{...<:>_GLOBAL_}]").getString();

        config.save();
    }

    /**
     * will overwrite the current physical file.
     */
    public void forceSave()
    {
        config.save();

        Property prop = config.get("general", "removeDuplicateCommands", true);
        prop.comment = ("Remove commands from the list if they already exist outside of FE.");
        CommandSetChecker.removeDuplicateCommands = prop.getBoolean(true);

        config.addCustomCategoryComment("Core.groups", largeComment_Cat_Groups);

        config.get("Core.groups", "groupPrefix", "").set(groupPrefixFormat);
        config.get("Core.groups", "groupSuffix", "").set(groupSuffixFormat);
        config.get("Core.groups", "rank", "").set(groupRankFormat);

    }
}
