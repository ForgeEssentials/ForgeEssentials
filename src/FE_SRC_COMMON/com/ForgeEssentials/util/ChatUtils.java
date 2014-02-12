package com.ForgeEssentials.util;

import net.minecraft.command.ICommandSender;
import net.minecraft.server.management.ServerConfigurationManager;
import net.minecraft.util.ChatMessageComponent;

public class ChatUtils
{

	/**
	 * Sends a chat message to the given command sender (usually a player) with the given text and no
	 * special formatting.
	 *
	 * @param recipient The recipient of the chat message.
	 * @param message   The message to send.
	 */
	public static void sendMessage(ICommandSender recipient, String message)
	{
		recipient.sendChatToPlayer(ChatMessageComponent.createFromText(message));
	}

	/**
	 * Sends a message that will be translated on the client-side with a list of replacement arguments.
	 *
	 * @param recipient The recipient of the chat message.
	 * @param messageId The localization id of the text that should be displayed. (This is client side localization).
	 * @param args      The formatting arguments for the message.
	 */
	public static void sendLocalizedMessage(ICommandSender recipient, String messageId, Object... args)
	{
		ChatMessageComponent message = ChatMessageComponent.createFromTranslationWithSubstitutions(messageId, args);
		recipient.sendChatToPlayer(message);
	}

	/**
	 * Sends a global chat message.
	 *
	 * @param configurationManager The configuration manager used to send the message.
	 * @param message              The message to send.
	 */
	public static void sendMessage(ServerConfigurationManager configurationManager, String message)
	{
		configurationManager.sendChatMsg(ChatMessageComponent.createFromText(message));
	}

}
