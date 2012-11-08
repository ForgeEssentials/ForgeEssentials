package com.ForgeEssentials.commands;

import com.ForgeEssentials.AreaSelector.Point;
import com.ForgeEssentials.core.OutputHandler;
import com.ForgeEssentials.core.PlayerInfo;
import com.ForgeEssentials.core.commands.ForgeEssentialsCommandBase;

import net.minecraft.src.EntityPlayer;
import net.minecraft.src.EntityPlayerMP;
import net.minecraft.src.ICommandSender;

public class CommandHome extends ForgeEssentialsCommandBase
{

	@Override
	public String getCommandName()
	{
		return "home";
	}

	@Override
	public void processCommandPlayer(EntityPlayer player, String[] args)
	{
		if (args.length >= 1 && args[0].equals("here"))
			PlayerInfo.getPlayerInfo(player).home = new Point((int) player.posX, (int) player.posY, (int) player.posZ);
		else if (args.length >= 3)
		{
			try
			{
				PlayerInfo.getPlayerInfo(player).home = new Point(new Integer(args[0]), new Integer(args[1]), new Integer(args[2]));
			}
			catch (NumberFormatException e)
			{
				OutputHandler.chatError(player, "That won't work. try " + getSyntaxPlayer(player));
			}
		}
		else
		{
			Point home = PlayerInfo.getPlayerInfo(player).home;
			if (home == null)
				OutputHandler.chatError(player, "No home set. Try " + getSyntaxPlayer(player));
			else
				((EntityPlayerMP) player).playerNetServerHandler.setPlayerLocation(home.x, home.y, home.z, player.rotationYaw, player.rotationPitch);
		}
	}

	@Override
	public void processCommandConsole(ICommandSender sender, String[] args)
	{
	}

	@Override
	public String getSyntaxConsole()
	{
		return null;
	}

	@Override
	public String getSyntaxPlayer(EntityPlayer player)
	{
		return "/home [here|<x> <y> <z>]";
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
	public String getInfoConsole()
	{
		return null;
	}

	@Override
	public String getInfoPlayer(EntityPlayer player)
	{
		return "Teleports you to/sets your home";
	}

}
