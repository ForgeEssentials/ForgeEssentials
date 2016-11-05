package com.forgeessentials.util;

import java.util.regex.Pattern;

import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.IChatComponent;
import net.minecraftforge.common.util.FakePlayer;

public final class ChatUtil
{

    public static final char COLOR_FORMAT_CHARACTER = '\u00a7';

    public static final Pattern FORMAT_CODE_PATTERN;

    public static EnumChatFormatting chatErrorColor = EnumChatFormatting.RED;
    public static EnumChatFormatting chatWarningColor = EnumChatFormatting.YELLOW;
    public static EnumChatFormatting chatConfirmationColor = EnumChatFormatting.GREEN;
    public static EnumChatFormatting chatNotificationColor = EnumChatFormatting.AQUA;

    static
    {
        String codes = "";
        for (EnumChatFormatting code : EnumChatFormatting.values())
            codes += code.getFormattingCode();
        FORMAT_CODE_PATTERN = Pattern.compile(ChatUtil.COLOR_FORMAT_CHARACTER + "([" + codes + "])");
    }

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
            Utils.felog.info(String.format("Fakeplayer %s: %s", recipient.getCommandSenderName(), message.getUnformattedText()));
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
        sendMessage(sender, error(msg));
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
        sendMessage(sender, confirmation(msg));
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
        sendMessage(sender, warning(msg));
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
        sendMessage(sender, notification(msg));
    }

    /* ------------------------------------------------------------ */

    /**
     * Sends a message to all clients
     *
     * @param message
     *            The message to send
     */
    public static void broadcast(String message)
    {
        broadcast(new ChatComponentText(message));
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

}
