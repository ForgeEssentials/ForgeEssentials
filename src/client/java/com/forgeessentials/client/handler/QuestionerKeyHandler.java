package com.forgeessentials.client.handler;

import net.minecraft.client.settings.KeyBinding;

import org.lwjgl.input.Keyboard;

import cpw.mods.fml.client.FMLClientHandler;
import cpw.mods.fml.client.registry.ClientRegistry;
import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.InputEvent.KeyInputEvent;

/**
 * Just a utility class. Pressing the buttons while there is no question asked will only give you an error message.
 */
public class QuestionerKeyHandler
{
    private static final String category = "keys.fe.questioner";
    private static final KeyBinding yes = new KeyBinding("keys.fe.yes", Keyboard.KEY_F8, category);
    private static final KeyBinding no = new KeyBinding("keys.fe.no", Keyboard.KEY_F9, category);

    public QuestionerKeyHandler()
    {
        ClientRegistry.registerKeyBinding(yes);
        ClientRegistry.registerKeyBinding(no);
        FMLCommonHandler.instance().bus().register(this);
    }
    @SubscribeEvent
    public void onKeyPress(KeyInputEvent e)
    {
        if (!FMLClientHandler.instance().getClient().inGameHasFocus)
        {
            return;
        }
        if (yes.getIsKeyPressed())
        {
            FMLClientHandler.instance().getClientPlayerEntity().sendChatMessage("/yes");
        }
        else if (no.getIsKeyPressed())
        {
            FMLClientHandler.instance().getClientPlayerEntity().sendChatMessage("/no");
        }
    }
}
