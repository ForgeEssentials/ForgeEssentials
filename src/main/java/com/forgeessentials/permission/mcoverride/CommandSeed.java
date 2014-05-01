package com.forgeessentials.permission.mcoverride;

import net.minecraft.command.CommandShowSeed;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayer;

import com.forgeessentials.api.APIRegistry;
import com.forgeessentials.api.permissions.query.PermQueryPlayer;

public class CommandSeed extends CommandShowSeed
{

	@Override
	public boolean canCommandSenderUseCommand(ICommandSender sender)
	{
		if (sender instanceof EntityPlayer)
			return APIRegistry.perms.checkPermAllowed(new PermQueryPlayer((EntityPlayer) sender, "mc." + getCommandName()));
		else
			return super.canCommandSenderUseCommand(sender);
	}
}
