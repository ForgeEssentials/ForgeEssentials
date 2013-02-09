package com.ForgeEssentials.WorldControl.TickTasks;

import java.util.ArrayList;

import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;

import com.ForgeEssentials.WorldControl.ConfigWorldControl;
import com.ForgeEssentials.core.PlayerInfo;
import com.ForgeEssentials.util.BackupArea;
import com.ForgeEssentials.util.BlockSaveable;
import com.ForgeEssentials.util.ITickTask;
import com.ForgeEssentials.util.Localization;
import com.ForgeEssentials.util.OutputHandler;
import com.ForgeEssentials.util.AreaSelector.AreaBase;
import com.ForgeEssentials.util.AreaSelector.Point;
import com.ForgeEssentials.util.AreaSelector.Selection;

public class TickTaskReplaceSelection implements ITickTask
{
	private BackupArea backup;
	private EntityPlayer player;
	private int changed;
	private int ticks;
	private ArrayList<AreaBase> applicable;

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
		targetId = firstID;
		targetMeta = firstMeta;
		newId = secondID;
		newMeta = secondMeta;

		changed = 0;
		high = selection.getHighPoint();
		first = current = selection.getLowPoint();

		backup = backupArea;
		this.player = player;
	}

	public TickTaskReplaceSelection(EntityPlayer player, int firstID, int firstMeta, int secondID, int secondMeta, BackupArea backupArea, Selection selection,
			ArrayList<AreaBase> applicable)
	{
		this(player, firstID, firstMeta, secondID, secondMeta, backupArea, selection);
		this.applicable = applicable;
	}

	@Override
	public void tick()
	{
		ticks++;
		int currentTickChanged = 0;
		boolean continueFlag = true;

		int x = current.x;
		int y = current.y;
		int z = current.z;

		while (continueFlag)
		{
			if (targetMeta == -1)
			{
				if (targetId == player.worldObj.getBlockId(x, y, z) && isApplicable(x, y, z))
				{
					doReplace(x, y, z);
					currentTickChanged++;
				}
			}
			else
			{
				if (targetId == player.worldObj.getBlockId(x, y, z) && targetMeta == player.worldObj.getBlockMetadata(x, y, z) && isApplicable(x, y, z))
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
						isComplete = true;
					}
				}
			}

			if (isComplete || currentTickChanged >= ConfigWorldControl.blocksPerTick)
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
		PlayerInfo.getPlayerInfo(player.username).addUndoAction(backup);
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
		OutputHandler.chatConfirmation(player, Localization.format("message.wc.replaceConfirmBlocksChanged", changed, targetName, newName));
	}

	@Override
	public boolean isComplete()
	{
		return isComplete;
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
		{
			return true;
		}

		boolean contains = false;

		for (AreaBase area : applicable)
		{
			contains = area.contains(p);
			if (contains)
			{
				return true;
			}
		}

		return contains;
	}

}
