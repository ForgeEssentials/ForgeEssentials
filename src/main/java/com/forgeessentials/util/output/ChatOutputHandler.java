package com.forgeessentials.util.output;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import net.minecraft.command.ICommandSender;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.ChatStyle;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.IChatComponent;
import net.minecraftforge.common.config.Configuration;

import org.apache.commons.lang3.StringEscapeUtils;

import com.forgeessentials.core.moduleLauncher.config.ConfigLoaderBase;
import com.forgeessentials.util.ChatUtil;

public final class ChatOutputHandler extends ConfigLoaderBase
{

    public static final String CONFIG_CAT = "Core.Output";

    public static EnumChatFormatting chatErrorColor, chatWarningColor, chatConfirmationColor, chatNotificationColor;

    /* ------------------------------------------------------------ */

    /* ------------------------------------------------------------ */

    public static IChatComponent confirmation(String message)
    {
        return ChatUtil.setChatColor(new ChatComponentText(ChatUtil.formatColors(message)), chatConfirmationColor);
    }

    public static IChatComponent notification(String message)
    {
        return ChatUtil.setChatColor(new ChatComponentText(ChatUtil.formatColors(message)), chatNotificationColor);
    }

    public static IChatComponent warning(String message)
    {
        return ChatUtil.setChatColor(new ChatComponentText(ChatUtil.formatColors(message)), chatWarningColor);
    }

    public static IChatComponent error(String message)
    {
        return ChatUtil.setChatColor(new ChatComponentText(ChatUtil.formatColors(message)), chatErrorColor);
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
        ChatUtil.sendMessage(sender, msg, chatErrorColor);
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
        ChatUtil.sendMessage(sender, msg, chatConfirmationColor);
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
        ChatUtil.sendMessage(sender, msg, chatWarningColor);
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
        ChatUtil.sendMessage(sender, msg, chatNotificationColor);
    }

    /* ------------------------------------------------------------ */

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
                sb.append("<span class=\"");
                EnumChatFormatting color = style.getColor();
                if (color != null)
                {
                    sb.append(" mcf");
                    sb.append(color.getFormattingCode());
                }
                if (style.getBold())
                {
                    sb.append(" mcf");
                    sb.append(EnumChatFormatting.BOLD.getFormattingCode());
                }
                if (style.getItalic())
                {
                    sb.append(" mcf");
                    sb.append(EnumChatFormatting.ITALIC.getFormattingCode());
                }
                if (style.getUnderlined())
                {
                    sb.append(" mcf");
                    sb.append(EnumChatFormatting.UNDERLINE.getFormattingCode());
                }
                if (style.getObfuscated())
                {
                    sb.append(" mcf");
                    sb.append(EnumChatFormatting.OBFUSCATED.getFormattingCode());
                }
                if (style.getStrikethrough())
                {
                    sb.append(" mcf");
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
        Matcher matcher = ChatUtil.FORMAT_CODE_PATTERN.matcher(message);
        while (matcher.find())
        {
            sb.append(StringEscapeUtils.escapeHtml4(message.substring(pos, matcher.start())));
            pos = matcher.end();
            char formatChar = matcher.group(1).charAt(0);
            for (EnumChatFormatting format : EnumChatFormatting.values())
            {
                if (format.getFormattingCode() == formatChar)
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

    public static boolean isStyleEmpty(ChatStyle style)
    {
        return !style.getBold() && !style.getItalic() && !style.getObfuscated() && !style.getStrikethrough() && !style.getUnderlined()
                && style.getColor() == null;
    }

    public static enum ChatFormat
    {

        PLAINTEXT, HTML, MINECRAFT, DETAIL;

        public Object format(IChatComponent message)
        {
            switch (this)
            {
            case HTML:
                return ChatOutputHandler.formatHtml(message);
            case MINECRAFT:
                return ChatOutputHandler.getFormattedMessage(message);
            case DETAIL:
                return message;
            default:
            case PLAINTEXT:
                return ChatUtil.stripFormatting(ChatOutputHandler.getUnformattedMessage(message));
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
     * @param time in seconds
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
     * @param time in milliseconds
     * @return Time in string format
     */
    public static String formatTimeDurationReadableMilli(long time, boolean showSeconds)
    {
        return formatTimeDurationReadable(time / 1000, showSeconds);
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
        config.addCustomCategoryComment(CONFIG_CAT,
                "This controls the colors of the various chats output by ForgeEssentials." + "\nValid output colors are as follows:"
                        + "\naqua, black, blue, dark_aqua, dark_blue, dark_gray, dark_green, dark_purple, dark_red"
                        + "\ngold, gray, green, light_purple, red, white, yellow");

        setConfirmationColor(config.get(CONFIG_CAT, "confirmationColor", "green", "Defaults to green.").getString());
        setErrorColor(config.get(CONFIG_CAT, "errorOutputColor", "red", "Defaults to red.").getString());
        setNotificationColor(config.get(CONFIG_CAT, "notificationOutputColor", "aqua", "Defaults to aqua.").getString());
        setWarningColor(config.get(CONFIG_CAT, "warningOutputColor", "yellow", "Defaults to yellow.").getString());
    }

}
