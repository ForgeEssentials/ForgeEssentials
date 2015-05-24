package com.forgeessentials.client.remote;

import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.event.RenderWorldLastEvent;

import org.lwjgl.opengl.GL11;

import com.forgeessentials.client.core.ClientProxy;

import cpw.mods.fml.client.FMLClientHandler;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

@SideOnly(value = Side.CLIENT)
public class QRRenderer
{

    @SubscribeEvent
    public void render(RenderWorldLastEvent event)
    {
        EntityPlayer player = FMLClientHandler.instance().getClient().thePlayer;
        if (player == null)
            return;
        Minecraft mc = Minecraft.getMinecraft();
        ResourceLocation qrCode = ClientProxy.getQRCode();
        if (mc.currentScreen != null && qrCode != null)
        {
            mc.entityRenderer.setupOverlayRendering();
            mc.renderEngine.bindTexture(qrCode);
            GL11.glPushMatrix();
            GL11.glScalef(0.5F, 0.5F, 0);
            mc.currentScreen.drawTexturedModalRect(0, 0, 0, 0, 256, 256);
            GL11.glPopMatrix();
        }
        else if (qrCode != null)
        {
            mc.renderEngine.deleteTexture(qrCode);
            ClientProxy.setQRCode(null);
        }
    }

}
