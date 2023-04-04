package com.forgeessentials.util.output;

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
import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.common.ForgeConfigSpec.Builder;
import net.minecraftforge.common.util.FakePlayer;
import net.minecraftforge.fml.server.ServerLifecycleHooks;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang3.StringEscapeUtils;

//import org.apache.commons.text.StringEscapeUtils;

import com.forgeessentials.core.misc.Translator;

public final class ChatOutputHandler
{

    public static final char COLOR_FORMAT_CHARACTER = '\u00a7';

    public static final String CONFIG_MAIN_OUTPUT = "Output";

    public static TextFormatting chatErrorColor, chatWarningColor, chatConfirmationColor, chatNotificationColor;

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

    /**
     * Sends a message to a {@link ICommandSender} and performs some security checks
     * 
     * @param recipient
     * @param message
     */
    public static void sendMessage(CommandSource recipient, TextComponent message)
    {
    	sendMessageI(recipient, message);
    }

    /**
     * Sends a message to a {@link ICommandSender} and performs some security checks
     * 
     * @param recipient
     * @param message
     */
    public static void sendMessageI(CommandSource recipient, ITextComponent message)
    {
        Entity entity = recipient.getEntity();
        if (entity instanceof FakePlayer && ((ServerPlayerEntity) entity).connection.getConnection() == null)
            LoggingHandler.felog.info(String.format("Fakeplayer %s: %s", entity.getDisplayName().getString(), message.plainCopy()));
        else if(entity instanceof ServerPlayerEntity)
            recipient.getServer().getPlayerList().broadcastMessage(message, ChatType.CHAT, entity.getUUID());
        else
        	recipient.sendSuccess(message, false);
    }

    /**
     * actually sends the color-formatted message to the sender
     *
     * @param recipient
     *            CommandSender to chat to.
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

    /**
     * Sends a message to all clients
     *
     * @param message
     *            The message to send
     */
    public static void broadcast(String message)
    {
        broadcast(new StringTextComponent(message));;
    }

    /**
     * Sends a message to all clients
     *
     * @param message
     *            The message to send
     */
    public static void broadcast(TextComponent message)
    {
        for (PlayerEntity p : ServerLifecycleHooks.getCurrentServer().getPlayerList().getPlayers())
        {
            ServerLifecycleHooks.getCurrentServer().getPlayerList().broadcastMessage(message, ChatType.CHAT, p.getUUID());
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
        for (PlayerEntity p : ServerLifecycleHooks.getCurrentServer().getPlayerList().getPlayers())
        {
            ServerLifecycleHooks.getCurrentServer().getPlayerList().broadcastMessage(message, ChatType.CHAT, p.getUUID());
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
     * Utility method to set {@link IChatComponent} color
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
     *            CommandSender to chat to.
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

    public static void chatError(PlayerEntity sender, String msg)
    {
        chatError(sender.createCommandSourceStack(), msg);
    }

    /**
     * outputs a confirmation message to the chat box of the given sender.
     *
     * @param sender
     *            CommandSender to chat to.
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

    public static void chatConfirmation(PlayerEntity sender, String msg)
    {
        sendMessage(sender.createCommandSourceStack(), msg);
    }

    /**
     * outputs a warning message to the chat box of the given sender.
     *
     * @param sender
     *            CommandSender to chat to.
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

    public static void chatWarning(PlayerEntity sender, String msg)
    {
        sendMessage(sender.createCommandSourceStack(), msg);
    }

    /**
     * outputs a notification message to the chat box of the given sender.
     * 
     * @param sender
     *            CommandSender to chat to.
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

    public static void chatNotification(PlayerEntity sender, String msg)
    {
        sendMessage(sender.createCommandSourceStack(), msg);
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

    public static final char FORMAT_CHARACTERS[] = new char[TextFormatting.values().length];

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
     * Apply a set of {@link EnumChatFormatting} to a {@link ChatStyle}
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
     * Apply an {@link EnumChatFormatting} to a {@link ChatStyle}
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
     * Take a string of chat format codes (without \u00a7) and return them as {@link EnumChatFormatting} collection
     * 
     * @param textFormats
     * @return
     */
    public static Collection<TextFormatting> enumChatFormattings(String textFormats)
    {
        List<TextFormatting> result = new ArrayList<TextFormatting>();
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

    public static String formatHtml(String message)
    {
        StringBuilder sb = new StringBuilder();
        int pos = 0;
        int tagCount = 0;
        Matcher matcher = FORMAT_CODE_PATTERN.matcher(message);
        while (matcher.find())
        {
            sb.append(StringEscapeUtils.escapeHtml4(message.substring(pos, matcher.start())));
            pos = matcher.end();
            char formatChar = matcher.group(1).charAt(0);
            for (TextFormatting format : TextFormatting.values())
            {
                if (FORMAT_CHARACTERS[format.ordinal()] == formatChar)
                {
                    sb.append("<span class=\"mcf");
                    sb.append(formatChar);
                    sb.append("\">");
                    tagCount++;
                    break;
                }
            }
        }
        sb.append(StringEscapeUtils.escapeHtml4(message.substring(pos, message.length())));
        // for (; pos < message.length(); pos++)
        // sb.append(message.charAt(pos));
        for (int i = 0; i < tagCount; i++)
            sb.append("</span>");
        return sb.toString();
    }

    public static boolean isStyleEmpty(Style style)
    {
        return !style.isBold() && !style.isItalic() && !style.isObfuscated() && !style.isStrikethrough() && !style.isUnderlined()
                && style.getColor() == null;
    }

    public static enum ChatFormat
    {

        PLAINTEXT, /* HTML, */ MINECRAFT, DETAIL;

        public Object format(TextComponent message)
        {
            switch (this)
            {
            // case HTML:
            // return ChatOutputHandler.formatHtml(message);
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
        BUILDER.comment(
                "This controls the colors of the various chats output by ForgeEssentials." + "\nValid output colors are as follows:"
                        + "\naqua, black, blue, dark_aqua, dark_blue, dark_gray, dark_green, dark_purple, dark_red"
                        + "\ngold, gray, green, light_purple, red, white, yellow")
                .push(CONFIG_MAIN_OUTPUT);
        FEchatConfirmationColor = BUILDER.comment("Defaults to green.")
                .define("confirmationColor", "green");
        FEchatErrorColor = BUILDER.comment("Defaults to red.")
                .define("errorOutputColor", "red");
        FEchatNotificationColor = BUILDER.comment("Defaults to aqua.")
                .define("notificationOutputColor", "aqua");
        FEchatWarningColor = BUILDER.comment("Defaults to yellow.")
                .define("warningOutputColor", "yellow");
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
