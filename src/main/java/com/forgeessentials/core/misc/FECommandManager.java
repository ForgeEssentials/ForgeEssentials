package com.forgeessentials.core.misc;

import java.util.HashSet;
import java.util.Set;

import com.forgeessentials.core.FEConfig;
import com.forgeessentials.core.commands.ForgeEssentialsCommandBuilder;
import com.forgeessentials.core.misc.commandperms.PermissionManager;
import com.forgeessentials.util.output.logger.LoggingHandler;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.tree.LiteralCommandNode;

import net.minecraft.command.CommandSource;
import net.minecraft.command.Commands;

public class FECommandManager
{

    public static interface ConfigurableCommand
    {
        public void loadData();
    }

    protected static Set<FECommandData> loadedFEcommands = new HashSet<>();
    protected static Set<String> registeredFEcommands = new HashSet<>();
    protected static Set<String> registeredAiliases = new HashSet<>();
    protected static Set<String> loadedConfigurableCommand = new HashSet<>();

    public static FEAliasesManager aliaseManager;

    public FECommandManager()
    {
        aliaseManager = new FEAliasesManager();
    }

    public static void registerCommand(ForgeEssentialsCommandBuilder commandBuilder, CommandDispatcher<CommandSource> dispatcher)
    {
        FECommandData command = new FECommandData(commandBuilder);
        loadedFEcommands.add(command);
        if (!registeredFEcommands.contains(command.getData().getName()))
        {
            // FEAliasesManager.bakeCommandConfig(command);
            register(command, dispatcher);
        }
        // FEAliasesManager.loadCommandConfig(command);
    }

    public static void clearRegisteredCommands()
    {
        LoggingHandler.felog.debug("ForgeEssentials clearing commands");
        loadedFEcommands.clear();
        registeredFEcommands.clear();
        registeredAiliases.clear();
    }

    public static void loadConfigurableCommand() {
    	for (FECommandData command : loadedFEcommands) {
    		if (command.getData() instanceof ConfigurableCommand)
                ((ConfigurableCommand) command.getData()).loadData();
    	}
    }

    /**
     * Registers this command and it's permission node
     */
    public static void register(FECommandData commandData, CommandDispatcher<CommandSource> dispatcher)
    {

        String name = commandData.getData().getName();
        if (commandData.isRegistered())
        {
            LoggingHandler.felog
                    .error(String.format("Tried to register command %s, but it is alredy registered", name));
            return;
        }
        if (commandData.getData().setExecution() == null)
        {
            LoggingHandler.felog.error(String.format("Tried to register command %s with null execution", name));
            return;
        }
        if (commandData.getData().isEnabled())
        {
            if (registeredFEcommands.contains(name))
            {
                LoggingHandler.felog.error(String.format("Command %s already registered!", name));
                return;
            }

            LiteralCommandNode<CommandSource> literalcommandnode = dispatcher
                    .register(commandData.getData().getMainBuilder());
            //LoggingHandler.felog.debug("Registered Command: " + name);
            if (FEConfig.enableCommandAliases)
            {
                if (commandData.getData().getAliases() != null && !commandData.getData().getAliases().isEmpty())
                {
                    try
                    {
                        for (String alias : commandData.getData().getAliases())
                        {
                            if (registeredAiliases.contains(alias))
                            {
                                LoggingHandler.felog
                                        .error(String.format("Command alias %s already registered!", alias));
                                continue;
                            }
                            dispatcher.register(Commands.literal(alias).redirect(literalcommandnode)
                                    .requires(source -> source.hasPermission(PermissionManager
                                            .fromDefaultPermissionLevel(commandData.getData().getPermissionLevel()))));
                            LoggingHandler.felog.info("Registered Command: " + name + "'s alias: " + alias);
                            registeredAiliases.add(alias);
                        }
                    }
                    catch (NullPointerException e)
                    {
                        LoggingHandler.felog.error("Failed to register aliases for command: " + name);
                    }
                }
            }
            commandData.setRegistered(true);
            registeredFEcommands.add(name);
        }
        commandData.getData().registerExtraPermissions();
    }

    public static int getTotalCommandNumber() {
    	return FECommandManager.registeredFEcommands.size() + FECommandManager.registeredAiliases.size();
    }
}
