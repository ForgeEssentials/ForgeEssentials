package com.forgeessentials.chat;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import net.minecraft.command.ICommandSender;
import net.minecraft.command.server.CommandMessage;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.event.ClickEvent;
import net.minecraft.event.ClickEvent.Action;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.ChatComponentTranslation;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.IChatComponent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.CommandEvent;
import net.minecraftforge.event.ServerChatEvent;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.PlayerEvent;
import net.minecraftforge.permission.PermissionLevel;

import com.forgeessentials.api.APIRegistry;
import com.forgeessentials.api.UserIdent;
import com.forgeessentials.api.permissions.FEPermissions;
import com.forgeessentials.api.permissions.GroupEntry;
import com.forgeessentials.api.permissions.ServerZone;
import com.forgeessentials.chat.command.CommandGroupMessage;
import com.forgeessentials.chat.command.CommandIrc;
import com.forgeessentials.chat.command.CommandIrcBot;
import com.forgeessentials.chat.command.CommandIrcPm;
import com.forgeessentials.chat.command.CommandMessageReplacement;
import com.forgeessentials.chat.command.CommandMute;
import com.forgeessentials.chat.command.CommandNickname;
import com.forgeessentials.chat.command.CommandPm;
import com.forgeessentials.chat.command.CommandReply;
import com.forgeessentials.chat.command.CommandTimedMessages;
import com.forgeessentials.chat.command.CommandUnmute;
import com.forgeessentials.chat.irc.IrcHandler;
import com.forgeessentials.commands.util.ModuleCommandsEventHandler;
import com.forgeessentials.commons.selections.WorldPoint;
import com.forgeessentials.core.ForgeEssentials;
import com.forgeessentials.core.misc.FECommandManager;
import com.forgeessentials.core.moduleLauncher.FEModule;
import com.forgeessentials.scripting.ScriptArguments;
import com.forgeessentials.util.PlayerUtil;
import com.forgeessentials.util.ServerUtil;
import com.forgeessentials.util.events.FEModuleEvent.FEModuleInitEvent;
import com.forgeessentials.util.events.FEModuleEvent.FEModuleServerInitEvent;
import com.forgeessentials.util.events.FEModuleEvent.FEModuleServerPostInitEvent;
import com.forgeessentials.util.events.FEModuleEvent.FEModuleServerStopEvent;
import com.forgeessentials.util.events.FEPlayerEvent.NoPlayerInfoEvent;
import com.forgeessentials.util.output.ChatOutputHandler;
import com.forgeessentials.util.output.LoggingHandler;

@FEModule(name = "Chat", parentMod = ForgeEssentials.class)
public class ModuleChat
{

    public static final String CONFIG_FILE = "Chat";

    public static final String CONFIG_CATEGORY = "Chat";

    public static final String PERM = "fe.chat";

    public static final String PERM_CHAT = PERM + ".chat";

    public static final String PERM_COLOR = PERM + ".usecolor";

    public static final String PERM_URL = PERM + ".urls";

    private static final String PERM_TEXTFORMAT = PERM + ".textformat";

    private static final String PERM_PLAYERFORMAT = PERM + ".playerformat";

    public static final String PERM_RANGE = PERM + ".range";

    // @formatter:off
    static final Pattern URL_PATTERN = Pattern.compile(
            //         schema                          ipv4            OR           namespace                 port     path         ends
            //   |-----------------|        |-------------------------|  |----------------------------|    |---------| |--|   |---------------|
            "((?:(?:http|https):\\/\\/)?(?:(?:[0-9]{1,3}\\.){3}[0-9]{1,3}|(?:[-\\w_\\.]{1,}\\.[a-z]{2,}?))(?::[0-9]{1,5})?.*?(?=[!\"\u00A7 \n]|$))",
            Pattern.CASE_INSENSITIVE);
    // @formatter:on

    public static final Map<String, String> chatConstReplacements = new HashMap<>();

    @FEModule.Instance
    public static ModuleChat instance;

    private PrintWriter logWriter;

    public static Censor censor;

    public Mailer mailer;

    public IrcHandler ircHandler;

    /* ------------------------------------------------------------ */

    @SubscribeEvent
    public void moduleLoad(FEModuleInitEvent e)
    {
        MinecraftForge.EVENT_BUS.register(this);
        FMLCommonHandler.instance().bus().register(this);

        ForgeEssentials.getConfigManager().registerLoader(CONFIG_FILE, new ChatConfig());

        ircHandler = new IrcHandler();
        censor = new Censor();
        mailer = new Mailer();

        setupChatReplacements();
    }

