package com.forgeessentials.chat;

import net.minecraftforge.common.config.Configuration;

import com.forgeessentials.core.moduleLauncher.config.ConfigLoader.ConfigLoaderBase;

public class ChatConfig extends ConfigLoaderBase
{

    private static final String CATEGORY = ModuleChat.CONFIG_CATEGORY;

    private static final String CAT_CONST = CATEGORY + ".Constants";

    public static final String CHAT_FORMAT_HELP = "Format for chat. Always needs to contain all 5 placeholders like the default! Use %1 ... %5 if you want to switch their order.";

    public static String gamemodeCreative;

    public static String gamemodeAdventure;

    protected static String gamemodeSurvival;

    public static String chatFormat;

    public static String welcomeMessage;

    @Override
    public void load(Configuration config, boolean isReload)
    {
        config.addCustomCategoryComment("Chat", "Chat configuration");

        ModuleChat.instance.setChatLogging(config.get(CATEGORY, "LogChat", true, "Log all chat messages").getBoolean(true));

        chatFormat = config.get("Chat", "ChatFormat", "%1%2<%3>%4%5 ", CHAT_FORMAT_HELP).getString();
        welcomeMessage = config.get("Chat", "WelcomeMessage", "Welcome %username to the server!",
                "Welcome messages for new players. Can be colour formatted. Insert %username for the player's username.").getString();

        config.addCustomCategoryComment(CAT_CONST, "Some constants");
        gamemodeSurvival = config.get(CAT_CONST, "Survival", "[Sur]").getString();
        gamemodeCreative = config.get(CAT_CONST, "Creative", "[Cre]").getString();
        gamemodeAdventure = config.get(CAT_CONST, "Adventure", "[Adv]").getString();

        // config.addCustomCategoryComment("Chat.mute", "Settings for muted players");
        // CommandMuter.mutedCommands.clear();
        // for (String cmd : config.get("Chat.mute", "mutedCommands", new String[] { "me" },
        // "All commands in here will be blocked if the player is muted.")
        // .getStringList())
        // {
        // CommandMuter.mutedCommands.add(cmd);
        // }

        // config.addCustomCategoryComment("Chat.irc", "Configure the built-in IRC bot here.");
        // ModuleChat.connectToIRC = config.get("Chat.irc", "enable", false,
        // "Enable IRC interoperability?").getBoolean(false);
        // IRCHelper.port = config.get("Chat.irc", "port", 5555,
        // "The port to connect to the IRC server through.").getInt();
        // IRCHelper.name = config.get("Chat.irc", "name", "FEIRCBot",
        // "The nickname used to connect to the IRC server with.").getString();
        // IRCHelper.server = config.get("Chat.irc", "server", "irc.something.com",
        // "Hostname of the server to connect to").getString();
        // IRCHelper.channel = config.get("Chat.irc", "channel", "#something", "Channel to connect to").getString();
        // IRCHelper.suppressEvents = config.get("Chat.irc", "suppressEvents", true,
        // "Suppress all IRC/game notifications. Some channels require this.")
        // .getBoolean(true);
        // IRCHelper.password = config.get("Chat.irc", "nickservPass", "",
        // "Nickserv password for the bot.").getString();
        // IRCHelper.serverPass = config.get("Chat.irc", "serverPass", "", "Server password for the bot.").getString();
        // IRCHelper.silentMode = config.get("Chat.irc", "silentMode", false,
        // "If set to true, messages will only be passed from IRC, and no messages will be sent to channels.").getBoolean();
        // IRCHelper.twitchMode = config.get("Chat.irc", "twitchMode", false,
        // "If set to true, sets connection to twitch mode.").getBoolean();
        // IRCHelper.debugMode = config.get("Chat.irc", "debugMode", false,
        // "If set to true, all output from irc will be logged.").getBoolean();
        // CommandMuter.muteCmdBlocks = config.get("Chat.irc", "muteCmdBlocks", false,
        // "Mute command block output.").getBoolean();
        // IRCChatFormatter.ircHeader = config.get("Chat.irc", "ircOutput", "(IRC) [%channel] <%ircUser>",
        // "String to identify IRC channel output. %channel is replaced by the channel name, %ircuser is replaced by the IRC user's nick").getString();
        // IRCChatFormatter.ircPrivateHeader = config.get("Chat.irc", "ircPrivateOutput", "&6(IRC) [%ircUser]&7",
        // "String to identify IRC Private MSG output. %channel is replaced by the channel name, %ircuser is replaced by the IRC user's nick").getString();
        // IRCChatFormatter.mcHeader = config.get("Chat.irc", "mcFormat", "<%username> %message",
        // "String for formatting messages posted to the IRC channel by the bot.").getString();
    }

    @Override
    public void save(Configuration config)
    {
    }

}
