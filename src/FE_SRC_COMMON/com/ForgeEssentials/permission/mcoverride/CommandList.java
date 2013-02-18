package com.ForgeEssentials.permission.mcoverride;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.command.CommandServerList;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayer;

import com.ForgeEssentials.api.permissions.PermissionsAPI;
import com.ForgeEssentials.api.permissions.query.PermQueryPlayer;

public class CommandList extends CommandServerList
{
	@Override
    public List getCommandAliases()
    {
		ArrayList list = new ArrayList();
		list.add("who");
		list.add("online");
		list.add("players");
        return list;
    }

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
