package com.forgeessentials.util.tasks;

//Depreciated
import java.util.EnumSet;
import java.util.concurrent.ConcurrentLinkedQueue;

import cpw.mods.fml.common.ITickHandler;
import cpw.mods.fml.common.TickType;

public final class TickTaskHandler implements ITickHandler
{
	public static final int						MAX_BLOCK_UPDATES	= 10;
	protected ConcurrentLinkedQueue<ITickTask>	tasks				= new ConcurrentLinkedQueue<ITickTask>();

	@Override
	public void tickStart(EnumSet<TickType> type, Object... tickData)
	{

		int blockCounter = 0;

		for (ITickTask task : tasks)
		{
			// remove the compelte ones
			if (task.isComplete())
			{
				task.onComplete();
				tasks.remove(task);
			}

			// add the blockCounter if it edits blocks
			else if (task.editsBlocks() && blockCounter <= MAX_BLOCK_UPDATES)
			{
				task.tick();
				blockCounter++;
			}

			// otherwise just tick
			else
			{
				task.tick();
			}
		}
	}

	@Override
	public void tickEnd(EnumSet<TickType> type, Object... tickData)
	{
		// DO NOTHING!!!! NOTHING HERE!!!!
	}

	@Override
	public EnumSet<TickType> ticks()
	{
		return EnumSet.of(TickType.SERVER);
	}

	@Override
	public String getLabel()
	{
		return "FE_TickTasks";
	}

}
