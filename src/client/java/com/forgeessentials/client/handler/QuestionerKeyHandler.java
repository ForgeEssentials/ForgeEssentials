package com.forgeessentials.client.handler;

import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.language.I18n;
import net.minecraftforge.client.event.InputEvent.ClickInputEvent;
import net.minecraftforge.client.event.InputEvent.KeyInputEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fmlclient.registry.ClientRegistry;

/**
 * Just a utility class. Pressing the buttons while there is no question asked will only give you an error message.
 */
public class QuestionerKeyHandler
{
    private static final String category = I18n.get("forgeessentialsclient.questioner");
    private static final KeyMapping yes = new KeyMapping(I18n.get("forgeessentialsclient.yes"), 297, category);
    private static final KeyMapping no = new KeyMapping(I18n.get("forgeessentialsclient.no"), 298, category);

    public QuestionerKeyHandler()
    {
        ClientRegistry.registerKeyBinding(yes);
        ClientRegistry.registerKeyBinding(no);
        MinecraftForge.EVENT_BUS.register(this);
    }

    @SubscribeEvent
    public void onKeyPress(ClickInputEvent e)
    {
        if (Packet07RemoteQRRenderer.qrCode != null)
        {
            Packet07RemoteQRRenderer.qrCode = null;
        }
    }

    @SubscribeEvent
    public void onKeyPress(KeyInputEvent e)
    {
        Minecraft minecraft = Minecraft.getInstance();
        if (!minecraft.isWindowActive())
        {
            return;
        }
        if (yes.isDown())
        {
            minecraft.player.chat("/feyes");
        }
        else if (no.isDown())
        {
            minecraft.player.chat("/feno");
        }
    }
}
