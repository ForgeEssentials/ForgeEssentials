package com.forgeessentials.client.handler;

import net.minecraft.client.settings.KeyBinding;

import org.lwjgl.input.Keyboard;

import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.client.FMLClientHandler;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.InputEvent.KeyInputEvent;

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
        MinecraftForge.EVENT_BUS.register(this);
    }
    @SubscribeEvent
    public void onKeyPress(KeyInputEvent e)
    {
        if (!FMLClientHandler.instance().getClient().inGameHasFocus)
        {
            return;
        }
        if (yes.isPressed())
        {
            FMLClientHandler.instance().getClientPlayerEntity().sendChatMessage("/yes");
        }
        else if (no.isPressed())
        {
            FMLClientHandler.instance().getClientPlayerEntity().sendChatMessage("/no");
        }
    }
}
