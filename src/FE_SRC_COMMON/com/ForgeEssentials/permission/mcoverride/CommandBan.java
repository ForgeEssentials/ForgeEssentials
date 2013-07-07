package com.ForgeEssentials.permission.mcoverride;

import net.minecraft.command.CommandServerBan;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayer;

import com.ForgeEssentials.api.APIRegistry;
import com.ForgeEssentials.permission.query.PermQueryPlayer;

public class CommandBan extends CommandServerBan{
	@Override
	public boolean canCommandSenderUseCommand(ICommandSender sender)
	{
		if (sender instanceof EntityPlayer)
			return APIRegistry.perms.checkPermAllowed((EntityPlayer) sender, "Minecraft.commands." + getCommandName());
		else
			return true;
	}

}
