package com.ForgeEssentials.WorldControl.TickTasks;

//Depreciated
import java.util.ArrayList;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.MathHelper;

import com.ForgeEssentials.WorldControl.BlockArray;
import com.ForgeEssentials.WorldControl.ConfigWorldControl;
import com.ForgeEssentials.util.BackupArea;
import com.ForgeEssentials.util.BlockSaveable;
import com.ForgeEssentials.util.ITickTask;
import com.ForgeEssentials.util.OutputHandler;

public class TickTaskLoadBlockArray implements ITickTask
{
	// stuff needed
	private final EntityPlayer			player;

	// actually used
	private final int					last;
	private int							current;
	private int							changed;
	private BlockArray back;
	private ArrayList<BlockArray.LoadingBlock> blocksToLoad;

	/**
	 * 
	 * @param player
	 * @param back
	 * BackupArea
	 * @param before
	 * true = redo -- false = undo
	 */
	public TickTaskLoadBlockArray(EntityPlayer player, BlockArray back)
	{
		this.player = player;
		this.back = back;
		blocksToLoad = this.back.getBlocksToLoad();
		last = back.sizeX * back.sizeY * back.sizeZ;
	}

	@Override
	public void tick()
	{
		int lastChanged = changed;
		
		int xzBlocksSquared = back.sizeX * back.sizeZ;
		
		int x = 0;
		int z = 0;
		int y = 0;
		

		for (int i = current; i <= last; i++)
		{
			current = i;

			if (blocksToLoad.get(i).placeBlock(player.worldObj, (back.isRelative?MathHelper.floor_double(player.posX):0) + back.offX + x, (back.isRelative?MathHelper.floor_double(player.posY):0) + back.offY + y, (back.isRelative?MathHelper.floor_double(player.posZ):0) + back.offZ + z))
			{
				changed++;
			}

			if (lastChanged >= ConfigWorldControl.blocksPerTick)
			{
				return;
			}
			x++;
			if(x>back.sizeX) {
				x = 0;
				z++;
			}
			if(z>back.sizeZ) {
				z = 0;
				y++;
			}
			if(y>back.sizeY)current = last;
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
