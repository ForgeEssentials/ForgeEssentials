package com.forgeessentials.util.output;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.forgeessentials.core.misc.Translator;
import com.forgeessentials.core.moduleLauncher.ModuleLauncher;
import com.forgeessentials.util.output.logger.LoggingHandler;

import net.minecraft.command.CommandSource;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.util.text.ChatType;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.Style;
import net.minecraft.util.text.TextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.event.ClickEvent;
import net.minecraft.util.text.event.ClickEvent.Action;
import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.common.ForgeConfigSpec.Builder;
import net.minecraftforge.common.util.FakePlayer;
import net.minecraftforge.fml.server.ServerLifecycleHooks;

public final class ChatOutputHandler
{

    public static final char COLOR_FORMAT_CHARACTER = '\u00a7';

    public static final String CONFIG_MAIN_OUTPUT = "Output";

    static final Pattern URL_PATTERN = Pattern.compile(
            //         schema                          ipv4            OR           namespace                 port     path         ends
            //   |-----------------|        |-------------------------|  |----------------------------|    |---------| |--|   |---------------|
            "((?:(?:http|https):\\/\\/)?(?:(?:[0-9]{1,3}\\.){3}[0-9]{1,3}|(?:[-\\w_\\.]{1,}\\.[a-z]{2,}?))(?::[0-9]{1,5})?.*?(?=[!\"\u00A7 \n]|$))",
            Pattern.CASE_INSENSITIVE);

    public static TextFormatting chatErrorColor, chatWarningColor, chatConfirmationColor, chatNotificationColor;

    public static DiscordMessageHandlerBase discordMessageHandler = new DiscordMessageHandlerBase();

    /* ------------------------------------------------------------ */

    /**
     * Sends a chat message to the given command sender (usually a player) with the given text and no special formatting.
     *
     * @param recipient
     *            The recipient of the chat message.
     * @param message
     *            The message to send.
     */
    public static void sendMessage(CommandSource recipient, String message)
    {
        sendMessageI(recipient, new StringTextComponent(message));
    }

    public static void sendMessage(PlayerEntity recipient, String message)
    {
        sendMessageI(recipient.createCommandSourceStack(), new StringTextComponent(message));
    }

    /**
     * Sends a message to a {@link CommandSource} and performs some security checks
     *
     * @param recipient
     * @param message
     */
    public static void sendMessage(CommandSource recipient, TextComponent message)
    {
        sendMessageI(recipient, message);
    }

    public static void sendMessage(PlayerEntity recipient, TextComponent message)
    {
        sendMessageI(recipient.createCommandSourceStack(), message);
    }

    /**
     * Sends a message to a {@link CommandSource} and performs some security checks
     * 
     * @param recipient
     * @param message
     */
    public static void sendMessageI(CommandSource recipient, ITextComponent message)
    {
        Entity entity = recipient.getEntity();
        if (entity instanceof FakePlayer && ((ServerPlayerEntity) entity).connection.getConnection() == null)
            LoggingHandler.felog
                    .info(String.format("Fakeplayer %s: %s", entity.getDisplayName().getString(), message.plainCopy()));
        else if (entity instanceof ServerPlayerEntity)
        {
            recipient.sendSuccess(message, false);
        }
        else
            recipient.sendSuccess(message, false);
    }

    /**
     * actually sends the color-formatted message to the sender
     *
     * @param recipient
     *            CommandSource to chat to.
     * @param message
     *            The message to be sent
     * @param color
     *            Color of text to format
     */
    public static void sendMessage(CommandSource recipient, String message, TextFormatting color)
    {
        message = formatColors(message);
        if (recipient.getEntity() instanceof PlayerEntity)
        {
            TextComponent component = new StringTextComponent(message);
            component.withStyle(color);
            sendMessage(recipient, component);
        }
        else
            sendMessage(recipient, stripFormatting(message));
    }

    public static void sendMessage(PlayerEntity recipient, String message, TextFormatting color)
    {
        TextComponent component = new StringTextComponent(message);
        component.withStyle(color);
        sendMessage(recipient, component);
    }

    /**
     * Sends a message to all clients
     *
     * @param message
     *            The message to send
     */
    public static void broadcast(String message)
    {
        broadcast(message, true);
    }

    public static void broadcast(String message, boolean sendToDiscord)
    {
        broadcast(new StringTextComponent(message), sendToDiscord);
    }

