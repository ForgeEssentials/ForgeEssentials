package com.ForgeEssentials.WorldControl.commands;

//Depreciated
import net.minecraft.src.EntityPlayer;

import com.ForgeEssentials.WorldControl.BackupArea;
import com.ForgeEssentials.WorldControl.tickTasks.TickTaskHandler;
import com.ForgeEssentials.WorldControl.tickTasks.TickTaskSetBackup;
import com.ForgeEssentials.core.PlayerInfo;

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
