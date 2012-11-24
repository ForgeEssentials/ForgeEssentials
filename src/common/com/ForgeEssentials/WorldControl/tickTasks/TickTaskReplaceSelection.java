package com.ForgeEssentials.WorldControl.tickTasks;

import net.minecraft.src.EntityPlayer;

import com.ForgeEssentials.AreaSelector.Point;
import com.ForgeEssentials.AreaSelector.Selection;
import com.ForgeEssentials.WorldControl.BackupArea;
import com.ForgeEssentials.WorldControl.BlockSaveable;
import com.ForgeEssentials.WorldControl.ModuleWorldControl;
import com.ForgeEssentials.core.PlayerInfo;
import com.ForgeEssentials.util.OutputHandler;

public class TickTaskReplaceSelection implements ITickTask
{
	private BackupArea		backup;
	private EntityPlayer	player;
	
	// Stores our actual task.
	private int targetId;
	private int targetMeta;
	private int newId;
	private int newMeta;
	
	// Defines our bounds and current position
	private int changed;
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
		int currentTickChanged = 0;
		boolean continueFlag = true;
		
		int x = current.x;
		int y = current.y;
		int z = current.z;
		
		while (continueFlag && !this.isComplete)
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
		OutputHandler.chatConfirmation(player, String.format("Replace command complete. %d %s replaced.", changed, (changed == 1) ? "block" : "blocks"));
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