    /**
     * Sends a message to all clients
     *
     * @param message
     *            The message to send
     * @param sendToDiscord
     *            Broadcast Message to discord
     */
    public static void broadcast(TextComponent message, boolean sendToDiscord)
    {
        // TODO: merge ITexcComponent and TextComponent methods to avoid duplication
        for (PlayerEntity p : ServerLifecycleHooks.getCurrentServer().getPlayerList().getPlayers())
        {
            ServerLifecycleHooks.getCurrentServer().getPlayerList().broadcastMessage(message, ChatType.CHAT,
                    p.getGameProfile().getId());
        }

        if (sendToDiscord && ModuleLauncher.getModuleList().contains("DiscordBridge"))
        {
            discordMessageHandler.sendMessage(message.getString());
        }
    }

    /**
     * Sends a message to all clients
     *
     * @param message
     *            The message to send
     */
    public static void broadcast(ITextComponent message)
    {
        broadcast(message, true);
    }

    /**
     * Sends a message to all clients
     *
     * @param message
     *            The message to send
     * @param sendToDiscord
     *            Broadcast Message to discord
     */
    public static void broadcast(ITextComponent message, boolean sendToDiscord)
    {
        for (PlayerEntity p : ServerLifecycleHooks.getCurrentServer().getPlayerList().getPlayers())
        {
            ServerLifecycleHooks.getCurrentServer().getPlayerList().broadcastMessage(message, ChatType.CHAT,
                    p.getGameProfile().getId());
        }

        if (sendToDiscord && ModuleLauncher.getModuleList().contains("DiscordBridge"))
        {
        	discordMessageHandler.sendMessage(message.getString());
        }
    }

    /* ------------------------------------------------------------ */

    public static TextComponent confirmation(String message)
    {
        return setChatColor(new StringTextComponent(formatColors(message)), chatConfirmationColor);
    }

    public static TextComponent notification(String message)
    {
        return setChatColor(new StringTextComponent(formatColors(message)), chatNotificationColor);
    }

    public static TextComponent warning(String message)
    {
        return setChatColor(new StringTextComponent(formatColors(message)), chatWarningColor);
    }

    public static TextComponent error(String message)
    {
        return setChatColor(new StringTextComponent(formatColors(message)), chatErrorColor);
    }

    /**
     * Utility method to set {@link TextComponent} color
     *
     * @param message
     * @param color
     * @return message
     */
    public static TextComponent setChatColor(TextComponent message, TextFormatting color)
    {
        message.withStyle(color);
        return message;
    }

    /* ------------------------------------------------------------ */

    /**
     * outputs an error message to the chat box of the given sender.
     *
     * @param sender
     *            CommandSource to chat to.
     * @param msg
     *            the message to be sent
     */
    public static void chatError(CommandSource sender, String msg, Object... args)
    {
        sendMessage(sender, Translator.format(msg, args), chatErrorColor);
    }

    public static void chatError(CommandSource sender, String msg)
    {
        sendMessage(sender, msg, chatErrorColor);
    }

    public static void chatError(PlayerEntity sender, String msg, Object... args)
    {
        sendMessage(sender, Translator.format(msg, args), chatErrorColor);
    }

    public static void chatError(PlayerEntity sender, String msg)
    {
        sendMessage(sender, msg, chatErrorColor);
    }

    /**
     * outputs a confirmation message to the chat box of the given sender.
     *
     * @param sender
     *            CommandSource to chat to.
     * @param msg
     *            the message to be sent
     */
    public static void chatConfirmation(CommandSource sender, String msg, Object... args)
    {
        sendMessage(sender, Translator.format(msg, args), chatConfirmationColor);
    }

    public static void chatConfirmation(CommandSource sender, String msg)
    {
        sendMessage(sender, msg, chatConfirmationColor);
    }

    public static void chatConfirmation(PlayerEntity sender, String msg, Object... args)
    {
        sendMessage(sender, Translator.format(msg, args), chatConfirmationColor);
    }

    public static void chatConfirmation(PlayerEntity sender, String msg)
    {
        sendMessage(sender, msg, chatConfirmationColor);
    }

    /**
     * outputs a warning message to the chat box of the given sender.
     *
     * @param sender
     *            CommandSource to chat to.
     * @param msg
     *            the message to be sent
     */
    public static void chatWarning(CommandSource sender, String msg, Object... args)
    {
        sendMessage(sender, Translator.format(msg, args), chatWarningColor);
    }

