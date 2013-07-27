package com.ForgeEssentials.util;

import net.minecraft.command.ICommandSender;
import net.minecraft.server.management.ServerConfigurationManager;
import net.minecraft.util.ChatMessageComponent;

public class ChatUtils {

    public static void sendMessage(ICommandSender recipient, String message) {
        recipient.sendChatToPlayer(ChatMessageComponent.func_111066_d(message));
    }

    public static void sendMessage(ServerConfigurationManager configurationManager, String message) {
        configurationManager.sendChatMsg(ChatMessageComponent.func_111066_d(message));
    }

    public static void sendTranslatedMessage(ICommandSender recipient, String messageId, Object... args) {
        ChatMessageComponent message = ChatMessageComponent.func_111082_b(messageId, args);
        recipient.sendChatToPlayer(message);
    }

}
