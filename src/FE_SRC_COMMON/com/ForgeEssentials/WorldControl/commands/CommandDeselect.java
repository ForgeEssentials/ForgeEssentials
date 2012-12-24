package com.ForgeEssentials.WorldControl.commands;

import net.minecraft.entity.player.EntityPlayer;

import com.ForgeEssentials.core.PlayerInfo;
import com.ForgeEssentials.util.Localization;

public class CommandDeselect extends WorldControlCommandBase
{

	public CommandDeselect()
	{
		super(true);
	}

	@Override
	public String getName()
	{
		return "deselect";
	}

	@Override
	public void processCommandPlayer(EntityPlayer sender, String[] args)
	{
		PlayerInfo info = PlayerInfo.getPlayerInfo(sender);
		info.setPoint1(null);
		info.setPoint2(null);
		
		sender.sendChatToPlayer(Localization.COMMAND_DESELECT);
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

}
