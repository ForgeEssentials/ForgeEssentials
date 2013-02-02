package com.ForgeEssentials.commands;

import com.ForgeEssentials.commands.util.AFKdata;
import com.ForgeEssentials.commands.util.TickHandlerCommands;
import com.ForgeEssentials.core.commands.ForgeEssentialsCommandBase;
import com.ForgeEssentials.util.FunctionHelper;
import com.ForgeEssentials.util.OutputHandler;

import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;

import net.minecraftforge.common.Configuration;

public class CommandLocate extends ForgeEssentialsCommandBase
{
	@Override
	public String getCommandName()
	{
		return "locate";
	}
	
	public String[] getDefaultAliases()
	{
		return new String[] {"gps", "loc"};
	}

	@Override
	public void processCommandPlayer(EntityPlayer sender, String[] args)
	{
		if(args.length != 1)
		{
			OutputHandler.chatError(sender, "Specity a player");
		}
		else
		{
			locate(sender, args[0]);
		}
	}

	@Override
	public void processCommandConsole(ICommandSender sender, String[] args)
	{
		if(args.length != 1)
		{
			OutputHandler.chatError(sender, "Specity a player");
		}
		else
		{
			locate(sender, args[0]);
		}
	}
	
	public void locate(ICommandSender sender, String username)
	{
		EntityPlayerMP player = FunctionHelper.getPlayerFromPartialName(username);
		if(player == null)
		{
			sender.sendChatToPlayer(username + " not found!");
		}
		else
		{
			sender.sendChatToPlayer(player.username + " is at X: " + (int)player.posX + " Y: " + (int)player.posY + " Z: " + (int)player.posZ + " in dim: " + player.dimension);
		}
	}

	@Override
	public boolean canConsoleUseCommand()
	{
		return true;
	}

	@Override
	public String getCommandPerm()
	{
		return "ForgeEssentials.BasicCommands." + getCommandName();
	}
}
