package com.forgeessentials.chat;

import java.io.File;
import java.util.Arrays;

import net.minecraft.command.ICommandSender;
import net.minecraftforge.common.Configuration;
import net.minecraftforge.common.Property;

import com.forgeessentials.chat.irc.IRCHelper;
import com.forgeessentials.core.moduleLauncher.ModuleConfigBase;
import com.forgeessentials.util.FunctionHelper;
import com.forgeessentials.util.OutputHandler;

public class ConfigChat extends ModuleConfigBase
{
	public static String	chatFormat;
	public static String	largeComment_chatFormat	= "";
	
	public Configuration	config;
    public static boolean   logchat;
    public static boolean   logcmd;

	// this is designed so it will work for any class.
	public ConfigChat(File file)
	{
		super(file);
	}

	static
	{
		largeComment_chatFormat += "This String formats the Chat.";
		largeComment_chatFormat += "\nIf you want both a color and special formatcodes, the color needs to be first before the special code";
		largeComment_chatFormat += "\nExamples: '%red%username' '%red%bold%username'\nNot OK:'%bold%gold%underline%username' In this example you would get the username in gold and underline but without bold";
		largeComment_chatFormat += "\nList of possible variables:";
		largeComment_chatFormat += "\nFor the username: %username The health of the player can be used with %health, %healthcolor will be a variable color depending on health.";
		largeComment_chatFormat += "\nThe variable, you need for the message:%message ";
		largeComment_chatFormat += "\nFor the player prefix and sufix use %playerPrefix and %playerSuffix";
		largeComment_chatFormat += "\nColors:%black,%darkblue,%darkgreen,%darkaqua,%darkred,%purple,%gold,%grey,%darkgrey,%indigo,\n       %green,%aqua,%red,%pink,%yellow,%white";
		largeComment_chatFormat += "\nSpecial formatcodes: %random,%bold,%strike,%underline,%italics";
		largeComment_chatFormat += "\nTo reset all formatcodes, you can use %reset";
		largeComment_chatFormat += "\nUse %rank to display a users rank as specified, %zone to specify there current zone";
		largeComment_chatFormat += "\nUse %groupPrefix and %groupSuffix to display the group prefixes and suffixes as specified";
		largeComment_chatFormat += "\n'%gm' is a variable formatcode. It changes depending on wich gamemode the player is in. Set the value below.";
	}

	@Override
	public void init()
	{
		OutputHandler.felog.finer("Loading chatconfigs");
		config = new Configuration(file, true);

		config.addCustomCategoryComment("Chat", "Chat Configs");
		config.addCustomCategoryComment("Automessage", "Automated spamm");

		String[] msg = config.get("Automessage", "messages", new String[]
		{ "\"This server uses ForgeEssentials\"", "\"Change these messages in the Chat config\"", "\"The timing can be changed there too!\"" }, "Each line is 1 message. You can use color coldes. YOU MUST USE DOUBLE QUOTES").getStringList().clone();
		for (int i = 0; i < msg.length; i++)
		{
			AutoMessage.msg.add(FunctionHelper.formatColors(FunctionHelper.format(msg[i].substring(1, msg[i].length() - 1))));
		}

		AutoMessage.random = config.get("Automessage", "random", false, "Randomize the order of messages").getBoolean(false);
		AutoMessage.waittime = config.get("Automessage", "inverval", 60, "Time in between each message in minutes").getInt();
		AutoMessage.enable = config.get("Automessage", "enable", false).getBoolean(true);

		chatFormat = config.get("Chat", "chatformat", "%playerPrefix%groupPrefix<%username>%groupSuffix%playerSuffix %reset%message", largeComment_chatFormat).getString();

		config.addCustomCategoryComment("Chat.gm", "\"%gm\" gets replaced by the values below");
		ChatFormatter.gmS = config.get("Chat.gm", "Survival", "[Sur]").getString();
		ChatFormatter.gmC = config.get("Chat.gm", "Creative", "[Cre]").getString();
		ChatFormatter.gmA = config.get("Chat.gm", "Adventure", "[Adv]").getString();

		ChatFormatter.censor = config.get("BannedWords", "censor", true, "censor the words in the censorList").getBoolean(true);
		ChatFormatter.bannedWords = Arrays.asList(config.get("BannedWords", "censorList", new String[]
		{ "fuck", "ass", "bitch", "shit" }, "List of words to be censored").getStringList());
		ChatFormatter.censorSlap = config.get("BannedWords", "slapDamage", 1, "0 is off, 1 is 1/2 heart, ...").getInt();
		ChatFormatter.censorSymbol = config.get("BannedWords", "censorSymbol", "#", "Character to replace censored words with (Use only one character in this config)").getString();

		config.addCustomCategoryComment("Chat.groups", "THIS HAS BEEN MOVED TO THE CORE CONFIG");

		config.addCustomCategoryComment("Chat.mute", "Settings for muted players");
		
		for (String cmd : config.get("Chat.mute", "mutedCommands", new String[] {"me"}, "All commands in here will be blocked if the player is muted.").getStringList())
		    CommandMuter.mutedCommands.add(cmd);
		
		String logCat = "Chat.log";
		config.addCustomCategoryComment(logCat, "Logging of all things going through chat.");
		
		logchat = config.get(logCat, "logchat", true, "Log all chat messages").getBoolean(true);
		logcmd = config.get(logCat, "logcmd", true, "Log all commands").getBoolean(true);
		
		config.addCustomCategoryComment("irc", "Configure the built-in IRC bot here.");
		
		ModuleChat.connectToIRC = config.get("irc", "enable", false, "Enable IRC interoperability?").getBoolean(false);
		IRCHelper.port = config.get("irc", "port", 5555, "The port to connect to the IRC server through.").getInt();
		IRCHelper.name = config.get("irc", "name", "FEIRCBot", "The nickname used to connect to the IRC server with.").getString();
		IRCHelper.server = config.get("irc", "server", "irc.something.com", "Hostname of the server to connect to").getString();
		IRCHelper.channel = config.get("irc", "channel", "#something", "Channel to connect to").getString();
		IRCHelper.suppressEvents = config.get("irc", "suppressEvents", true, "Suppress all IRC/game notifications. Some channels require this.").getBoolean(true);
		IRCHelper.password = config.get("irc", "nickservPass", "", "Nickserv password for the bot.").getString();
		IRCHelper.serverPass = config.get("irc", "serverPass", "", "Server password for the bot.").getString();
		
		config.save();
	}

