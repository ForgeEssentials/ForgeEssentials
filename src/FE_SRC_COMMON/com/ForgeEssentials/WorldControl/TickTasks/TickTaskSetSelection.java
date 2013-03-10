package com.ForgeEssentials.WorldControl.TickTasks;

//Depreciated
import java.util.ArrayList;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;

import com.ForgeEssentials.WorldControl.ConfigWorldControl;
import com.ForgeEssentials.core.PlayerInfo;
import com.ForgeEssentials.util.BackupArea;
import com.ForgeEssentials.util.BlockSaveable;
import com.ForgeEssentials.util.Localization;
import com.ForgeEssentials.util.OutputHandler;
import com.ForgeEssentials.util.AreaSelector.AreaBase;
import com.ForgeEssentials.util.AreaSelector.Point;
import com.ForgeEssentials.util.tasks.ITickTask;

public class TickTaskSetSelection implements ITickTask
{
	// stuff needed
	private final int			blockID;
	private final int			metadata;
	private BackupArea			back;
	private EntityPlayer		player;
	private ArrayList<AreaBase>	applicable;

	// actually used
	private Point				first;
	private Point				last;
	private Point				current;
	private int					changed;
	private boolean				isComplete;

	public TickTaskSetSelection(EntityPlayer player, int blockID, int metadata, BackupArea back, AreaBase area)
	{
		this.player = player;
		this.blockID = blockID;
		this.metadata = metadata;
		this.back = back;
		last = area.getHighPoint();
		first = current = area.getLowPoint();

		isComplete = false;
	}

	public TickTaskSetSelection(EntityPlayer player, int blockID, int metadata, BackupArea back, AreaBase area, ArrayList<AreaBase> appliccable)
	{
		this(player, blockID, metadata, back, area);
		applicable = appliccable;
	}

	@Override
	public void tick()
	{
		int currentTickChanged = 0;
		boolean continueFlag = true;

		int x = current.x;
		int y = current.y;
		int z = current.z;

		while (continueFlag)
		{
			if (metadata == -1)
			{
				if (blockID != player.worldObj.getBlockId(x, y, z) && isApplicable(x, y, z))
				{
					back.before.add(new BlockSaveable(player.worldObj, x, y, z));
					player.worldObj.func_94575_c(x, y, z, blockID);
					back.after.add(new BlockSaveable(player.worldObj, x, y, z));
					currentTickChanged++;
				}
			}
			else
			{
				if ((blockID != player.worldObj.getBlockId(x, y, z) || metadata != player.worldObj.getBlockMetadata(x, y, z)) && isApplicable(x, y, z))
				{
					back.before.add(new BlockSaveable(player.worldObj, x, y, z));
					player.worldObj.setBlockAndMetadataWithNotify(x, y, z, blockID, metadata, 3);
					back.after.add(new BlockSaveable(player.worldObj, x, y, z));
					currentTickChanged++;
				}
			}

			y++;
			// Bounds checking comes first to avoid fencepost errors.
			if (y > last.y)
			{
				// Reset y, increment z.
				y = first.y;
				z++;

				if (z > last.z)
				{
					// Reset z, increment x.
					z = first.z;
					x++;

					// Check stop condition
					if (x > last.x)
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

	@Override
	public void onComplete()
	{
		PlayerInfo.getPlayerInfo(player.username).addUndoAction(back);
		String blockName = blockID + ":" + metadata;

		if (blockID == 0)
		{
			blockName = Localization.get("tile.air.name");
		}
		else
		{
			try
			{
				blockName = new ItemStack(blockID, 1, metadata).getDisplayName();
			}
			catch (Exception e)
			{
				blockName = blockID + ":" + metadata;
				OutputHandler.info("Could not retrieve the name of the block represented by ID " + blockID + " with meta " + metadata
						+ ". This is a problem in the mod that provides the block, caused by not supporting getDisplayName for their block.");
			}
		}

		OutputHandler.chatConfirmation(player, Localization.format("message.wc.setConfirmBlocksChanged", changed, blockName));
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
