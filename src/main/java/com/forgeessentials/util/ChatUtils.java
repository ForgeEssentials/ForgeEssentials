package com.forgeessentials.util;

import net.minecraft.command.ICommandSender;
import net.minecraft.server.management.ServerConfigurationManager;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.ChatStyle;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.IChatComponent;

public class ChatUtils {

    /**
     * Sends a chat message to the given command sender (usually a player) with the given text and no
     * special formatting.
     *
     * @param recipient The recipient of the chat message.
     * @param message   The message to send.
     */
    public static void sendMessage(ICommandSender recipient, String message)
    {
        recipient.addChatMessage(createFromText(message));
    }

    /**
     * Sends a global chat message.
     *
     * @param configurationManager The configuration manager used to send the message.
     * @param message              The message to send.
     */
    public static void sendMessage(ServerConfigurationManager configurationManager, String message)
    {
        configurationManager.sendChatMsg(createFromText(message));
    }

    public static IChatComponent createFromText(String string)
    {
        ChatComponentText component = new ChatComponentText(string);
        return component;
    }

    /**
     * Processes an IChatComponent and adds formatting to it.
     *
     * @param toColour
     * @param colour
     * @param others
     * @return
     */
    public static IChatComponent colourize(IChatComponent toColour, EnumChatFormatting colour, boolean[] others)
    {
        ChatStyle style = new ChatStyle().setColor(colour);
        toColour.setChatStyle(style);
    }

}
