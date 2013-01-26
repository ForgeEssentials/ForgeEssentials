package com.ForgeEssentials.permission.mcoverride;

import net.minecraft.command.CommandServerMessage;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayer;

import com.ForgeEssentials.api.permissions.query.PermQueryPlayer;
import com.ForgeEssentials.permission.APIHelper;

public class CommandTell extends CommandServerMessage
{

	@Override
	public boolean canCommandSenderUseCommand(ICommandSender sender)
	{
		if (sender instanceof EntityPlayer)
		{
			return APIHelper.checkPermAllowed(new PermQueryPlayer((EntityPlayer) sender, getCommandPerm()));
		}
		else
		{
			return true;
		}
	}

	public String getCommandPerm()
	{
		return "ForgeEssentials.thiswillneverseethelightofday." + getCommandName();
	}
}
