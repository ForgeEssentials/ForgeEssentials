package com.ForgeEssentials.WorldControl.tickTasks;

import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;

import com.ForgeEssentials.WorldControl.BackupArea;
import com.ForgeEssentials.WorldControl.BlockSaveable;
import com.ForgeEssentials.WorldControl.ModuleWorldControl;
import com.ForgeEssentials.core.PlayerInfo;
import com.ForgeEssentials.util.Localization;
import com.ForgeEssentials.util.OutputHandler;
import com.ForgeEssentials.util.AreaSelector.Point;
import com.ForgeEssentials.util.AreaSelector.Selection;

public class TickTaskReplaceSelection implements ITickTask
{
	private BackupArea		backup;
	private EntityPlayer	player;
	private int changed;
	private int ticks;
	
	// Stores our actual task.
	private int targetId;
	private int targetMeta;
	private int newId;
	private int newMeta;
	
	// Defines our bounds and current position
	private Point high;
	private Point current;
	private Point first;
	private boolean isComplete;

	public TickTaskReplaceSelection(EntityPlayer player, int firstID, int firstMeta, int secondID, int secondMeta, BackupArea backupArea, Selection selection)
	{
		this.targetId = firstID;
		this.targetMeta = firstMeta;
		this.newId = secondID;
		this.newMeta = secondMeta;
		
		changed = 0;
		high = selection.getHighPoint();
		first = current = selection.getLowPoint();
		
		this.backup = backupArea;
		this.player = player;
	}

	@Override
	public void tick()
	{
		this.ticks++;
		int currentTickChanged = 0;
		boolean continueFlag = true;
		
		int x = current.x;
		int y = current.y;
		int z = current.z;
		
		while (continueFlag)
		{
			if (targetMeta == -1)
			{
				if (targetId == player.worldObj.getBlockId(x, y, z))
				{
					doReplace(x, y, z);
					currentTickChanged++; 
				}
			}
			else
			{
				if (targetId == player.worldObj.getBlockId(x, y, z) && targetMeta == player.worldObj.getBlockMetadata(x, y, z))
				{
					doReplace(x, y, z);
					currentTickChanged++;
				}
			}
			
			y++;
			if (y > high.y)
			{
				// Reset y, increment z.
				y = first.y;
				z++;
				
				if (z > high.z)
				{
					// Reset z, increment x.
					z = first.z;
					x++;
					
					// Check stop condition
					if (x > high.x)
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

	private void doReplace(int x, int y, int z)
	{
		backup.before.add(new BlockSaveable(player.worldObj, x, y, z));
		player.worldObj.setBlockAndMetadata(x, y, z, newId, newMeta);
		backup.after.add(new BlockSaveable(player.worldObj, x, y, z));
	}

	@Override
	public void onComplete()
	{
		PlayerInfo.getPlayerInfo(player).addUndoAction(backup);
		String targetName;
		// Determine the target block name
		if (targetId == 0)
		{
			targetName = Localization.get("tile.air.name");
		}
		else
		{
			if (targetMeta == -1)
			{
				targetName = new ItemStack(Block.blocksList[targetId]).getDisplayName();
			}
			else
			{
				targetName = new ItemStack(targetId, 1, targetMeta).getDisplayName();
			}
		}
		String newName;
		// Determine the new block name.
		if (newId == 0)
		{
			newName = Localization.get("tile.air.name");
		}
		else
		{
			if (newMeta == -1)
			{
				newName = new ItemStack(Block.blocksList[newId]).getDisplayName();
			}
			else
			{
				newName = new ItemStack(newId, 1, newMeta).getDisplayName();
			}
		}		
		OutputHandler.chatConfirmation(player, Localization.format("message.wc.replaceConfirmBlocksChanged",
				changed, targetName, newName));
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

}
