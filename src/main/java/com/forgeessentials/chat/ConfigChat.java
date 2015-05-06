package com.forgeessentials.chat;

import java.util.ArrayList;
import java.util.Arrays;

import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.common.config.Property;

import com.forgeessentials.chat.irc.IRCChatFormatter;
import com.forgeessentials.chat.irc.IRCHelper;
import com.forgeessentials.core.moduleLauncher.config.IConfigLoader.ConfigLoaderBase;
import com.forgeessentials.util.OutputHandler;

public class ConfigChat extends ConfigLoaderBase {
    
    public static String chatFormat;
    public static String largeComment_chatFormat = "";
    public static boolean logchat;
    public static boolean logcmd;
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
    public void load(Configuration config, boolean isReload)
    {
        OutputHandler.felog.finer("Loading chatconfigs");

        config.addCustomCategoryComment("Chat", "Chat Configs");

        chatFormat = config.get("Chat", "chatformat", "%playerPrefix%groupPrefix<%username>%groupSuffix%playerSuffix %reset%message", largeComment_chatFormat)
                .getString();

        ModuleChat.welcomeNewPlayers = config.get("Chat", "welcomeNewPlayers", true, "Post a welcome message when a new player joins the server.").getBoolean();
        ModuleChat.welcomeNewPlayerMsg = config.get("Chat", "welcomeNewPlayerMsg", "Welcome %username to the server!", "Welcome messages for new players. Can be colour formatted. Insert %username for the player's username.").getString();

        config.addCustomCategoryComment("Chat.gm", "\"%gm\" gets replaced by the values below");
        ChatFormatter.gmS = config.get("Chat.gm", "Survival", "[Sur]").getString();
        ChatFormatter.gmC = config.get("Chat.gm", "Creative", "[Cre]").getString();
        ChatFormatter.gmA = config.get("Chat.gm", "Adventure", "[Adv]").getString();

        ChatFormatter.censor = config.get("BannedWords", "censor", true, "censor the words in the censorList").getBoolean(true);
        ChatFormatter.bannedWords = new ArrayList<>(Arrays.asList(config.get("BannedWords", "censorList", new String[] { "fuck", "ass", "bitch", "shit" },
                "List of words to be censored").getStringList()));
        ChatFormatter.censorSlap = config.get("BannedWords", "slapDamage", 1, "0 is off, 1 is 1/2 heart, ...").getInt();
        ChatFormatter.censorSymbol = config.get("BannedWords", "censorSymbol", "#",
                "Character to replace censored words with (Use only one character in this config)").getString();

        config.addCustomCategoryComment("Chat.mute", "Settings for muted players");
        CommandMuter.mutedCommands.clear();
        for (String cmd : config.get("Chat.mute", "mutedCommands", new String[] { "me" }, "All commands in here will be blocked if the player is muted.")
                .getStringList())
        {
            CommandMuter.mutedCommands.add(cmd);
        }

        String logCat = "Chat.log";
        config.addCustomCategoryComment(logCat, "Logging of all things going through chat.");

        logchat = config.get(logCat, "logchat", true, "Log all chat messages").getBoolean(true);
        logcmd = config.get(logCat, "logcmd", true, "Log all commands").getBoolean(true);

        config.addCustomCategoryComment("Chat.irc", "Configure the built-in IRC bot here.");

        ModuleChat.connectToIRC = config.get("Chat.irc", "enable", false, "Enable IRC interoperability?").getBoolean(false);
        IRCHelper.port = config.get("Chat.irc", "port", 5555, "The port to connect to the IRC server through.").getInt();
        IRCHelper.name = config.get("Chat.irc", "name", "FEIRCBot", "The nickname used to connect to the IRC server with.").getString();
        IRCHelper.server = config.get("Chat.irc", "server", "irc.something.com", "Hostname of the server to connect to").getString();
        IRCHelper.channel = config.get("Chat.irc", "channel", "#something", "Channel to connect to").getString();
        IRCHelper.suppressEvents = config.get("Chat.irc", "suppressEvents", true, "Suppress all IRC/game notifications. Some channels require this.")
                .getBoolean(true);
        IRCHelper.password = config.get("Chat.irc", "nickservPass", "", "Nickserv password for the bot.").getString();
        IRCHelper.serverPass = config.get("Chat.irc", "serverPass", "", "Server password for the bot.").getString();
        IRCHelper.silentMode = config.get("Chat.irc", "silentMode", false, "If set to true, messages will only be passed from IRC, and no messages will be sent to channels.").getBoolean();
        IRCHelper.twitchMode = config.get("Chat.irc", "twitchMode", false, "If set to true, sets connection to twitch mode.").getBoolean();
        IRCHelper.debugMode = config.get("Chat.irc", "debugMode", false, "If set to true, all output from irc will be logged.").getBoolean();
        CommandMuter.muteCmdBlocks = config.get("Chat.irc", "muteCmdBlocks", false, "Mute command block output.").getBoolean();
        IRCChatFormatter.ircHeader = config.get("Chat.irc", "ircOutput", "(IRC) [%channel] <%ircUser>", "String to identify IRC channel output. %channel is replaced by the channel name, %ircuser is replaced by the IRC user's nick").getString();
        IRCChatFormatter.ircPrivateHeader = config.get("Chat.irc", "ircPrivateOutput", "&6(IRC) [%ircUser]&7", "String to identify IRC Private MSG output. %channel is replaced by the channel name, %ircuser is replaced by the IRC user's nick").getString();
        IRCChatFormatter.mcHeader = config.get("Chat.irc", "mcFormat", "<%username> %message", "String for formatting messages posted to the IRC channel by the bot.").getString();

        config.save();
    }

