package com.ForgeEssentials.commands;

import java.util.List;

import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;

import com.ForgeEssentials.core.commands.ForgeEssentialsCommandBase;
import com.ForgeEssentials.util.FunctionHelper;
import com.ForgeEssentials.util.OutputHandler;

public class CommandLocate extends ForgeEssentialsCommandBase
{
	@Override
	public String getCommandName()
	{
		return "locate";
	}

	@Override
	public String[] getDefaultAliases()
	{
		return new String[]
		{ "gps", "loc" };
	}

	@Override
	public void processCommandPlayer(EntityPlayer sender, String[] args)
	{
		if (args.length != 1)
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
		if (args.length != 1)
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
		if (player == null)
		{
			sender.sendChatToPlayer(username + " not found!");
		}
		else
		{
			sender.sendChatToPlayer(player.username + " is at X: " + (int) player.posX + " Y: " + (int) player.posY + " Z: " + (int) player.posZ + " in dim: " + player.dimension);
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

	@Override
	public List<?> addTabCompletionOptions(ICommandSender sender, String[] args)
	{
		// TODO Auto-generated method stub
		return null;
	}
}
