package com.forgeessentials.chat.irc;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import net.minecraft.command.CommandException;
import net.minecraft.command.ICommand;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.event.ClickEvent.Action;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.ChatComponentTranslation;
import net.minecraft.util.IChatComponent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.CommandEvent;
import net.minecraftforge.event.ServerChatEvent;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.PlayerEvent.PlayerLoggedInEvent;
import net.minecraftforge.fml.common.gameevent.PlayerEvent.PlayerLoggedOutEvent;

import org.apache.commons.lang3.StringUtils;
import org.pircbotx.Configuration;
import org.pircbotx.PircBotX;
import org.pircbotx.User;
import org.pircbotx.exception.IrcException;
import org.pircbotx.hooks.ListenerAdapter;
import org.pircbotx.hooks.events.ActionEvent;
import org.pircbotx.hooks.events.ConnectEvent;
import org.pircbotx.hooks.events.DisconnectEvent;
import org.pircbotx.hooks.events.JoinEvent;
import org.pircbotx.hooks.events.KickEvent;
import org.pircbotx.hooks.events.MessageEvent;
import org.pircbotx.hooks.events.NickAlreadyInUseEvent;
import org.pircbotx.hooks.events.NickChangeEvent;
import org.pircbotx.hooks.events.PartEvent;
import org.pircbotx.hooks.events.PrivateMessageEvent;
import org.pircbotx.hooks.events.QuitEvent;

import com.forgeessentials.api.APIRegistry;
import com.forgeessentials.chat.ModuleChat;
import com.forgeessentials.chat.irc.command.CommandHelp;
import com.forgeessentials.chat.irc.command.CommandListPlayers;
import com.forgeessentials.chat.irc.command.CommandMessage;
import com.forgeessentials.chat.irc.command.CommandReply;
import com.forgeessentials.core.ForgeEssentials;
import com.forgeessentials.core.misc.Translator;
import com.forgeessentials.core.moduleLauncher.config.ConfigLoader;
import com.forgeessentials.util.events.FEPlayerEvent.NoPlayerInfoEvent;
import com.forgeessentials.util.output.ChatOutputHandler;
import com.forgeessentials.util.output.LoggingHandler;

public class IrcHandler extends ListenerAdapter<PircBotX> implements ConfigLoader
{

    private static final String CATEGORY = ModuleChat.CONFIG_CATEGORY + ".IRC";

    private static final String CHANNELS_HELP = "List of channels to connect to, together with the # character";

    private static final String ADMINS_HELP = "List of privileged users that can use more commands via the IRC bot";

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

    private boolean showGameEvents;

    private boolean showMessages;

    private boolean sendMessages;

    private String ircHeader;

    private String ircHeaderGlobal;

    private String mcSayHeader;

    private String mcHeader;

    private int messageDelay;

    private boolean allowCommands;

    private boolean allowMcCommands;

    private static Thread connectionThread;

    public final Map<String, IrcCommand> commands = new HashMap<>();

    // This map is used to keep the ICommandSender from being recycled by the garbage collector,
    // so they can be used as WeakReferences in CommandReply
    public final Map<User, IrcCommandSender> ircUserCache = new HashMap<>();

    /* ------------------------------------------------------------ */

    public IrcHandler()
    {
        ForgeEssentials.getConfigManager().registerLoader(ModuleChat.CONFIG_FILE, this);
        FMLCommonHandler.instance().bus().register(this);
        MinecraftForge.EVENT_BUS.register(this);
        APIRegistry.getFEEventBus().register(this);

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
                LoggingHandler.felog.warn(String.format("IRC command name %s used twice!", commandName));
    }

    public void connect()
    {
        if (bot != null)
            disconnect();

        LoggingHandler.felog.info("Initializing IRC connection. This may take a while.");

        bot = new PircBotX(constructConfig());

        LoggingHandler.felog.info(String.format("Attempting to join IRC server %s on port %d", server, port));
        connectionThread = new Thread(new Runnable() {
            @Override
            public void run()
            {
                try
                {
                    bot.startBot();
                }
                catch (IOException e)
                {
                    LoggingHandler.felog.warn("[IRC] Connection failed, could not reach the server");
                }
                catch (IrcException e)
                {
                    LoggingHandler.felog.warn("[IRC] Connection failed: " + e.getMessage());
                }

            }
        });

        connectionThread.start();

    }

    public void disconnect()
    {
        if (bot != null && bot.isConnected())
        {
            bot.sendIRC().quitServer();
            ircUserCache.clear();
            bot = null;
        }
    }

    public Configuration<PircBotX> constructConfig()
    {
        Configuration.Builder<PircBotX> builder = new Configuration.Builder<PircBotX>();
        builder.addListener(this);
        builder.setName(botName);
        builder.setLogin("FEIRCBot");
        builder.setAutoNickChange(true);
        builder.setMessageDelay(messageDelay);
        builder.setCapEnabled(!twitchMode);
        builder.setServer(server, port, serverPassword.isEmpty() ? "" : serverPassword);

        if (!nickPassword.isEmpty())
            builder.setNickservPassword(nickPassword);

        if (twitchMode)
            // Prevent pesky messages from jtv because we are sending too fast
            builder.setMessageDelay(3000);
        for (String channel : channels)
        {
            builder.addAutoJoinChannel(channel);
        }

        return builder.buildConfiguration();
    }

