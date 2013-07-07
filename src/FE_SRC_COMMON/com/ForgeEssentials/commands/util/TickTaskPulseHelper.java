package com.ForgeEssentials.commands.util;

import net.minecraft.world.World;

import com.ForgeEssentials.api.AreaSelector.Point;
import com.ForgeEssentials.util.BackupArea;
import com.ForgeEssentials.util.BlockSaveable;
import com.ForgeEssentials.util.tasks.ITickTask;


public class TickTaskPulseHelper implements ITickTask
{
	// stuff needed
	private final World WorldObj;
		private final Point			BlockCord;
		// actually used
		private final int					timeToLive;
		private int							ticks = 0;
		private BackupArea	back = new BackupArea();

	public TickTaskPulseHelper(World worlObj, Point Cordniates, int ticks)
	{
		WorldObj = worlObj;
		BlockCord = Cordniates;
		timeToLive = ticks;
	}

	@Override
	public void tick()
	{
		if (ticks == 0){
			//place block
			back.before.add(new BlockSaveable(WorldObj, BlockCord.x, BlockCord.y, BlockCord.z));
			WorldObj.setBlock(BlockCord.x, BlockCord.y, BlockCord.z, 152);
			back.after.add(new BlockSaveable(WorldObj, BlockCord.x, BlockCord.y, BlockCord.z));
			
		}else if (ticks == timeToLive){			
				back.before.get(0).setinWorld(WorldObj);			
			ticks = -1;
			return;
		}
		ticks++;
	}

	@Override
	public void onComplete()
	{
		

	}

	@Override
	public boolean isComplete()
	{
		// TODO Auto-generated method stub
		return ticks == -1;
	}

	@Override
	public boolean editsBlocks()
	{
		// TODO Auto-generated method stub
		return false;
	}

}