    public static void chatWarning(CommandSource sender, String msg)
    {
        sendMessage(sender, msg, chatWarningColor);
    }

    public static void chatWarning(PlayerEntity sender, String msg, Object... args)
    {
        sendMessage(sender, Translator.format(msg, args), chatWarningColor);
    }

    public static void chatWarning(PlayerEntity sender, String msg)
    {
        sendMessage(sender, msg, chatWarningColor);
    }

    /**
     * outputs a notification message to the chat box of the given sender.
     * 
     * @param sender
     *            CommandSource to chat to.
     * @param msg
     */
    public static void chatNotification(CommandSource sender, String msg, Object... args)
    {
        sendMessage(sender, Translator.format(msg, args), chatNotificationColor);
    }

    public static void chatNotification(CommandSource sender, String msg)
    {
        sendMessage(sender, msg, chatNotificationColor);
    }

    public static void chatNotification(PlayerEntity sender, String msg, Object... args)
    {
        sendMessage(sender, Translator.format(msg, args), chatNotificationColor);
    }

    public static void chatNotification(PlayerEntity sender, String msg)
    {
        sendMessage(sender, msg, chatNotificationColor);
    }
    /* ------------------------------------------------------------ */

    /**
     * Format color codes
     *
     * @param message
     * @return formatted message
     */
    public static String formatColors(String message)
    {
        if (message == null) {
            return "";
        }
        
        // TODO: Improve this to replace codes less aggressively
        char[] b = message.toCharArray();
        for (int i = 0; i < b.length - 1; i++)
        {
            if (b[i] == '&' && "0123456789AaBbCcDdEeFfKkLlMmNnOoRr".indexOf(b[i + 1]) > -1)
            {
                b[i] = COLOR_FORMAT_CHARACTER;
                b[i + 1] = Character.toLowerCase(b[i + 1]);
            }
        }
        return new String(b);
    }

    public static final Pattern FORMAT_CODE_PATTERN;

    public static final char[] FORMAT_CHARACTERS = new char[TextFormatting.values().length];

    static
    {
        for (TextFormatting code : TextFormatting.values())
            FORMAT_CHARACTERS[code.ordinal()] = code.toString().charAt(1);
        FORMAT_CODE_PATTERN = Pattern.compile(COLOR_FORMAT_CHARACTER + "([" + new String(FORMAT_CHARACTERS) + "])");
    }

    /**
     * Strips any minecraft formatting codes
     * 
     * @param message
     * @return
     */
    public static String stripFormatting(String message)
    {
        return FORMAT_CODE_PATTERN.matcher(message).replaceAll("");
    }

    /**
     * Creates a chat click event with the given parameters
     * 
     * @return {@Link TextComponent}
     */
    public static TextComponent clickChatComponent(String text, Action action, String uri)
    {
        TextComponent component = new StringTextComponent(ChatOutputHandler.formatColors(text));
        ClickEvent click = new ClickEvent(action, uri);
        component.withStyle((style) -> style.withClickEvent(click));
        return component;
    }

    /**
     * Apply a set of {@link TextFormatting} to a {@link Style}
     * 
     * @param chatStyle
     * @param formattings
     */
    public static void applyFormatting(Style chatStyle, Collection<TextFormatting> formattings)
    {
        for (TextFormatting format : formattings)
            applyFormatting(chatStyle, format);
    }

    /**
     * Apply an {@link TextFormatting} to a {@link Style}
     * 
     * @param chatStyle
     * @param formatting
     */
    public static void applyFormatting(Style chatStyle, TextFormatting formatting)
    {
        switch (formatting)
        {
        case BOLD:
            chatStyle.withBold(true);
            break;
        case ITALIC:
            chatStyle.withItalic(true);
            break;
        case OBFUSCATED:
            chatStyle.setObfuscated(true);
            break;
        case STRIKETHROUGH:
            chatStyle.setStrikethrough(true);
            break;
        case UNDERLINE:
            chatStyle.setUnderlined(true);
            break;
        case RESET:
            break;
        default:
            chatStyle.withColor(formatting);
            break;
        }
    }

