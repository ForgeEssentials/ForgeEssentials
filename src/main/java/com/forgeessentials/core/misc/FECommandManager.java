package com.forgeessentials.core.misc;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.common.config.Property;

import com.forgeessentials.core.ForgeEssentials;
import com.forgeessentials.core.commands.CommandFeSettings;
import com.forgeessentials.core.commands.ForgeEssentialsCommandBase;
import com.forgeessentials.core.moduleLauncher.config.ConfigLoaderBase;

public class FECommandManager extends ConfigLoaderBase
{

    public static interface ConfigurableCommand
    {

        public void loadConfig(Configuration config, String category);

        public void loadData();

    }

    public static final int COMMANDS_VERSION = 4;

    protected static Map<String, ForgeEssentialsCommandBase> commands = new HashMap<>();

    protected static Set<ForgeEssentialsCommandBase> registeredCommands = new HashSet<>();

    protected static Configuration config;

    protected static boolean newMappings;

    public FECommandManager()
    {
        ForgeEssentials.getConfigManager().registerLoader("Commands", this);
    }

    @Override
    public void load(Configuration config, boolean isReload)
    {
        FECommandManager.config = config;
        if (config.get("CommandsConfig", "version", COMMANDS_VERSION).getInt() < COMMANDS_VERSION)
        {
            newMappings = true;
            config.get("CommandsConfig", "version", COMMANDS_VERSION).set(COMMANDS_VERSION);
        }
        for (ForgeEssentialsCommandBase command : commands.values())
            loadCommandConfig(command);
    }

    private static void loadCommandConfig(ForgeEssentialsCommandBase command)
    {
        if (config == null)
            return;
        String category = "Commands." + command.getName();
        Property aliasesProperty = config.get(category, "aliases", command.getDefaultAliases());

        if (newMappings)
            aliasesProperty.set(command.getDefaultAliases());
        command.setAliases(aliasesProperty.getStringList());

        if (command instanceof ConfigurableCommand)
            ((ConfigurableCommand) command).loadConfig(config, category);
    }

    public static void registerCommand(ForgeEssentialsCommandBase command)
    {
        registerCommand(command, false);
    }

    public static void registerCommand(ForgeEssentialsCommandBase command, boolean registerNow)
    {
        commands.put(command.getName(), command);
        if (config != null)
        {
            loadCommandConfig(command);
        }
        if (registerNow)
            command.register();
    }

    public static void deegisterCommand(String name)
    {
        ForgeEssentialsCommandBase command = commands.remove(name);
        if (command != null)
            command.deregister();
    }

    public static void registerCommands()
    {
        ForgeEssentials.getConfigManager().load("Commands");
        for (ForgeEssentialsCommandBase command : commands.values())
            if (!registeredCommands.contains(command))
            {
                registeredCommands.add(command);
                command.register();
                if (command instanceof ConfigurableCommand)
                    ((ConfigurableCommand) command).loadData();
            }
        CommandFeSettings.getInstance().loadSettings();
    }

    public static void clearRegisteredCommands()
    {
        registeredCommands.clear();
    }

}
