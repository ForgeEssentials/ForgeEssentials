package com.forgeessentials.chat;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import com.forgeessentials.api.APIRegistry;
import com.forgeessentials.api.UserIdent;
import com.forgeessentials.api.permissions.FEPermissions;
import com.forgeessentials.api.permissions.GroupEntry;
import com.forgeessentials.api.permissions.ServerZone;
import com.forgeessentials.chat.command.CommandGroupMessage;
import com.forgeessentials.chat.command.CommandMute;
import com.forgeessentials.chat.command.CommandNickname;
import com.forgeessentials.chat.command.CommandPm;
import com.forgeessentials.chat.command.CommandReply;
import com.forgeessentials.chat.command.CommandTimedMessages;
import com.forgeessentials.chat.command.CommandUnmute;
import com.forgeessentials.commands.util.ModuleCommandsEventHandler;
import com.forgeessentials.commons.selections.WorldPoint;
import com.forgeessentials.core.ForgeEssentials;
import com.forgeessentials.core.commands.registration.FECommandManager;
import com.forgeessentials.core.config.ConfigData;
import com.forgeessentials.core.config.ConfigSaver;
import com.forgeessentials.core.moduleLauncher.FEModule;
import com.forgeessentials.scripting.ScriptArguments;
import com.forgeessentials.util.CommandUtils;
import com.forgeessentials.util.CommandUtils.CommandInfo;
import com.forgeessentials.util.PlayerUtil;
import com.forgeessentials.util.ServerUtil;
import com.forgeessentials.util.events.FEModuleEvent.FEModuleServerStartingEvent;
import com.forgeessentials.util.events.FEModuleEvent.FEModuleServerStoppingEvent;
import com.forgeessentials.util.events.player.FEPlayerEvent.NoPlayerInfoEvent;
import com.forgeessentials.util.output.ChatOutputHandler;
import com.forgeessentials.util.output.logger.LoggingHandler;
import com.mojang.brigadier.exceptions.CommandSyntaxException;

import net.minecraft.command.CommandSource;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.util.text.event.ClickEvent.Action;
import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.common.ForgeConfigSpec.Builder;
import net.minecraftforge.event.CommandEvent;
import net.minecraftforge.event.RegisterCommandsEvent;
import net.minecraftforge.event.ServerChatEvent;
import net.minecraftforge.event.entity.player.PlayerEvent.PlayerLoggedInEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.server.permission.DefaultPermissionLevel;

@FEModule(name = "Chat", parentMod = ForgeEssentials.class, version=ForgeEssentials.CURRENT_MODULE_VERSION)
public class ModuleChat implements ConfigSaver
{
    private static ForgeConfigSpec CHAT_CONFIG;
    public static final ConfigData data = new ConfigData("Chat", CHAT_CONFIG, new ForgeConfigSpec.Builder());

    public static final String CONFIG_FILE = "Chat";

    public static final String PERM = "fe.chat";

    public static final String PERM_CHAT = PERM + ".chat";

    public static final String PERM_COLOR = PERM + ".usecolor";

    public static final String PERM_URL = PERM + ".urls";

    private static final String PERM_TEXTFORMAT = PERM + ".textformat";

    private static final String PERM_PLAYERFORMAT = PERM + ".playerformat";

    public static final String PERM_RANGE = PERM + ".range";

    public static final Map<String, String> chatConstReplacements = new HashMap<>();

    @FEModule.Instance
    public static ModuleChat instance;

    private PrintWriter logWriter;

    public static Censor censor = new Censor();

    public Mailer mailer = new Mailer();

    public static TimedMessages timedMessages = new TimedMessages();

    private ScoreBoardColors scoreBoardColors;

    /* ------------------------------------------------------------ */

