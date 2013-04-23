package com.ForgeEssentials.WorldControl.TickTasks;

import java.util.ArrayList;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;

import com.ForgeEssentials.core.PlayerInfo;
import com.ForgeEssentials.util.BlockSaveable;
import com.ForgeEssentials.util.OutputHandler;
import com.ForgeEssentials.util.tasks.ITickTask;
import com.ForgeEssentials.util.tasks.TaskRegistry;

public class TickTaskPulseHelper implements ITickTask
{
	// stuff needed
		private final EntityPlayer			player;
		// actually used
		private final int					timeToLive;
		private int							ticks = 0;
		private ArrayList<BlockSaveable>	list;

	public TickTaskPulseHelper(EntityPlayerMP dummy, int ticks)
	{
		player = dummy;
		timeToLive = ticks;
	}

	@Override
	public void tick()
	{
		if (ticks == timeToLive){
			TaskRegistry.registerTask(new TickTaskSetBackup(player, PlayerInfo.getPlayerInfo(player.username).getNextUndo(), false));
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
