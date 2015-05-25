package com.forgeessentials.chat.irc;

import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.event.ClickEvent.Action;
import net.minecraft.util.ChatComponentTranslation;
import net.minecraft.util.IChatComponent;
import net.minecraftforge.common.config.Configuration;

import org.pircbotx.PircBotX;
import org.pircbotx.User;
import org.pircbotx.exception.IrcException;
import org.pircbotx.exception.NickAlreadyInUseException;
import org.pircbotx.hooks.ListenerAdapter;
import org.pircbotx.hooks.events.ConnectEvent;
import org.pircbotx.hooks.events.DisconnectEvent;
import org.pircbotx.hooks.events.JoinEvent;
import org.pircbotx.hooks.events.KickEvent;
import org.pircbotx.hooks.events.MessageEvent;
import org.pircbotx.hooks.events.NickChangeEvent;
import org.pircbotx.hooks.events.PartEvent;
import org.pircbotx.hooks.events.PrivateMessageEvent;
import org.pircbotx.hooks.events.QuitEvent;

import com.forgeessentials.chat.ModuleChat;
import com.forgeessentials.chat.irc.command.CommandHelp;
import com.forgeessentials.chat.irc.command.CommandListPlayers;
import com.forgeessentials.chat.irc.command.CommandMessage;
import com.forgeessentials.chat.irc.command.CommandReply;
import com.forgeessentials.core.ForgeEssentials;
import com.forgeessentials.core.misc.Translator;
import com.forgeessentials.core.moduleLauncher.config.ConfigLoader;
import com.forgeessentials.util.FunctionHelper;
import com.forgeessentials.util.OutputHandler;

public class IrcHandler extends ListenerAdapter<PircBotX> implements ConfigLoader
{

    private static final String CATEGORY = ModuleChat.CONFIG_CATEGORY + ".IRC";

    private static final String CHANNELS_HELP = "List of channels to connect to, together with the # character";

    private static final String ADMINS_HELP = "List of priviliged users that can use more commands via the IRC bot";

    public static final String COMMAND_CHAR = "%";

    public static final String COMMAND_MC_CHAR = "!";

    private PircBotX bot;

    private String server;

    private int port;

    private String botName;

    private String serverPassword;

    private String nickPassword;

    private Set<String> channels = new HashSet<>();

    private Set<String> admins = new HashSet<>();

    private boolean twitchMode;

    private boolean showEvents;

    private boolean showMessages;

    private boolean sendMessages;

    public final Map<String, IrcCommand> commands = new HashMap<>();

    // This map is used to keep the ICommandSender from being recycled by the garbage collector,
    // so they can be used as WeakReferences in CommandReply
    public final Map<User, IrcCommandSender> ircUserCache = new HashMap<>();

    /* ------------------------------------------------------------ */

    public IrcHandler()
    {
        ForgeEssentials.getConfigManager().registerLoader(ModuleChat.CONFIG_FILE, this);

        registerCommand(new CommandHelp());
        registerCommand(new CommandListPlayers());
        registerCommand(new CommandMessage());
        registerCommand(new CommandReply());
    }

    public static IrcHandler getInstance()
    {
        return ModuleChat.instance.ircHandler;
    }

    public void registerCommand(IrcCommand command)
    {
        for (String commandName : command.getCommandNames())
            if (commands.put(commandName, command) != null)
                OutputHandler.felog.warning(String.format("IRC command name %s used twice!", commandName));
    }

    public void connect()
    {
        if (bot != null)
            disconnect();

        OutputHandler.felog.info("Initializing IRC connection");
        bot = new PircBotX();
        bot.getListenerManager().addListener(this);
        bot.setName(botName);
        bot.setLogin(botName);
        bot.setVerbose(false);
        bot.setAutoNickChange(true);
        bot.setMessageDelay(0);

        bot.setCapEnabled(!twitchMode);
        if (twitchMode)
            // Prevent pesky messages from jtv because we are sending too fast
            bot.setMessageDelay(3000);

        try
        {
            OutputHandler.felog.info(String.format("Attempting to join IRC server %s on port %d", server, port));
            bot.connect(server, port, serverPassword.isEmpty() ? null : serverPassword);
            bot.identify(nickPassword);

            OutputHandler.felog.info("Attempting to join channels...");
            for (String channel : channels)
            {
                OutputHandler.felog.info(String.format("Attempting to join #%s", channel));
                bot.joinChannel(channel);
            }
            OutputHandler.felog.info("IRC bot connected");
        }
        catch (NickAlreadyInUseException e)
        {
            OutputHandler.felog.warning("[IRC] Connection failed, assigned nick already in use");
        }
        catch (IOException e)
        {
            OutputHandler.felog.warning("[IRC] Connection failed, could not reach the server");
        }
        catch (IrcException e)
        {
            OutputHandler.felog.warning("[IRC] Connection failed: " + e.getMessage());
        }
    }

