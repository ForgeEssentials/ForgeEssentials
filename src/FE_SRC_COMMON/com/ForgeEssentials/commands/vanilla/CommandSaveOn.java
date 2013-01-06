package com.ForgeEssentials.commands.vanilla;

import net.minecraft.command.CommandServerSaveOn;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayer;

import com.ForgeEssentials.permission.PermissionsAPI;
import com.ForgeEssentials.permission.query.PermQueryPlayer;

public class CommandSaveOn extends CommandServerSaveOn
{

	@Override
	public boolean canCommandSenderUseCommand(ICommandSender sender)
	{
		if (sender instanceof EntityPlayer)
		{
			return PermissionsAPI.checkPermAllowed(new PermQueryPlayer(
					(EntityPlayer) sender, getCommandPerm()));
		} else
		{
			return true;
		}
	}

	public String getCommandPerm()
	{
		return "ForgeEssentials.BasicCommands." + getCommandName();
	}
}
