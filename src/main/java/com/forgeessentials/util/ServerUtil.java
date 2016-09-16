package com.forgeessentials.util;

import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import net.minecraft.command.CommandHandler;
import net.minecraft.command.ICommand;
import net.minecraft.command.server.CommandMessage;
import net.minecraft.server.MinecraftServer;

import com.forgeessentials.core.commands.ForgeEssentialsCommandBase;
import com.forgeessentials.core.environment.CommandSetChecker;
import com.forgeessentials.util.output.LoggingHandler;

import cpw.mods.fml.relauncher.ReflectionHelper;

public abstract class ServerUtil
{

    public static void replaceCommand(Class<CommandMessage> clazz, ICommand newCommand)
    {
        try
        {
            CommandHandler commandHandler = (CommandHandler) MinecraftServer.getServer().getCommandManager();
            Map<String, ICommand> commandMap = ReflectionHelper.getPrivateValue(CommandHandler.class, commandHandler, "commandMap", "a", "field_71562_a");
            Set<ICommand> commandSet = ReflectionHelper.getPrivateValue(CommandHandler.class, commandHandler, CommandSetChecker.FIELDNAME);
            for (Iterator<Entry<String, ICommand>> it = commandMap.entrySet().iterator(); it.hasNext();)
            {
                Entry<String, ICommand> command = it.next();
                if (clazz.isAssignableFrom(command.getValue().getClass()))
                {
                    commandSet.remove(command.getValue());
                    commandSet.add(newCommand);
                    command.setValue(newCommand);
                }
            }
        }
        catch (Exception e)
        {
            LoggingHandler.felog.error(String.format("Error replacing command /%s", clazz.getClass().getName()));
            e.printStackTrace();
        }
        if (newCommand instanceof ForgeEssentialsCommandBase)
            ((ForgeEssentialsCommandBase) newCommand).register();
    }

    public static void replaceCommand(ICommand oldCommand, ICommand newCommand)
    {
        try
        {
            CommandHandler commandHandler = (CommandHandler) MinecraftServer.getServer().getCommandManager();
            Map<String, ICommand> commandMap = ReflectionHelper.getPrivateValue(CommandHandler.class, commandHandler, "commandMap", "a", "field_71562_a");
            Set<ICommand> commandSet = ReflectionHelper.getPrivateValue(CommandHandler.class, commandHandler, CommandSetChecker.FIELDNAME);
            for (Iterator<Entry<String, ICommand>> it = commandMap.entrySet().iterator(); it.hasNext();)
            {
                Entry<String, ICommand> command = it.next();
                if (command.getValue() == oldCommand)
                {
                    commandSet.remove(command.getValue());
                    commandSet.add(newCommand);
                    command.setValue(newCommand);
                }
            }
        }
        catch (Exception e)
        {
            LoggingHandler.felog.error(String.format("Error replacing command /%s", oldCommand.getCommandName()));
            e.printStackTrace();
        }
        if (newCommand instanceof ForgeEssentialsCommandBase)
            ((ForgeEssentialsCommandBase) newCommand).register();
    }

    public static void replaceCommand(String command, ICommand newCommand)
    {
        ICommand oldCommand = (ICommand) MinecraftServer.getServer().getCommandManager().getCommands().get(command);
        if (oldCommand != null)
            replaceCommand(oldCommand, newCommand);
        else
            LoggingHandler.felog.error(String.format("Could not find command /%s to replace", command));
    }

}
