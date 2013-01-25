package com.ForgeEssentials.permission.mcoverride;

import net.minecraft.command.CommandServerSaveOff;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayer;

import com.ForgeEssentials.permission.APIHelper;
import com.ForgeEssentials.permission.query.PermQueryPlayer;

public class CommandSaveOff extends CommandServerSaveOff
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