    public void disconnect()
    {
        if (bot != null)
        {
            bot.shutdown();
            ircUserCache.clear();
            bot = null;
        }
    }

    public boolean isConnected()
    {
        return bot != null && bot.isConnected();
    }

    /* ------------------------------------------------------------ */

    @Override
    public void load(Configuration config, boolean isReload)
    {
        config.addCustomCategoryComment(CATEGORY, "Configure the built-in IRC bot here");
        server = config.get(CATEGORY, "server", "irc.something.com").getString();
        port = config.get(CATEGORY, "port", 5555).getInt();
        botName = config.get(CATEGORY, "botName", "FEIRCBot").getString();
        serverPassword = config.get(CATEGORY, "serverPassword", "").getString();
        nickPassword = config.get(CATEGORY, "nickPassword", "", "NickServ password").getString();
        twitchMode = config.get(CATEGORY, "twitchMode", false, "If set to true, sets connection to twitch mode").getBoolean();
        showEvents = config.get(CATEGORY, "showEvents", true, "Show IRC events ingame (e.g., join, leave, kick, etc.)").getBoolean();
        showMessages = config.get(CATEGORY, "showMessages", true, "Show chat messages from IRC ingame").getBoolean();
        sendMessages = config.get(CATEGORY, "sendMessages", false, "If enabled, ingame messages will be sent to IRC as well").getBoolean();

        channels.clear();
        for (String channel : config.get(CATEGORY, "channels", new String[] { "#someChannelName" }, CHANNELS_HELP).getStringList())
            channels.add(channel);

        admins.clear();
        for (String admin : config.get(CATEGORY, "admins", new String[] {}, ADMINS_HELP).getStringList())
            admins.add(admin);

        // ircHeader = config.get(CATEGORY, "ircOutput", "(IRC) [%channel] <%ircUser>",
        // "String to identify IRC channel output. %channel is replaced by the channel name, %ircuser is replaced by the IRC user's nick").getString();
        // ircPrivateHeader = config.get(CATEGORY, "ircPrivateOutput", "&6(IRC) [%ircUser]&7",
        // "String to identify IRC Private MSG output. %channel is replaced by the channel name, %ircuser is replaced by the IRC user's nick").getString();
        // mcHeader = config.get(CATEGORY, "mcFormat", "<%username> %message",
        // "String for formatting messages posted to the IRC channel by the bot").getString();

        boolean connectToIrc = config.get(CATEGORY, "enable", false, "Enable IRC interoperability?").getBoolean(false);
        if (connectToIrc)
            connect();
        else
            disconnect();
    }

    @Override
    public void save(Configuration config)
    {
        // TODO Auto-generated method stub
    }

    @Override
    public boolean supportsCanonicalConfig()
    {
        return true;
    }

    /* ------------------------------------------------------------ */

    public void sendMessage(User user, String message)
    {
        if (!isConnected())
            return;
        // ignore messages to jtv
        if (twitchMode && user.getNick() == "jtv")
            return;
        user.sendMessage(message);
    }

    public void sendMessage(String message)
    {
        if (!isConnected())
            return;
        for (String channel : channels)
            bot.sendMessage(channel, message);
    }

    public void sendPlayerMessage(String message, String playerName)
    {
        if (!isConnected() || !sendMessages)
            return;
        sendMessage(String.format("<%s> %s", playerName, message));
    }

    private void mcSendMessage(String message, User user)
    {
        ModuleChat.instance.logChatMessage("IRC-" + user.getNick(), message);

        String headerText = String.format("[\u00a7cIRC\u00a7r]<%s> ", user.getNick());
        IChatComponent header = ModuleChat.suggestCommandComponent(headerText, Action.SUGGEST_COMMAND, "/ircm ");
        IChatComponent messageComponent = ModuleChat.filterChatLinks(FunctionHelper.formatColors(message));

        OutputHandler.broadcast(new ChatComponentTranslation("%s%s", header, messageComponent));
    }

    private void mcSendMessage(String message)
    {
        IChatComponent messageComponent = ModuleChat.filterChatLinks(FunctionHelper.formatColors(message));
        OutputHandler.broadcast(new ChatComponentTranslation("[\u00a7cIRC\u00a7r] %s", messageComponent));
    }

    public ICommandSender getIrcUser(String username)
    {
        if (!isConnected())
            return null;
        for (User user : bot.getUsers())
        {
            if (user.getNick().equals(username))
            {
                IrcCommandSender sender = new IrcCommandSender(user);
                ircUserCache.put(sender.getUser(), sender);
                return sender;
            }
        }
        return null;
    }