    public void setupChatReplacements()
    {
        chatConstReplacements.put("smile", "\u263A");
        chatConstReplacements.put("copyrighted", "\u00A9");
        chatConstReplacements.put("registered", "\u00AE");
        chatConstReplacements.put("diamond", "\u2662");
        chatConstReplacements.put("spade", "\u2664");
        chatConstReplacements.put("club", "\u2667");
        chatConstReplacements.put("heart", "\u2661");
        chatConstReplacements.put("female", "\u2640");
        chatConstReplacements.put("male", "\u2642");

        // replace colors
        chatConstReplacements.put("red", EnumChatFormatting.RED.toString());
        chatConstReplacements.put("yellow", EnumChatFormatting.YELLOW.toString());
        chatConstReplacements.put("black", EnumChatFormatting.BLACK.toString());
        chatConstReplacements.put("darkblue", EnumChatFormatting.DARK_BLUE.toString());
        chatConstReplacements.put("darkgreen", EnumChatFormatting.DARK_GREEN.toString());
        chatConstReplacements.put("darkaqua", EnumChatFormatting.DARK_AQUA.toString());
        chatConstReplacements.put("darkred", EnumChatFormatting.DARK_RED.toString());
        chatConstReplacements.put("purple", EnumChatFormatting.DARK_PURPLE.toString());
        chatConstReplacements.put("gold", EnumChatFormatting.GOLD.toString());
        chatConstReplacements.put("grey", EnumChatFormatting.GRAY.toString());
        chatConstReplacements.put("darkgrey", EnumChatFormatting.DARK_GRAY.toString());
        chatConstReplacements.put("indigo", EnumChatFormatting.BLUE.toString());
        chatConstReplacements.put("green", EnumChatFormatting.GREEN.toString());
        chatConstReplacements.put("aqua", EnumChatFormatting.AQUA.toString());
        chatConstReplacements.put("pink", EnumChatFormatting.LIGHT_PURPLE.toString());
        chatConstReplacements.put("white", EnumChatFormatting.WHITE.toString());

        // replace MC formating
        chatConstReplacements.put("rnd", EnumChatFormatting.OBFUSCATED.toString());
        chatConstReplacements.put("bold", EnumChatFormatting.BOLD.toString());
        chatConstReplacements.put("strike", EnumChatFormatting.STRIKETHROUGH.toString());
        chatConstReplacements.put("underline", EnumChatFormatting.UNDERLINE.toString());
        chatConstReplacements.put("italics", EnumChatFormatting.ITALIC.toString());
        chatConstReplacements.put("reset", EnumChatFormatting.RESET.toString());
    }

    @SubscribeEvent
    public void serverStarting(FEModuleServerInitEvent e)
    {
        FECommandManager.registerCommand(new CommandMute());
        FECommandManager.registerCommand(new CommandNickname());
        FECommandManager.registerCommand(new CommandPm());
        FECommandManager.registerCommand(new CommandReply());
        FECommandManager.registerCommand(new CommandTimedMessages());
        FECommandManager.registerCommand(new CommandUnmute());
        FECommandManager.registerCommand(new CommandGroupMessage());

        FECommandManager.registerCommand(new CommandIrc());
        FECommandManager.registerCommand(new CommandIrcPm());
        FECommandManager.registerCommand(new CommandIrcBot());

        APIRegistry.perms.registerPermissionDescription(PERM, "Chat permissions");
        APIRegistry.perms.registerPermission(PERM_CHAT, PermissionLevel.TRUE, "Allow players to use the public chat");
        APIRegistry.perms.registerPermission(PERM_COLOR, PermissionLevel.TRUE, "Allow players to use colors in the public chat");
        APIRegistry.perms.registerPermission(PERM_URL, PermissionLevel.TRUE, "Allow players to post clickable links in public chat.");
        APIRegistry.perms.registerPermissionProperty(PERM_TEXTFORMAT, "", "Textformat colors. USE ONLY THE COLOR CHARACTERS AND NO &");
        APIRegistry.perms.registerPermissionProperty(PERM_PLAYERFORMAT, "", "Text to show in front of the player name in chat messages");
        APIRegistry.perms.registerPermissionProperty(PERM_RANGE, "", "Send chat messages only to players in this range of the sender");
    }

