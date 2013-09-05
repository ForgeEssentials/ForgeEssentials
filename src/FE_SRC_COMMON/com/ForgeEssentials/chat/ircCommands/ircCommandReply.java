package com.ForgeEssentials.chat.ircCommands;

import net.minecraft.entity.player.EntityPlayerMP;

import org.pircbotx.User;

import com.ForgeEssentials.chat.commands.CommandMsg;
import com.ForgeEssentials.util.ChatUtils;
import com.ForgeEssentials.util.FEChatFormatCodes;
import com.ForgeEssentials.util.FunctionHelper;

public class ircCommandReply extends ircCommand
{
	@Override
	public String getCommandInfo()
	{
		return "Replies to the last message";
	}
	@Override
	public String getCommandUsage()
	{
		return "%reply [message]";
	}
	@Override
	public String[] getAliases()
	{
		return new String[]{"r"};
	}
	@Override
	public void execute(String[] args, User user)
	{
		try
		{
			String playername = CommandMsg.getPlayerReply("irc" + user.getNick().toLowerCase());
			String message = "";

			for (int i = 1; i < args.length; i++)
			{
				message += " " + args[i];
			}

			EntityPlayerMP player = FunctionHelper.getPlayerForName(playername);

			if (player == null)
			{
				user.sendMessage("Unable to send message: Player not found.");
				return;
			}

			String send = FEChatFormatCodes.GOLD + "(IRC)[" + user.getNick() + " -> me] " + FEChatFormatCodes.GREY + message;
			String recipt = "(IRC)[me -> " + player.getCommandSenderName() + "] " + message;

			ChatUtils.sendMessage(player, send);
			user.sendMessage(recipt);
		}
		catch (Exception ex)
		{
			user.sendMessage("Unable to send message: Something went really wrong.");
			return;
		}
	}

}
