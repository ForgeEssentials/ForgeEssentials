package com.forgeessentials.util.output;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.ChatStyle;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.IChatComponent;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.common.util.FakePlayer;

import com.forgeessentials.core.moduleLauncher.config.ConfigLoader.ConfigLoaderBase;

public final class ChatOutputHandler extends ConfigLoaderBase
{

    public static final char COLOR_FORMAT_CHARACTER = '\u00a7';

    public static final String CONFIG_CAT = "Core.Output";

    public static EnumChatFormatting chatErrorColor, chatWarningColor, chatConfirmationColor, chatNotificationColor;

    /* ------------------------------------------------------------ */

    /**
     * Sends a chat message to the given command sender (usually a player) with the given text and no special
     * formatting.
     *
     * @param recipient
     *            The recipient of the chat message.
     * @param message
     *            The message to send.
     */
    public static void sendMessage(ICommandSender recipient, String message)
    {
        sendMessage(recipient, new ChatComponentText(message));
    }

    /**
     * Sends a message to a {@link ICommandSender} and performs some security checks
     * 
     * @param recipient
     * @param message
     */
    public static void sendMessage(ICommandSender recipient, IChatComponent message)
    {
        if (recipient instanceof FakePlayer && ((EntityPlayerMP) recipient).playerNetServerHandler == null)
            LoggingHandler.felog.info(String.format("Fakeplayer %s: %s", recipient.getCommandSenderName(), message.getUnformattedText()));
        else
            recipient.addChatMessage(message);
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
    public static void sendMessage(ICommandSender recipient, String message, EnumChatFormatting color)
    {
        message = formatColors(message);
        if (recipient instanceof EntityPlayer)
        {
            ChatComponentText component = new ChatComponentText(message);
            component.getChatStyle().setColor(color);
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
    public static void broadcast(IChatComponent message)
    {
        MinecraftServer.getServer().getConfigurationManager().sendChatMsg(message);
    }

    /* ------------------------------------------------------------ */

    public static IChatComponent confirmation(String message)
    {
        return setChatColor(new ChatComponentText(formatColors(message)), chatConfirmationColor);
    }

    public static IChatComponent notification(String message)
    {
        return setChatColor(new ChatComponentText(formatColors(message)), chatNotificationColor);
    }

    public static IChatComponent warning(String message)
    {
        return setChatColor(new ChatComponentText(formatColors(message)), chatWarningColor);
    }

    public static IChatComponent error(String message)
    {
        return setChatColor(new ChatComponentText(formatColors(message)), chatErrorColor);
    }

    /**
     * Utility method to set {@link IChatComponent} color
     *
     * @param message
     * @param color
     * @return message
     */
    public static IChatComponent setChatColor(IChatComponent message, EnumChatFormatting color)
    {
        message.getChatStyle().setColor(color);
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
    public static void chatError(ICommandSender sender, String msg)
    {
        sendMessage(sender, msg, chatErrorColor);
    }

    /**
     * outputs a confirmation message to the chat box of the given sender.
     *
     * @param sender
     *            CommandSender to chat to.
     * @param msg
     *            the message to be sent
     */
    public static void chatConfirmation(ICommandSender sender, String msg)
    {
        sendMessage(sender, msg, chatConfirmationColor);
    }

    /**
     * outputs a warning message to the chat box of the given sender.
     *
     * @param sender
     *            CommandSender to chat to.
     * @param msg
     *            the message to be sent
     */
    public static void chatWarning(ICommandSender sender, String msg)
    {
        sendMessage(sender, msg, chatWarningColor);
    }

    /**
     * outputs a notification message to the chat box of the given sender.
     * 
     * @param sender
     *            CommandSender to chat to.
     * @param msg
     */
    public static void chatNotification(ICommandSender sender, String msg)
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
    static
    {
        String codes = "";
        for (EnumChatFormatting code : EnumChatFormatting.values())
            codes += code.getFormattingCode();
        FORMAT_CODE_PATTERN = Pattern.compile(COLOR_FORMAT_CHARACTER + "([" + codes + "])");
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
    public static void applyFormatting(ChatStyle chatStyle, Collection<EnumChatFormatting> formattings)
    {
        for (EnumChatFormatting format : formattings)
            applyFormatting(chatStyle, format);
    }

    /**
     * Apply an {@link EnumChatFormatting} to a {@link ChatStyle}
     * 
     * @param chatStyle
     * @param formatting
     */
    public static void applyFormatting(ChatStyle chatStyle, EnumChatFormatting formatting)
    {
        switch (formatting)
        {
        case BOLD:
            chatStyle.setBold(true);
            break;
        case ITALIC:
            chatStyle.setItalic(true);
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
            chatStyle.setColor(formatting);
            break;
        }
    }

    /**
     * Take a string of chat format codes (without \u00a7) and return them as {@link EnumChatFormatting} collection
     * 
     * @param textFormats
     * @return
     */
    public static Collection<EnumChatFormatting> enumChatFormattings(String textFormats)
    {
        List<EnumChatFormatting> result = new ArrayList<EnumChatFormatting>();
        for (int i = 0; i < textFormats.length(); i++)
        {
            char formatChar = textFormats.charAt(i);
            for (EnumChatFormatting format : EnumChatFormatting.values())
                if (format.getFormattingCode() == formatChar)
                {
                    result.add(format);
                    break;
                }
        }
        return result;
    }

    /* ------------------------------------------------------------ */

    public static String getUnformattedMessage(IChatComponent message)
    {
        StringBuilder sb = new StringBuilder();
        for (Object msg : message)
            sb.append(((IChatComponent) msg).getUnformattedTextForChat());
        return sb.toString();
    }

    public static String getFormattedMessage(IChatComponent message)
    {
        StringBuilder sb = new StringBuilder();
        for (Object msg : message)
            sb.append(((IChatComponent) msg).getFormattedText());
        return sb.toString();
    }

    public static String formatHtml(IChatComponent message)
    {
        // TODO: HTML formatting function
        StringBuilder sb = new StringBuilder();
        for (Object msgObj : message)
        {
            IChatComponent msg = (IChatComponent) msgObj;
            ChatStyle style = msg.getChatStyle();
            if (!isStyleEmpty(style))
            {
                sb.append("<span class=\"mcf");
                EnumChatFormatting color = style.getColor();
                if (color != null)
                {
                    sb.append(" ");
                    sb.append(color.getFormattingCode());
                }
                if (style.getBold())
                {
                    sb.append(" ");
                    sb.append(EnumChatFormatting.BOLD.getFormattingCode());
                }
                if (style.getItalic())
                {
                    sb.append(" ");
                    sb.append(EnumChatFormatting.ITALIC.getFormattingCode());
                }
                if (style.getUnderlined())
                {
                    sb.append(" ");
                    sb.append(EnumChatFormatting.UNDERLINE.getFormattingCode());
                }
                if (style.getObfuscated())
                {
                    sb.append(" ");
                    sb.append(EnumChatFormatting.OBFUSCATED.getFormattingCode());
                }
                if (style.getStrikethrough())
                {
                    sb.append(" ");
                    sb.append(EnumChatFormatting.STRIKETHROUGH.getFormattingCode());
                }
                sb.append("\">");
                sb.append(formatHtml(msg.getUnformattedTextForChat()));
                sb.append("</span>");
            }
            else
            {
                sb.append(formatHtml(msg.getUnformattedTextForChat()));
            }
        }
        return sb.toString();
    }

    public static String formatHtml(String message)
    {
        StringBuilder sb = new StringBuilder();
        int pos = 0;
        int tagCount = 0;
        Matcher matcher = FORMAT_CODE_PATTERN.matcher(message);
        while (matcher.find())
        {
            for (; pos < matcher.start(); pos++)
                sb.append(message.charAt(pos));
            pos = matcher.end();
            char formatChar = matcher.group(1).charAt(0);
            for (EnumChatFormatting format : EnumChatFormatting.values())
            {
                if (format.getFormattingCode() == formatChar)
                {
                    sb.append("<span class=\"mcf ");
                    sb.append(formatChar);
                    sb.append("\">");
                    tagCount++;
                    break;
                }
            }
        }
        for (; pos < message.length(); pos++)
            sb.append(message.charAt(pos));
        for (int i = 0; i < tagCount; i++)
            sb.append("</span>");
        return sb.toString();
    }

    public static boolean isStyleEmpty(ChatStyle style)
    {
        return !style.getBold() && !style.getItalic() && !style.getObfuscated() && !style.getStrikethrough() && !style.getUnderlined()
                && style.getColor() == null;
    }

    /* ------------------------------------------------------------ */

    public static final long SECOND = 1;
    public static final long MINUTE_SECONDS = 60 * SECOND;
    public static final long HOUR_SECONDS = 60 * MINUTE_SECONDS;
    public static final long DAY_SECONDS = 24 * HOUR_SECONDS;
    public static final long WEEK_SECONDS = 7 * DAY_SECONDS;

    /**
     * Gets a nice string with only needed elements. Max time is weeks
     *
     * @param time
     * @return Time in string format
     */
    public static String formatTimeDurationReadable(long time, boolean showSeconds)
    {
        long weeks = time / WEEK_SECONDS;
        time -= WEEK_SECONDS * weeks;
        long days = time / DAY_SECONDS;
        time -= DAY_SECONDS * days;
        long hours = time / HOUR_SECONDS;
        time -= HOUR_SECONDS * hours;
        long minutes = time / MINUTE_SECONDS;
        time -= MINUTE_SECONDS * minutes;
        long seconds = time / SECOND;

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

    /* ------------------------------------------------------------ */

    public static void setConfirmationColor(String color)
    {
        chatConfirmationColor = EnumChatFormatting.getValueByName(color);
        if (chatConfirmationColor == null)
            chatConfirmationColor = EnumChatFormatting.GREEN;
    }

    public static void setErrorColor(String color)
    {
        chatErrorColor = EnumChatFormatting.getValueByName(color);
        if (chatErrorColor == null)
            chatErrorColor = EnumChatFormatting.RED;
    }

    public static void setNotificationColor(String color)
    {
        chatNotificationColor = EnumChatFormatting.getValueByName(color);
        if (chatNotificationColor == null)
            chatNotificationColor = EnumChatFormatting.AQUA;
    }

    public static void setWarningColor(String color)
    {
        chatWarningColor = EnumChatFormatting.getValueByName(color);
        if (chatWarningColor == null)
            chatWarningColor = EnumChatFormatting.YELLOW;
    }

    @Override
    public void load(Configuration config, boolean isReload)
    {
        config.addCustomCategoryComment(CONFIG_CAT, "This controls the colors of the various chats output by ForgeEssentials."
                + "\nValid output colors are as follows:" + "\naqua, black, blue, dark_aqua, dark_blue, dark_gray, dark_green, dark_purple, dark_red"
                + "\ngold, gray, green, light_purple, red, white, yellow");

        setConfirmationColor(config.get(CONFIG_CAT, "confirmationColor", "green", "Defaults to green.").getString());
        setErrorColor(config.get(CONFIG_CAT, "errorOutputColor", "red", "Defaults to red.").getString());
        setNotificationColor(config.get(CONFIG_CAT, "notificationOutputColor", "aqua", "Defaults to aqua.").getString());
        setWarningColor(config.get(CONFIG_CAT, "warningOutputColor", "yellow", "Defaults to yellow.").getString());
    }

}
