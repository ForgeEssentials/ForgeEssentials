package com.ForgeEssentials.permission.mcoverride;

import net.minecraft.command.CommandServerStop;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayer;

import com.ForgeEssentials.api.APIRegistry;

public class CommandStop extends CommandServerStop
{

	@Override
	public boolean canCommandSenderUseCommand(ICommandSender sender)
	{
		if (sender instanceof EntityPlayer)
			return APIRegistry.perms.checkPermAllowed((EntityPlayer) sender, "Minecraft.commands." + getCommandName());
		else
			return true;
	}
}
