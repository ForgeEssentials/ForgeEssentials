package com.ForgeEssentials.commands;

import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.server.MinecraftServer;

import com.ForgeEssentials.api.permissions.PermissionsAPI;
import com.ForgeEssentials.api.permissions.query.PermQueryPlayer;
import com.ForgeEssentials.core.commands.ForgeEssentialsCommandBase;

import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.relauncher.Side;

public class CommandServerDo extends ForgeEssentialsCommandBase
{

	@Override
	public String getCommandName()
	{
		return "serverdo";
	}

	@Override
	public void processCommandPlayer(EntityPlayer player, String[] args)
	{
		if (FMLCommonHandler.instance().getEffectiveSide() == Side.SERVER && args.length >= 1)
		{
			String cmd = args[0];
			for (int i = 1; i < args.length; ++i)
			{
				cmd = cmd + " " + args[i];
			}
			String result = MinecraftServer.getServer().executeCommand(cmd);
			player.sendChatToPlayer(result);
		}
	}

	@Override
	public boolean canConsoleUseCommand()
	{
		return false;
	}

	/**
	 * Restricts the usage of this command to ops so random jerkbags can't op
	 * themselves. Once our permissions system gets working, we can use
	 * canPlayerUseCommand instead.
	 */
	@Override
	public int getRequiredPermissionLevel()
	{
		return 3;
	}

	@Override
	public void processCommandConsole(ICommandSender sender, String[] args)
	{
		// Does nothing on the console.
	}

	@Override
	public boolean canPlayerUseCommand(EntityPlayer player)
	{
		return PermissionsAPI.checkPermAllowed(new PermQueryPlayer(player, getCommandPerm()));
	}

	@Override
	public String getCommandPerm()
	{
		return "ForgeEssentials.BasicCommands." + getCommandName();
	}
}
