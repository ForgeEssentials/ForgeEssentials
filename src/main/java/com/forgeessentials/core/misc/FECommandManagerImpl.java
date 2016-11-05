package com.forgeessentials.core.misc;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.common.config.Property;

import com.forgeessentials.core.ForgeEssentials;
import com.forgeessentials.core.commands.CommandFeSettings;
import com.forgeessentials.core.moduleLauncher.config.ConfigLoaderBase;
import com.forgeessentials.util.FECommandManager.ConfigurableCommand;
import com.forgeessentials.util.ForgeEssentialsCommandBase;
import com.forgeessentials.util.IFECommandManager;

public class FECommandManagerImpl extends ConfigLoaderBase implements IFECommandManager
{

    public static final int COMMANDS_VERSION = 4;

    protected Map<String, ForgeEssentialsCommandBase> commands = new HashMap<>();

    protected Set<ForgeEssentialsCommandBase> registeredCommands = new HashSet<>();

    protected Configuration config;

    protected boolean newMappings;

    public FECommandManagerImpl()
    {
        ForgeEssentials.getConfigManager().registerLoader("Commands", this);
    }

    @Override
    public void load(Configuration config, boolean isReload)
    {
        this.config = config;
        if (config.get("CommandsConfig", "version", COMMANDS_VERSION).getInt() < COMMANDS_VERSION)
        {
            newMappings = true;
            config.get("CommandsConfig", "version", COMMANDS_VERSION).set(COMMANDS_VERSION);
        }
        for (ForgeEssentialsCommandBase command : commands.values())
            loadCommandConfig(command);
    }

    private void loadCommandConfig(ForgeEssentialsCommandBase command)
    {
        if (config == null)
            return;
        String category = "Commands." + command.getCommandName();
        Property aliasesProperty = config.get(category, "aliases", command.getDefaultAliases());

        if (newMappings)
            aliasesProperty.set(command.getDefaultAliases());
        command.setAliases(aliasesProperty.getStringList());

        if (command instanceof ConfigurableCommand)
            ((ConfigurableCommand) command).loadConfig(config, category);
    }

    @Override
    public void registerCommand(ForgeEssentialsCommandBase command)
    {
        registerCommand(command, false);
    }

    @Override
    public void registerCommand(ForgeEssentialsCommandBase command, boolean registerNow)
    {
        commands.put(command.getCommandName(), command);
        if (config != null)
        {
            loadCommandConfig(command);
        }
        if (registerNow)
            command.register();
    }

    @Override
    public void deegisterCommand(String name)
    {
        ForgeEssentialsCommandBase command = commands.remove(name);
        if (command != null)
            command.deregister();
    }

    @Override
    public void registerCommands()
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

    @Override
    public void clearRegisteredCommands()
    {
        registeredCommands.clear();
    }

}
