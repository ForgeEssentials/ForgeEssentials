package com.ForgeEssentials.WorldControl.commands;

//Depreciated
import net.minecraft.entity.player.EntityPlayer;

import com.ForgeEssentials.WorldControl.BackupArea;
import com.ForgeEssentials.WorldControl.TickTasks.TickTaskSetBackup;
import com.ForgeEssentials.core.PlayerInfo;
import com.ForgeEssentials.util.TickTaskHandler;

public class CommandRedo extends WorldControlCommandBase
{

	public CommandRedo()
	{
		super(true);
	}

	@Override
	public String getName()
	{
		return "redo";
	}

	@Override
	public void processCommandPlayer(EntityPlayer player, String[] args)
	{
		BackupArea back = PlayerInfo.getPlayerInfo(player).getNextRedo();
		TickTaskHandler.addTask(new TickTaskSetBackup(player, back, true));
	}
}
