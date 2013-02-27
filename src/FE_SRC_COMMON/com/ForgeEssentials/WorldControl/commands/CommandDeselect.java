package com.ForgeEssentials.WorldControl.commands;

import net.minecraft.entity.player.EntityPlayer;

import com.ForgeEssentials.core.PlayerInfo;
import com.ForgeEssentials.util.Localization;
import com.ForgeEssentials.util.OutputHandler;

public class CommandDeselect extends WorldControlCommandBase
{

	public CommandDeselect()
	{
		super(true);
		aliasList.add("/desel");
	}

	@Override
	public String getName()
	{
		return "deselect";
	}

	@Override
	public void processCommandPlayer(EntityPlayer sender, String[] args)
	{
		PlayerInfo info = PlayerInfo.getPlayerInfo(sender.username);
		info.clearSelection();

		OutputHandler.chatConfirmation(sender, Localization.get(Localization.COMMAND_DESELECT));
	}

	@Override
	public String getSyntaxPlayer(EntityPlayer player)
	{
		return "/" + getCommandName();
	}

	@Override
	public String getInfoPlayer(EntityPlayer player)
	{
		return "Clears the currently selected area";
	}
	
	@Override
	public String getCommandPerm()
	{
		return "ForgeEssentials.WorldControl.selection";
	}
}
