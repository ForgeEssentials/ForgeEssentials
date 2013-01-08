package com.ForgeEssentials.permission.mcoverride;

import net.minecraft.command.CommandServerDeop;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayer;

import com.ForgeEssentials.permission.PermissionsAPI;
import com.ForgeEssentials.permission.query.PermQueryPlayer;

public class CommandDeop extends CommandServerDeop
{

	@Override
	public boolean canCommandSenderUseCommand(ICommandSender sender)
	{
		if (sender instanceof EntityPlayer)
		{
			return PermissionsAPI.checkPermAllowed(new PermQueryPlayer((EntityPlayer) sender, getCommandPerm()));
		}
		else
		{
			return true;
		}
	}

	public String getCommandPerm()
	{
		return "ForgeEssentials.BasicCommands." + getCommandName();
	}
}
