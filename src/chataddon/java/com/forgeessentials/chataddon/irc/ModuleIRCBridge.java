package com.forgeessentials.chataddon.irc;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

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
import org.pircbotx.hooks.events.QuitEvent;

import com.forgeessentials.chataddon.FEChatAddons;
import com.forgeessentials.chataddon.irc.commands.CommandHelp;
import com.forgeessentials.chataddon.irc.commands.CommandListPlayers;
import com.forgeessentials.core.ForgeEssentials;
import com.forgeessentials.core.commands.registration.FECommandManager;
import com.forgeessentials.core.config.ConfigBase;
import com.forgeessentials.core.config.ConfigData;
import com.forgeessentials.core.config.ConfigSaver;
import com.forgeessentials.core.misc.Translator;
import com.forgeessentials.core.moduleLauncher.FEModule;
import com.forgeessentials.util.CommandUtils;
import com.forgeessentials.util.CommandUtils.CommandInfo;
import com.forgeessentials.util.events.FEModuleEvent.FEModuleServerStoppingEvent;
import com.forgeessentials.util.events.player.FEPlayerEvent.NoPlayerInfoEvent;
import com.forgeessentials.util.output.ChatOutputHandler;
import com.forgeessentials.util.output.logger.LoggingHandler;
import com.mojang.brigadier.ParseResults;

import net.minecraft.commands.CommandRuntimeException;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.network.chat.BaseComponent;
import net.minecraft.network.chat.ClickEvent.Action;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.common.ForgeConfigSpec.Builder;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.CommandEvent;
import net.minecraftforge.event.RegisterCommandsEvent;
import net.minecraftforge.event.ServerChatEvent;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.event.entity.player.PlayerEvent.PlayerLoggedInEvent;
import net.minecraftforge.event.entity.player.PlayerEvent.PlayerLoggedOutEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fmllegacy.server.ServerLifecycleHooks;

@FEModule(name = "IrcBridge", parentMod = FEChatAddons.class, defaultModule = false, version=ForgeEssentials.CURRENT_MODULE_VERSION)
public class ModuleIRCBridge extends ListenerAdapter implements ConfigSaver
{
    private static ForgeConfigSpec IRC_CONFIG;
	private static final ConfigData data = new ConfigData("IrcBridge", IRC_CONFIG, new ForgeConfigSpec.Builder());
	@FEModule.Instance
    public static ModuleIRCBridge instance;
    private static final String CATEGORY = "IRC";

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

    /* ------------------------------------------------------------ */

    public ModuleIRCBridge()
    {
        MinecraftForge.EVENT_BUS.register(this);

        registerCommand(new CommandHelp());
        registerCommand(new CommandListPlayers());
    }

    @SubscribeEvent
    public void registerCommands(RegisterCommandsEvent event)
    {
    	FECommandManager.registerCommand(new CommandIrcBot(true), event.getDispatcher());
    	FECommandManager.registerCommand(new CommandIrc(true), event.getDispatcher());
    }

    public static ModuleIRCBridge getInstance()
    {
        return instance;
    }

    public void registerCommand(IrcCommand command)
    {
        for (String commandName : command.getCommandNames())
            if (commands.put(commandName, command) != null)
                LoggingHandler.felog.warn(String.format("IRC command name %s used twice!", commandName));
    }

