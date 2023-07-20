package com.forgeessentials.chat.discord;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.forgeessentials.api.APIRegistry;
import com.forgeessentials.core.config.ConfigBase;
import com.forgeessentials.core.misc.Translator;
import com.forgeessentials.util.CommandUtils;
import com.forgeessentials.util.CommandUtils.CommandInfo;
import com.forgeessentials.util.events.FEModuleEvent.FEModuleServerStartedEvent;
import com.forgeessentials.util.events.FEModuleEvent.FEModuleServerStoppingEvent;
import com.forgeessentials.util.events.player.FEPlayerEvent.NoPlayerInfoEvent;
import com.forgeessentials.util.output.ChatOutputHandler;
import com.forgeessentials.util.output.logger.LoggingHandler;

import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.channel.ChannelType;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.exceptions.ErrorResponseException;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.requests.GatewayIntent;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.CommandEvent;
import net.minecraftforge.event.ServerChatEvent;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.event.entity.player.PlayerEvent.PlayerLoggedInEvent;
import net.minecraftforge.event.entity.player.PlayerEvent.PlayerLoggedOutEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.server.ServerLifecycleHooks;

public class DiscordHandler
{
    private static final String CATEGORY = "DISCORD";
    private static final String CHANNELS_HELP = "List of channels to connect to, not including the # character";

    private static final String ADMINS_HELP = "List of privileged users that can use more commands via the Discord bot";

    public Set<String> channels = new HashSet<>();
    private Long serverID;

    private Set<String> admins = new HashSet<>();

    private boolean showGameEvents;

    private boolean showMessages;

    private boolean sendMessages;

    public String selectedChannel;

    public ForgeConfigSpec.ConfigValue<String> FEselectedChannelConfig;
    static ForgeConfigSpec.ConfigValue<List<? extends String>> FEchannels;
    static ForgeConfigSpec.ConfigValue<List<? extends String>> FEadmins;
    public ForgeConfigSpec.ConfigValue<String> FEtoken;
    public ForgeConfigSpec.LongValue FEserverID;
    static ForgeConfigSpec.BooleanValue FEshowGameEvents;
    static ForgeConfigSpec.BooleanValue FEshowMessages;
    static ForgeConfigSpec.BooleanValue FEsendMessages;

    JDA jda = null;

    public DiscordHandler()
    {
        MinecraftForge.EVENT_BUS.register(this);
        APIRegistry.getFEEventBus().register(this);

    }

    public void load(ForgeConfigSpec.Builder BUILDER, boolean isReload)
    {
        BUILDER.comment("Configure the built-in Discord bot here -- Incubating, subject to change!").push(CATEGORY);

        FEselectedChannelConfig = BUILDER.comment(
                "The bot will send messages to this channel!  You can switch channels in game with /discord select (channel)")
                .define("selectedChannel", "");
        FEchannels = BUILDER.comment(CHANNELS_HELP).defineList("channels", new ArrayList<String>() {
            {
                add("#someChannelName");
            }
        }, ConfigBase.stringValidator);
        FEadmins = BUILDER.comment(ADMINS_HELP).defineList("admins", new ArrayList<>(),
                ConfigBase.stringValidator);

        FEtoken = BUILDER.comment("Discord Token for bot login").define("token", "");

        FEserverID = BUILDER.comment("Server ID").defineInRange("serverID", 0, 0, Long.MAX_VALUE);

        FEshowGameEvents = BUILDER.comment("Show game events in Discord (e.g., join, leave, death, etc.)")
                .define("showGameEvents", true);
        FEshowMessages = BUILDER.comment("Show chat messages from Discord ingame").define("showMessages", true);
        FEsendMessages = BUILDER.comment("If enabled, ingame messages will be sent to Discord as well")
                .define("sendMessages", true);
        BUILDER.pop();
    }

    public void bakeConfig(boolean reload)
    {
        selectedChannel = FEselectedChannelConfig.get();

        channels.clear();
        for (String channel : FEchannels.get())
        {
            if ("".equals(selectedChannel))
            {
                selectedChannel = channel;
            }
            channels.add(channel);
        }
        if (!channels.contains(selectedChannel))
        {
            channels.add(selectedChannel);
        }

        admins.clear();
        admins.addAll(FEadmins.get());

        String token = FEtoken.get();

        serverID = FEserverID.get();

        showGameEvents = FEshowGameEvents.get();
        showMessages = FEshowMessages.get();
        sendMessages = FEsendMessages.get();

        if (!"".equals(token) && serverID != 0)
        {
            if (jda != null)
            {
                jda.shutdown();
                jda = null;
            }

            jda = JDABuilder.createDefault(token).enableIntents(GatewayIntent.MESSAGE_CONTENT).build();
            jda.getPresence().setActivity(Activity.playing(ServerLifecycleHooks.getCurrentServer().getMotd()));
            jda.addEventListener(new MessageListener());
        }
    }

