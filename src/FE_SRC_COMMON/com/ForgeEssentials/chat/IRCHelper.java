package com.ForgeEssentials.chat;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Map;

import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.server.MinecraftServer;

import org.h2.bnf.Sentence;
import org.pircbotx.PircBotX;
import org.pircbotx.exception.IrcException;
import org.pircbotx.exception.NickAlreadyInUseException;
import org.pircbotx.hooks.Listener;
import org.pircbotx.hooks.ListenerAdapter;
import org.pircbotx.hooks.events.KickEvent;
import org.pircbotx.hooks.events.MessageEvent;
import org.pircbotx.hooks.events.NickChangeEvent;
import org.pircbotx.hooks.events.PrivateMessageEvent;
import org.pircbotx.hooks.events.QuitEvent;

import com.ForgeEssentials.chat.commands.CommandMsg;
import com.ForgeEssentials.chat.ircCommands.ircCommand;
import com.ForgeEssentials.chat.ircCommands.ircCommandMessage;
import com.ForgeEssentials.chat.ircCommands.ircCommandReply;
import com.ForgeEssentials.chat.ircCommands.ircCommands;
import com.ForgeEssentials.core.ForgeEssentials;
import com.ForgeEssentials.util.FEChatFormatCodes;
import com.ForgeEssentials.util.FunctionHelper;
import com.ForgeEssentials.util.OutputHandler;

import cpw.mods.fml.common.IPlayerTracker;

public class IRCHelper extends ListenerAdapter implements Listener
{

	public static int					port;
	public static String				server;
	public static String				name;
	public static String				channel;
	private static PircBotX				bot;
	public static boolean				suppressEvents;
	public static ircCommands			ircCmds;

	public static void connectToServer()
	{
		OutputHandler.felog.info("Initializing IRC.");
		bot = new PircBotX();
		bot.setName(name);
		bot.getListenerManager().addListener(new IRCHelper());
		bot.setLogin(name);
		bot.setVerbose(false);
		bot.setAutoNickChange(true);
		bot.setCapEnabled(true);
		try
		{
			OutputHandler.felog.info("Attempting to join IRC server: " + server + " on port: " + port);
			bot.connect(server, port);
			OutputHandler.felog.info("Successfully joined IRC server!");
			OutputHandler.felog.info("Attempting to join " + server + " channel: " + channel);
			bot.joinChannel(channel);
			OutputHandler.felog.info("Successfully joined IRC Channel!");

		}
		catch (NickAlreadyInUseException e)
		{
			OutputHandler.felog.warning("Could not connect to IRC server = someone is already using the name you have assigned.");
		}
		catch (IOException e1)
		{
			OutputHandler.felog.warning("Could not connect to IRC server.");
		}
		catch (IrcException e2)
		{
			OutputHandler.felog.warning("Could not connect to IRC server.");
		}

	}

	public static String getBotName()
	{
		return bot.getName();
	}

	public static void postIRC(String message)
	{
		if (ModuleChat.connectToIRC)
		{
			bot.sendMessage(channel, message);
		}
	}

	// For /msg
	public static void privateMessage(String from, String to, String message)
	{
		if (ModuleChat.connectToIRC)
		{
			bot.sendMessage(bot.getUser(to), "(IRC)[" + from + " -> me] " + message);
		}
	}

	//	In case something else wants to send something
	public static void privateMessage(String to, String message)
	{
		if (ModuleChat.connectToIRC)
		{
			bot.sendMessage(bot.getUser(to), message);
		}
	}

	private static void postMinecraft(String message)
	{
		MinecraftServer.getServer().getConfigurationManager().sendChatMsg(message);
	}

	public static void shutdown()
	{
		bot.disconnect();
	}

	public static void reconnect(ICommandSender sender)
	{
		try
		{
			bot.reconnect();
		}
		catch (NickAlreadyInUseException e)
		{
			sender.sendChatToPlayer("Could not reconnect to the IRC server - the assigned nick is already in use. Try again in a few minutes.");
		}
		catch (IOException e)
		{
			sender.sendChatToPlayer("Could not reconnect to the IRC server - something went wrong.");
		}
		catch (IrcException e)
		{
			sender.sendChatToPlayer("Could not reconnect to the IRC server - something went wrong, or you are already connected to the server.");
		}
	}

	// IRC events
	@Override
	public void onPrivateMessage(PrivateMessageEvent e)
	{
		// Good
		String raw = e.getMessage().trim();
		
		// Just in case
		// Remove excess :
		while(raw.startsWith(":")){
			raw.replace(":", "");
		}
		
		// Check to see if it is a command
		if (raw.startsWith("%"))
		{
			ircCommands.executeCommand(raw, e.getUser());
		}
		
		else
		{
			privateMessage(e.getUser().getNick(), "Hello... use %help for commands.");
		}
	}

	@Override
	public void onMessage(MessageEvent e)
	{
		if (!e.getUser().getNick().equalsIgnoreCase(name))
		{
			// Check to see if it is a command
			if (e.getMessage().trim().startsWith("%"))
			{
				ircCommands.executeCommand(e.getMessage().trim(), e.getUser());
			}
			
			else
			{
				String send = "(IRC)[" + e.getChannel().getName() + "] <" + e.getUser().getNick() + "> " + e.getMessage().trim();
				postMinecraft(send);
			}
		}
	}

	@Override
	public void onQuit(QuitEvent e)
	{
		if (!suppressEvents)
		{
			if (!e.getUser().getNick().equalsIgnoreCase(channel))
			{
				postMinecraft(FEChatFormatCodes.YELLOW + e.getUser().getNick() + " left the channel");
			}
		}
	}

	@Override
	public void onKick(KickEvent e)
	{
		if (!suppressEvents)
		{
			if (!e.getRecipient().getNick().equalsIgnoreCase(channel))
			{
				postMinecraft(FEChatFormatCodes.YELLOW + e.getRecipient().getNick() + " was kicked from " + e.getChannel().getName() + " by " + e.getSource().getNick() + " with reason " + e.getReason());
			}
		}
		OutputHandler.felog.warning("The IRC bot was kicked from " + e.getChannel().getName() + " by " + e.getSource().getNick() + " with reason " + e.getReason() + " , please attempt to reconnect.");

	}

	@Override
	public void onNickChange(NickChangeEvent e)
	{
		if (!suppressEvents)
		{
			postMinecraft(FEChatFormatCodes.YELLOW + e.getOldNick() + " changed nick to " + e.getNewNick());
		}

		// Minecraft events
		class EventListener implements IPlayerTracker
		{

			@Override
			public void onPlayerLogin(EntityPlayer player)
			{
				if (!suppressEvents)
				{
					postIRC("Player " + player.username + " joined the game.");
				}
			}

			@Override
			public void onPlayerLogout(EntityPlayer player)
			{
				if (!suppressEvents)
				{
					postIRC("Player " + player.username + " left the game.");
				}
			}

			@Override
			public void onPlayerChangedDimension(EntityPlayer player)
			{
			}

			@Override
			public void onPlayerRespawn(EntityPlayer player)
			{
			}
		}
	}

}