    /**
     * Take a string of chat format codes (without \u00a7) and return them as {@link TextFormatting} collection
     * 
     * @param textFormats
     * @return
     */
    public static Collection<TextFormatting> enumChatFormattings(String textFormats)
    {
        List<TextFormatting> result = new ArrayList<>();
        for (int i = 0; i < textFormats.length(); i++)
        {
            char formatChar = textFormats.charAt(i);
            for (TextFormatting format : TextFormatting.values())
                if (FORMAT_CHARACTERS[format.ordinal()] == formatChar)
                {
                    result.add(format);
                    break;
                }
        }
        return result;
    }

    /* ------------------------------------------------------------ */

    public static String getUnformattedMessage(TextComponent message)
    {
        return message.plainCopy().toString();
    }

    public static String getFormattedMessage(TextComponent message)
    {
        return message.copy().toString();
    }

    // public static String formatHtml(String message) {
    // StringBuilder sb = new StringBuilder();
    // int pos = 0;
    // int tagCount = 0;
    // Matcher matcher = FORMAT_CODE_PATTERN.matcher(message);
    // while (matcher.find()) {
    // sb.append(StringEscapeUtils.escapeHtml4(message.substring(pos, matcher.start())));
    // pos = matcher.end();
    // char formatChar = matcher.group(1).charAt(0);
    // for (TextFormatting format : TextFormatting.values()) {
    // if (FORMAT_CHARACTERS[format.ordinal()] == formatChar) {
    // sb.append("<span class=\"mcf");
    // sb.append(formatChar);
    // sb.append("\">");
    // tagCount++;
    // break;
    // }
    // }
    // }
    // sb.append(StringEscapeUtils.escapeHtml4(message.substring(pos, message.length())));
    // // for (; pos < message.length(); pos++)
    // // sb.append(message.charAt(pos));
    // for (int i = 0; i < tagCount; i++)
    // sb.append("</span>");
    // return sb.toString();
    // }

    public static boolean isStyleEmpty(Style style)
    {
        return !style.isBold() && !style.isItalic() && !style.isObfuscated() && !style.isStrikethrough()
                && !style.isUnderlined() && style.getColor() == null;
    }

    public static enum ChatFormat
    {

        PLAINTEXT/* , HTML */, MINECRAFT, DETAIL;

        public Object format(TextComponent message)
        {
            switch (this)
            {
            // case HTML:
            // return ChatOutputHandler.formatHtml(message.getString());
            case MINECRAFT:
                return ChatOutputHandler.getFormattedMessage(message);
            case DETAIL:
                return message;
            default:
            case PLAINTEXT:
                return ChatOutputHandler.stripFormatting(ChatOutputHandler.getUnformattedMessage(message));
            }
        }

        public static ChatFormat fromString(String format)
        {
            try
            {
                return ChatFormat.valueOf(format.toUpperCase());
            }
            catch (IllegalArgumentException e)
            {
                return ChatFormat.PLAINTEXT;
            }
        }

    }

    /* ------------------------------------------------------------ */

    /**
     * Gets a nice string with only needed elements. Max time is weeks
     *
     * @param time
     *            in seconds
     * @return Time in string format
     */
    public static String formatTimeDurationReadable(long time, boolean showSeconds)
    {
        int weeks = (int) (TimeUnit.SECONDS.toDays(time) / 7);
        int days = (int) (TimeUnit.SECONDS.toDays(time) - 7 * weeks);
        long hours = TimeUnit.SECONDS.toHours(time) - (TimeUnit.SECONDS.toDays(time) * 24);
        long minutes = TimeUnit.SECONDS.toMinutes(time) - (TimeUnit.SECONDS.toHours(time) * 60);
        long seconds = TimeUnit.SECONDS.toSeconds(time) - (TimeUnit.SECONDS.toMinutes(time) * 60);

        StringBuilder sb = new StringBuilder();
        if (weeks != 0)
            sb.append(String.format("%d weeks ", weeks));
        if (days != 0)
        {
            if (sb.length() > 0)
                sb.append(", ");
            sb.append(String.format("%d days ", days));
        }
        if (hours != 0)
        {
            if (sb.length() > 0)
                sb.append(", ");
            sb.append(String.format("%d hours ", hours));
        }
        if (minutes != 0 || !showSeconds)
        {
            if (sb.length() > 0)
                if (!showSeconds)
                    sb.append("and ");
                else
                    sb.append(", ");
            sb.append(String.format("%d minutes ", minutes));
        }
        if (showSeconds)
        {
            if (sb.length() > 0)
                sb.append("and ");
            sb.append(String.format("%d seconds ", seconds));
        }

        sb.setLength(sb.length() - 1);
        return sb.toString();
    }

