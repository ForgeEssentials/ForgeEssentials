package com.forgeessentials.WorldControl.TickTasks;

//Depreciated
import java.util.ArrayList;

import net.minecraft.entity.player.EntityPlayer;

import com.forgeessentials.WorldControl.ConfigWorldControl;
import com.forgeessentials.util.BackupArea;
import com.forgeessentials.util.BlockSaveable;
import com.forgeessentials.util.OutputHandler;
import com.forgeessentials.util.tasks.ITickTask;

public class TickTaskSetBackup implements ITickTask
{
	// stuff needed
	private final EntityPlayer			player;
	// actually used
	private final int					last;
	private int							current;
	private int							changed;
	private ArrayList<BlockSaveable>	list;

	/**
	 * @param player
	 * @param back
	 * BackupArea
	 * @param before
	 * true = redo -- false = undo
	 */
	public TickTaskSetBackup(EntityPlayer player, BackupArea back, boolean redo)
	{
		this.player = player;
		if (redo)
		{
			list = back.after;
		}
		else
		{
			list = back.before;
		}

		last = list.size() - 1;
		current = -1;
	}

	@Override
	public void tick()
	{
		int lastChanged = changed;

		for (int i = (current == -1? 0:current); i <= last; i++)
		{
			current = i;

			if (list.get(i).setinWorld(player.worldObj))
			{
				changed++;
			}

			if (lastChanged >= ConfigWorldControl.blocksPerTick)
				return;
		}
	}

	@Override
	public void onComplete()
	{			
		OutputHandler.chatConfirmation(player, "" + changed + " blocks changed");
	}

	@Override
	public boolean isComplete()
	{
		return current == last;
	}

	@Override
	public boolean editsBlocks()
	{
		return true;
	}

}