    @SubscribeEvent
    public void serverStarted(FEModuleServerPostInitEvent e)
    {
        ServerUtil.replaceCommand(CommandMessage.class, new CommandMessageReplacement());
    }

    @SubscribeEvent
    public void serverStopping(FEModuleServerStopEvent e)
    {
        closeLog();
        ircHandler.disconnect();
    }

    /* ------------------------------------------------------------ */

    @SubscribeEvent(priority = EventPriority.LOW)
    public void chatEvent(ServerChatEvent event)
    {
        UserIdent ident = UserIdent.get(event.player);

        if (!ident.checkPermission(PERM_CHAT))
        {
            ChatOutputHandler.chatWarning(event.player, "You don't have the permission to write in public chat.");
            event.setCanceled(true);
            return;
        }

        if (PlayerUtil.getPersistedTag(event.player, false).getBoolean("mute"))
        {
            ChatOutputHandler.chatWarning(event.player, "You are currently muted.");
            event.setCanceled(true);
            return;
        }

        if (CommandPm.getTarget(event.player) != null)
        {
            tell(event.player, event.getComponent(), CommandPm.getTarget(event.player));
            event.setCanceled(true);
            return;
        }

        // Log chat message
        logChatMessage(event.player.getName(), event.message);

        // Initialize parameters
        String message = processChatReplacements(event.player, censor.filter(event.message, event.player));
        IChatComponent header = getChatHeader(ident);

        // Apply colors
        if (event.message.contains("&") && ident.checkPermission(PERM_COLOR))
            message = ChatOutputHandler.formatColors(message);

        // Build message part with links
        IChatComponent messageComponent;
        if (ident.checkPermission(PERM_URL))
        {
            messageComponent = filterChatLinks(message);
        }
        else
        {
            messageComponent = new ChatComponentText(message);
        }

        String textFormats = APIRegistry.perms.getUserPermissionProperty(ident, ModuleChat.PERM_TEXTFORMAT);
        if (textFormats != null)
            ChatOutputHandler.applyFormatting(messageComponent.getChatStyle(), ChatOutputHandler.enumChatFormattings(textFormats));

        // Finish complete message
        event.setComponent(new ChatComponentTranslation("%s%s", header, messageComponent));

        // Handle chat range
        Double range = ServerUtil.tryParseDouble(ident.getPermissionProperty(PERM_RANGE));
        if (range != null)
        {
            WorldPoint source = new WorldPoint(event.player);
            for (EntityPlayerMP player : ServerUtil.getPlayerList())
            {
                if (player.dimension == source.getDimension() && source.distance(new WorldPoint(player)) <= range)
                    ChatOutputHandler.sendMessage(player, event.getComponent());
            }
            event.setCanceled(true);
        }
    }

    public static IChatComponent getChatHeader(UserIdent ident)
    {
        String playerName = ident.hasPlayer() ? getPlayerNickname(ident.getPlayer()) : ident.getUsernameOrUuid();

        // Get player name formatting
        String playerFormat = APIRegistry.perms.getUserPermissionProperty(ident, ModuleChat.PERM_PLAYERFORMAT);
        if (playerFormat == null)
            playerFormat = "";

        // Initialize header
        String playerCmd = "/msg " + ident.getUsernameOrUuid() + " ";
        IChatComponent groupPrefix = appendGroupPrefixSuffix(null, ident, false);
        IChatComponent playerPrefix = clickChatComponent(getPlayerPrefixSuffix(ident, false), Action.SUGGEST_COMMAND, playerCmd);
        IChatComponent playerText = clickChatComponent(playerFormat + playerName, Action.SUGGEST_COMMAND, playerCmd);
        IChatComponent playerSuffix = clickChatComponent(getPlayerPrefixSuffix(ident, true), Action.SUGGEST_COMMAND, playerCmd);
        IChatComponent groupSuffix = appendGroupPrefixSuffix(null, ident, true);
        IChatComponent header = new ChatComponentTranslation(ChatOutputHandler.formatColors(ChatConfig.chatFormat), //
                groupPrefix != null ? groupPrefix : "", //
                playerPrefix != null ? playerPrefix : "", //
                playerText, //
                playerSuffix != null ? playerSuffix : "", //
                groupSuffix != null ? groupSuffix : "");
        return header;
    }

