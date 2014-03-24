package com.forgeessentials.core.commands.selections;

import java.util.Arrays;
import java.util.List;

import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayer;

import com.forgeessentials.core.PlayerInfo;
import com.forgeessentials.core.commands.ForgeEssentialsCommandBase;
import com.forgeessentials.util.OutputHandler;

public class CommandDeselect extends ForgeEssentialsCommandBase
{

	@Override
	public String getCommandName()
	{
		return "/fedesel";
	}
	
	@Override
	public List<String> getCommandAliases()
	{
		return Arrays.asList("/fedeselect", "/deselect", "/sel");
	}

	@Override
	public void processCommandPlayer(EntityPlayer sender, String[] args)
	{
		PlayerInfo info = PlayerInfo.getPlayerInfo(sender.username);
		info.clearSelection();

		OutputHandler.chatConfirmation(sender, "Selection cleared.");
	}

	@Override
	public boolean canConsoleUseCommand()
	{
		return false;
	}

	@Override
	public String getCommandPerm()
	{
		return "fe.core.pos";
	}

	@Override
	public int compareTo(Object o) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public String getCommandUsage(ICommandSender sender) {
		// TODO Auto-generated method stub
		return "//fedesel Deselects the selection";
	}
}
