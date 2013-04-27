package com.ForgeEssentials.permission.mcoverride;

import net.minecraft.command.CommandServerKick;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayer;

import com.ForgeEssentials.api.permissions.PermissionsAPI;
import com.ForgeEssentials.api.permissions.query.PermQueryPlayer;

public class CommandKick extends CommandServerKick
{

	@Override
	public boolean canCommandSenderUseCommand(ICommandSender sender)
	{
		if (sender instanceof EntityPlayer)
			return PermissionsAPI.checkPermAllowed(new PermQueryPlayer((EntityPlayer) sender, "Minecraft.commands." + getCommandName()));
		else
			return true;
	}
}
