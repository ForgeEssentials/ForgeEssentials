package com.ForgeEssentials.commands;

import net.minecraft.src.EntityPlayerMP;
import net.minecraft.src.EntityPlayer;
import net.minecraft.src.ICommandSender;

import com.ForgeEssentials.core.commands.ForgeEssentialsCommandBase;
import com.ForgeEssentials.network.PacketCommandServerDo;

import cpw.mods.fml.common.network.PacketDispatcher;

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
		PacketDispatcher.sendPacketToServer(new PacketCommandServerDo(player, args));
	}

	@Override
	public String getSyntaxPlayer(EntityPlayer player)
	{
		return "/serverdo <command> [command args]";
	}

	@Override
	public String getInfoPlayer(EntityPlayer player)
	{
		return "Runs command with any args as though it had been typed from the server console.";
	}

	@Override
	public boolean canConsoleUseCommand()
	{
		return false;
	}

	@Override
	public boolean canPlayerUseCommand(EntityPlayer player)
	{
		return true;
	}

	@Override
	public void processCommandConsole(ICommandSender sender, String[] args)
	{
		// Does nothing on the console.
	}

	@Override
	public String getSyntaxConsole()
	{
		// Not meant to be run on the console.
		return null;
	}

	@Override
	public String getInfoConsole()
	{
		// Not meant to be run on the console.
		return null;
	}
}