    public ModuleChat()
    {
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
        chatConstReplacements.put("red", TextFormatting.RED.toString());
        chatConstReplacements.put("yellow", TextFormatting.YELLOW.toString());
        chatConstReplacements.put("black", TextFormatting.BLACK.toString());
        chatConstReplacements.put("darkblue", TextFormatting.DARK_BLUE.toString());
        chatConstReplacements.put("darkgreen", TextFormatting.DARK_GREEN.toString());
        chatConstReplacements.put("darkaqua", TextFormatting.DARK_AQUA.toString());
        chatConstReplacements.put("darkred", TextFormatting.DARK_RED.toString());
        chatConstReplacements.put("purple", TextFormatting.DARK_PURPLE.toString());
        chatConstReplacements.put("gold", TextFormatting.GOLD.toString());
        chatConstReplacements.put("grey", TextFormatting.GRAY.toString());
        chatConstReplacements.put("darkgrey", TextFormatting.DARK_GRAY.toString());
        chatConstReplacements.put("indigo", TextFormatting.BLUE.toString());
        chatConstReplacements.put("green", TextFormatting.GREEN.toString());
        chatConstReplacements.put("aqua", TextFormatting.AQUA.toString());
        chatConstReplacements.put("pink", TextFormatting.LIGHT_PURPLE.toString());
        chatConstReplacements.put("white", TextFormatting.WHITE.toString());

        // replace MC formating
        chatConstReplacements.put("rnd", TextFormatting.OBFUSCATED.toString());
        chatConstReplacements.put("bold", TextFormatting.BOLD.toString());
        chatConstReplacements.put("strike", TextFormatting.STRIKETHROUGH.toString());
        chatConstReplacements.put("underline", TextFormatting.UNDERLINE.toString());
        chatConstReplacements.put("italics", TextFormatting.ITALIC.toString());
        chatConstReplacements.put("reset", TextFormatting.RESET.toString());
    }

    @SubscribeEvent
    public void serverStarting(FEModuleServerStartingEvent e)
    {
        APIRegistry.perms.registerPermissionDescription(PERM, "Chat permissions");
        APIRegistry.perms.registerPermission(PERM_CHAT, DefaultPermissionLevel.ALL,
                "Allow players to use the public chat");
        APIRegistry.perms.registerPermission(PERM_COLOR, DefaultPermissionLevel.ALL,
                "Allow players to use colors in the public chat");
        APIRegistry.perms.registerPermission(PERM_URL, DefaultPermissionLevel.ALL,
                "Allow players to post clickable links in public chat.");
        APIRegistry.perms.registerPermissionProperty(PERM_TEXTFORMAT, "",
                "Textformat colors. USE ONLY THE COLOR CHARACTERS AND NO &");
        APIRegistry.perms.registerPermissionProperty(PERM_PLAYERFORMAT, "",
                "Text to show in front of the player name in chat messages");
        APIRegistry.perms.registerPermissionProperty(PERM_RANGE, "",
                "Send chat messages only to players in this range of the sender");
        if (ChatConfig.scoreboardEnabled) {
            scoreBoardColors.registerPerms();
        }
    }

    @SubscribeEvent
    public void registerCommands(RegisterCommandsEvent event)
    {
        FECommandManager.registerCommand(new CommandMute(true), event.getDispatcher());
        FECommandManager.registerCommand(new CommandNickname(true), event.getDispatcher());
        FECommandManager.registerCommand(new CommandPm(true), event.getDispatcher());
        FECommandManager.registerCommand(new CommandReply(true), event.getDispatcher());
        FECommandManager.registerCommand(new CommandTimedMessages(true), event.getDispatcher());
        FECommandManager.registerCommand(new CommandUnmute(true), event.getDispatcher());
        FECommandManager.registerCommand(new CommandGroupMessage(true), event.getDispatcher());
    }

//    @SubscribeEvent
//    public void serverStarted(FEModuleServerStartedEvent e)
//    {
//        ServerUtil.replaceCommand(MessageCommand.class, new
//    	CommandMessageReplacement());
//    }

    @SubscribeEvent
    public void serverStopping(FEModuleServerStoppingEvent e)
    {
        closeLog();
    }

    /* ------------------------------------------------------------ */

