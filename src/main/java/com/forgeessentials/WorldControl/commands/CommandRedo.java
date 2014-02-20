package com.forgeessentials.WorldControl.commands;

//Depreciated

import net.minecraft.entity.player.EntityPlayer;

import com.forgeessentials.WorldControl.TickTasks.TickTaskSetBackup;
import com.forgeessentials.core.PlayerInfo;
import com.forgeessentials.util.BackupArea;
import com.forgeessentials.util.ChatUtils;
import com.forgeessentials.util.Localization;
import com.forgeessentials.util.OutputHandler;
import com.forgeessentials.util.tasks.TaskRegistry;

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
		BackupArea back = PlayerInfo.getPlayerInfo(player.username).getNextRedo();

		if (back == null)
		{
			OutputHandler.chatError(player, Localization.get(Localization.WC_NOREDO));
			return;
		}

		TaskRegistry.registerTask(new TickTaskSetBackup(player, back, true));

		ChatUtils.sendMessage(player, "Working on redo");
	}

	@Override
	public int compareTo(Object o) {
		// TODO Auto-generated method stub
		return 0;
	}
}
