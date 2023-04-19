package com.forgeessentials.chat.discord;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.channel.ChannelType;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.requests.GatewayIntent;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.server.MinecraftServer;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.common.config.Property;
import net.minecraftforge.event.CommandEvent;
import net.minecraftforge.event.ServerChatEvent;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.event.entity.player.AchievementEvent;

import org.apache.commons.lang3.StringUtils;

import com.forgeessentials.api.APIRegistry;
import com.forgeessentials.chat.ModuleChat;
import com.forgeessentials.commons.BuildInfo;
import com.forgeessentials.core.ForgeEssentials;
import com.forgeessentials.core.misc.Translator;
import com.forgeessentials.core.moduleLauncher.config.ConfigLoaderBase;
import com.forgeessentials.playerlogger.PlayerLoggerEventHandler;
import com.forgeessentials.util.ServerUtil;
import com.forgeessentials.util.events.FEModuleEvent.FEModuleServerPostInitEvent;
import com.forgeessentials.util.events.FEModuleEvent.FEModuleServerStopEvent;
import com.forgeessentials.util.events.FEPlayerEvent.NoPlayerInfoEvent;
import com.forgeessentials.util.output.ChatOutputHandler;

import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.eventhandler.EventPriority;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.PlayerEvent.PlayerLoggedInEvent;
import cpw.mods.fml.common.gameevent.PlayerEvent.PlayerLoggedOutEvent;

public class DiscordHandler extends ConfigLoaderBase
{
    private static final String CATEGORY = ModuleChat.CONFIG_CATEGORY + ".DISCORD";

    private static final String CHANNELS_HELP = "List of channels to connect to, not including the # character";

    private static final String ADMINS_HELP = "List of privileged users that can use more commands via the Discord bot";

    public Set<String> channels = new HashSet<>();
    private Long serverID;

    private Set<String> admins = new HashSet<>();

    private boolean showGameEvents;

    private boolean showMessages;

    private boolean sendMessages;

    public String selectedChannel;

    public Property selectedChannelConfig;

    JDA jda = null;

    public DiscordHandler()
    {
        ForgeEssentials.getConfigManager().registerLoader(ModuleChat.CONFIG_FILE, this);
        FMLCommonHandler.instance().bus().register(this);
        MinecraftForge.EVENT_BUS.register(this);
        APIRegistry.getFEEventBus().register(this);

    }
    @Override
    public void load(Configuration config, boolean isReload)
    {
        config.addCustomCategoryComment(CATEGORY, "Configure the built-in Discord bot here -- Incubating, subject to change!");

        channels.clear();
        selectedChannelConfig = config.get(CATEGORY, "selectedChannel", "",
                "The bot will send messages to this channel!  You can switch channels in game with `/discord select (channel)");
        selectedChannel = selectedChannelConfig.getString();
        for (String channel : config.get(CATEGORY, "channels", new String[] { "general" }, CHANNELS_HELP).getStringList())
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
        for (String admin : config.get(CATEGORY, "admins", new String[] {}, ADMINS_HELP).getStringList())
        {
            admins.add(admin);
        }

        String token = config.getString("token", CATEGORY, "", "Discord Token for bot login");

        serverID = ServerUtil.parseLongDefault(config.getString("serverID", CATEGORY, "", "Server ID"), 0);

        showGameEvents = config.get(CATEGORY, "showGameEvents", true, "Show game events in Discord (e.g., join, leave, death, etc.)").getBoolean();
        showMessages = config.get(CATEGORY, "showMessages", true, "Show chat messages from Discord ingame").getBoolean();
        sendMessages = config.get(CATEGORY, "sendMessages", true, "If enabled, ingame messages will be sent to Discord as well").getBoolean();

        if (!"".equals(token) && serverID != 0)
        {
            if (jda != null)
            {
                jda.shutdown();
                jda = null;
            }

            jda = JDABuilder.createDefault(token)
                    .enableIntents(GatewayIntent.MESSAGE_CONTENT)
                    .build();
            jda.getPresence().setActivity(Activity.playing(MinecraftServer.getServer().getMOTD()));
            jda.addEventListener(new MessageListener());
        }
    }

    @Override public void save(Configuration config)
    {
        selectedChannelConfig.set(selectedChannel);
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
            if (showMessages && !event.isFromType(ChannelType.PRIVATE) && event.getGuild().getIdLong() == serverID && event.getMember() != null
                    && channels.contains(event.getChannel().getName()) && !event.getAuthor().equals(jda.getSelfUser()))
            {
                String suffix = String.format("<%s> %s", event.getMember().getEffectiveName(),
                        event.getMessage().getContentDisplay());
                String msg = selectedChannel.equals(event.getChannel().getName()) ? suffix : String.format("#%s %s", event.getChannel().getName(), suffix);
                ChatOutputHandler.broadcast(msg, false);
            }
        }
    }

    public void sendMessage(String msg)
    {
        if (isConnected())
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
    }

    @SubscribeEvent(priority = EventPriority.LOWEST)
    public void chatEvent(ServerChatEvent event)
    {
        if (sendMessages)
        {
            sendMessage(ChatOutputHandler.stripFormatting(event.component.getUnformattedText()));
        }
    }

    @SubscribeEvent(priority = EventPriority.LOWEST)
    public void playerLoginEvent(PlayerLoggedInEvent event)
    {
        if (showGameEvents)
        {
            sendMessage(Translator.format("%s joined the game", event.player.getCommandSenderName()));
        }
    }

    @SubscribeEvent(priority = EventPriority.LOWEST)
    public void playerLoginEvent(PlayerLoggedOutEvent event)
    {
        if (showGameEvents)
        {
            sendMessage(Translator.format("%s left the game", event.player.getCommandSenderName()));
        }
    }

    @SubscribeEvent(priority = EventPriority.LOWEST)
    public void playerDeathEvent(LivingDeathEvent event)
    {
        if (!(event.entityLiving instanceof EntityPlayer))
        {
            return;
        }
        if (showGameEvents)
        {
            sendMessage(Translator.format("%s died", event.entityLiving.getCommandSenderName()));
        }
    }

    @SubscribeEvent(priority = EventPriority.LOWEST)
    public void handleSay(CommandEvent event)
    {
        if (event.command.getCommandName().equals("say"))
        {
            sendMessage(Translator.format("[%s] %s", event.sender.getCommandSenderName(), StringUtils.join(event.parameters, " ")));
        }
        else if (event.command.getCommandName().equals("me"))
        {
            sendMessage(Translator.format("* %s %s", event.sender.getCommandSenderName(), StringUtils.join(event.parameters, " ")));
        }
    }

    public void serverStarted(FEModuleServerPostInitEvent e)
    {
        if (showGameEvents)
        {
            sendMessage(Translator.translate("Server Started!"));
        }
    }

    public void serverStopping(FEModuleServerStopEvent e)
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
