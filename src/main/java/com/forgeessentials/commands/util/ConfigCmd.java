package com.forgeessentials.commands.util;

import java.io.File;

import net.minecraft.command.ICommandSender;
import net.minecraftforge.common.config.Configuration;

import com.forgeessentials.commands.CommandRules;
import com.forgeessentials.commands.ModuleCommands;
import com.forgeessentials.core.moduleLauncher.ModuleConfigBase;

public class ConfigCmd extends ModuleConfigBase {
    public Configuration config;

    public ConfigCmd(File file)
    {
        super(file);
    }

    @Override
    public void init()
    {
        config = new Configuration(file, true);

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

        config.save();

        CommandRegistrar.commandConfigs(config);
    }
}