    public void save(boolean reload)
    {

        FEselectedChannelConfig.set(selectedChannel);
    }

    public boolean isConnected()
    {
        return jda != null;
    }

    public class MessageListener extends ListenerAdapter
    {
        @Override
        public void onMessageReceived(MessageReceivedEvent event)
        {
            if (showMessages && !event.isFromType(ChannelType.PRIVATE) && event.getGuild().getIdLong() == serverID
                    && event.getMember() != null && channels.contains(event.getChannel().getName())
                    && !event.getAuthor().equals(jda.getSelfUser()))
            {
                String suffix = String.format("<%s> %s", event.getMember().getEffectiveName(),
                        event.getMessage().getContentDisplay());
                String msg = selectedChannel.equals(event.getChannel().getName()) ? suffix
                        : String.format("#%s %s", event.getChannel().getName(), suffix);
                ChatOutputHandler.broadcast(msg, false);
            }
        }
    }

    public void sendMessage(String msg)
    {
        if (isConnected())
        {
            try
            {
                Guild guild = jda.getGuildById(serverID);
                if (guild != null)
                {
                    List<TextChannel> resolvedChannels = guild.getTextChannelsByName(selectedChannel, true);
                    if (!resolvedChannels.isEmpty())
                    {
                        resolvedChannels.get(0).sendMessage(msg).complete();
                    }
                }
            }
            catch (ErrorResponseException e)
            {
                LoggingHandler.felog.warn("Error Sending Discord Message: " + e.getMessage(), e);
            }
        }
    }

    @SubscribeEvent(priority = EventPriority.LOWEST)
    public void chatEvent(ServerChatEvent event)
    {
        if (sendMessages)
        {
            sendMessage(ChatOutputHandler.stripFormatting(event.getMessage()));
        }
    }

    @SubscribeEvent(priority = EventPriority.LOWEST)
    public void playerLoginEvent(PlayerLoggedInEvent event)
    {
        if (showGameEvents)
        {
            sendMessage(Translator.format("%s joined the game", event.getPlayer().getName()));
        }
    }

    @SubscribeEvent(priority = EventPriority.LOWEST)
    public void playerLoginEvent(PlayerLoggedOutEvent event)
    {
        if (showGameEvents)
        {
            sendMessage(Translator.format("%s left the game", event.getPlayer().getName()));
        }
    }

    @SubscribeEvent(priority = EventPriority.LOWEST)
    public void playerDeathEvent(LivingDeathEvent event)
    {
        if (!(event.getEntityLiving() instanceof PlayerEntity))
        {
            return;
        }
        if (showGameEvents)
        {
            sendMessage(Translator.format("%s died", event.getEntityLiving().getName()));
        }
    }

    @SubscribeEvent(priority = EventPriority.LOWEST)
    public void handleSay(CommandEvent event)
    {
        if (event.getParseResults().getContext().getNodes().isEmpty())
            return;
        CommandInfo info = CommandUtils.getCommandInfo(event);
        if (info.getCommandName().equals("say"))
        {
            sendMessage(Translator.format("[%s] %s", info.getSource().getTextName(), info.getActualArgsString()));
        }
        else if (info.getCommandName().equals("me"))
        {
            sendMessage(Translator.format("* %s %s", info.getSource().getTextName(), info.getActualArgsString()));
        }
    }

    public void serverStarted(FEModuleServerStartedEvent e)
    {
        if (showGameEvents)
        {
            sendMessage(Translator.translate("Server Started!"));
        }
    }

    public void serverStopping(FEModuleServerStoppingEvent e)
    {
        if (showGameEvents)
        {
            sendMessage(Translator.translate("Server Stopped!"));
        }
    }

    @SubscribeEvent(priority = EventPriority.LOWEST)
    public void welcomeNewPlayers(NoPlayerInfoEvent e)
    {
        if (showGameEvents)
        {
            sendMessage(Translator.format("New player %s has joined the server!", e.getPlayer()));
        }
    }
}
