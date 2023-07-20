package com.forgeessentials.core.misc;

import java.util.HashSet;
import java.util.Set;

import com.forgeessentials.core.FEConfig;
import com.forgeessentials.core.commands.CommandFeSettings;
import com.forgeessentials.core.commands.ForgeEssentialsCommandBuilder;
import com.forgeessentials.core.misc.commandperms.PermissionManager;
import com.forgeessentials.util.output.logger.LoggingHandler;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.tree.LiteralCommandNode;

import net.minecraft.command.CommandSource;
import net.minecraft.command.Commands;
import net.minecraftforge.fml.server.ServerLifecycleHooks;

public class FECommandManager
{

    public static interface ConfigurableCommand
    {
        public void loadData();
    }

    protected static Set<FECommandData> loadedFEcommands = new HashSet<>();
    protected static Set<String> registeredFEcommands = new HashSet<>();
    protected static Set<String> registeredAiliases = new HashSet<>();

    // protected static boolean useSingleConfigFile = false;

    public static FEAliasesManager aliaseManager;

    public FECommandManager()
    {
        aliaseManager = new FEAliasesManager();
    }

    public static void registerCommand(ForgeEssentialsCommandBuilder commandBuilder,
            CommandDispatcher<CommandSource> dispatcher)
    {
        FECommandData command = new FECommandData(commandBuilder, dispatcher);
        loadedFEcommands.add(command);
        // FEAliasesManager.loadCommandConfig(command);
        // if (useSingleConfigFile = false){}
    }

    public static void deegisterCommand(String name)
    {
        FECommandData command = null;
        for (FECommandData cmd : loadedFEcommands)
        {
            if (cmd.getData().getName().equals(name))
            {
                command = cmd;
                break;
            }
        }

        if (command != null)
        {
            registeredFEcommands.remove(name);
            deregister(command);
        }
        else
        {
            LoggingHandler.felog.error(String.format("Tried to deregister command %s, but got a nullpointer", name));
        }
    }

    public static void registerLoadedCommands()
    {
        LoggingHandler.felog.info("ForgeEssentials: Registering known commands");
        for (FECommandData command : loadedFEcommands)
        {
            if (!registeredFEcommands.contains(command.getData().getName()))
            {
                // FEAliasesManager.bakeCommandConfig(command);
                register(command);
                if (command.getData() instanceof ConfigurableCommand)
                    ((ConfigurableCommand) command.getData()).loadData();
            }
        }
        LoggingHandler.felog.info("Registered "
                + Integer.toString(registeredFEcommands.size() + registeredAiliases.size()) + " commands");
        CommandFeSettings.getInstance().loadSettings();
    }

    public static void clearRegisteredCommands()
    {
        LoggingHandler.felog.debug("ForgeEssentials clearing commands");
        loadedFEcommands.clear();
        registeredFEcommands.clear();
        registeredAiliases.clear();
    }

    /**
     * Registers this command and it's permission node
     */
    public static void register(FECommandData commandData)
    {

        if (ServerLifecycleHooks.getCurrentServer() == null)
            return;

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

            CommandDispatcher<CommandSource> dispatcher = commandData.getDisp();
            if (registeredFEcommands.contains(name))
            {
                LoggingHandler.felog.error(String.format("Command %s already registered!", name));
                return;
            }

            LiteralCommandNode<CommandSource> literalcommandnode = dispatcher
                    .register(commandData.getData().getMainBuilder());
            LoggingHandler.felog.debug("Registered Command: " + name);
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
        // PermissionManager.registerCommandPermission(commandData.getData().getPermissionNode(),
        // commandData.getData().getPermissionLevel());
        commandData.getData().registerExtraPermissions();
    }

    public static void deregister(FECommandData commandData)
    {
        /*
         * if (ServerLifecycleHooks.getCurrentServer() == null) return; CommandHandler cmdHandler = (CommandHandler)
         * FMLCommonHandler.instance().getMinecraftServerInstance().getCommandManager(); Map<String, ICommand> commandMap = cmdHandler.getCommands(); Set<ICommand> commandSet =
         * cmdHandler.commandSet;
         * 
         * String commandName = getName(); List<String> commandAliases = getAliases(); commandSet.remove(this); if (commandName != null) commandMap.remove(commandName); if
         * (commandAliases != null && !commandAliases.isEmpty()) { for (String alias : commandAliases) { commandMap.remove(alias); } }
         */
    }
}
