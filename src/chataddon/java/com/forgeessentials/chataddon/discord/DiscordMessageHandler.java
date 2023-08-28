package com.forgeessentials.chataddon.discord;

import com.forgeessentials.util.output.DiscordMessageHandlerBase;

public class DiscordMessageHandler extends DiscordMessageHandlerBase{

	public void sendMessage(String message) {
		ModuleDiscordBridge.instance.sendMessage(message);
	}

}
