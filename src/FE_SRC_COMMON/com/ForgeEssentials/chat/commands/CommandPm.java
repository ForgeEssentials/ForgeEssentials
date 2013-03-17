package com.ForgeEssentials.chat.commands;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.server.MinecraftServer;

import com.ForgeEssentials.api.permissions.PermissionsAPI;
import com.ForgeEssentials.api.permissions.query.PermQueryPlayer;
import com.ForgeEssentials.core.commands.ForgeEssentialsCommandBase;
import com.ForgeEssentials.util.FEChatFormatCodes;
import com.ForgeEssentials.util.FunctionHelper;
import com.ForgeEssentials.util.Localization;
import com.ForgeEssentials.util.OutputHandler;

public class CommandPm extends ForgeEssentialsCommandBase
{
	private static Map<String, String>	persistentMessage;
	private List<String>				aliasList;

	public CommandPm()
	{
		super();
		persistentMessage = new HashMap<String, String>();
		aliasList = new LinkedList<String>();
		aliasList.add("persistentmessage");
	}

	@Override
	public String getCommandName()
	{
		return "pm";
	}

	@Override
	public List<String> getCommandAliases()
	{
		return aliasList;
	}

	@Override
	public void processCommandPlayer(EntityPlayer sender, String[] args)
	{
		if (args.length == 0)
		{
			if (persistentMessage.containsKey(sender.getCommandSenderName()))
			{
				persistentMessage.remove(sender.getCommandSenderName());
				OutputHandler.chatConfirmation(sender, Localization.get("command.pm.disabled"));
			}
			else
			{
				OutputHandler.chatWarning(sender, Localization.get("command.pm.alreadyDisabled"));
			}
			return;
		}
		if (args.length > 0)
		{
			if (args[0].equalsIgnoreCase("help"))
			{
				OutputHandler.chatConfirmation(sender, Localization.get("command.pm.help"));
			}
			else
			{
				EntityPlayer target = FunctionHelper.getPlayerFromPartialName(args[0]);
				if (target == null)
				{
					OutputHandler.chatError(sender, Localization.format(Localization.ERROR_NOPLAYER, args[0]));
					return;
				}
				if (persistentMessage.containsKey(sender.getCommandSenderName()))
				{
					persistentMessage.remove(sender.getCommandSenderName());
				}
				persistentMessage.put(sender.getCommandSenderName(), target.getCommandSenderName());

				OutputHandler.chatConfirmation(sender, Localization.format("command.pm.enable", target.getCommandSenderName()));
			}
			return;
		}
		if (args.length > 1)
		{
			String[] args2 = new String[args.length - 1];
			for (int i = 1; i < args.length; i++)
			{
				args2[i - 1] = args[i];
			}
			processChat(sender, args2);
		}
	}

	@Override
	public void processCommandConsole(ICommandSender sender, String[] args)
	{
		if (args.length == 0)
		{
			if (persistentMessage.containsKey(sender.getCommandSenderName()))
			{
				persistentMessage.remove(sender.getCommandSenderName());
				OutputHandler.chatConfirmation(sender, Localization.get("command.pm.disabled"));
			}
			else
			{
				OutputHandler.chatWarning(sender, Localization.get("command.pm.alreadyDisabled"));
			}
			return;
		}
		if (args.length > 0)
		{
			if (args[0].equalsIgnoreCase("help"))
			{
				OutputHandler.chatConfirmation(sender, Localization.get("command.pm.help"));
			}
			else
			{
				EntityPlayer target = FunctionHelper.getPlayerFromPartialName(args[0]);
				if (target == null)
				{
					OutputHandler.chatError(sender, Localization.format(Localization.ERROR_NOPLAYER, args[0]));
					return;
				}
				if (persistentMessage.containsKey(sender.getCommandSenderName()))
				{
					persistentMessage.remove(sender.getCommandSenderName());
				}
				persistentMessage.put(sender.getCommandSenderName(), target.getCommandSenderName());
				OutputHandler.chatConfirmation(sender, Localization.format("command.pm.enable", target.getCommandSenderName()));
			}
			return;
		}
		if (args.length > 1)
		{
			EntityPlayer receiver = FunctionHelper.getPlayerFromPartialName(args[0]);
			if (receiver == null)
			{
				OutputHandler.chatError(sender, Localization.format(Localization.ERROR_NOPLAYER, args[0]));
				return;
			}
			else
			{
				CommandMsg.clearReply(receiver.getCommandSenderName());
				CommandMsg.addReply(receiver.getCommandSenderName(), "server");
				if (persistentMessage.containsKey("server"))
				{
					persistentMessage.remove("server");
				}
				persistentMessage.put(sender.getCommandSenderName(), receiver.getCommandSenderName());
				OutputHandler.chatConfirmation(sender, "Persistent message to " + receiver.getCommandSenderName() + " enabled.");
				String senderMessage = "[ me -> " + receiver.getCommandSenderName() + "] ";
				String receiverMessage = FEChatFormatCodes.GOLD + "[" + FEChatFormatCodes.PURPLE + "Server" + FEChatFormatCodes.GOLD + " -> me ] " + FEChatFormatCodes.GREY;
				for (int i = 1; i < args.length; i++)
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
		return false;
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

	public static boolean isMessagePersistent(String username)
	{
		return persistentMessage.containsKey(username);
	}

	public static void processChat(ICommandSender sender, String[] args)
	{
		if (sender instanceof EntityPlayer)
		{
			String target = persistentMessage.get(sender.getCommandSenderName());
			if (target.equalsIgnoreCase("server") || target.equalsIgnoreCase("console"))
			{
				CommandMsg.clearReply("server");
				CommandMsg.addReply("server", sender.getCommandSenderName());
				String senderMessage = FEChatFormatCodes.GOLD + "[ me -> " + FEChatFormatCodes.PURPLE + "Server" + FEChatFormatCodes.GOLD + "] " + FEChatFormatCodes.GREY;
				String receiverMessage = FEChatFormatCodes.GOLD + "[" + FEChatFormatCodes.PURPLE + "Server" + FEChatFormatCodes.GOLD + " -> me ] ";
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
				EntityPlayerMP receiver = FunctionHelper.getPlayerFromPartialName(target);
				if (receiver == null)
				{
					OutputHandler.chatError(sender, Localization.format(Localization.ERROR_NOPLAYER, args[0]));
					return;
				}
				CommandMsg.clearReply(receiver.getCommandSenderName());
				CommandMsg.addReply(receiver.getCommandSenderName(), sender.getCommandSenderName());
				String senderMessage = FEChatFormatCodes.GOLD + "[ me -> " + FEChatFormatCodes.GREY + receiver.getCommandSenderName() + FEChatFormatCodes.GOLD + "] " + FEChatFormatCodes.WHITE;
				String receiverMessage = FEChatFormatCodes.GOLD + "[" + FEChatFormatCodes.GREY + sender.getCommandSenderName() + FEChatFormatCodes.GOLD + " -> me ] " + FEChatFormatCodes.WHITE;
				for (int i = 1; i < args.length; i++)
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
	public List<?> addTabCompletionOptions(ICommandSender sender, String[] args)
	{
		return null;
	}
}
