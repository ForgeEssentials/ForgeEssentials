package com.ForgeEssentials.WorldControl.commands;

//Depreciated
import net.minecraft.entity.player.EntityPlayer;

import com.ForgeEssentials.WorldControl.TickTasks.TickTaskLoadBlockArray;
import com.ForgeEssentials.core.PlayerInfo;
import com.ForgeEssentials.util.BlockArray;
import com.ForgeEssentials.util.Localization;
import com.ForgeEssentials.util.OutputHandler;
import com.ForgeEssentials.util.TickTaskHandler;

public class CommandRedo extends WorldControlCommandBase
{

	public CommandRedo()
	{
		super(true);
	}
	
	@Override
	public String getCommandPerm()
	{
		return "ForgeEssentials.WorldControl.backup";
	}

	@Override
	public String getName()
	{
		return "redo";
	}

	@Override
	public void processCommandPlayer(EntityPlayer player, String[] args)
	{
		BlockArray back = PlayerInfo.getPlayerInfo(player.username).getNextRedo();

		if (back == null)
		{
			OutputHandler.chatError(player, Localization.get(Localization.WC_NOREDO));
			return;
		}

		TickTaskHandler.addTask(new TickTaskLoadBlockArray(player, back));

		player.sendChatToPlayer("Working on redo");
	}
}
