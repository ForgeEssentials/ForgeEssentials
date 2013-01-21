package com.ForgeEssentials.chat.commands;

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

public class CommandR extends ForgeEssentialsCommandBase
{
	public CommandR()
	{
		super();
	}

	@Override
	public String getCommandName()
	{
		return "r";
	}

	@Override
	public void processCommandPlayer(EntityPlayer sender, String[] args)
	{
		if (args.length == 0)
		{
			OutputHandler.chatError(sender, Localization.get(Localization.ERROR_BADSYNTAX) + "/r <message>");
			return;
		}
		if (args.length > 0)
		{
			String target = CommandMsg.getPlayerReply(sender.getCommandSenderName());
			if (target == null)
			{
				OutputHandler.chatError(sender, Localization.get("message.error.r.noPrevious"));
				return;
			}
			if (target.equalsIgnoreCase("server"))
			{
				String senderMessage = FEChatFormatCodes.GOLD + "[ me -> " + FEChatFormatCodes.PURPLE + "Server" + FEChatFormatCodes.GOLD + "] "
						+ FEChatFormatCodes.GREY;
				String receiverMessage = "[" + sender.getCommandSenderName() + " -> me ] ";
				for (int i = 0; i < args.length; i++)
				{
					receiverMessage += args[i];
					senderMessage += args[i];
					if (i != args.length - 1)
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
				EntityPlayerMP receiver = FunctionHelper.getPlayerFromUsername(target);
				if (receiver == null)
				{
					OutputHandler.chatError(sender, target + " is not a valid username");
					return;
				}
				String receiverMessage = FEChatFormatCodes.GOLD + "[ me -> " + FEChatFormatCodes.GREY + sender.getCommandSenderName() + FEChatFormatCodes.GOLD
						+ "] " + FEChatFormatCodes.GREY;
				String senderMessage = FEChatFormatCodes.GOLD + "[" + FEChatFormatCodes.GREY + sender.getCommandSenderName() + FEChatFormatCodes.GOLD
						+ " -> me ] " + FEChatFormatCodes.GREY;
				for (int i = 0; i < args.length; i++)
				{
					receiverMessage += args[i];
					senderMessage += args[i];
					if (i != args.length - 1)
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
		if (args.length == 0)
		{
			sender.sendChatToPlayer(Localization.ERROR_BADSYNTAX + "/msg <player> <message>");
			return;
		}
		if (args.length > 0)
		{
			String target = CommandMsg.getPlayerReply("server");
			if (target == null)
			{
				sender.sendChatToPlayer(Localization.get("message.error.r.noPrevious"));
				return;
			}
			EntityPlayer receiver = FunctionHelper.getPlayerFromUsername(target);
			if (receiver == null)
			{
				sender.sendChatToPlayer(target + " is not a valid username");
				return;
			}
			else
			{
				String senderMessage = "[ me -> " + receiver.getCommandSenderName() + "] ";
				String receiverMessage = FEChatFormatCodes.GOLD + "[" + FEChatFormatCodes.PURPLE + "Server" + FEChatFormatCodes.GOLD + " -> me ] "
						+ FEChatFormatCodes.GREY;
				for (int i = 0; i < args.length; i++)
				{
					receiverMessage += args[i];
					senderMessage += args[i];
					if (i != args.length - 1)
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
		return "ForgeEssentials.Chat." + getCommandName();
	}
}
