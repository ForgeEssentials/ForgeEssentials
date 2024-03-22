package com.forgeessentials.client.handler;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.AbstractGui;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

import ResourceLocation;

public class Packet07RemoteQRRenderer
{

    public static ResourceLocation qrCode;

    @SubscribeEvent
    public void onRenderGameOverlayEventPost(RenderGameOverlayEvent.Post pEvent)
    {
        if (pEvent.getType() == RenderGameOverlayEvent.ElementType.ALL)
        {
            if (qrCode != null)
            {
                Minecraft mc = Minecraft.getInstance();
                mc.getTextureManager().bind(qrCode);
                AbstractGui.blit(pEvent.getMatrixStack(), (mc.getWindow().getGuiScaledWidth() / 2) - 64, (mc.getWindow().getGuiScaledHeight() / 2) - 64, 0f, 0f,
                        128, 128, 128, 128);
                mc.getTextureManager().bind(AbstractGui.GUI_ICONS_LOCATION);
            }
        }
    }
}
