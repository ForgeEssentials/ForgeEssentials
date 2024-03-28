package com.forgeessentials.core.commands.registration;

import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.stream.Collectors;
import java.util.Set;

import com.forgeessentials.core.FEConfig;
import com.forgeessentials.core.ForgeEssentials;
import com.forgeessentials.core.commands.ForgeEssentialsCommandBuilder;
import com.forgeessentials.util.output.logger.LoggingHandler;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.tree.CommandNode;
import com.mojang.brigadier.tree.LiteralCommandNode;

import net.minecraft.commands.CommandSourceStack;
import net.minecraftforge.fml.util.ObfuscationReflectionHelper;

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

    public static void registerCommand(ForgeEssentialsCommandBuilder commandBuilder, CommandDispatcher<CommandSourceStack> dispatcher)
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
    public static void register(FECommandData commandData, CommandDispatcher<CommandSourceStack> dispatcher)
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
            LiteralArgumentBuilder<CommandSourceStack> builder = commandData.getBuilder().getMainBuilder();

            //Register alias under a redirect
//            //don't change main name if not using aliases
//            if(FEConfig.enableCommandAliases) {
//            	//set main name for commands to bypass minecraft command redirect empty trees
//                ObfuscationReflectionHelper.setPrivateValue(LiteralArgumentBuilder.class, builder, commandData.getMainName(), "literal");
//            }
            //LiteralCommandNode<CommandSource> literalcommandnode = dispatcher.register(builder);

            if(checkOverwritingCommands(commandData.getName(),dispatcher)) {
            	LoggingHandler.felog.warn("Registering command: ["+commandData.getName()+"] that conflicts with an existing command/alias");
            }
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
                            
                            if(checkOverwritingCommands(alias, dispatcher)) {
                            	LoggingHandler.felog.warn("Registering alias: ["+alias+"] that conflicts with an existing command/alias");
                            }
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

    public static boolean checkOverwritingCommands(String commandName, CommandDispatcher<CommandSourceStack> dispatcher) {
    	boolean commandRemoved=false;
    	Map<String, CommandNode<CommandSourceStack>> children = new LinkedHashMap<>(ObfuscationReflectionHelper.getPrivateValue(CommandNode.class, (CommandNode<CommandSourceStack>) dispatcher.getRoot(), "children"));
    	Map<String, CommandNode<CommandSourceStack>> newChildren = new LinkedHashMap<>();
    	for(Entry<String, CommandNode<CommandSourceStack>> child : children.entrySet()) {
    		if(child.getValue() instanceof LiteralCommandNode && child.getKey().equals(commandName)) {
    			if(FEConfig.overwriteConflictingCommands) {
    				LoggingHandler.felog.info("Removing conflicting command/alias:"+ commandName);
    			}
    			commandRemoved=true;
    		}
    		else {
    			if(FEConfig.overwriteConflictingCommands) {
    				newChildren.put(child.getKey(), child.getValue());
    			}
    		}
    	}
		if(commandRemoved && FEConfig.overwriteConflictingCommands) {
			newChildren.entrySet().stream().sorted(Map.Entry.comparingByValue()).collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue, (e1, e2) -> e1, LinkedHashMap::new));
			ObfuscationReflectionHelper.setPrivateValue(CommandNode.class, (CommandNode<CommandSourceStack>) dispatcher.getRoot(), newChildren, "children");
			return false;
		}
		else if(commandRemoved && !FEConfig.overwriteConflictingCommands){
			return true;
		}
		else {
			return false;
		}
    }
}
