package com.ForgeEssentials.commands;

import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.server.MinecraftServer;

import com.ForgeEssentials.core.commands.ForgeEssentialsCommandBase;
import com.ForgeEssentials.permission.PermissionsAPI;
import com.ForgeEssentials.permission.query.PermQueryPlayer;
import com.ForgeEssentials.util.FEChatFormatCodes;
import com.ForgeEssentials.util.FunctionHelper;
import com.ForgeEssentials.util.Localization;
import com.ForgeEssentials.util.OutputHandler;

import cpw.mods.fml.common.FMLCommonHandler;

public class CommandMsg extends ForgeEssentialsCommandBase
{
	@Override
	public String getCommandName()
	{
		return "msg";
	}

	@Override
	public void processCommandPlayer(EntityPlayer sender, String[] args)
	{
		if(args.length == 0)
		{
			OutputHandler.chatError(sender, Localization.get(Localization.ERROR_BADSYNTAX) + "/msg <player> <message>");
			return;
		}
		if(args.length == 1)
		{
			OutputHandler.chatError(sender, Localization.get(Localization.ERROR_BADSYNTAX) + "/msg <player> <message>");
			return;
		}
		if(args.length > 1)
		{
			if(args[0].equalsIgnoreCase("server") || args[0].equalsIgnoreCase("console"))
			{
				String senderMessage = FEChatFormatCodes.GOLD + "[ me -> " + FEChatFormatCodes.PURPLE + "Server" + FEChatFormatCodes.GOLD + "] " + FEChatFormatCodes.GREY;
				String receiverMessage = "[" + sender.getCommandSenderName() + " -> me ] ";
				for(int i = 0; i < args.length; i++)
				{
					receiverMessage += args[i];
					senderMessage += args[i];
					if(i != args.length - 1)
					{
						receiverMessage += " ";
						senderMessage += " ";
					}
				}
				MinecraftServer.getServer().sendChatToPlayer(receiverMessage);
				sender.sendChatToPlayer(senderMessage);
			}
			else
			{
				EntityPlayerMP receiver = FunctionHelper.getPlayerFromUsername(args[0]);
				if(receiver == null)
				{
					OutputHandler.chatError(sender, args[0] + " is not a valid username");
					return;
				}
				String receiverMessage = FEChatFormatCodes.GOLD + "[ me -> " + FEChatFormatCodes.GREY + sender.getCommandSenderName() + FEChatFormatCodes.GOLD + "] " + FEChatFormatCodes.GREY;
				String senderMessage = FEChatFormatCodes.GOLD + "[" + FEChatFormatCodes.GREY + sender.getCommandSenderName() + FEChatFormatCodes.GOLD + " -> me ] " + FEChatFormatCodes.GREY;
				for(int i = 0; i < args.length; i++)
				{
					receiverMessage += args[i];
					senderMessage += args[i];
					if(i != args.length - 1)
					{
						receiverMessage += " ";
						senderMessage += " ";
					}
				}
				sender.sendChatToPlayer(senderMessage);
				receiver.sendChatToPlayer(receiverMessage);
			}
		}
	}

	@Override
	public void processCommandConsole(ICommandSender sender, String[] args)
	{
		if(args.length == 0)
		{
			sender.sendChatToPlayer(Localization.ERROR_BADSYNTAX + "/msg <player> <message>");
			return;
		}
		if(args.length == 1)
		{
			sender.sendChatToPlayer(Localization.ERROR_BADSYNTAX + "/msg <player> <message>");
			return;
		}
		if(args.length > 1)
		{
			EntityPlayer receiver = FunctionHelper.getPlayerFromUsername(args[0]);
			if(receiver == null)
			{
				sender.sendChatToPlayer(Localization.format(Localization.ERROR_NOPLAYER, args[0]));
				return;
			}
			else
			{
				String senderMessage = "[ me -> " + sender.getCommandSenderName() + "] ";
				String receiverMessage = FEChatFormatCodes.GOLD + "[" + FEChatFormatCodes.PURPLE + sender.getCommandSenderName() + FEChatFormatCodes.GOLD + " -> me ] " + FEChatFormatCodes.GREY;
				for(int i = 0; i < args.length; i++)
				{
					receiverMessage += args[i];
					senderMessage += args[i];
					if(i != args.length - 1)
					{
						receiverMessage += " ";
						senderMessage += " ";
					}
				}
			}
		}
	}

	@Override
	public boolean canConsoleUseCommand()
	{
		return true;
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
