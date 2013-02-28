package com.ForgeEssentials.permission.mcoverride;

import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayer;

import com.ForgeEssentials.api.permissions.PermissionsAPI;
import com.ForgeEssentials.api.permissions.query.PermQueryPlayer;

public class CommandWeather extends net.minecraft.command.CommandWeather
{

	@Override
	public boolean canCommandSenderUseCommand(ICommandSender sender)
	{
		if (sender instanceof EntityPlayer)
			return PermissionsAPI.checkPermAllowed(new PermQueryPlayer((EntityPlayer) sender, getCommandPerm()));
		else
			return true;
	}

	public String getCommandPerm()
	{
		return "ForgeEssentials.BasicCommands." + getCommandName();
	}
}
