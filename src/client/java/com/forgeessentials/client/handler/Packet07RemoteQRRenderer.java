package com.forgeessentials.client.handler;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;


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
                mc.getTextureManager().bindForSetup(qrCode);
                GuiComponent.blit(pEvent.getMatrixStack(), (mc.getWindow().getGuiScaledWidth() / 2) - 64, (mc.getWindow().getGuiScaledHeight() / 2) - 64, 0f, 0f,
                        128, 128, 128, 128);
                mc.getTextureManager().bindForSetup(GuiComponent.GUI_ICONS_LOCATION);
            }
        }
    }
}