    public boolean isConnected()
    {
        return bot != null && bot.isConnected();
    }

    public Set<User> getIrcUsers()
    {
        return bot.getUserChannelDao().getAllUsers();
    }

    public Collection<String> getIrcUserNames()
    {
        List<String> users = new ArrayList<>();
        for (User user : bot.getUserChannelDao().getAllUsers())
            users.add(user.getNick());
        return users;
    }

    /* ------------------------------------------------------------ */

    @Override
    public void load(net.minecraftforge.common.config.Configuration config, boolean isReload)
    {
        config.addCustomCategoryComment(CATEGORY, "Configure the built-in IRC bot here");
        server = config.get(CATEGORY, "server", "irc.something.com", "Server address").getString();
        port = config.get(CATEGORY, "port", 5555, "Server port").getInt();
        botName = config.get(CATEGORY, "botName", "FEIRCBot", "Bot name").getString();
        serverPassword = config.get(CATEGORY, "serverPassword", "", "Server password").getString();
        nickPassword = config.get(CATEGORY, "nickPassword", "", "NickServ password").getString();
        twitchMode = config.get(CATEGORY, "twitchMode", false, "If set to true, sets connection to twitch mode").getBoolean();
        showEvents = config.get(CATEGORY, "showEvents", true, "Show IRC events ingame (e.g., join, leave, kick, etc.)").getBoolean();
        showGameEvents = config.get(CATEGORY, "showGameEvents", true, "Show game events in IRC (e.g., join, leave, death, etc.)").getBoolean();
        showMessages = config.get(CATEGORY, "showMessages", true, "Show chat messages from IRC ingame").getBoolean();
        sendMessages = config.get(CATEGORY, "sendMessages", false, "If enabled, ingame messages will be sent to IRC as well").getBoolean();
        ircHeader = config.get(CATEGORY, "ircHeader", "[\u00a7cIRC\u00a7r]<%s> ", "Header for messages sent from IRC. Must contain one \"%s\"").getString();
        ircHeaderGlobal = config.get(CATEGORY, "ircHeaderGlobal", "[\u00a7cIRC\u00a7r] ", "Header for IRC events. Must NOT contain any \"%s\"").getString();
        mcHeader = config.get(CATEGORY, "mcHeader", "<%s> %s", "Header for messages sent from MC to IRC. Must contain two \"%s\"").getString();
        mcSayHeader = config.get(CATEGORY, "mcSayHeader", "[%s] %s", "Header for messages sent with the /say command from MC to IRC. Must contain two \"%s\"")
                .getString();
        messageDelay = config.get(CATEGORY, "messageDelay", 0, "Delay between messages sent to IRC").getInt();
        allowCommands = config.get(CATEGORY, "allowCommands", true, "If enabled, allows usage of bot commands").getBoolean();
        allowMcCommands = config.get(CATEGORY, "allowMcCommands", true,
                "If enabled, allows usage of MC commands through the bot (only if the IRC user is in the admins list)").getBoolean();

        channels.clear();
        for (String channel : config.get(CATEGORY, "channels", new String[] { "#someChannelName" }, CHANNELS_HELP).getStringList())
            channels.add(channel);

        admins.clear();
        for (String admin : config.get(CATEGORY, "admins", new String[] {}, ADMINS_HELP).getStringList())
            admins.add(admin);

        // mcHeader = config.get(CATEGORY, "mcFormat", "<%username> %message",
        // "String for formatting messages posted to the IRC channel by the bot").getString();

        boolean connectToIrc = config.get(CATEGORY, "enable", false, "Enable IRC interoperability?").getBoolean(false);
        if (connectToIrc)
            connect();
        else
            disconnect();
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
        user.send().message(message);
    }

    public void sendMessage(String message)
    {
        if (isConnected())
            for (String channel : channels)
                bot.sendIRC().message(channel, message);
    }

    public void sendPlayerMessage(ICommandSender sender, IChatComponent message)
    {
        if (isConnected())
            sendMessage(String.format(mcHeader, sender.getName(), ChatOutputHandler.stripFormatting(message.getUnformattedText())));
    }

    private void mcSendMessage(String message, User user)
    {
        String filteredMessage = ModuleChat.censor.filterIRC(message);
        ModuleChat.instance.logChatMessage("IRC-" + user.getNick(), filteredMessage);

        String headerText = String.format(ircHeader, user.getNick());
        IChatComponent header = ModuleChat.clickChatComponent(headerText, Action.SUGGEST_COMMAND, "/ircpm " + user.getNick() + " ");
        IChatComponent messageComponent = ModuleChat.filterChatLinks(ChatOutputHandler.formatColors(filteredMessage));
        ChatOutputHandler.broadcast(new ChatComponentTranslation("%s%s", header, messageComponent));
    }

