package com.ForgeEssentials.WorldControl.tickTasks;

//Depreciated
import net.minecraft.src.EntityPlayer;
import net.minecraft.src.ItemStack;

import com.ForgeEssentials.AreaSelector.AreaBase;
import com.ForgeEssentials.AreaSelector.Point;
import com.ForgeEssentials.WorldControl.BackupArea;
import com.ForgeEssentials.WorldControl.BlockSaveable;
import com.ForgeEssentials.core.OutputHandler;
import com.ForgeEssentials.core.PlayerInfo;

public class TickTaskSetSelection implements ITickTask
{
	// stuff needed
	private final int		blockID;
	private final int		metadata;
	private BackupArea		back;
	private EntityPlayer	player;

	// actually used
	private Point			last;
	private Point			current;
	private int				changed;

	public TickTaskSetSelection(EntityPlayer player, int blockID, int metadata, BackupArea back, AreaBase area)
	{
		this.player = player;
		this.blockID = blockID;
		this.metadata = metadata;
		this.back = back;
		last = area.getHighPoint();
		current = area.getLowPoint();
	}

	@Override
	public void tick()
	{
		int lastChanged = changed;
		
		for (int x = current.x; x <= last.x; x++)
			for (int z = current.z; z <= last.z; z++)
				for (int y = current.y; y <= last.y; y++)
				{
					current = new Point(x, y, z);
					
					if (metadata == -1)
					{
						if (blockID == player.worldObj.getBlockId(x, y, z))
							continue;

						back.before.add(new BlockSaveable(player.worldObj, x, y, z));
						player.worldObj.setBlock(x, y, z, blockID);
						back.after.add(new BlockSaveable(player.worldObj, x, y, z));
						changed++;
					}
					else
					{
						if (blockID == player.worldObj.getBlockId(x, y, z) && metadata == player.worldObj.getBlockMetadata(x, y, z))
							continue;

						back.before.add(new BlockSaveable(player.worldObj, x, y, z));
						player.worldObj.setBlockAndMetadata(x, y, z, blockID, metadata);
						back.after.add(new BlockSaveable(player.worldObj, x, y, z));
						changed++;
					}
					
					if (lastChanged >= 20)
						return;
				}
	}

	@Override
	public void onComplete()
	{
		PlayerInfo.getPlayerInfo(player).addUndoAction(back);
		OutputHandler.chatConfirmation(player, "Set " + changed + " Blocks to " + (blockID == 0 ? "Air" : new ItemStack(blockID, 1, metadata).getDisplayName()));
	}

	@Override
	public boolean isComplete()
	{
		return last.equals(current);
	}

	@Override
	public boolean editsBlocks()
	{
		return true;
	}

}
