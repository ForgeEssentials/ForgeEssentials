package com.forgeessentials.util;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
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

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.forgeessentials.core.ForgeEssentials;
import com.forgeessentials.core.moduleLauncher.config.ConfigLoader.ConfigLoaderBase;

public final class OutputHandler extends ConfigLoaderBase
{

    public static final char COLOR_FORMAT_CHARACTER = '\u00a7';

    public static final LogWrapper felog = new LogWrapper(LogManager.getLogger("ForgeEssentials"));

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
            felog.info("Fakeplayer %s: " + message.getUnformattedText());
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
        FORMAT_CODE_PATTERN = Pattern.compile(COLOR_FORMAT_CHARACTER + "[" + codes + "]");
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

    /**
     * outputs a string to the console if the code is in MCP
     *
     * @param msg
     *            message to be outputted
     */
    public static void debug(Object msg)
    {
        if (ForgeEssentials.isDebugMode())
        {
            System.out.println(" {DEBUG} >>>> " + msg);
        }
    }

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

    public static class LogWrapper
    {
        private Logger wrapped;

        protected LogWrapper(Logger logger)
        {
            wrapped = logger;
        }

        public void finest(String message)
        {
            wrapped.log(Level.ALL, message);
        }

        public void finer(String message)
        {
            wrapped.log(Level.DEBUG, message);
        }

        public void fine(String message)
        {
            wrapped.log(Level.INFO, message);
        }

        public void info(String message)
        {
            wrapped.log(Level.INFO, message);
        }

        public void warning(String message)
        {
            wrapped.log(Level.WARN, message);
        }

        public void severe(String message)
        {
            wrapped.log(Level.ERROR, message);
        }

        public void log(Level level, String message, Throwable error)
        {
            wrapped.log(level, message, error);
        }

        public Logger getWrapper()
        {
            return wrapped;
        }
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
