package com.forgeessentials.commands;

import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.server.MinecraftServer;

import com.forgeessentials.api.permissions.RegGroup;
import com.forgeessentials.commands.util.FEcmdModuleCommands;
import com.forgeessentials.util.ChatUtils;

import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.relauncher.Side;

public class CommandServerDo extends FEcmdModuleCommands
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
			ChatUtils.sendMessage(player, result);
		}
	}

	@Override
	public boolean canConsoleUseCommand()
	{
		return false;
	}

	@Override
	public RegGroup getReggroup()
	{
		return RegGroup.OWNERS;
	}

	@Override
	public int compareTo(Object o) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public String getCommandUsage(ICommandSender sender) {
		// TODO Auto-generated method stub
		return "/serverdo Run a command as the console.";
	}
}
