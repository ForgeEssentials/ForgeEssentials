package com.ForgeEssentials.WorldControl.tickTasks;

//Depreciated
import java.util.ArrayList;
import java.util.EnumSet;
import java.util.concurrent.ConcurrentLinkedQueue;

import cpw.mods.fml.common.ITickHandler;
import cpw.mods.fml.common.TickType;

public final class TickTaskHandler implements ITickHandler
{
	public static final int MAX_BLOCK_UPDATES = 10;
	private static ConcurrentLinkedQueue<ITickTask> tasks = new ConcurrentLinkedQueue<ITickTask>();
	
	public static void addTask(ITickTask task)
	{
		if (!tasks.contains(task))
			tasks.add(task);
	}

	@Override
	public void tickStart(EnumSet<TickType> type, Object... tickData)
	{
		
		int blockCounter = 0;
		
		for (ITickTask task: tasks)
		{
			if (task.isComplete())
			{
				task.onComplete();
				tasks.remove(task);
			}
			
			else if (task.editsBlocks() && blockCounter <= MAX_BLOCK_UPDATES)
			{
				task.tick();
				blockCounter++;
			}
			else
				task.tick();
		}
	}

	@Override
	public void tickEnd(EnumSet<TickType> type, Object... tickData)
	{
		// DO NOTHING!!!!  NOTHING HERE!!!!
	}

	@Override
	public EnumSet<TickType> ticks()
	{
		return EnumSet.of(TickType.SERVER);
	}

	@Override
	public String getLabel()
	{
		return "WorldControlTickTaskHandler";
	}

}