    @SubscribeEvent(priority = EventPriority.LOW)
    public void chatEvent(ServerChatEvent event)
    {
        UserIdent ident = UserIdent.get(event.getPlayer());

        if (!ident.checkPermission(PERM_CHAT))
        {
            ChatOutputHandler.chatWarning(event.getPlayer().createCommandSourceStack(),
                    "You don't have the permission to write in public chat.");
            event.setCanceled(true);
            return;
        }

        if (PlayerUtil.getPersistedTag(event.getPlayer(), false).getBoolean("mute"))
        {
            ChatOutputHandler.chatWarning(event.getPlayer().createCommandSourceStack(), "You are currently muted.");
            event.setCanceled(true);
            return;
        }

        if (CommandPm.getTarget(event.getPlayer()) != null)
        {
            TextComponent message = new StringTextComponent("");
            message.append(event.getComponent());
            tell(event.getPlayer().createCommandSourceStack(), message,
                    CommandPm.getTarget(event.getPlayer()).createCommandSourceStack());
            event.setCanceled(true);
            return;
        }

        // Log chat message
        logChatMessage(event.getPlayer().getDisplayName().getString(), event.getMessage());

        // Initialize parameters
        String message = processChatReplacements(event.getPlayer().createCommandSourceStack(),
                censor.filter(event.getMessage(), event.getPlayer()), false);
        TextComponent header = getChatHeader(ident);

        // Apply colors
        if (event.getMessage().contains("&") && ident.checkPermission(PERM_COLOR))
        {
            message = ChatOutputHandler.formatColors(message);
        }

        // Apply Text format prefix
        String textFormats = APIRegistry.perms.getUserPermissionProperty(ident, ModuleChat.PERM_TEXTFORMAT);
        if (textFormats != null)
            message = ChatOutputHandler.formatColors(textFormats) + message;

        // Build message part with links
        TextComponent messageComponent;
        if (ident.checkPermission(PERM_URL))
        {
            messageComponent = ChatOutputHandler.filterChatLinks(message);
        }
        else
        {
            messageComponent = new StringTextComponent(message);
        }

        // Finish complete message
        event.setComponent(header.append(messageComponent));

        // Handle chat range
        Double range = ServerUtil.tryParseDouble(ident.getPermissionProperty(PERM_RANGE));
        if (range != null)
        {
            WorldPoint source = new WorldPoint(event.getPlayer());
            for (ServerPlayerEntity player : ServerUtil.getPlayerList())
            {
                if (player.level == source.getWorld() && source.distance(new WorldPoint(player)) <= range)
                    ChatOutputHandler.sendMessageI(player.createCommandSourceStack(), event.getComponent());
            }
            event.setCanceled(true);
        }
    }

    public static TextComponent getChatHeader(UserIdent ident)
    {
        String playerName = ident.hasPlayer() ? getPlayerNickname(ident.getPlayer()) : ident.getUsernameOrUuid();

        // Get player name formatting
        String playerFormat = APIRegistry.perms.getUserPermissionProperty(ident, ModuleChat.PERM_PLAYERFORMAT);
        if (playerFormat == null)
            playerFormat = "";

        // Initialize header
        String playerCmd = "/msg " + ident.getUsernameOrUuid() + " ";
        TextComponent groupPrefix = appendGroupPrefixSuffix(null, ident, false);
        TextComponent playerPrefix = ChatOutputHandler.clickChatComponent(getPlayerPrefixSuffix(ident, false), Action.SUGGEST_COMMAND,
                playerCmd);
        TextComponent playerText = ChatOutputHandler.clickChatComponent(playerFormat + playerName, Action.SUGGEST_COMMAND, playerCmd);
        TextComponent playerSuffix = ChatOutputHandler.clickChatComponent(getPlayerPrefixSuffix(ident, true), Action.SUGGEST_COMMAND,
                playerCmd);
        TextComponent groupSuffix = appendGroupPrefixSuffix(null, ident, true);
        return new TranslationTextComponent(ChatOutputHandler.formatColors(ChatConfig.chatFormat), //
                groupPrefix != null ? groupPrefix : "", //
                playerPrefix != null ? playerPrefix : "", //
                playerText, //
                playerSuffix != null ? playerSuffix : "", //
                groupSuffix != null ? groupSuffix : "");
    }

    @SubscribeEvent(priority = EventPriority.LOW)
    public void commandEvent(CommandEvent event)
    {
        if (event.getParseResults().getContext().getNodes().isEmpty())
            return;
        if (!(event.getParseResults().getContext().getSource().getEntity() instanceof ServerPlayerEntity))
            return;
        CommandInfo info = CommandUtils.getCommandInfo(event);
        ServerPlayerEntity player = (ServerPlayerEntity) info.getSource().getEntity();
        if (!PlayerUtil.getPersistedTag(player, false).getBoolean("mute"))
            return;
        if (!ChatConfig.mutedCommands.contains(info.getCommandName()))
            return;
        ChatOutputHandler.chatWarning(info.getSource(), "You are currently muted.");
        event.setCanceled(true);
    }