    private void mcSendMessage(String message)
    {
        String filteredMessage = ModuleChat.censor.filterIRC(message);
        IChatComponent header = ModuleChat.clickChatComponent(ircHeaderGlobal, Action.SUGGEST_COMMAND, "/irc ");
        IChatComponent messageComponent = ModuleChat.filterChatLinks(ChatOutputHandler.formatColors(filteredMessage));
        ChatOutputHandler.broadcast(new ChatComponentTranslation("%s%s", header, messageComponent));
    }

    public ICommandSender getIrcUser(String username)
    {
        if (!isConnected())
            return null;
        for (User user : bot.getUserChannelDao().getAllUsers())
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

    private void processMcCommand(User user, String cmdLine)
    {
        if (!admins.contains(user.getNick()))
        {
            sendMessage(user, "Permission denied. You are not an admin");
            return;
        }

        String[] args = cmdLine.split(" ");
        String commandName = args[0].substring(1);
        args = Arrays.copyOfRange(args, 1, args.length);

        ICommand command = (ICommand) MinecraftServer.getServer().getCommandManager().getCommands().get(commandName);
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

    /* ------------------------------------------------------------ */

    @SubscribeEvent(priority = EventPriority.LOW)
    public void chatEvent(ServerChatEvent event)
    {
        if (isConnected() && sendMessages)
            sendMessage(ChatOutputHandler.stripFormatting(event.getComponent().getUnformattedText()));
    }

    @SubscribeEvent(priority = EventPriority.LOWEST)
    public void playerLoginEvent(PlayerLoggedInEvent event)
    {
        if (showGameEvents)
            sendMessage(Translator.format("%s joined the game", event.player.getName()));
    }

    @SubscribeEvent(priority = EventPriority.LOWEST)
    public void playerLoginEvent(PlayerLoggedOutEvent event)
    {
        if (showGameEvents)
            sendMessage(Translator.format("%s left the game", event.player.getName()));
    }

    @SubscribeEvent(priority = EventPriority.LOWEST)
    public void playerDeathEvent(LivingDeathEvent event)
    {
        if (!(event.entityLiving instanceof EntityPlayer))
            return;
        if (showGameEvents)
            sendMessage(Translator.format("%s died", event.entityLiving.getName()));
    }

    @SubscribeEvent(priority = EventPriority.LOWEST)
    public void handleSay(CommandEvent event)
    {
        if (event.command.getCommandName().equals("say"))
        {
            sendMessage(Translator.format(mcSayHeader, event.sender.getName(), StringUtils.join(event.parameters, " ")));
        }
        else if (event.command.getCommandName().equals("me"))
        {
            sendMessage(Translator.format("* %s %s", event.sender.getName(), StringUtils.join(event.parameters, " ")));
        }
    }

    @SubscribeEvent
    public void welcomeNewPlayers(NoPlayerInfoEvent e)
    {
        if (showGameEvents)
            sendMessage(Translator.format("New player %s has joined the server!", e.getPlayer()));
    }

    /* ------------------------------------------------------------ */

    @Override
    public void onPrivateMessage(PrivateMessageEvent<PircBotX> event)
    {
        String raw = event.getMessage().trim();
        while (raw.startsWith(":"))
            raw.replace(":", "");

        // Check to see if it is a command
        if (raw.startsWith(COMMAND_CHAR) && allowCommands)
        {
            processCommand(event.getUser(), raw);
        }
        else if (raw.startsWith(COMMAND_MC_CHAR) && allowMcCommands)
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

        if (raw.startsWith(COMMAND_CHAR) && allowCommands)
        {
            processCommand(event.getUser(), raw);
        }
        else if (raw.startsWith(COMMAND_MC_CHAR) && allowMcCommands)
        {
            processMcCommand(event.getUser(), raw);
        }
        else if (showMessages)
            mcSendMessage(raw, event.getUser());
    }

    @Override
    public void onKick(KickEvent<PircBotX> event)
    {
        if (event.getRecipient() != bot.getUserBot())
        {
            if (showEvents)
                mcSendMessage(String.format("%s has been kicked from %s by %s: %s", event.getRecipient().getNick(), event.getChannel().getName(), event
                        .getUser().getNick(), event.getReason()));
        }
        else
        {
            LoggingHandler.felog.warn(String.format("The IRC bot was kicked from %s by %s: ", event.getChannel().getName(), event.getUser().getNick(),
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

    @Override
    public void onAction(ActionEvent<PircBotX> event) throws Exception
    {
        mcSendMessage(Translator.format("* %s %s", event.getUser().getNick(), event.getMessage()));
    }

    @Override
    public void onNickAlreadyInUse(NickAlreadyInUseEvent<PircBotX> event) throws Exception
    {
        LoggingHandler.felog.warn(Translator.format("Nick %s already in use, switching to nick %s", event.getUsedNick(), event.getAutoNewNick()));
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
