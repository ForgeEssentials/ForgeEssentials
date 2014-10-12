package com.forgeessentials.commands.util;

import com.forgeessentials.commands.CommandRules;
import com.forgeessentials.commands.ModuleCommands;
import com.forgeessentials.core.moduleLauncher.ModuleConfigBase;
import net.minecraft.command.ICommandSender;

public class ConfigCmd extends ModuleConfigBase {

    @Override
    public void init()
    {

        config.addCustomCategoryComment("general", "General Commands configuration.");
        config.save();
    }

    @Override
    public void forceSave()
    {
        // TODO: may have problems..
        String path = CommandRules.rulesFile.getPath();
        path = path.replace(ModuleCommands.cmddir.getPath(), "");

        config.addCustomCategoryComment("general", "General Commands configuration.");
        config.save();
    }

    @Override
    public void forceLoad(ICommandSender sender)
    {
        config.load();
        CommandRegistrar.commandConfigs(config);
        config.save();
    }

    public boolean universalConfigAllowed(){return false;}
}
