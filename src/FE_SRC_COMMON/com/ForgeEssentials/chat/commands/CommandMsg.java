package com.ForgeEssentials.chat.commands;

import java.util.HashMap;
import java.util.Map;

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

public class CommandMsg extends ForgeEssentialsCommandBase
{
	private static Map<String, String> playerReply;
	
	public CommandMsg()
	{
		super();
		playerReply = new HashMap<String, String>();
	}
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
				if(playerReply.containsKey(sender.getCommandSenderName()))
					playerReply.remove(sender.getCommandSenderName());
				if(playerReply.containsKey("server"))
					playerReply.remove("server");
				playerReply.put(sender.getCommandSenderName(), "server");
				playerReply.put("server", sender.getCommandSenderName());
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
				if(playerReply.containsKey(sender.getCommandSenderName()))
					playerReply.remove(sender.getCommandSenderName());
				if(playerReply.containsKey(receiver.getCommandSenderName()))
					playerReply.remove(receiver.getCommandSenderName());
				playerReply.put(sender.getCommandSenderName(), receiver.getCommandSenderName());
				playerReply.put(receiver.getCommandSenderName(), sender.getCommandSenderName());
				String senderMessage = FEChatFormatCodes.GOLD + "[ me -> " + FEChatFormatCodes.GREY + sender.getCommandSenderName() + FEChatFormatCodes.GOLD + "] " + FEChatFormatCodes.WHITE;
				String receiverMessage = FEChatFormatCodes.GOLD + "[" + FEChatFormatCodes.GREY + sender.getCommandSenderName() + FEChatFormatCodes.GOLD + " -> me ] " + FEChatFormatCodes.WHITE;
				for(int i = 1; i < args.length; i++)
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
				sender.sendChatToPlayer(args[0] + " is not a valid username");
				return;
			}
			else
			{
				if(playerReply.containsKey(receiver.getCommandSenderName()))
					playerReply.remove(receiver.getCommandSenderName());
				if(playerReply.containsKey("server"))
					playerReply.remove("server");
				playerReply.put(receiver.getCommandSenderName(), "server");
				playerReply.put("server", receiver.getCommandSenderName());
				String senderMessage = "[ me -> " + receiver.getCommandSenderName() + "] ";
				String receiverMessage = FEChatFormatCodes.GOLD + "[" + FEChatFormatCodes.PURPLE + "Server" + FEChatFormatCodes.GOLD + " -> me ] " + FEChatFormatCodes.GREY;
				for(int i = 1; i < args.length; i++)
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
		return "ForgeEssentials.Chat.commands." + getCommandName();
	}
	
	public static String getPlayerReply(String player)
	{
		return playerReply.get(player);
	}
}
