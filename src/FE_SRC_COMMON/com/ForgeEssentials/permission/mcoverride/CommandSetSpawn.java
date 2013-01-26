package com.ForgeEssentials.permission.mcoverride;

import net.minecraft.command.CommandSetSpawnpoint;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayer;

import com.ForgeEssentials.api.permissions.query.PermQueryPlayer;
import com.ForgeEssentials.permission.APIHelper;

public class CommandSetSpawn extends CommandSetSpawnpoint
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
		return "ForgeEssentials.BasicCommands." + getCommandName();
	}
}
