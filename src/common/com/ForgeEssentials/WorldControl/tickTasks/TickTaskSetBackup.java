package com.ForgeEssentials.WorldControl.tickTasks;

//Depreciated
import java.util.ArrayList;

import net.minecraft.src.EntityPlayer;
import net.minecraft.src.ItemStack;

import com.ForgeEssentials.AreaSelector.AreaBase;
import com.ForgeEssentials.AreaSelector.Point;
import com.ForgeEssentials.WorldControl.BackupArea;
import com.ForgeEssentials.WorldControl.BlockSaveable;
import com.ForgeEssentials.core.OutputHandler;
import com.ForgeEssentials.core.PlayerInfo;

public class TickTaskSetBackup implements ITickTask
{
	// stuff needed
	private final EntityPlayer			player;
	private final boolean				redo;		// true = redo. // false = undo

	// actually used
	private final int					last;
	private int							current;
	private int							changed;
	private ArrayList<BlockSaveable>	list;

	/**
	 * 
	 * @param player
	 * @param back BackupArea
	 * @param before true = redo -- false = undo
	 */
	public TickTaskSetBackup(EntityPlayer player, BackupArea back, boolean redo)
	{
		this.player = player;
		this.redo = redo;

		if (redo)
			list = back.after;
		else
			list = back.before;

		last = list.size()-1;
	}

	@Override
	public void tick()
	{
		int lastChanged = changed;
		
		for (int i = current; i <= last; i++ )
		{
			current = i;
			
			if (list.get(i).setinWorld(player.worldObj))
				changed++;
			
			if (lastChanged >= 20)
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