    public static String processChatReplacements(CommandSource sender, String message)
    {
        return processChatReplacements(sender, message, true);
    }

    public static String processChatReplacements(CommandSource sender, String message, boolean formatColors)
    {
        message = ScriptArguments.processSafe(message, sender);
        for (Entry<String, String> r : chatConstReplacements.entrySet())
            message = message.replaceAll("%" + r.getKey(), r.getValue());
        if (formatColors)
        {
            message = ChatOutputHandler.formatColors(message);
        }
        return message;
    }

    public static String getPlayerPrefixSuffix(UserIdent player, boolean isSuffix)
    {
        String fix = APIRegistry.perms.getServerZone().getPlayerPermission(player,
                isSuffix ? FEPermissions.SUFFIX : FEPermissions.PREFIX);
        if (fix == null)
            return "";
        return fix;
    }

    public static TextComponent appendGroupPrefixSuffix(TextComponent header, UserIdent ident, boolean isSuffix)
    {
        WorldPoint point = ident.hasPlayer() ? new WorldPoint(ident.getPlayer())
                : new WorldPoint("minecraft:overworld", 0, 0, 0);
        for (GroupEntry group : APIRegistry.perms.getServerZone().getAdditionalPlayerGroups(ident,
                new WorldPoint(ident.getPlayer())))
        {
            String text = APIRegistry.perms.getGroupPermissionProperty(group.getGroup(), point,
                    isSuffix ? FEPermissions.SUFFIX : FEPermissions.PREFIX);
            if (text != null)
            {
                TextComponent component = ChatOutputHandler.clickChatComponent(text, Action.SUGGEST_COMMAND,
                        "/gmsg " + group.getGroup() + " ");
                if (header == null)
                    header = component;
                else
                    header.append(component);
            }
        }
        return header;
    }

    /* ------------------------------------------------------------ */

    @SubscribeEvent
    public void onPlayerFirstJoin(NoPlayerInfoEvent event)
    {
        if (!ChatConfig.welcomeMessage.isEmpty())
        {
            String message = processChatReplacements(event.getPlayer().createCommandSourceStack(),
                    ChatConfig.welcomeMessage);
            ChatOutputHandler.broadcast(ChatOutputHandler.filterChatLinks(message));
        }
    }

    @SubscribeEvent
    public void onPlayerLogin(PlayerLoggedInEvent e)
    {
        if (e.getPlayer() instanceof ServerPlayerEntity)
            sendMotd(e.getPlayer().createCommandSourceStack());
    }

    public static void sendMotd(CommandSource sender)
    {
        for (String message : ChatConfig.loginMessage)
        {
            message = processChatReplacements(sender, message);
            ChatOutputHandler.sendMessage(sender, ChatOutputHandler.filterChatLinks(message));
        }
    }

    /* ------------------------------------------------------------ */

    public void logChatMessage(String sender, String message)
    {
        if (logWriter == null)
            return;
        String logMessage = String.format("[%1$tY-%1$tm-%1$te %1$tH:%1$tM:%1$tS] %2$s: %3$s", new Date(), sender,
                message);
        logWriter.write(logMessage + "\n");
    }