    @Override
    public void save(Configuration config)
    {
        config.addCustomCategoryComment("Chat", "Chatconfigs");
        config.addCustomCategoryComment("Chat.Automessage", "Automated spam");

        Property prop = config.get("Chat", "chatformat", "%groupPrefix%playerPrefix<%username>%playerSuffix%groupSuffix %reset%message",
                largeComment_chatFormat);
        prop.set(chatFormat);

        config.get("BannedWords", "censor", true, "censor the words in the censorList").set(ChatFormatter.censor);
        config.get("BannedWords", "censorList", new String[] {}, "List of words to be censored").set(
                ChatFormatter.bannedWords.toArray(new String[ChatFormatter.bannedWords.size()]));
        config.get("BannedWords", "slapDamage", 1, "0 is off, 1 is 1/2 heart, ...").set(ChatFormatter.censorSlap);

        config.addCustomCategoryComment("Chat.groups", "THIS HAS BEEN MOVED TO THE CORE CONFIG");

        config.addCustomCategoryComment("Chat.mute", "Settings for muted players");

        config.get("Chat.mute", "mutedCommands", new String[] { "me" }, "All commands in here will be blocked if the player is muted.").set(
                CommandMuter.mutedCommands.toArray(new String[CommandMuter.mutedCommands.size()]));

        String logCat = "Chat.log";
        config.addCustomCategoryComment(logCat, "Logging of all things going through chat.");

        config.get(logCat, "logchat", true, "Log all chat messages").set(logchat);
        config.get(logCat, "logcmd", true, "Log all commands").set(logcmd);

        config.get("Chat.irc", "enable", false, "Enable IRC interoperability?").set(ModuleChat.connectToIRC);
        config.get("Chat.irc", "port", 5555, "The port to connect to the IRC server through.").set(IRCHelper.port);
        config.get("Chat.irc", "name", "FEIRCBot", "The nickname used to connect to the IRC server with.").set(IRCHelper.name);
        config.get("Chat.irc", "server", "irc.something.com", "Hostname of the server to connect to").set(IRCHelper.server);
        config.get("Chat.irc", "channel", "#something", "Channel to connect to").set(IRCHelper.channel);
        config.get("Chat.irc", "suppressEvents", true, "Suppress all IRC/game notifications. Some channels require this.").set(IRCHelper.suppressEvents);
        config.get("Chat.irc", "nickservPass", "", "Nickserv password for the bot.").set(IRCHelper.password);
        config.get("Chat.irc", "serverPass", "", "Server password for the bot.").set(IRCHelper.serverPass);
        config.get("Chat.irc", "silentMode", false, "If set to true, messages will only be passed from IRC, and no messages will be sent to channels.").set(IRCHelper.silentMode);
        config.get("Chat.irc", "twitchMode", false, "If set to true, sets connection to twitch mode.").set(IRCHelper.twitchMode);
        config.get("Chat.irc", "debugMode", false, "If set to true, all output from irc will be logged.").set(IRCHelper.debugMode);
        
        config.get("Chat.irc", "muteCmdBlocks", false, "Mute command block output.").set(CommandMuter.muteCmdBlocks);
        config.get("Chat.irc", "ircHeader", "(IRC) [%channel] <%ircUser>", "String to identify IRC channel output. %channel is replaced by the channel name, %ircuser is replaced by the IRC user's nick").set(IRCChatFormatter.ircHeader);
        config.get("Chat.irc", "ircPrivateHeader", "&6(IRC) [%ircUser]&7", "String to identify IRC Private MSG output. %channel is replaced by the channel name, %ircuser is replaced by the IRC user's nick").set(IRCChatFormatter.ircPrivateHeader);

        config.save();
    }

}
