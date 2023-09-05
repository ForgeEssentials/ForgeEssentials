package com.forgeessentials.core.misc.commandTools;

import java.util.HashSet;
import java.util.Set;

import com.forgeessentials.core.FEConfig;
import com.forgeessentials.core.ForgeEssentials;
import com.forgeessentials.core.commands.ForgeEssentialsCommandBuilder;
import com.forgeessentials.util.output.logger.LoggingHandler;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.tree.LiteralCommandNode;

import net.minecraft.command.CommandSource;
import net.minecraft.command.Commands;
import net.minecraftforge.fml.common.ObfuscationReflectionHelper;

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
        final FECommandData command = new FECommandData(commandBuilder);
        loadedFEcommands.add(command);
        if (!registeredFEcommands.contains(command.getName()))
        {
        	if(FEConfig.enableCommandAliases) {
        		aliaseManager.loadCommandAliases(command);
        	}
            register(command, dispatcher);
        }
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
    		if (command.getBuilder() instanceof ConfigurableCommand)
                ((ConfigurableCommand) command.getBuilder()).loadData();
    	}
    }

    /**
     * Registers this command and it's permission node
     */
    public static void register(FECommandData commandData, CommandDispatcher<CommandSource> dispatcher)
    {
        if (commandData.isRegistered())
        {
            LoggingHandler.felog.error(String.format("Tried to register command %s/%s, but it is alredy registered", commandData.getName(),commandData.getMainName()));
            return;
        }
        if (commandData.getBuilder().setExecution() == null)
        {
            LoggingHandler.felog.error(String.format("Tried to register command %s/%s with null execution", commandData.getName(),commandData.getMainName()));
            return;
        }
        if (commandData.getBuilder().isEnabled())
        {
            if (registeredFEcommands.contains(commandData.getName()))
            {
                LoggingHandler.felog.error(String.format("Command %s/%s already registered!", commandData.getName(),commandData.getMainName()));
                return;
            }

            LiteralArgumentBuilder<CommandSource> builder = commandData.getBuilder().getMainBuilder();
            
            //don't change main name if not using aliases
            if(FEConfig.enableCommandAliases) {
            	//set main name for commands to bypass minecraft command redirect empty trees
                ObfuscationReflectionHelper.setPrivateValue(LiteralArgumentBuilder.class, builder, commandData.getMainName(), "literal");
            }
            LiteralCommandNode<CommandSource> literalcommandnode = dispatcher.register(builder);
            if(ForgeEssentials.isDebug())
            	LoggingHandler.felog.debug("Registered Command: " + commandData.getName()+"/"+commandData.getMainName());
            if (FEConfig.enableCommandAliases)
            {
                if (commandData.getMainAliases()!= null && !commandData.getMainAliases().isEmpty())
                {
                    try
                    {
                        for (String alias : commandData.getMainAliases())
                        {
                            if (registeredAiliases.contains(alias))
                            {
                                LoggingHandler.felog
                                        .error(String.format("Command alias %s already registered!", alias));
                                continue;
                            }
                            dispatcher.register(Commands.literal(alias).redirect(literalcommandnode)
                                    .requires(source -> source.hasPermission(PermissionManager
                                            .fromDefaultPermissionLevel(commandData.getBuilder().getPermissionLevel()))));
                            if(ForgeEssentials.isDebug())
                            	LoggingHandler.felog.info("Registered Command: " + commandData.getName()+"/"+commandData.getMainName() + "'s alias: " + alias);
                            registeredAiliases.add(alias);
                        }
                    }
                    catch (NullPointerException e)
                    {
                        LoggingHandler.felog.error("Failed to register aliases for command: " + commandData.getMainName());
                    }
                }
            }
            commandData.setRegistered(true);
            registeredFEcommands.add(commandData.getName());
        }
        commandData.getBuilder().registerExtraPermissions();
    }

    public static int getTotalCommandNumber() {
    	return FECommandManager.registeredFEcommands.size() + FECommandManager.registeredAiliases.size();
    }
}