    public void setChatLogging(boolean enabled)
    {
        if (logWriter != null && enabled)
            return;
        closeLog();
        if (enabled)
        {
            File logFile = new File(ForgeEssentials.getFEDirectory(),
                    String.format("ChatLog/%1$tY-%1$tm-%1$te_%1$tH.%1$tM.log", new Date()));
            try
            {
                File dir = logFile.getParentFile();
                if (!dir.exists() && !dir.mkdirs())
                {
                    LoggingHandler.felog
                            .warn(String.format("Could not create chat log directory %s!", logFile.getPath()));
                }
                else
                {
                    logWriter = new PrintWriter(logFile);
                }
            }
            catch (FileNotFoundException e)
            {
                LoggingHandler.felog
                        .error(String.format("Could not create chat log file %s.", logFile.getAbsolutePath()));
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

    public static void setPlayerNickname(PlayerEntity player, String nickname)
    {
        if (nickname == null)
            PlayerUtil.getPersistedTag(player, false).remove("nickname");
        else
            PlayerUtil.getPersistedTag(player, true).putString("nickname", nickname);
    }

    public static String getPlayerNickname(PlayerEntity player)
    {
        String nickname = PlayerUtil.getPersistedTag(player, false).getString("nickname");
        if (nickname == null || nickname.isEmpty())
            nickname = player.getDisplayName().getString();
        return nickname;
    }

    public static boolean doesPlayerHaveNickname(PlayerEntity player)
    {
        String nickname = PlayerUtil.getPersistedTag(player, false).getString("nickname");
        return nickname != null && !nickname.isEmpty();
    }
    /* ------------------------------------------------------------ */

    public static void tell(CommandSource sender, TextComponent message, CommandSource target)
    {
        TranslationTextComponent sentMsg = new TranslationTextComponent("commands.message.display.incoming",
                new Object[] { sender.getDisplayName().getString(), message.copy() });
        TranslationTextComponent senderMsg = new TranslationTextComponent("commands.message.display.outgoing",
                new Object[] { target.getDisplayName().getString(), message });
        sentMsg.withStyle(TextFormatting.GRAY).withStyle(TextFormatting.ITALIC);
        senderMsg.withStyle(TextFormatting.GRAY).withStyle(TextFormatting.ITALIC);
        ChatOutputHandler.sendMessage(target, sentMsg);
        ChatOutputHandler.sendMessage(sender, senderMsg);
        if (sender.getEntity() instanceof PlayerEntity && target.getEntity() instanceof PlayerEntity)
        {
            CommandReply.messageSent((PlayerEntity) sender.getEntity(), (PlayerEntity) target.getEntity());
        }
        try
        {
            ModuleCommandsEventHandler.checkAfkMessage(target, message);
        }
        catch (CommandSyntaxException e)
        {
            ChatOutputHandler.chatError(sender, "Failed to send message");
        }
    }

    public static void tellGroup(CommandSource sender, String message, String group, boolean formatColors)
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

        TextComponent msg;
        PlayerEntity player = sender.getEntity() instanceof PlayerEntity ? (PlayerEntity) sender.getEntity() : null;
        msg = player != null ? getChatHeader(UserIdent.get((PlayerEntity) sender.getEntity()))
                : new StringTextComponent("SERVER ");
        String censored = censor.filter(message, player);
        String formatted = processChatReplacements(sender, censored, formatColors);

        TextComponent msgGroup = new StringTextComponent("@" + groupName + "@ ");
        msgGroup.withStyle(TextFormatting.GRAY).withStyle(TextFormatting.ITALIC);
        msg.append(msgGroup);

        TextComponent msgBody = new StringTextComponent(formatted);
        msgBody.withStyle(TextFormatting.GRAY);
        msg.append(msgBody);

        for (ServerPlayerEntity p : ServerUtil.getPlayerList())
        {
            List<String> groups = GroupEntry.toList(sz.getPlayerGroups(UserIdent.get(p)));
            if (groups.contains(group))
            {
                ChatOutputHandler.sendMessage(p.createCommandSourceStack(), msg);
            }
        }
    }

    @Override
    public void load(Builder BUILDER, boolean isReload)
    {
        ChatConfig.load(BUILDER, isReload);
        censor.load(BUILDER, isReload);
        timedMessages.load(BUILDER, isReload);
    }

    @Override
    public void bakeConfig(boolean reload)
    {
        censor.bakeConfig(reload);
        timedMessages.bakeConfig(reload);
        ChatConfig.bakeConfig(reload);
        // This is a little ugly, but we need ScoreBoardColors run after ChatConfig is baked,
        // while still early enough to register to Minecraft's the event bus, so this is the best place to do it.
        if (ChatConfig.scoreboardEnabled)
        {
            scoreBoardColors = new ScoreBoardColors();
        }
    }

    @Override
    public ConfigData returnData()
    {
        return data;
    }

    @Override
    public void save(boolean reload)
    {
        timedMessages.save(reload);
    }
}