    private void processCommand(User user, String cmdLine)
    {
        String[] args = cmdLine.split(" ");
        String commandName = args[0].substring(1);
        args = Arrays.copyOfRange(args, 1, args.length);

        IrcCommand command = commands.get(commandName);
        if (command == null)
        {
            sendMessage(user, String.format("Error: Command %s not found!", commandName));
            return;
        }

        IrcCommandSender sender = new IrcCommandSender(user);
        ircUserCache.put(sender.getUser(), sender);
        try
        {
            command.processCommand(sender, args);
        }
        catch (CommandException e)
        {
            sendMessage(user, "Error: " + e.getMessage());
        }
    }

    private boolean processMcCommand(User user, String raw)
    {
        if (!admins.contains(user.getNick()))
        {
            sendMessage(user, "Permission denied. You are not an admin");
            return true;
        }

        sendMessage(user, "Not yet implemented!");
        return true;
    }

    /* ------------------------------------------------------------ */

    @Override
    public void onPrivateMessage(PrivateMessageEvent<PircBotX> event)
    {
        String raw = event.getMessage().trim();
        while (raw.startsWith(":"))
            raw.replace(":", "");

        // Check to see if it is a command
        if (raw.startsWith(COMMAND_CHAR))
        {
            processCommand(event.getUser(), raw);
        }
        else if (raw.startsWith(COMMAND_MC_CHAR))
        {
            processMcCommand(event.getUser(), raw);
        }
        else
        {
            if (twitchMode && (event.getUser().getNick() == "jtv"))
                return;
            sendMessage(event.getUser(), String.format("Hello %s, use %%help for commands", event.getUser().getNick()));
        }
    }

    @Override
    public void onMessage(MessageEvent<PircBotX> event)
    {
        if (event.getUser().getNick().equalsIgnoreCase(bot.getNick()))
            return;

        String raw = event.getMessage().trim();
        while (raw.startsWith(":"))
            raw.replace(":", "");

        if (raw.startsWith(COMMAND_CHAR))
        {
            processCommand(event.getUser(), raw);
            return;
        }

        if (raw.startsWith(COMMAND_MC_CHAR))
        {
            if (processMcCommand(event.getUser(), raw))
                return;
        }

        if (showMessages)
            mcSendMessage(raw, event.getUser());
    }

    @Override
    public void onKick(KickEvent<PircBotX> event)
    {
        if (event.getRecipient() != bot.getUserBot())
        {
            if (showEvents)
                mcSendMessage(String.format("%s has been kicked from %s by %s: %s", event.getRecipient().getNick(), event.getChannel().getName(), event
                        .getSource().getNick(), event.getReason()));
        }
        else
        {
            OutputHandler.felog.warning(String.format("The IRC bot was kicked from %s by %s: ", event.getChannel().getName(), event.getSource().getNick(),
                    event.getReason()));
        }
    }

    @Override
    public void onQuit(QuitEvent<PircBotX> event)
    {
        if (!showEvents || event.getUser() == bot.getUserBot())
            return;
        mcSendMessage(String.format("%s left the channel %s: %s", event.getUser().getNick(), event.getReason()));
    }

    @Override
    public void onNickChange(NickChangeEvent<PircBotX> event)
    {
        if (!showEvents || event.getUser() == bot.getUserBot())
            return;
        mcSendMessage(Translator.format("%s changed his nick to %s", event.getOldNick(), event.getNewNick()));
    }

    @Override
    public void onJoin(JoinEvent<PircBotX> event) throws Exception
    {
        if (!showEvents || event.getUser() == bot.getUserBot())
            return;
        mcSendMessage(Translator.format("%s joined the channel %s", event.getUser().getNick(), event.getChannel().getName()));
    }

    @Override
    public void onPart(PartEvent<PircBotX> event) throws Exception
    {
        ircUserCache.remove(event.getUser());
        if (!showEvents || event.getUser() == bot.getUserBot())
            return;
        mcSendMessage(Translator.format("%s left the channel %s: %s", event.getUser().getNick(), event.getChannel().getName(), event.getReason()));
    }

    @Override
    public void onConnect(ConnectEvent<PircBotX> event) throws Exception
    {
        mcSendMessage("IRC bot connected to the network");
    }

    @Override
    public void onDisconnect(DisconnectEvent<PircBotX> event) throws Exception
    {
        mcSendMessage("IRC bot disconnected from the network");
    }

    /* ------------------------------------------------------------ */

    public boolean isSendMessages()
    {
        return sendMessages;
    }

    public void setSendMessages(boolean sendMessages)
    {
        this.sendMessages = sendMessages;
    }

}