    /**
     * Filter urls from chat
     *
     * @param time
     *            in milliseconds
     * @return Time in string format
     */
    public static TextComponent filterChatLinks(String text)
    {
        // Includes ipv4 and domain pattern
        // Matches an ip (xx.xxx.xx.xxx) or a domain (something.com) with or
        // without a protocol or path.
        TextComponent ichat = new StringTextComponent("");
        Matcher matcher = URL_PATTERN.matcher(text);
        int lastEnd = 0;

        // Find all urls
        while (matcher.find())
        {
            int start = matcher.start();
            int end = matcher.end();

            // Append the previous left overs.
            ichat.append(text.substring(lastEnd, start));
            lastEnd = end;
            String url = text.substring(start, end);
            TextComponent link = new StringTextComponent(url);
            link.withStyle(TextFormatting.UNDERLINE);

            try
            {
                // Add schema so client doesn't crash.
                if ((new URI(url)).getScheme() == null)
                    url = "http://" + url;
                LoggingHandler.felog.info("Url made: " + url);

            }
            catch (URISyntaxException e)
            {
                // Bad syntax bail out!
                ichat.append(url);
                continue;
            }

            // Set the click event and append the link.
            ClickEvent click = new ClickEvent(ClickEvent.Action.OPEN_URL, url);
            link.withStyle((style) -> style.withClickEvent(click));
            ichat.append(link);
        }
        // Append the rest of the message.
        ichat.append(text.substring(lastEnd));

        return ichat;
    }

    /**
     * Gets a nice string with only needed elements. Max time is weeks
     *
     * @param time
     *            in milliseconds
     * @return Time in string format
     */
    public static String formatTimeDurationReadableMilli(long time, boolean showSeconds)
    {
        return formatTimeDurationReadable(time / 1000, showSeconds);
    }

    /* ------------------------------------------------------------ */

    public static void setConfirmationColor(String color)
    {
        chatConfirmationColor = TextFormatting.getByName(color);
        if (chatConfirmationColor == null)
            chatConfirmationColor = TextFormatting.GREEN;
    }

    public static void setErrorColor(String color)
    {
        chatErrorColor = TextFormatting.getByName(color);
        if (chatErrorColor == null)
            chatErrorColor = TextFormatting.RED;
    }

    public static void setNotificationColor(String color)
    {
        chatNotificationColor = TextFormatting.getByName(color);
        if (chatNotificationColor == null)
            chatNotificationColor = TextFormatting.AQUA;
    }

    public static void setWarningColor(String color)
    {
        chatWarningColor = TextFormatting.getByName(color);
        if (chatWarningColor == null)
            chatWarningColor = TextFormatting.YELLOW;
    }

    static ForgeConfigSpec.ConfigValue<String> FEchatConfirmationColor;
    static ForgeConfigSpec.ConfigValue<String> FEchatErrorColor;
    static ForgeConfigSpec.ConfigValue<String> FEchatNotificationColor;
    static ForgeConfigSpec.ConfigValue<String> FEchatWarningColor;

    public static Builder load(Builder BUILDER, boolean isReload)
    {
        BUILDER.comment("This controls the colors of the various chats output by ForgeEssentials."
                + "\nValid output colors are as follows:"
                + "\naqua, black, blue, dark_aqua, dark_blue, dark_gray, dark_green, dark_purple, dark_red"
                + "\ngold, gray, green, light_purple, red, white, yellow").push(CONFIG_MAIN_OUTPUT);
        FEchatConfirmationColor = BUILDER.comment("Defaults to green.").define("confirmationColor", "green");
        FEchatErrorColor = BUILDER.comment("Defaults to red.").define("errorOutputColor", "red");
        FEchatNotificationColor = BUILDER.comment("Defaults to aqua.").define("notificationOutputColor", "aqua");
        FEchatWarningColor = BUILDER.comment("Defaults to yellow.").define("warningOutputColor", "yellow");
        BUILDER.pop();
        return BUILDER;
    }

    public static void bakeConfig(boolean reload)
    {
        setConfirmationColor(FEchatConfirmationColor.get());
        setErrorColor(FEchatErrorColor.get());
        setNotificationColor(FEchatNotificationColor.get());
        setWarningColor(FEchatWarningColor.get());
    }

}
