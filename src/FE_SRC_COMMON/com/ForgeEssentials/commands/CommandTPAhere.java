package com.ForgeEssentials.commands;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.server.MinecraftServer;
import net.minecraftforge.common.Configuration;

import com.ForgeEssentials.commands.util.TPAdata;
import com.ForgeEssentials.commands.util.TickHandlerCommands;
import com.ForgeEssentials.core.commands.ForgeEssentialsCommandBase;
import com.ForgeEssentials.util.FunctionHelper;
import com.ForgeEssentials.util.Localization;
import com.ForgeEssentials.util.OutputHandler;
import com.ForgeEssentials.util.TeleportCenter;
import com.ForgeEssentials.util.AreaSelector.WarpPoint;

public class CommandTPAhere extends ForgeEssentialsCommandBase
{
	/*
	 * Config
	 */
	public static int	timeout	= 25;

	@Override
	public void doConfig(Configuration config, String category)
	{
		timeout = config.get(category, "timeout", 25, "Amount of sec a user has to accept a TPAhere request").getInt();
	}
	
	@Override
	public String getCommandName()
	{
		return "tpahere";
	}

	@Override
	public void processCommandPlayer(EntityPlayer sender, String[] args)
	{
		if(args.length == 0)
		{
			OutputHandler.chatError(sender, Localization.get(Localization.ERROR_BADSYNTAX));
			return;
		}
		
		if(args[0].equalsIgnoreCase("accept"))
		{
			for(TPAdata data : TickHandlerCommands.tpaList)
			{
				if(data.tphere)
				{
					if(data.receiver == sender)
					{
						data.sender.sendChatToPlayer("TPAhere accepted.");
						data.receiver.sendChatToPlayer("TPAhere accepted.");
						TickHandlerCommands.tpaListToRemove.add(data);
						TeleportCenter.addToTpQue(new WarpPoint(data.sender), data.receiver);
						return;
					}
				}
			}
			return;
		}
		
		if(args[0].equalsIgnoreCase("decline"))
		{
			for(TPAdata data : TickHandlerCommands.tpaList)
			{
				if(data.tphere)
				{
					if(data.receiver == sender)
					{
						data.sender.sendChatToPlayer("TPAhere declined.");
						data.receiver.sendChatToPlayer("TPAhere declined.");
						TickHandlerCommands.tpaListToRemove.add(data);
						return;
					}
				}
			}
			return;
		}
		
		EntityPlayerMP receiver = FunctionHelper.getPlayerFromPartialName(args[0]);
		if(receiver == null)
		{
			sender.sendChatToPlayer(args[0] + " not found.");
		}
		else
		{
			TickHandlerCommands.tpaListToAdd.add(new TPAdata((EntityPlayerMP) sender, receiver, true));
			
			sender.sendChatToPlayer("Send a TPAhere request to " + receiver.username);
			receiver.sendChatToPlayer("Got a TPAhere request from " + sender.username);	
		}
	}

	@Override
	public void processCommandConsole(ICommandSender sender, String[] args)
	{
		
	}

	@Override
	public boolean canConsoleUseCommand()
	{
		return false;
	}

	@Override
	public String getCommandPerm()
	{
		return "ForgeEssentials.BasicCommands." + getCommandName();
	}
	
	@Override
	public List addTabCompletionOptions(ICommandSender par1ICommandSender, String[] args)
	{	
		if (args.length == 1)
		{
			ArrayList<String> list = new ArrayList<String>();
			list.add("accept");
			list.add("decline");
			list.addAll(Arrays.asList(MinecraftServer.getServer().getAllUsernames()));
			return getListOfStringsFromIterableMatchingLastWord(args, list);
		}
		else
			return null;
	}
}
