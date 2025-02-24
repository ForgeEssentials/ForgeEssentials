package com.forgeessentials.client.handler;

import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.language.I18n;
import net.minecraftforge.client.event.RegisterKeyMappingsEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.TickEvent.ClientTickEvent;
import net.minecraftforge.event.TickEvent.Phase;
import net.minecraftforge.eventbus.api.SubscribeEvent;

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
        MinecraftForge.EVENT_BUS.register(this);
    }

    @SubscribeEvent
    public void registerBindings(RegisterKeyMappingsEvent event) {
        event.register(yes);
        event.register(no);
    }

    /*@SubscribeEvent
    public void onKeyPress(ClickInputEvent e)
    {
        if (Packet07RemoteQRRenderer.qrCode != null)
        {
            Packet07RemoteQRRenderer.qrCode = null;
        }
    }*/

    @SubscribeEvent
    public void onKeyPress(ClientTickEvent e)
    {
        if (e.phase == Phase.END)
        {

            Minecraft minecraft = Minecraft.getInstance();
            if (!minecraft.isWindowActive())
            {
                return;
            }
            if (yes.isDown())
            {
                minecraft.player.connection.sendCommand("/feyes");
            }
            else if (no.isDown())
            {
                minecraft.player.connection.sendCommand("/feno");
            }
        }
    }
}
