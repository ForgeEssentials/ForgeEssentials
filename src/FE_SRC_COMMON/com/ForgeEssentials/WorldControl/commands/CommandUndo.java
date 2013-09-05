package com.ForgeEssentials.WorldControl.commands;

//Depreciated

import com.ForgeEssentials.util.ChatUtils;
import net.minecraft.entity.player.EntityPlayer;

import com.ForgeEssentials.WorldControl.TickTasks.TickTaskSetBackup;
import com.ForgeEssentials.core.PlayerInfo;
import com.ForgeEssentials.util.BackupArea;
import com.ForgeEssentials.util.Localization;
import com.ForgeEssentials.util.OutputHandler;
import com.ForgeEssentials.util.tasks.TaskRegistry;

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

		TaskRegistry.registerTask(new TickTaskSetBackup(player, back, false));

		ChatUtils.sendMessage(player, "Working on undo.");
	}
}
