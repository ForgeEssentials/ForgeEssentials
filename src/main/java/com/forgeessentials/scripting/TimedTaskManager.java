package com.forgeessentials.scripting;

import com.forgeessentials.core.commands.ForgeEssentialsCommandBase;
import com.forgeessentials.data.api.ClassContainer;
import com.forgeessentials.data.api.DataStorageManager;
import com.forgeessentials.data.v2.DataManager;
import com.forgeessentials.util.OutputHandler;
import net.minecraft.command.ICommandSender;
import net.minecraftforge.permissions.PermissionsManager.RegisteredPermValue;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class TimedTaskManager extends ForgeEssentialsCommandBase {

    private static HashMap<String, TimedTask> taskList = new HashMap<String, TimedTask>();

    private static ClassContainer conTT = new ClassContainer(TimedTask.class);

    private static final String syntax = "/timedtask [add|remove|list] <interval> <name> <command> Regularily run a command as the console. Not to be abused.";

    public TimedTaskManager()
    {
        List<TimedTask> tasks = DataManager.getInstance().loadAll(TimedTask.class);
        if (!tasks.isEmpty())
            for (TimedTask task : tasks)
                taskList.put(task.getName(), task);
        else
        {
            Object[] objs = DataStorageManager.getReccomendedDriver().loadAllObjects(conTT);
            for (Object obj : objs)
            {
                TimedTask task = (TimedTask) obj;
                taskList.put(task.getName(), task);
                DataManager.getInstance().save(task, task.getName());
            }
        }
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
            OutputHandler.chatNotification(sender, syntax);
        }

        else if (args[0].equalsIgnoreCase("create"))
        {
            String command = "";
            for (int i = 3; i < args.length; i++)
            {
                command = command + args[i] + " ";
            }
            TimedTask task = new TimedTask(args[2], command, args[1]);
            taskList.put(args[1], task);
            DataManager.getInstance().save(task, task.getName());
            DataStorageManager.getReccomendedDriver().saveObject(conTT, task);
            OutputHandler.chatConfirmation(sender, "Added timed task " + args[1]);
        }

        else if (args[0].equalsIgnoreCase("remove"))
        {
            taskList.remove(args[1]);
            DataManager.getInstance().delete(TimedTask.class, args[1]);
            DataStorageManager.getReccomendedDriver().deleteObject(conTT, args[1]);
            OutputHandler.chatConfirmation(sender, "Removed timed task " + args[1]);
        }
        else if (args[0].equalsIgnoreCase("list"))
        {
            OutputHandler.chatNotification(sender, "Listing all registered timed tasks");
            Iterator it = taskList.entrySet().iterator();
            while (it.hasNext())
            {
                Map.Entry pairs = (Map.Entry) it.next();
                OutputHandler.chatNotification(sender, String.format("%s1 - %s2", pairs.getKey(), ((TimedTask) pairs.getValue()).getCommand()));
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
    public RegisteredPermValue getDefaultPermission()
    {
        return RegisteredPermValue.OP;
    }

}
