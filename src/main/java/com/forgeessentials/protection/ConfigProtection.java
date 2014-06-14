package com.forgeessentials.protection;

import com.forgeessentials.core.moduleLauncher.ModuleConfigBase;
import net.minecraft.command.ICommandSender;
import net.minecraftforge.common.Configuration;

import java.io.File;

/**
 * This generates the configuration structure + an example file.
 *
 * @author Dries007
 */
public class ConfigProtection extends ModuleConfigBase {
    public Configuration config;

    public ConfigProtection(File file)
    {
        super(file);
    }

    @Override
    public void init()
    {
        config = new Configuration(file, true);
        String cat = "Protection";

        config.addCustomCategoryComment(cat, "You can override the default permission values in the permissions config. (or in the database.)");
        ModuleProtection.enable = config.get(cat, "enable", true, "Guess what this does?").getBoolean(true);
        ModuleProtection.enableMobSpawns = config.get(cat, "enableMobCheck", false, "If the mobSpawn checking should be done or not.").getBoolean(true);

        config.save();
    }

    @Override
    public void forceSave()
    {
        config.save();
    }

    @Override
    public void forceLoad(ICommandSender sender)
    {
        config.load();
        String cat = "Protection";

        config.addCustomCategoryComment(cat, "You can override the default permission values in the permissions config. (or in the database.)");
        ModuleProtection.enable = config.get(cat, "enable", true, "Guess what this does?").getBoolean(true);
        ModuleProtection.enableMobSpawns = config.get(cat, "enableMobCheck", true).getBoolean(true);
    }
}
