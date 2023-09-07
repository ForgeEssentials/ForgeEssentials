package com.forgeessentials.core.misc.commandTools;

import java.util.HashSet;
import java.util.Set;

import com.forgeessentials.core.FEConfig;
import com.forgeessentials.core.ForgeEssentials;
import com.forgeessentials.core.commands.ForgeEssentialsCommandBuilder;
import com.forgeessentials.util.output.logger.LoggingHandler;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;

import net.minecraft.command.CommandSource;
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
            LoggingHandler.felog.error(String.format("Tried to register command %s, but it is alredy registered", commandData.getName()));
            return;
        }
        if (commandData.getBuilder().setExecution() == null)
        {
            LoggingHandler.felog.error(String.format("Tried to register command %s with null execution", commandData.getName()));
            return;
        }
        if (commandData.getBuilder().isEnabled())
        {
            if (registeredFEcommands.contains(commandData.getName()))
            {
                LoggingHandler.felog.error(String.format("Command %s already registered!", commandData.getName()));
                return;
            }
            if (registeredAiliases.contains(commandData.getName()))
            {
                LoggingHandler.felog.error(String.format("Command %s already registered as an alias!", commandData.getName()));
                return;
            }
            LiteralArgumentBuilder<CommandSource> builder = commandData.getBuilder().getMainBuilder();

            //Register alias under a redirect
//            //don't change main name if not using aliases
//            if(FEConfig.enableCommandAliases) {
//            	//set main name for commands to bypass minecraft command redirect empty trees
//                ObfuscationReflectionHelper.setPrivateValue(LiteralArgumentBuilder.class, builder, commandData.getMainName(), "literal");
//            }
            //LiteralCommandNode<CommandSource> literalcommandnode = dispatcher.register(builder);

            //Register alias as full command. No redirects
            dispatcher.register(builder);
            if(ForgeEssentials.isDebug())
            	LoggingHandler.felog.debug("Registered Command: " + commandData.getName());
            registeredFEcommands.add(commandData.getName());
            
            if (FEConfig.enableCommandAliases)
            {
                if (commandData.getAliases()!= null && !commandData.getAliases().isEmpty())
                {
                    try
                    {
                        for (String alias : commandData.getAliases())
                        {
                            if (registeredAiliases.contains(alias))
                            {
                                LoggingHandler.felog
                                        .error(String.format("Command alias %s already registered!", alias));
                                continue;
                            }
                            if (registeredFEcommands.contains(alias))
                            {
                                LoggingHandler.felog
                                        .error(String.format("Command alias %s already registered as a main command!", alias));
                                continue;
                            }

                            //Register alias under a redirect
//                            dispatcher.register(Commands.literal(alias).redirect(literalcommandnode)
//                                    .requires(source -> source.hasPermission(PermissionManager
//                                            .fromDefaultPermissionLevel(commandData.getBuilder().getPermissionLevel()))));
                            
                            //Register alias as full command. No redirects
                            ObfuscationReflectionHelper.setPrivateValue(LiteralArgumentBuilder.class, builder, alias, "literal");
                            dispatcher.register(builder);
                            
                            if(ForgeEssentials.isDebug())
                            	LoggingHandler.felog.info("Registered Command: " + commandData.getName() + "'s alias: " + alias);
                            registeredAiliases.add(alias);
                        }
                    }
                    catch (NullPointerException e)
                    {
                        LoggingHandler.felog.error("Failed to register aliases for command: " + commandData.getName());
                    }
                }
            }
            commandData.setRegistered(true);
        }
        commandData.getBuilder().registerExtraPermissions();
    }

    public static int getTotalCommandNumber() {
    	return FECommandManager.registeredFEcommands.size() + FECommandManager.registeredAiliases.size();
    }
}