	@Override
	public void forceSave()
	{
		config = new Configuration(file, true);

		config.addCustomCategoryComment("Chat", "Chatconfigs");
		config.addCustomCategoryComment("Automessage", "Automated spam");

		Property prop = config.get("Chat", "chatformat", "%groupPrefix%playerPrefix<%username>%playerSuffix%groupSuffix %reset%message", largeComment_chatFormat);
		prop.set(chatFormat);

		String[] msg = AutoMessage.msg.toArray(new String[0]);
		for (int i = 0; i < msg.length; i++)
		{
			msg[i] = "\"" + msg[i] + "\"";
		}

		config.get("Automessage", "messages", new String[] {}, "Each line is 1 message. You can use color coldes. YOU MUST USE DOUBLE QUOTES").set(msg);
		config.get("Automessage", "random", false, "Randomize the order of messages").set(AutoMessage.random);
		config.get("Automessage", "inverval", 1, "Time in between each message in minutes").set(AutoMessage.waittime);
		config.get("Automessage", "enable", true).set(AutoMessage.enable);

		config.get("BannedWords", "censor", true, "censor the words in the censorList").set(ChatFormatter.censor);
		config.get("BannedWords", "censorList", new String[] {}, "List of words to be censored").set(ChatFormatter.bannedWords.toArray(new String[ChatFormatter.bannedWords.size()]));
		config.get("BannedWords", "slapDamage", 1, "0 is off, 1 is 1/2 heart, ...").set(ChatFormatter.censorSlap);
		
		config.addCustomCategoryComment("Chat.groups", "THIS HAS BEEN MOVED TO THE CORE CONFIG");
		
		config.addCustomCategoryComment("Chat.mute", "Settings for muted players");
		
		config.get("Chat.mute", "mutedCommands", new String[] {"me"}, "All commands in here will be blocked if the player is muted.").set(CommandMuter.mutedCommands.toArray(new String[CommandMuter.mutedCommands.size()]));
		
		String logCat = "Chat.log";
        config.addCustomCategoryComment(logCat, "Logging of all things going through chat.");
        
        config.get(logCat, "logchat", true, "Log all chat messages").set(logchat);
        config.get(logCat, "logcmd", true, "Log all commands").set(logcmd);
        
        config.get("irc", "enable", false, "Enable IRC interoperability?").set(ModuleChat.connectToIRC);
        config.get("irc", "port", 5555, "The port to connect to the IRC server through.").set(IRCHelper.port);
		config.get("irc", "name", "FEIRCBot", "The nickname used to connect to the IRC server with.").set(IRCHelper.name);
		config.get("irc", "server", "irc.something.com", "Hostname of the server to connect to").set(IRCHelper.server);
		config.get("irc", "channel", "#something", "Channel to connect to").set(IRCHelper.channel);
		config.get("irc", "suppressEvents", true, "Suppress all IRC/game notifications. Some channels require this.").set(IRCHelper.suppressEvents);
		config.get("irc", "nickservPass", "", "Nickserv password for the bot.").set(IRCHelper.password);
		config.get("irc", "serverPass", "", "Server password for the bot.").set(IRCHelper.serverPass);
		
		config.save();
	}

