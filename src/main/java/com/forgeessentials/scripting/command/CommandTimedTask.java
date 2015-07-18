package com.forgeessentials.scripting.command;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import net.minecraft.command.ICommandSender;
import net.minecraftforge.permission.PermissionLevel;

import com.forgeessentials.core.commands.ForgeEssentialsCommandBase;
import com.forgeessentials.core.misc.Translator;
import com.forgeessentials.data.v2.DataManager;
import com.forgeessentials.scripting.TimedTask;
import com.forgeessentials.util.output.ChatOutputHandler;

public class CommandTimedTask extends ForgeEssentialsCommandBase
{

    private static Map<String, TimedTask> taskList = new HashMap<String, TimedTask>();

    private static final String syntax = "/timedtask [add|remove|list] <interval> <name> <command> Regularily run a command as the console. Not to be abused.";

    public CommandTimedTask()
    {
        taskList = DataManager.getInstance().loadAll(TimedTask.class);
    }

    @Override
    public String getCommandName()
    {
        return "timedtask";
    }

    @Override
    public String getCommandUsage(ICommandSender sender)
    {
        return syntax;
    }

    @Override
    public void processCommand(ICommandSender sender, String[] args)
    {
        if (args.length == 0)
        {
            ChatOutputHandler.chatNotification(sender, syntax);
        }

        else if (args[0].equalsIgnoreCase("create"))
        {
            String command = "";
            for (int i = 3; i < args.length; i++)
            {
                command = command + args[i] + " ";
            }
            TimedTask task = new TimedTask(Integer.parseInt(args[2]), command, args[1]);
            taskList.put(args[1], task);
            DataManager.getInstance().save(task, task.getName());
            ChatOutputHandler.chatConfirmation(sender, "Added timed task " + args[1]);
        }

        else if (args[0].equalsIgnoreCase("remove"))
        {
            taskList.remove(args[1]);
            DataManager.getInstance().delete(TimedTask.class, args[1]);
            ChatOutputHandler.chatConfirmation(sender, "Removed timed task " + args[1]);
        }
        else if (args[0].equalsIgnoreCase("list"))
        {
            ChatOutputHandler.chatNotification(sender, "Listing all registered timed tasks");
            for (Entry<String, TimedTask> task : taskList.entrySet())
            {
                ChatOutputHandler.chatNotification(sender, Translator.format("%s1 - %s2", task.getKey(), task.getValue().getCommand()));
            }
        }

    }

    @Override
    public boolean canConsoleUseCommand()
    {
        return true;
    }

    @Override
    public String getPermissionNode()
    {
        return "fe.script.timedtask";
    }

    @Override
    public PermissionLevel getPermissionLevel()
    {
        return PermissionLevel.OP;
    }

}
