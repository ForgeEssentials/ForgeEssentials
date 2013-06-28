package com.ForgeEssentials.chat;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import net.minecraft.server.MinecraftServer;

import org.pircbotx.PircBotX;
import org.pircbotx.exception.IrcException;
import org.pircbotx.exception.NickAlreadyInUseException;
import org.pircbotx.hooks.Listener;
import org.pircbotx.hooks.ListenerAdapter;
import org.pircbotx.hooks.events.MessageEvent;

import com.ForgeEssentials.util.OutputHandler;

public class IRCHelper extends ListenerAdapter implements Listener
{

	public static int		port;
	public static String	server;
	public static String	name;
	public static String	password;
	public static String	channel;
	private static PircBotX	bot;

	public static void connectToServer()
	{
		OutputHandler.felog.info("Attempting to join IRC server: " + server + " on port: " + port);
		bot = new PircBotX();
		bot.setName(name);
		bot.getListenerManager().addListener(new IRCHelper());
		bot.setLogin(name);
		bot.setVerbose(false);
		bot.setAutoNickChange(true);
		bot.setCapEnabled(true);
		try
		{
			if (password.length() > 0)
				bot.connect(server, port, password);
			else
				bot.connect(server, port);
			bot.joinChannel(channel);
			OutputHandler.felog.info("Successfully joined IRC server!");
		}
		catch (NickAlreadyInUseException e)
		{
			OutputHandler.felog.warning("Could not connect to IRC server: Someone is already using the name you have assigned.");
			OutputHandler.felog.warning("Turning IRC off for now.");
			ModuleChat.enableIRC = false;
		}
		catch (IOException e1)
		{
			OutputHandler.felog.warning("Could not connect to IRC server.");
			OutputHandler.felog.warning("Turning IRC off for now.");
			ModuleChat.enableIRC = false;
		}
		catch (IrcException e2)
		{
			OutputHandler.felog.warning("Could not connect to IRC server.");
			OutputHandler.felog.warning("Turning IRC off for now.");
			ModuleChat.enableIRC = false;
		}
		catch (Exception e)
		{
			OutputHandler.felog.warning("Something went really wrong with irc.");
			OutputHandler.felog.warning("Check the configs.");
			OutputHandler.felog.warning("Turning IRC off for now.");
			ModuleChat.enableIRC = false;
		}

	}

	@Override
	public void onMessage(MessageEvent e)
	{
		String send = "[" + e.getChannel().getName() + "] <" + e.getUser().getNick() + "> " + e.getMessage();
		postMinecraft(send);
	}

	public static void postIRC(String message)
	{
		if (ModuleChat.enableIRC) // Just in case
		{
			try
			{
				bot.sendMessage(channel, message);
			}
			catch (Exception e) // Catch errors... Posting to IRC AIN'T THAT IMPORTANT... rather not have the server fail...
			{
				OutputHandler.felog.warning("Could not post message to IRC channel.");
			}
		}
	}

	private static void postMinecraft(String message)
	{
		MinecraftServer.getServer().getConfigurationManager().sendChatMsg(message);
	}

	public static void shutdown()
	{
		try
		{
			bot.disconnect();
		}
		catch (Exception e)
		{
			OutputHandler.felog.warning("Giving up on IRC shutdown.");
		}
	}
}
