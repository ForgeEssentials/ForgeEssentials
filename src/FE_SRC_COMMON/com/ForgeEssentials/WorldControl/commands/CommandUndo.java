package com.ForgeEssentials.WorldControl.commands;

//Depreciated
import com.ForgeEssentials.WorldControl.TickTasks.TickTaskSetBackup;
import com.ForgeEssentials.core.PlayerInfo;
import com.ForgeEssentials.util.BackupArea;
import com.ForgeEssentials.util.Localization;
import com.ForgeEssentials.util.OutputHandler;
import com.ForgeEssentials.util.TickTaskHandler;

import net.minecraft.entity.player.EntityPlayer;

public class CommandUndo extends WorldControlCommandBase
{

	public CommandUndo()
	{
		super(true);
	}

	@Override
	public String getName()
	{
		return "undo";
	}

	@Override
	public void processCommandPlayer(EntityPlayer player, String[] args)
	{
		BackupArea back = PlayerInfo.getPlayerInfo(player.username).getNextUndo();

		if (back == null)
		{
			OutputHandler.chatError(player, Localization.get(Localization.WC_NOUNDO));
			return;
		}

		TickTaskHandler.addTask(new TickTaskSetBackup(player, back, false));
		
		player.sendChatToPlayer("Working on undo.");
	}
}
