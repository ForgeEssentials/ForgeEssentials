package com.forgeessentials.chataddon.discord;

import com.forgeessentials.chat.handlers.GenericDiscordMessageHandler;

public class ActualDiscordMessageHandler extends GenericDiscordMessageHandler{

	@Override
	public void sendMessage(String message) {
		ModuleDiscordBridge.instance.sendMessage(message);
	}

}
