package com.forgeessentials.core.misc;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import net.minecraftforge.common.config.Configuration;

import com.forgeessentials.core.ForgeEssentials;
import com.forgeessentials.core.commands.ForgeEssentialsCommandBase;
import com.forgeessentials.core.moduleLauncher.config.ConfigLoader.ConfigLoaderBase;

public class FECommandManager extends ConfigLoaderBase
{

    public static interface ConfigurableCommand
    {
        public void loadConfig(Configuration config, String category);
    }

    private static Map<String, ForgeEssentialsCommandBase> commands = new HashMap<>();

    private static Set<ForgeEssentialsCommandBase> registeredCommands = new HashSet<>();

    private static Configuration config;

    public FECommandManager()
    {
        ForgeEssentials.getConfigManager().registerLoader("Commands", this);
    }

    @Override
    public void load(Configuration config, boolean isReload)
    {
        FECommandManager.config = config;
        for (ForgeEssentialsCommandBase command : commands.values())
            loadCommandConfig(command);
    }

    private static void loadCommandConfig(ForgeEssentialsCommandBase command)
    {
        if (config == null)
            return;
        String category = "Commands." + command.getCommandName();
        command.setAliases(config.get(category, "aliases", command.getDefaultAliases()).getStringList());
        if (command instanceof ConfigurableCommand)
            ((ConfigurableCommand) command).loadConfig(config, category);
    }

    public static void registerCommand(ForgeEssentialsCommandBase command)
    {
        commands.put(command.getCommandName(), command);
        if (config != null)
        {
            loadCommandConfig(command);
            command.register();
        }
    }

    public static void registerCommands()
    {
        ForgeEssentials.getConfigManager().load("Commands");
        for (ForgeEssentialsCommandBase command : commands.values())
            if (!registeredCommands.contains(command))
            {
                registeredCommands.add(command);
                command.register();
            }
    }

    public static void clearRegisteredCommands()
    {
        registeredCommands.clear();
    }

}
