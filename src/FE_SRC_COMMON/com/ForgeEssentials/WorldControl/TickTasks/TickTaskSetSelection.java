package com.ForgeEssentials.WorldControl.TickTasks;

//Depreciated
import java.util.ArrayList;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;

import com.ForgeEssentials.WorldControl.ModuleWorldControl;
import com.ForgeEssentials.core.PlayerInfo;
import com.ForgeEssentials.util.BackupArea;
import com.ForgeEssentials.util.BlockSaveable;
import com.ForgeEssentials.util.ITickTask;
import com.ForgeEssentials.util.Localization;
import com.ForgeEssentials.util.OutputHandler;
import com.ForgeEssentials.util.AreaSelector.AreaBase;
import com.ForgeEssentials.util.AreaSelector.Point;

public class TickTaskSetSelection implements ITickTask
{
	// stuff needed
	private final int blockID;
	private final int metadata;
	private BackupArea back;
	private EntityPlayer player;
	private ArrayList<AreaBase> applicable;

	// actually used
	private Point first;
	private Point last;
	private Point current;
	private int changed;
	private boolean isComplete;

	public TickTaskSetSelection(EntityPlayer player, int blockID, int metadata, BackupArea back, AreaBase area)
	{
		this.player = player;
		this.blockID = blockID;
		this.metadata = metadata;
		this.back = back;
		last = area.getHighPoint();
		first = current = area.getLowPoint();
		
		this.isComplete = false;
	}
	
	public TickTaskSetSelection(EntityPlayer player, int blockID, int metadata, BackupArea back, AreaBase area, ArrayList<AreaBase> appliccable)
	{
		this(player, blockID, metadata, back, area);
		this.applicable = appliccable;
	}

	@Override
	public void tick()
	{
		int currentTickChanged = 0;
		boolean continueFlag = true;
		
		int x = current.getX();
		int y = current.getY();
		int z = current.getZ();
		
		while (continueFlag)
		{
			if (metadata == -1)
			{
				if (blockID != player.worldObj.getBlockId(x, y, z) && isApplicable(x, y, z))
				{
					back.before.add(new BlockSaveable(player.worldObj, x, y, z));
					player.worldObj.setBlock(x, y, z, blockID);
					back.after.add(new BlockSaveable(player.worldObj, x, y, z));
					currentTickChanged++;
				}
			}
			else
			{
				if ((!(blockID == player.worldObj.getBlockId(x, y, z) || metadata != player.worldObj.getBlockMetadata(x, y, z)) && isApplicable(x, y, z)))
				{
					back.before.add(new BlockSaveable(player.worldObj, x, y, z));
					player.worldObj.setBlockAndMetadata(x, y, z, blockID, metadata);
					back.after.add(new BlockSaveable(player.worldObj, x, y, z));
					currentTickChanged++;
				}
			}
			
			y++;
			// Bounds checking comes first to avoid fencepost errors.
			if (y > last.getY())
			{
				// Reset y, increment z.
				y = first.getY();
				z++;
				
				if (z > last.getZ())
				{
					// Reset z, increment x.
					z = first.getZ();
					x++;
					
					// Check stop condition
					if (x > last.getX())
					{
						this.isComplete = true;
					}
				}
			}
			
			if (isComplete || currentTickChanged >= ModuleWorldControl.WCblocksPerTick)
			{
				// Stop running this tick.
				changed += currentTickChanged;
				current = new Point(x, y, z);
				continueFlag = false;
			}
		}
	}

	@Override
	public void onComplete()
	{
		PlayerInfo.getPlayerInfo(player).addUndoAction(back);
		OutputHandler.chatConfirmation(player, Localization.format("message.wc.setConfirmBlocksChanged",
				changed, 
				(blockID == 0) ? Localization.get("tile.air.name") : new ItemStack(blockID, 1, metadata).getDisplayName()));
	}

	@Override
	public boolean isComplete()
	{
		return this.isComplete;
	}

	@Override
	public boolean editsBlocks()
	{
		return true;
	}
	
	private boolean isApplicable(int x, int y, int z)
	{
		Point p = new Point(x, y, z);
		if (applicable == null)
			return true;
		
		boolean contains = false;
		
		for (AreaBase area : applicable)
		{
			contains = area.contains(p);
			if (contains)
				return true;
		}
		
		return contains;
	}

}