    @SubscribeEvent
    public void serverStopping(FEModuleServerStoppingEvent e)
    {
        disconnect();
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
        }, "FEircHandler");

        connectionThread.start();

    }

    public void disconnect()
    {
        if (bot != null && bot.isConnected())
        {
            bot.sendIRC().quitServer();
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
        builder.addServer(server, port);
        builder.setServerPassword(serverPassword.isEmpty() ? "" : serverPassword);

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
    static ForgeConfigSpec.ConfigValue<List<? extends String>> FEchannels;
    static ForgeConfigSpec.ConfigValue<List<? extends String>> FEadmins;
    static ForgeConfigSpec.BooleanValue FEenable;

    @Override
    public void load(Builder BUILDER, boolean isReload)
    {
        BUILDER.comment("Configure the built-in IRC bot here").push(CATEGORY);
        FEserver = BUILDER.comment("Server address").define("server", "irc.something.com");
        FEport = BUILDER.comment("Server port").defineInRange("port", 6667, 0, 65535);
        FEbotName = BUILDER.comment("Bot name").define("botName", "FEIRCBot");
        FEserverPassword = BUILDER.comment("Server password").define("serverPassword", "");
        FEnickPassword = BUILDER.comment("NickServ password").define("nickPassword", "");
        FEtwitchMode = BUILDER.comment("If set to true, sets connection to twitch mode").define("twitchMode", false);
        FEshowEvents = BUILDER.comment("Show IRC events ingame (e.g., join, leave, kick, etc.)").define("showEvents",
                true);
        FEshowGameEvents = BUILDER.comment("Show game events in IRC (e.g., join, leave, death, etc.)")
                .define("showGameEvents", true);
        FEshowMessages = BUILDER.comment("Show chat messages from IRC ingame").define("showMessages", true);
        FEsendMessages = BUILDER.comment("If enabled, ingame messages will be sent to IRC as well")
                .define("sendMessages", false);
        FEircHeader = BUILDER.comment("Header for messages sent from MC to IRC. Must contain two \"%s\"")
                .define("ircHeader", "[\u00a7cIRC\u00a7r]<%s> ");
        FEircHeaderGlobal = BUILDER.comment("Header for IRC events. Must NOT contain any \"%s\"")
                .define("ircHeaderGlobal", "[\u00a7cIRC\u00a7r] ");
        FEmcHeader = BUILDER.comment("Header for messages sent from MC to IRC. Must contain two \"%s\"")
                .define("mcHeader", "<%s> %s");
        FEmcSayHeader = BUILDER
                .comment("Header for messages sent with the /say command from MC to IRC. Must contain two \"%s\"")
                .define("mcSayHeader", "[%s] %s");
        FEmessageDelay = BUILDER.comment("Delay between messages sent to IRC").defineInRange("messageDelay", 0, 0, 60);
        FEallowCommands = BUILDER.comment("If enabled, allows usage of bot commands").define("allowCommands", true);
        FEallowMcCommands = BUILDER.comment(
                "If enabled, allows usage of MC commands through the bot (only if the IRC user is in the admins list)")
                .define("allowMcCommands", true);
        FEchannels = BUILDER.comment(CHANNELS_HELP).defineList("channels", new ArrayList<String>() {
            {
                add("#someChannelName");
            }
        }, ConfigBase.stringValidator);
        FEadmins = BUILDER.comment(ADMINS_HELP).defineList("admins", new ArrayList<>(),
                ConfigBase.stringValidator);
        FEenable = BUILDER.comment("Enable IRC interoperability?").define("enable", false);
        BUILDER.pop();
    }

    @Override
    public void bakeConfig(boolean reload)
    {
    	instance.server = FEserver.get();
    	instance.port = FEport.get();
    	instance.botName = FEbotName.get();
    	instance.serverPassword = FEserverPassword.get();
    	instance.nickPassword = FEnickPassword.get();
    	instance.twitchMode = FEtwitchMode.get();
    	instance.showEvents = FEshowEvents.get();
    	instance.showGameEvents = FEshowGameEvents.get();
    	instance.showMessages = FEshowMessages.get();
    	instance.sendMessages = FEsendMessages.get();
    	instance.ircHeader = FEircHeader.get();
    	instance.ircHeaderGlobal = FEircHeaderGlobal.get();
    	instance.mcHeader = FEmcHeader.get();
    	instance.mcSayHeader = FEmcSayHeader.get();
    	instance.messageDelay = FEmessageDelay.get();
    	instance.allowCommands = FEallowCommands.get();
    	instance.allowMcCommands = FEallowMcCommands.get();

    	instance.channels.clear();
    	instance.channels.addAll(FEchannels.get());

    	instance.admins.clear();
    	instance.admins.addAll(FEadmins.get());

        // mcHeader = config.get(CATEGORY, "mcFormat", "<%username> %message",
        // "String for formatting messages posted to the IRC channel by the
        // bot").getString();

        boolean connectToIrc = FEenable.get();
        if (connectToIrc)
        	instance.connect();
        else
        	instance.disconnect();
    }
    /* ------------------------------------------------------------ */

    public void ircSendMessage(String message)
    {
        if (isConnected())
            for (String channel : channels)
                bot.sendIRC().message(channel, message);
    }

    public void sendPlayerMessage(CommandSourceStack sender, BaseComponent message)
    {
        if (isConnected())
            ircSendMessage(String.format(mcHeader, sender.getTextName(),
                    ChatOutputHandler.stripFormatting(message.getString())));
    }

    private void mcSendMessage(String message, User user)
    {
        //String filteredMessage = ModuleChat.censor.filterIRC(message);
        String headerText = String.format(ircHeader, user.getNick());
        BaseComponent header = ChatOutputHandler.clickChatComponent(headerText, Action.SUGGEST_COMMAND,
                "/ircpm " + user.getNick() + " ");
        BaseComponent messageComponent = ChatOutputHandler.filterChatLinks(ChatOutputHandler.formatColors(message));
        ChatOutputHandler.broadcast(header.append(messageComponent));
    }

    private void mcSendMessage(String message)
    {
        //String filteredMessage = ModuleChat.censor.filterIRC(message);
    	BaseComponent header =ChatOutputHandler.clickChatComponent(ircHeaderGlobal, Action.SUGGEST_COMMAND, "/irc ");
    	BaseComponent messageComponent = ChatOutputHandler.filterChatLinks(ChatOutputHandler.formatColors(message));
        ChatOutputHandler.broadcast(header.append(messageComponent));
    }

    private void processCommand(MessageEvent event, String cmdLine)
    {
        String[] args = cmdLine.split(" ");
        String commandName = args[0].substring(1);
        args = Arrays.copyOfRange(args, 1, args.length);

        IrcCommand command = commands.get(commandName);
        if (command == null)
        {
            event.respondWith(String.format("Error: Command %s not found!", commandName));
            return;
        }

        try
        {
            command.processCommand(event, args);
        }
        catch (CommandRuntimeException e)
        {
            event.respondWith("Error: " + e.getMessage());
        }
    }

    private void processMcCommand(MessageEvent event, String cmdLine)
    {
        if (!admins.contains(event.getUser().getNick()))
        {
        	event.respondWith("Permission denied. You are not an admin");
            return;
        }

        String commandRaw = cmdLine.substring(1);
        MinecraftServer server = ServerLifecycleHooks.getCurrentServer();

        CommandSourceStack sender= new IrcCommandFaker().createCommandSourceStack(4, event);
        ParseResults<CommandSourceStack> command = (ParseResults<CommandSourceStack>) server.getCommands().getDispatcher()
                .parse(commandRaw, sender);
        if (command.getContext().getNodes().isEmpty())
        {
        	event.respondWith(String.format("Error: Command \"%s\" not found!", commandRaw));
            return;
        }

        try
        {
            server.getCommands().performCommand(sender, commandRaw);
        }
        catch (CommandRuntimeException e)
        {
        	event.respondWith("Error: " + e.getMessage());
        }
    }

    /* ------------------------------------------------------------ */

    @SubscribeEvent(priority = EventPriority.LOW)
    public void chatEvent(ServerChatEvent event)
    {
        if (isConnected() && sendMessages)
            sendPlayerMessage(event.getPlayer().createCommandSourceStack(), new TextComponent(ChatOutputHandler.stripFormatting(event.getMessage())));
    }

    @SubscribeEvent(priority = EventPriority.LOWEST)
    public void playerLoginEvent(PlayerLoggedInEvent event)
    {
        if (showGameEvents)
            ircSendMessage(Translator.format("%s joined the game", event.getPlayer().getDisplayName().getString()));
    }

    @SubscribeEvent(priority = EventPriority.LOWEST)
    public void playerLoginEvent(PlayerLoggedOutEvent event)
    {
        if (showGameEvents)
            ircSendMessage(Translator.format("%s left the game", event.getPlayer().getDisplayName().getString()));
    }

    @SubscribeEvent(priority = EventPriority.LOWEST)
    public void playerDeathEvent(LivingDeathEvent event)
    {
        if (!(event.getEntityLiving() instanceof Player))
            return;
        if (showGameEvents)
            ircSendMessage(Translator.format("%s died", event.getEntityLiving().getDisplayName().getString()));
    }

    @SubscribeEvent(priority = EventPriority.LOWEST)
    public void handleSay(CommandEvent event)
    {
        if (event.getParseResults().getContext().getNodes().isEmpty())
            return;
        CommandInfo info = CommandUtils.getCommandInfo(event);
        if (info.getCommandName().equals("say"))
        {
            ircSendMessage(Translator.format(mcSayHeader, info.getSource().getTextName(), info.getActualArgsString()));
        }
        else if (info.getCommandName().equals("me"))
        {
            ircSendMessage(Translator.format("* %s %s", info.getSource().getTextName(), info.getActualArgsString()));
        }
    }

    @SubscribeEvent
    public void welcomeNewPlayers(NoPlayerInfoEvent e)
    {
        if (showGameEvents)
            ircSendMessage(Translator.format("New player %s has joined the server!", e.getPlayer().getDisplayName().getString()));
    }

    /* ------------------------------------------------------------ */

//    @Override
//    public void onPrivateMessage(PrivateMessageEvent event)
//    {
//        System.out.println("E1");
//        String raw = event.getMessage().trim();
//        while (raw.startsWith(":"))
//            raw = raw.replace(":", "");
//
//        // Check to see if it is a command
//        if (raw.startsWith(COMMAND_CHAR) && allowCommands)
//        {
//            processCommand(event, raw);
//        }
//        else if (raw.startsWith(COMMAND_MC_CHAR) && allowMcCommands)
//        {
//            processMcCommand(event, raw);
//        }
//        else
//        {
//            if (twitchMode && (event.getUser().getNick().equals("jtv")))
//                return;
//            ircSendMessageUser(event.getUser(),
//                    String.format("Hello %s, use %%help for commands", event.getUser().getNick()));
//        }
//    }

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
            processCommand(event, raw);
        }
        else if (raw.startsWith(COMMAND_MC_CHAR) && allowMcCommands)
        {
            processMcCommand(event, raw);
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
                mcSendMessage(String.format("%s has been kicked from %s by %s: %s", event.getRecipient().getNick(),
                        event.getChannel().getName(), event.getUser().getNick(), event.getReason()));
        }
        else
        {
            LoggingHandler.felog.warn(String.format("The IRC bot was kicked from %s by %s: %s",
                    event.getChannel().getName(), event.getUser().getNick(), event.getReason()));
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
        if (!showEvents || event.getUser() == bot.getUserBot())
            return;
        mcSendMessage(Translator.format("%s left the channel %s: %s", event.getUser().getNick(),
                event.getChannel().getName(), event.getReason()));
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
        LoggingHandler.felog.warn(Translator.format("Nick %s already in use, switching to nick %s", event.getUsedNick(),
                event.getAutoNewNick()));
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
		return data;
	}

	@Override
	public void save(boolean reload) {
	}

	public String getServer() {
		return server;
	}

	public Set<String> getChannels() {
		return channels;
	}
}
