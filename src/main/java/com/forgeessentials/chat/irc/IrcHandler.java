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
import net.minecraft.command.CommandSource;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.text.event.ClickEvent.Action;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.util.text.ITextComponent;
import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.common.ForgeConfigSpec.Builder;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.CommandEvent;
import net.minecraftforge.event.ServerChatEvent;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.event.entity.player.PlayerEvent.PlayerLoggedInEvent;
import net.minecraftforge.event.entity.player.PlayerEvent.PlayerLoggedOutEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.server.ServerLifecycleHooks;

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
import com.forgeessentials.core.config.ConfigData;
import com.forgeessentials.core.config.ConfigLoader;
import com.forgeessentials.core.misc.Translator;
import com.forgeessentials.util.events.FEPlayerEvent.NoPlayerInfoEvent;
import com.forgeessentials.util.output.ChatOutputHandler;
import com.forgeessentials.util.output.LoggingHandler;
import com.mojang.brigadier.ParseResults;

public class IrcHandler extends ListenerAdapter implements ConfigLoader
{

    private static final String CATEGORY = ModuleChat.CONFIG_CATEGORY + "_IRC";

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
        ForgeEssentials.getConfigManager().registerSpecs(ModuleChat.CONFIG_FILE, this);
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

    public Configuration constructConfig()
    {
        Configuration.Builder builder = new Configuration.Builder();
        builder.addListener(this);
        builder.setName(botName);
        builder.setLogin("FEIRCBot");
        builder.setAutoNickChange(true);
        builder.setMessageDelay(messageDelay);
        builder.setCapEnabled(!twitchMode);
        builder.buildForServer(server, port, serverPassword.isEmpty() ? "" : serverPassword);

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

    static ForgeConfigSpec.ConfigValue<String> FEserver;
    static ForgeConfigSpec.IntValue FEport;
    static ForgeConfigSpec.ConfigValue<String> FEbotName;
    static ForgeConfigSpec.ConfigValue<String> FEserverPassword;
    static ForgeConfigSpec.ConfigValue<String> FEnickPassword;
    static ForgeConfigSpec.BooleanValue FEtwitchMode;
    static ForgeConfigSpec.BooleanValue FEshowEvents;
    static ForgeConfigSpec.BooleanValue FEshowGameEvents;
    static ForgeConfigSpec.BooleanValue FEshowMessages;
    static ForgeConfigSpec.BooleanValue FEsendMessages;
    static ForgeConfigSpec.ConfigValue<String> FEircHeader;
    static ForgeConfigSpec.ConfigValue<String> FEircHeaderGlobal;
    static ForgeConfigSpec.ConfigValue<String> FEmcHeader;
    static ForgeConfigSpec.ConfigValue<String> FEmcSayHeader;
    static ForgeConfigSpec.IntValue FEmessageDelay;
    static ForgeConfigSpec.BooleanValue FEallowCommands;
    static ForgeConfigSpec.BooleanValue FEallowMcCommands;
    static ForgeConfigSpec.ConfigValue<String[]> FEchannels;
    static ForgeConfigSpec.ConfigValue<String[]> FEadmins;
    static ForgeConfigSpec.BooleanValue FEenable;

	@Override
	public void load(Builder BUILDER, boolean isReload)
	{
        BUILDER.comment("Configure the built-in IRC bot here").push(CATEGORY);
        FEserver = BUILDER.comment("Server address").define("server", "irc.something.com");
        FEport = BUILDER.comment("Server port").defineInRange("port", 5555, 0, 65535);
        FEbotName = BUILDER.comment("Bot name").define("botName", "FEIRCBot");
        FEserverPassword = BUILDER.comment("Server password").define("serverPassword", "");
        FEnickPassword = BUILDER.comment("NickServ password").define("nickPassword", "");
        FEtwitchMode = BUILDER.comment("If set to true, sets connection to twitch mode").define("twitchMode", false);
        FEshowEvents = BUILDER.comment("Show IRC events ingame (e.g., join, leave, kick, etc.)").define("showEvents", true);
        FEshowGameEvents = BUILDER.comment("Show game events in IRC (e.g., join, leave, death, etc.)").define("showGameEvents", true);
        FEshowMessages = BUILDER.comment("Show chat messages from IRC ingame").define("showMessages", true);
        FEsendMessages = BUILDER.comment("If enabled, ingame messages will be sent to IRC as well").define("sendMessages", false);
        FEircHeader = BUILDER.comment("Header for messages sent from MC to IRC. Must contain two \"%s\"").define("ircHeader", "[\u00a7cIRC\u00a7r]<%s> ");
        FEircHeaderGlobal = BUILDER.comment("Header for IRC events. Must NOT contain any \"%s\"").define("ircHeaderGlobal", "[\u00a7cIRC\u00a7r] ");
        FEmcHeader = BUILDER.comment("Header for messages sent from MC to IRC. Must contain two \"%s\"").define("mcHeader", "<%s> %s");
        FEmcSayHeader = BUILDER.comment("Header for messages sent with the /say command from MC to IRC. Must contain two \"%s\"").define("mcSayHeader",
                "[%s] %s");
        FEmessageDelay = BUILDER.comment("Delay between messages sent to IRC").defineInRange("messageDelay", 0, 0, 60);
        FEallowCommands = BUILDER.comment("If enabled, allows usage of bot commands").define("allowCommands", true);
        FEallowMcCommands = BUILDER.comment("If enabled, allows usage of MC commands through the bot (only if the IRC user is in the admins list)")
                .define("allowMcCommands", true);
        FEchannels = BUILDER.comment(CHANNELS_HELP).define("channels", new String[] { "#someChannelName" });
        FEadmins = BUILDER.comment(ADMINS_HELP).define("admins", new String[] {});
        FEenable = BUILDER.comment("Enable IRC interoperability?").define("enable", false);
    }

	@Override
	public void bakeConfig(boolean reload)
	{
        ModuleChat.instance.ircHandler.server = FEserver.get();
        ModuleChat.instance.ircHandler.port = FEport.get();
        ModuleChat.instance.ircHandler.botName = FEbotName.get();
        ModuleChat.instance.ircHandler.serverPassword = FEserverPassword.get();
        ModuleChat.instance.ircHandler.nickPassword = FEnickPassword.get();
        ModuleChat.instance.ircHandler.twitchMode = FEtwitchMode.get();
        ModuleChat.instance.ircHandler.showEvents = FEshowEvents.get();
        ModuleChat.instance.ircHandler.showGameEvents = FEshowGameEvents.get();
        ModuleChat.instance.ircHandler.showMessages = FEshowMessages.get();
        ModuleChat.instance.ircHandler.sendMessages = FEsendMessages.get();
        ModuleChat.instance.ircHandler.ircHeader = FEircHeader.get();
        ModuleChat.instance.ircHandler.ircHeaderGlobal = FEircHeaderGlobal.get();
        ModuleChat.instance.ircHandler.mcHeader = FEmcHeader.get();
        ModuleChat.instance.ircHandler.mcSayHeader = FEmcSayHeader.get();
        ModuleChat.instance.ircHandler.messageDelay = FEmessageDelay.get();
        ModuleChat.instance.ircHandler.allowCommands = FEallowCommands.get();
        ModuleChat.instance.ircHandler.allowMcCommands = FEallowMcCommands.get();

        ModuleChat.instance.ircHandler.channels.clear();
        for (String channel : FEchannels.get())
            ModuleChat.instance.ircHandler.channels.add(channel);

        ModuleChat.instance.ircHandler.admins.clear();
        for (String admin : FEadmins.get())
            ModuleChat.instance.ircHandler.admins.add(admin);

        // mcHeader = config.get(CATEGORY, "mcFormat", "<%username> %message",
        // "String for formatting messages posted to the IRC channel by the bot").getString();

        boolean connectToIrc = FEenable.get();
        if (connectToIrc)
            ModuleChat.instance.ircHandler.connect();
        else
            ModuleChat.instance.ircHandler.disconnect();
    }
    /* ------------------------------------------------------------ */

    public void ircSendMessageUser(User user, String message)
    {
        if (!isConnected())
            return;
        // ignore messages to jtv
        if (twitchMode && user.getNick().equals("jtv"))
            return;
        user.send().message(message);
    }

    public void ircSendMessage(String message)
    {
        if (isConnected())
            for (String channel : channels)
                bot.sendIRC().message(channel, message);
    }

    public void sendPlayerMessage(CommandSource sender, ITextComponent message)
    {
        if (isConnected())
            ircSendMessage(String.format(mcHeader, sender.getTextName(), ChatOutputHandler.stripFormatting(message.getString())));
    }

    private void mcSendMessage(String message, User user)
    {
        String filteredMessage = ModuleChat.censor.filterIRC(message);
        ModuleChat.instance.logChatMessage("IRC-" + user.getNick(), filteredMessage);

        String headerText = String.format(ircHeader, user.getNick());
        ITextComponent header = ModuleChat.clickChatComponent(headerText, Action.SUGGEST_COMMAND, "/ircpm " + user.getNick() + " ");
        ITextComponent messageComponent = ModuleChat.filterChatLinks(ChatOutputHandler.formatColors(filteredMessage));
        ChatOutputHandler.broadcast(new TranslationTextComponent("%s%s", header, messageComponent));
    }

    private void mcSendMessage(String message)
    {
        String filteredMessage = ModuleChat.censor.filterIRC(message);
        ITextComponent header = ModuleChat.clickChatComponent(ircHeaderGlobal, Action.SUGGEST_COMMAND, "/irc ");
        ITextComponent messageComponent = ModuleChat.filterChatLinks(ChatOutputHandler.formatColors(filteredMessage));
        ChatOutputHandler.broadcast(new TranslationTextComponent("%s%s", header, messageComponent));
    }

    @SuppressWarnings("resource")
    public CommandSource getIrcUser(String username)
    {
        if (!isConnected())
            return null;
        for (User user : bot.getUserChannelDao().getAllUsers())
        {
            if (user.getNick().equals(username))
            {
                IrcCommandSender sender = new IrcCommandSender(user);
                ircUserCache.put(sender.getUser(), sender);
                return sender.createCommandSourceStack();
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
            ircSendMessageUser(user, String.format("Error: Command %s not found!", commandName));
            return;
        }

        IrcCommandSender sender = new IrcCommandSender(user);
        ircUserCache.put(sender.getUser(), sender);
        try
        {
            command.processCommand(sender.createCommandSourceStack(), args);
        }
        catch (CommandException e)
        {
            ircSendMessageUser(user, "Error: " + e.getMessage());
        }
    }

    private void processMcCommand(User user, String cmdLine)
    {
        if (!admins.contains(user.getNick()))
        {
            ircSendMessageUser(user, "Permission denied. You are not an admin");
            return;
        }

        String[] args = cmdLine.split(" ");
        String commandName = args[0].substring(1);
        args = Arrays.copyOfRange(args, 1, args.length);
        MinecraftServer server = ServerLifecycleHooks.getCurrentServer();
        
        IrcCommandSender sender = new IrcCommandSender(user);
        ircUserCache.put(sender.getUser(), sender);
        
        ParseResults<CommandSource> command = (ParseResults<CommandSource>) server.getCommands().getDispatcher().parse(commandName, sender.createCommandSourceStack());
        if (command.getReader().canRead() != true)
        {
            ircSendMessageUser(user, String.format("Error: Command %s not found!", commandName));
            return;
        }

        try
        {
            server.getCommands().performCommand(sender.createCommandSourceStack(), commandName.substring(1));
        }
        catch (CommandException e)
        {
            ircSendMessageUser(user, "Error: " + e.getMessage());
        }
    }

    /* ------------------------------------------------------------ */

    @SubscribeEvent(priority = EventPriority.LOW)
    public void chatEvent(ServerChatEvent event)
    {
        if (isConnected() && sendMessages)
            ircSendMessage(ChatOutputHandler.stripFormatting(event.getMessage()));
    }

    @SubscribeEvent(priority = EventPriority.LOWEST)
    public void playerLoginEvent(PlayerLoggedInEvent event)
    {
        if (showGameEvents)
            ircSendMessage(Translator.format("%s joined the game", event.getPlayer().getName()));
    }

    @SubscribeEvent(priority = EventPriority.LOWEST)
    public void playerLoginEvent(PlayerLoggedOutEvent event)
    {
        if (showGameEvents)
            ircSendMessage(Translator.format("%s left the game", event.getPlayer().getName()));
    }

    @SubscribeEvent(priority = EventPriority.LOWEST)
    public void playerDeathEvent(LivingDeathEvent event)
    {
        if (!(event.getEntityLiving() instanceof PlayerEntity))
            return;
        if (showGameEvents)
            ircSendMessage(Translator.format("%s died", event.getEntityLiving().getName()));
    }

    @SubscribeEvent(priority = EventPriority.LOWEST)
    public void handleSay(CommandEvent event)
    {
        if (event.getParseResults().getContext().getNodes().get(0).getNode().getName() == "say")
        {
            ircSendMessage(Translator.format(mcSayHeader, event.getParseResults().getContext().getSource().getTextName(),
                    StringUtils.join(event.getParseResults().getReader().toString().substring(5+event.getParseResults().getContext().getSource().getTextName().length()+1))));
        }
        else if (event.getParseResults().getContext().getNodes().get(0).getNode().getName() == "me")
        {
            ircSendMessage(
                    Translator.format("* %s %s", event.getParseResults().getContext().getSource().getTextName(), event.getParseResults().getReader().toString().substring(4+event.getParseResults().getContext().getSource().getTextName().length()+1)));
        }
    }

    @SubscribeEvent
    public void welcomeNewPlayers(NoPlayerInfoEvent e)
    {
        if (showGameEvents)
            ircSendMessage(Translator.format("New player %s has joined the server!", e.getPlayer()));
    }

    /* ------------------------------------------------------------ */

    @Override
    public void onPrivateMessage(PrivateMessageEvent event)
    {
        String raw = event.getMessage().trim();
        while (raw.startsWith(":"))
            raw = raw.replace(":", "");

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
            if (twitchMode && (event.getUser().getNick().equals("jtv")))
                return;
            ircSendMessageUser(event.getUser(), String.format("Hello %s, use %%help for commands", event.getUser().getNick()));
        }
    }

    @Override
    public void onMessage(MessageEvent event)
    {
        if (event.getUser().getNick().equalsIgnoreCase(bot.getNick()))
            return;

        String raw = event.getMessage().trim();
        while (raw.startsWith(":"))
            raw = raw.replace(":", "");

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
    public void onKick(KickEvent event)
    {
        if (event.getRecipient() != bot.getUserBot())
        {
            if (showEvents)
                mcSendMessage(String.format("%s has been kicked from %s by %s: %s", event.getRecipient().getNick(), event.getChannel().getName(), event
                        .getUser().getNick(), event.getReason()));
        }
        else
        {
            LoggingHandler.felog.warn(String.format("The IRC bot was kicked from %s by %s: %s", event.getChannel().getName(), event.getUser().getNick(),
                    event.getReason()));
        }
    }

    @Override
    public void onQuit(QuitEvent event)
    {
        if (!showEvents || event.getUser() == bot.getUserBot())
            return;
        mcSendMessage(String.format("%s left the channel: %s", event.getUser().getNick(), event.getReason()));
    }

    @Override
    public void onNickChange(NickChangeEvent event)
    {
        if (!showEvents || event.getUser() == bot.getUserBot())
            return;
        mcSendMessage(Translator.format("%s changed his nick to %s", event.getOldNick(), event.getNewNick()));
    }

    @Override
    public void onJoin(JoinEvent event) throws Exception
    {
        if (!showEvents || event.getUser() == bot.getUserBot())
            return;
        mcSendMessage(Translator.format("%s joined the channel %s", event.getUser().getNick(), event.getChannel().getName()));
    }

    @Override
    public void onPart(PartEvent event) throws Exception
    {
        ircUserCache.remove(event.getUser());
        if (!showEvents || event.getUser() == bot.getUserBot())
            return;
        mcSendMessage(Translator.format("%s left the channel %s: %s", event.getUser().getNick(), event.getChannel().getName(), event.getReason()));
    }

    @Override
    public void onConnect(ConnectEvent event) throws Exception
    {
        mcSendMessage("IRC bot connected to the network");
    }

    @Override
    public void onDisconnect(DisconnectEvent event) throws Exception
    {
        mcSendMessage("IRC bot disconnected from the network");
    }

    @Override
    public void onAction(ActionEvent event) throws Exception
    {
        mcSendMessage(Translator.format("* %s %s", event.getUser().getNick(), event.getMessage()));
    }

    @Override
    public void onNickAlreadyInUse(NickAlreadyInUseEvent event) throws Exception
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


	@Override
	public ConfigData returnData() {
		return ModuleChat.data;
	}
}
