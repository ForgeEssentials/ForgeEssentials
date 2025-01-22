package com.forgeessentials.chat.discord;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import com.forgeessentials.chat.ChatConfig;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Message.Attachment;
import net.dv8tion.jda.api.entities.channel.ChannelType;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.exceptions.ErrorResponseException;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.requests.GatewayIntent;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.event.ClickEvent;
import net.minecraft.event.ClickEvent.Action;
import net.minecraft.event.HoverEvent;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.IChatComponent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.common.config.Property;
import net.minecraftforge.common.util.FakePlayer;
import net.minecraftforge.event.CommandEvent;
import net.minecraftforge.event.ServerChatEvent;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.event.entity.player.AchievementEvent;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.PlayerEvent.PlayerLoggedInEvent;
import net.minecraftforge.fml.common.gameevent.PlayerEvent.PlayerLoggedOutEvent;

import org.apache.commons.lang3.StringUtils;

import com.forgeessentials.api.APIRegistry;
import com.forgeessentials.chat.ModuleChat;
import com.forgeessentials.core.ForgeEssentials;
import com.forgeessentials.core.misc.Translator;
import com.forgeessentials.core.moduleLauncher.config.ConfigLoaderBase;
import com.forgeessentials.util.ServerUtil;
import com.forgeessentials.util.events.FEModuleEvent.FEModuleServerPostInitEvent;
import com.forgeessentials.util.events.FEModuleEvent.FEModuleServerStopEvent;
import com.forgeessentials.util.events.FEPlayerEvent.NoPlayerInfoEvent;
import com.forgeessentials.util.output.ChatOutputHandler;
import com.forgeessentials.util.output.LoggingHandler;

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
        MinecraftForge.EVENT_BUS.register(this);
        APIRegistry.getFEEventBus().register(this);

    }
    @Override
    public void load(Configuration config, boolean isReload)
    {
        config.addCustomCategoryComment(CATEGORY, "Configure the built-in Discord bot here -- Incubating, subject to change!");

        channels.clear();
        selectedChannelConfig = config.get(CATEGORY, "selectedChannel", "",
                "The bot will send messages to this channel!  You can switch channels in game with `/discord select (channel)`!  Note, this will append to the channel list below!");
        selectedChannel = selectedChannelConfig.getString().trim();
        for (String channel : config.get(CATEGORY, "channels", new String[] { "general" }, CHANNELS_HELP).getStringList())
        {
            channel = channel.trim();
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
            String motd = MinecraftServer.getServer().getMOTD().replaceAll("§.","");
            if (motd.length() > 128) {
                motd = motd.substring(0,128);
            }
            jda.getPresence().setActivity(Activity.playing(motd));
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

    private String convertToMC(String msg, String format)
    {
        return msg.replaceAll("(?<=\\s|^)\\*\\*\\*([^*]+)\\*\\*\\*", "&l&o$1" + (format != null ? ("&"+format) : ""))
                .replaceAll("(?<=\\s|^)\\*\\*([^*]+)\\*\\*","&l$1&r" + (format != null ? ("&"+format) : ""))
                .replaceAll("(?<=\\s|^)\\*([^*]+)\\*","&o$1&r" + (format != null ? ("&"+format) : ""))
                .replaceAll("(?<=\\s|^)_([^_]+)_","&o$1&r" + (format != null ? ("&"+format) : ""))
                .replaceAll("(?<=\\s|^)__\\*([^_]+)\\*__", "&n&o$1&r" + (format != null ? ("&"+format) : ""))
                .replaceAll("(?<=\\s|^)__\\*\\*([^*]+)\\*\\*__", "&n&l$1&r" + (format != null ? ("&"+format) : ""))
                .replaceAll("(?<=\\s|^)__\\*\\*\\*([^*]+)\\*\\*\\*__", "&n&l&o$1&r" + (format != null ? ("&"+format) : ""))
                .replaceAll("(?<=\\s|^)__([^_]+)__", "&n$1&r" + (format != null ? ("&"+format) : ""))
                .replaceAll("(?<=\\s|^)~~([^~]*)~~", "&m$1&f&"+format)
                .replaceFirst("^#+(\\s*)", "$l$1");
    }

    public class MessageListener extends ListenerAdapter
    {
        @Override
        public void onMessageReceived(MessageReceivedEvent event)
        {
            if (showMessages && !event.isFromType(ChannelType.PRIVATE) && event.getGuild().getIdLong() == serverID && event.getMember() != null
                    && channels.contains(event.getChannel().getName()) && !event.getAuthor().equals(jda.getSelfUser()))
            {
                String content = event.getMessage().getContentDisplay();
                String suffix = String.format("<%s> %s", event.getMember().getEffectiveName(),
                        content);

                String msg = selectedChannel.equals(event.getChannel().getName()) ? suffix : String.format("#%s %s", event.getChannel().getName(), suffix);

                try
                {
                    String textFormats = APIRegistry.perms.getGlobalPermissionProperty(ModuleChat.getPermTextformat());

                    String text = (textFormats != null ? ("&" + textFormats) : "") + convertToMC(msg, textFormats);
                    if (!event.getMessage().getAttachments().isEmpty()) {
                        text += !content.isEmpty() ? " : " : "Attachments: ";
                    }
                    IChatComponent _msg = new ChatComponentText(ChatOutputHandler.formatColors(text));

                    List<Attachment> attachments = event.getMessage().getAttachments();
                    if (!attachments.isEmpty()) {
                        for (int i = 0; i < attachments.size(); i++)
                        {
                            String desc = attachments.get(i).getDescription();
                            String fileName = attachments.get(i).getFileName();


                            if (i != attachments.size() - 1) {
                                fileName += " ";
                            }
                            ChatComponentText link = new ChatComponentText(fileName);
                            if (textFormats != null) {
                            ChatOutputHandler.applyFormatting(link.getChatStyle(), ChatOutputHandler.enumChatFormattings(textFormats));
                            }
                            link.getChatStyle().setChatClickEvent(new ClickEvent(Action.OPEN_URL, attachments.get(i).getUrl()));
                            link.getChatStyle().setUnderlined(true);
                            if (desc != null)
                            {
                                link.getChatStyle().setChatHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ChatComponentText(desc)));
                            }
                            _msg.appendSibling(link);
                        }
                    }
                    if (ChatConfig.logDiscord)
                    {
                        ModuleChat.instance.logChatMessage("Discord:" + event.getChannel().getName() + ":" + event.getMember().getEffectiveName(), content);
                    }
                    ChatOutputHandler.broadcast(_msg, false);
                } catch (Exception e) { //Catch Exceptions to prevent a crash if the server isn't fully loaded yet when a message is received
                    LoggingHandler.felog.warn("Error caught when receiving message: " + msg + " " + e.getMessage(), e);
                }
            }
        }
    }

    private boolean checkMessage(String msg)
    {
        return msg.contains("New player EntityPlayerMP");
    }

    public void sendMessage(String msg)
    {
        if (checkMessage(msg)) {
            return;
        }

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
            } catch (ErrorResponseException e) {
                LoggingHandler.felog.warn("Error Sending Discord Message: {}", e.getMessage(), e);
            }
        }
    }

    HashMap<UUID, HashSet<String>> playerMap = new HashMap<>();
    @SubscribeEvent(priority =  EventPriority.LOWEST)
    public  void achievementEvent(AchievementEvent event) {
        if (sendMessages && !(event.entityPlayer instanceof FakePlayer) && event.entityPlayer instanceof EntityPlayerMP && !((EntityPlayerMP) event.entityPlayer).getStatFile().hasAchievementUnlocked(event.achievement))
        {
            if (!playerMap.containsKey(event.entityPlayer.getUniqueID()))
            {
                playerMap.put(event.entityPlayer.getUniqueID(), new HashSet<>());
            }

            if (playerMap.get(event.entityPlayer.getUniqueID()).contains(event.achievement.statId)) {
                LoggingHandler.felog.debug("Duplicate Entry Detected for {}:{}! {}", event.entityPlayer.getUniqueID(),event.entityPlayer.getName(), event.achievement.statId);
                return;
            }

            playerMap.get(event.entityPlayer.getUniqueID()).add(event.achievement.statId);
            LoggingHandler.felog.debug(event.achievement.toString());
            sendMessage(Translator.format("%s has just earned the achievement ***`%s`***", event.entityPlayer.getName(), event.achievement.getStatName().getUnformattedText()));

        }
    }

    @SubscribeEvent(priority = EventPriority.LOWEST)
    public void chatEvent(ServerChatEvent event)
    {
        if (sendMessages&& !(event.player instanceof FakePlayer))
        {
            sendMessage(ChatOutputHandler.stripFormatting(event.getComponent().getUnformattedText()));
        }
    }

    @SubscribeEvent(priority = EventPriority.LOWEST)
    public void playerLoginEvent(PlayerLoggedInEvent event)
    {
        if (showGameEvents && !(event.player instanceof FakePlayer))
        {
            sendMessage(Translator.format("%s joined the game", event.player.getName()));
        }
    }

    @SubscribeEvent(priority = EventPriority.LOWEST)
    public void playerLoginEvent(PlayerLoggedOutEvent event)
    {
        if (showGameEvents && !(event.player instanceof FakePlayer))
        {
            sendMessage(Translator.format("%s left the game", event.player.getName()));
        }
    }

    @SubscribeEvent(priority = EventPriority.LOWEST)
    public void playerDeathEvent(LivingDeathEvent event)
    {
        if (!(event.entityLiving instanceof EntityPlayer))
        {
            return;
        }
        if (showGameEvents && !(event.entityLiving instanceof FakePlayer))
        {
            sendMessage(Translator.format("_%s died_", event.entityLiving.getName()));
        }
    }

    @SubscribeEvent(priority = EventPriority.LOWEST)
    public void handleSay(CommandEvent event)
    {
        if (event.command.getCommandName().equals("say"))
        {
            sendMessage(Translator.format("[%s] %s", event.sender.getName(), StringUtils.join(event.parameters, " ")));
        }
        else if (event.command.getCommandName().equals("me"))
        {
            sendMessage(Translator.format("* %s %s", event.sender.getName(), StringUtils.join(event.parameters, " ")));
        }
    }

    public void serverStarted(FEModuleServerPostInitEvent e)
    {
        if (showGameEvents)
        {
            sendMessage(Translator.translate("**Server Started!**"));
        }
    }

    public void serverStopping(FEModuleServerStopEvent e)
    {
        if (showGameEvents)
        {
            sendMessage(Translator.translate("**Server Stopped!**"));
        }
    }

    @SubscribeEvent(priority = EventPriority.LOWEST)
    public void welcomeNewPlayers(NoPlayerInfoEvent e)
    {
        if (showGameEvents)
        {
            sendMessage(Translator.format("***New player %s has joined the server!***", e.getPlayer()));
        }
    }
}
