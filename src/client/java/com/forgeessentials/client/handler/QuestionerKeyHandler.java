package com.forgeessentials.client.handler;

import net.minecraft.client.Minecraft;
import net.minecraft.client.settings.KeyBinding;
import net.minecraftforge.client.event.InputEvent.ClickInputEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.client.registry.ClientRegistry;

/**
 * Just a utility class. Pressing the buttons while there is no question asked
 * will only give you an error message.
 */
public class QuestionerKeyHandler {
	private static final String category = "keys.fe.questioner";
	private static final KeyBinding yes = new KeyBinding("keys.fe.yes", 297, category);
	private static final KeyBinding no = new KeyBinding("keys.fe.no", 298, category);

	public QuestionerKeyHandler() {
		ClientRegistry.registerKeyBinding(yes);
		ClientRegistry.registerKeyBinding(no);
		MinecraftForge.EVENT_BUS.register(this);
	}

	@SubscribeEvent
	public void onKeyPress(ClickInputEvent e) {
		Minecraft minecraft = Minecraft.getInstance();
		if (!minecraft.isWindowActive()) {
			return;
		}
		if (yes.isDown()) {
			minecraft.player.chat("/yes");
		} else if (no.isDown()) {
			minecraft.player.chat("/no");
		}
	}
}
