package com.ForgeEssentials.WorldControl.commands;

//Depreciated
import net.minecraft.src.EntityPlayer;

import com.ForgeEssentials.WorldControl.BackupArea;
import com.ForgeEssentials.WorldControl.tickTasks.TickTaskHandler;
import com.ForgeEssentials.WorldControl.tickTasks.TickTaskSetBackup;
import com.ForgeEssentials.core.PlayerInfo;

public class CommandRedo extends WorldControlCommandBase
{

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

	@Override
	public boolean canPlayerUseCommand(EntityPlayer player)
	{
		// TODO: check permissions.
		return true;
	}

	@Override
	public String getSyntaxPlayer(EntityPlayer player)
	{
		return "/" + getCommandName();
	}

	@Override
	public String getInfoPlayer(EntityPlayer player)
	{
		return "Redos the last WorldControl action";
	}
}