    @SubscribeEvent(priority = EventPriority.LOW)
    public void commandEvent(CommandEvent event)
    {
        if (!(event.sender instanceof EntityPlayerMP))
            return;
        EntityPlayerMP player = (EntityPlayerMP) event.sender;
        if (!PlayerUtil.getPersistedTag(player, false).getBoolean("mute"))
            return;
        if (!ChatConfig.mutedCommands.contains(event.command.getCommandName()))
            return;
        ChatOutputHandler.chatWarning(event.sender, "You are currently muted.");
        event.setCanceled(true);
    }

    public static String processChatReplacements(ICommandSender sender, String message)
    {
        message = ScriptArguments.processSafe(message, sender);
        for (Entry<String, String> r : chatConstReplacements.entrySet())
            message = message.replaceAll("%" + r.getKey(), r.getValue());
        message = ChatOutputHandler.formatColors(message);
        return message;
    }

    public static IChatComponent clickChatComponent(String text, Action action, String uri)
    {
        IChatComponent component = new ChatComponentText(ChatOutputHandler.formatColors(text));
        component.getChatStyle().setChatClickEvent(new ClickEvent(Action.SUGGEST_COMMAND, uri));
        return component;
    }

    public static String getPlayerPrefixSuffix(UserIdent player, boolean isSuffix)
    {
        String fix = APIRegistry.perms.getServerZone().getPlayerPermission(player, isSuffix ? FEPermissions.SUFFIX : FEPermissions.PREFIX);
        if (fix == null)
            return "";
        return fix;
    }

    public static IChatComponent appendGroupPrefixSuffix(IChatComponent header, UserIdent ident, boolean isSuffix)
    {
        WorldPoint point = ident.hasPlayer() ? new WorldPoint(ident.getPlayer()) : new WorldPoint(0, 0, 0, 0);
        for (GroupEntry group : APIRegistry.perms.getServerZone().getAdditionalPlayerGroups(ident, new WorldPoint(ident.getPlayer())))
        {
            String text = APIRegistry.perms.getGroupPermissionProperty(group.getGroup(), point, isSuffix ? FEPermissions.SUFFIX : FEPermissions.PREFIX);
            if (text != null)
            {
                IChatComponent component = clickChatComponent(text, Action.SUGGEST_COMMAND, "/gmsg " + group.getGroup() + " ");
                if (header == null)
                    header = component;
                else
                    header.appendSibling(component);
            }
        }
        return header;
    }

    public static IChatComponent filterChatLinks(String text)
    {
        // Includes ipv4 and domain pattern
        // Matches an ip (xx.xxx.xx.xxx) or a domain (something.com) with or
        // without a protocol or path.
        IChatComponent ichat = new ChatComponentText("");
        Matcher matcher = URL_PATTERN.matcher(text);
        int lastEnd = 0;

        // Find all urls
        while (matcher.find())
        {
            int start = matcher.start();
            int end = matcher.end();

            // Append the previous left overs.
            ichat.appendText(text.substring(lastEnd, start));
            lastEnd = end;
            String url = text.substring(start, end);
            IChatComponent link = new ChatComponentText(url);
            link.getChatStyle().setUnderlined(true);

            try
            {
                // Add schema so client doesn't crash.
                if ((new URI(url)).getScheme() == null)
                    url = "http://" + url;
            }
            catch (URISyntaxException e)
            {
                // Bad syntax bail out!
                ichat.appendText(url);
                continue;
            }

            // Set the click event and append the link.
            ClickEvent click = new ClickEvent(ClickEvent.Action.OPEN_URL, url);
            link.getChatStyle().setChatClickEvent(click);
            ichat.appendSibling(link);
        }

        // Append the rest of the message.
        ichat.appendText(text.substring(lastEnd));
        return ichat;
    }

    /* ------------------------------------------------------------ */

    @SubscribeEvent
    public void onPlayerFirstJoin(NoPlayerInfoEvent event)
    {
        if (!ChatConfig.welcomeMessage.isEmpty())
        {
            String message = processChatReplacements(event.getPlayer(), ChatConfig.welcomeMessage);
            ChatOutputHandler.broadcast(filterChatLinks(message));
        }
    }

    @SubscribeEvent
    public void onPlayerLogin(PlayerEvent.PlayerLoggedInEvent e)
    {
        if (e.player instanceof EntityPlayerMP)
            sendMotd(e.player);
    }

    public static void sendMotd(ICommandSender sender)
    {
        for (String message : ChatConfig.loginMessage)
        {
            message = processChatReplacements(sender, message);
            ChatOutputHandler.sendMessage(sender, filterChatLinks(message));
        }
    }

