package com.forgeessentials.core.commands.selections;

import java.util.Arrays;
import java.util.List;

import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayer;

import com.forgeessentials.core.PlayerInfo;
import com.forgeessentials.core.commands.ForgeEssentialsCommandBase;
import com.forgeessentials.util.Localization;
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
		return Arrays.asList("/fedeselect");
	}

	@Override
	public void processCommandPlayer(EntityPlayer sender, String[] args)
	{
		PlayerInfo info = PlayerInfo.getPlayerInfo(sender.username);
		info.clearSelection();

		OutputHandler.chatConfirmation(sender, Localization.get(Localization.COMMAND_DESELECT));
	}

	@Override
	public void processCommandConsole(ICommandSender sender, String[] args)
	{
	}

	@Override
	public boolean canConsoleUseCommand()
	{
		return false;
	}

	@Override
	public String getCommandPerm()
	{
		return "ForgeEssentials.CoreCommands.select.deselect";
	}

	@Override
	public List<?> addTabCompletionOptions(ICommandSender sender, String[] args)
	{
		return null;
	}

	@Override
	public int compareTo(Object o) {
		// TODO Auto-generated method stub
		return 0;
	}
}