	@Override
	public void forceLoad(ICommandSender sender)
	{
		config = new Configuration(file, true);

		config.addCustomCategoryComment("Chat", "Chat Configs");
		config.addCustomCategoryComment("Automessage", "Automated spamm");

		String[] msg = config.get("Automessage", "messages", new String[]
		{ "\"This server uses ForgeEssentials\"", "\"Change these messages in the Chat config\"", "\"The timing can be changed there too!\"" }, "Each line is 1 message. You can use color coldes. YOU MUST USE DOUBLE QUOTES").getStringList().clone();
		for (int i = 0; i < msg.length; i++)
		{
			AutoMessage.msg.add(FunctionHelper.formatColors(FunctionHelper.format(msg[i].substring(1, msg[i].length() - 1))));
		}

		AutoMessage.random = config.get("Automessage", "random", false, "Randomize the order of messages").getBoolean(false);
		AutoMessage.waittime = config.get("Automessage", "inverval", 1, "Time in between each message in minutes").getInt();
		AutoMessage.enable = config.get("Automessage", "enable", true).getBoolean(true);

		chatFormat = config.get("Chat", "chatformat", "%playerPrefix%groupPrefix<%username>%groupSuffix%playerSuffix %reset%message", largeComment_chatFormat).getString();

		ChatFormatter.censor = config.get("BannedWords", "censor", true, "censor the words in the censorList").getBoolean(true);
		ChatFormatter.bannedWords = Arrays.asList(config.get("BannedWords", "censorList", new String[]
		{ "fuck", "ass", "bitch", "shit" }, "List of words to be censored").getStringList());
		ChatFormatter.censorSymbol = config.get("BannedWords", "censorSymbol", "#", "Character to replace censored words with (Use only one character in this config)").getString();
		ChatFormatter.censorSlap = config.get("BannedWords", "slapDamage", 1, "0 is off, 1 is 1/2 heart, ...").getInt();
		config.addCustomCategoryComment("Chat.groups", "THIS HAS BEEN MOVED TO THE CORE CONFIG");

		config.addCustomCategoryComment("Chat.mute", "Settings for muted players");
        
		CommandMuter.mutedCommands.clear();
        for (String cmd : config.get("Chat.mute", "mutedCommands", new String[] {"me"}, "All commands in here will be blocked if the player is muted.").getStringList())
            CommandMuter.mutedCommands.add(cmd);
		
        String logCat = "Chat.log";
        config.addCustomCategoryComment(logCat, "Logging of all things going through chat.");
        
        logchat = config.get(logCat, "logchat", true, "Log all chat messages").getBoolean(true);
        logcmd = config.get(logCat, "logcmd", true, "Log all commands").getBoolean(true);
        
        ModuleChat.connectToIRC = config.get("irc", "enable", false, "Enable IRC interoperability?").getBoolean(false);
        IRCHelper.port = config.get("irc", "port", 5555, "The port to connect to the IRC server through.").getInt();
		IRCHelper.name = config.get("irc", "name", "FEIRCBot", "The nickname used to connect to the IRC server with.").getString();
		IRCHelper.server = config.get("irc", "server", "irc.something.com", "Hostname of the server to connect to").getString();
		IRCHelper.channel = config.get("irc", "channel", "#something", "Channels to connect to").getString();
		IRCHelper.suppressEvents = config.get("irc", "suppressEvents", true, "Suppress all IRC/game notifications. Some channels require this.").getBoolean(true);
		IRCHelper.password = config.get("irc", "nickservPass", "", "Nickserv password for the bot.").getString();
		IRCHelper.serverPass = config.get("irc", "serverPass", "", "Server password for the bot.").getString();
        
		config.save();
	}
}