    /* ------------------------------------------------------------ */

    public void logChatMessage(String sender, String message)
    {
        if (logWriter == null)
            return;
        String logMessage = String.format("[%1$tY-%1$tm-%1$te %1$tH:%1$tM:%1$tS] %2$s: %3$s", new Date(), sender, message);
        logWriter.write(logMessage + "\n");
    }

    public void setChatLogging(boolean enabled)
    {
        if (logWriter != null && enabled)
            return;
        closeLog();
        if (enabled)
        {
            File logFile = new File(ForgeEssentials.getFEDirectory(), String.format("ChatLog/%1$tY-%1$tm-%1$te_%1$tH.%1$tM.log", new Date()));
            try
            {
                File dir = logFile.getParentFile();
                if (!dir.exists() && !dir.mkdirs())
                {
                    LoggingHandler.felog.warn(String.format("Could not create chat log directory %s!", logFile.getPath()));
                }
                else
                {
                    logWriter = new PrintWriter(logFile);
                }
            }
            catch (FileNotFoundException e)
            {
                LoggingHandler.felog.error(String.format("Could not create chat log file %s.", logFile.getAbsolutePath()));
            }
        }
    }

    private void closeLog()
    {
        if (logWriter != null)
        {
            logWriter.close();
            logWriter = null;
        }
    }

    /* ------------------------------------------------------------ */

    public static void setPlayerNickname(EntityPlayer player, String nickname)
    {
        if (nickname == null)
            PlayerUtil.getPersistedTag(player, false).removeTag("nickname");
        else
            PlayerUtil.getPersistedTag(player, true).setString("nickname", nickname);
    }

    public static String getPlayerNickname(EntityPlayer player)
    {
        String nickname = PlayerUtil.getPersistedTag(player, false).getString("nickname");
        if (nickname == null || nickname.isEmpty())
            nickname = player.getName();
        return nickname;
    }

    /* ------------------------------------------------------------ */

    public static void tell(ICommandSender sender, IChatComponent message, ICommandSender target)
    {
        ChatComponentTranslation sentMsg = new ChatComponentTranslation("commands.message.display.incoming", new Object[] { sender.getDisplayName(),
                message.createCopy() });
        ChatComponentTranslation senderMsg = new ChatComponentTranslation("commands.message.display.outgoing",
                new Object[] { target.getDisplayName(), message });
        sentMsg.getChatStyle().setColor(EnumChatFormatting.GRAY).setItalic(Boolean.valueOf(true));
        senderMsg.getChatStyle().setColor(EnumChatFormatting.GRAY).setItalic(Boolean.valueOf(true));
        ChatOutputHandler.sendMessage(target, sentMsg);
        ChatOutputHandler.sendMessage(sender, senderMsg);
        CommandReply.messageSent(sender, target);
        ModuleCommandsEventHandler.checkAfkMessage(target, message);
    }
    public static void tellGroup(ICommandSender sender, String message, String group)
    {
        ServerZone sz = APIRegistry.perms.getServerZone();
        for (String g : sz.getGroups())
            if (group.equalsIgnoreCase(g))
            {
                group = g;
                break;
            }
        String groupName = sz.getGroupPermission(group, FEPermissions.GROUP_NAME);
        if (groupName == null)
            groupName = group;

        IChatComponent msg;
        EntityPlayer player = sender instanceof EntityPlayer ? (EntityPlayer) sender : null;
        msg = player != null ? getChatHeader(UserIdent.get((EntityPlayer) sender)) : new ChatComponentTranslation("SERVER ");
        String censored = censor.filter(message, player);
        String formatted = processChatReplacements(sender, censored);

        IChatComponent msgGroup = new ChatComponentText("@" + groupName + "@ ");
        msgGroup.getChatStyle().setColor(EnumChatFormatting.GRAY).setItalic(true);
        msg.appendSibling(msgGroup);

        IChatComponent msgBody = new ChatComponentText(formatted);
        msgBody.getChatStyle().setColor(EnumChatFormatting.GRAY);
        msg.appendSibling(msgBody);

        for (EntityPlayerMP p : ServerUtil.getPlayerList())
        {
            List<String> groups = GroupEntry.toList(sz.getPlayerGroups(UserIdent.get(p)));
            if (groups.contains(group))
            {
                ChatOutputHandler.sendMessage(p, msg);
            }
        }
    }
}
