package com.ForgeEssentials.permission.mcoverride;

import com.ForgeEssentials.api.APIRegistry;
import com.ForgeEssentials.api.permissions.query.PermQueryPlayer;

import net.minecraft.command.CommandServerPardon;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayer;

public class CommandPardon extends CommandServerPardon{
	@Override
	public boolean canCommandSenderUseCommand(ICommandSender sender)
	{
		if (sender instanceof EntityPlayer)
			return APIRegistry.perms.checkPermAllowed(new PermQueryPlayer((EntityPlayer) sender, "Minecraft.commands." + getCommandName()));
		else
			return true;
	}

}
