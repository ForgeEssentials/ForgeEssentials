package com.forgeessentials.scripting;

import java.util.TimerTask;

import net.minecraft.server.MinecraftServer;

import com.forgeessentials.data.api.IReconstructData;
import com.forgeessentials.data.api.SaveableObject;
import com.forgeessentials.data.api.SaveableObject.Reconstructor;
import com.forgeessentials.data.api.SaveableObject.SaveableField;
import com.forgeessentials.util.tasks.TaskRegistry;

@SaveableObject
public class TimedTask extends TimerTask{
	
	@SaveableField
	private int interval; // in seconds
	
	@SaveableField
	private String command;
	
	@SaveableField
	private String name;
	
	public TimedTask(int interval, String command, String name)
	{
		this.interval = interval;
		this.command = command;
		this.name = name;
		
		TaskRegistry.registerRecurringTask(this, 0, 0, interval, 0, 0, 0, interval, 0);
	
	}

	@Override
	public void run()
	{
		MinecraftServer.getServer().executeCommand(command);
	}
	
	public TimedTask(Object interval, Object command, Object name)
	{
		this.interval = (int) interval;
		this.command = (String) command;
		this.name = (String) name;
		
		TaskRegistry.registerRecurringTask(this, 0, 0, (int)interval, 0, 0, 0, (int)interval, 0);
	}
	
	@Reconstructor
	private static TimedTask reconstruct(IReconstructData tag)
	{
		return new TimedTask(tag.getFieldValue("interval"), tag.getFieldValue("command"), tag.getFieldValue("name"));
	}
	
	public String getName()
	{
		return name;
	}
	
	public String getCommand()
	{
		return command;
	}
	public int getInterval()
	{
		return interval;
	}

}
