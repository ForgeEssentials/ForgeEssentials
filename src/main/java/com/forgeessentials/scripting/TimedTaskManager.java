package com.forgeessentials.scripting;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import net.minecraft.command.ICommandSender;

import com.forgeessentials.api.permissions.RegGroup;
import com.forgeessentials.core.commands.ForgeEssentialsCommandBase;
import com.forgeessentials.data.AbstractDataDriver;
import com.forgeessentials.data.api.ClassContainer;
import com.forgeessentials.data.api.DataStorageManager;
import com.forgeessentials.util.ChatUtils;

public class TimedTaskManager extends ForgeEssentialsCommandBase {

	private static HashMap<String, TimedTask> taskList = new HashMap<String, TimedTask>();
	
	private static AbstractDataDriver data;
	private static ClassContainer conTT = new ClassContainer(TimedTask.class);
	
	private static final String syntax = "/timedtask [add|remove|list] <interval> <name> <command> Regularily run a command as the console. Not to be abused.";
	
	public TimedTaskManager()
	{
		data = DataStorageManager.getReccomendedDriver();
		Object[] objs = data.loadAllObjects(conTT);
		for (Object obj : objs) {
		
			TimedTask task = (TimedTask)obj;
			taskList.put(task.getName(), task);
		}
	}
	
	@Override
	public String getCommandName() {
		return "timedtask";
	}

	@Override
	public String getCommandUsage(ICommandSender sender) {
		return syntax;
	}
	
	@Override
	public void processCommand(ICommandSender sender, String[] args)
	{
		if(args.length == 0){
			ChatUtils.sendMessage(sender, syntax);
		}
		
		else if (args[0].equalsIgnoreCase("create")){
			String command = "";
			for (int i = 3; i < args.length; i++)
			{
				command = command + args[i] + " ";
			}
			TimedTask task =  new TimedTask(args[2], command, args[1]);
			taskList.put(args[1], task);
			data.saveObject(conTT, task);
			ChatUtils.sendMessage(sender, "Added timed task "+ args[1]);
		}
		
		else if (args[0].equalsIgnoreCase("remove"))
		{
			taskList.remove(args[1]);
			data.deleteObject(conTT, args[1]);
			ChatUtils.sendMessage(sender, "Removed timed task "+ args[1]);
		}
		else if (args[0].equalsIgnoreCase("list")){
			ChatUtils.sendMessage(sender, "Listing all registered timed tasks");
			Iterator it = taskList.entrySet().iterator();
		    while (it.hasNext()) {
		        Map.Entry pairs = (Map.Entry)it.next();
		        ChatUtils.sendMessage(sender, String.format("%s1 - %s2", (String)pairs.getKey(), ((TimedTask)pairs.getValue()).getCommand()));
		    }
		}
		
	}

	@Override
	public boolean canConsoleUseCommand() {
		return true;
	}

	@Override
	public String getCommandPerm() {
		return "fe.script.timedtask";
	}

	@Override
	public RegGroup getReggroup() {
		return RegGroup.ZONE_ADMINS;
	}

}
