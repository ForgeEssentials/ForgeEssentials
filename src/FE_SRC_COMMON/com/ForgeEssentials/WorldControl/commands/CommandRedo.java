package com.ForgeEssentials.WorldControl.commands;

//Depreciated
import com.ForgeEssentials.WorldControl.TickTasks.TickTaskSetBackup;
import com.ForgeEssentials.core.PlayerInfo;
import com.ForgeEssentials.util.BackupArea;
import com.ForgeEssentials.util.Localization;
import com.ForgeEssentials.util.OutputHandler;
import com.ForgeEssentials.util.TickTaskHandler;

import net.minecraft.entity.player.EntityPlayer;

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

		if (back == null)
		{
			OutputHandler.chatError(player, Localization.get(Localization.WC_NOREDO));
			return;
		}

		TickTaskHandler.addTask(new TickTaskSetBackup(player, back, true));
		
		player.sendChatToPlayer("Working on redo");
	}
}
