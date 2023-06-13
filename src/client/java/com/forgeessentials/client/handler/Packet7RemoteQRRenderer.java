package com.forgeessentials.client.handler;

import org.lwjgl.opengl.GL11;

import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.event.RenderWorldLastEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

public class Packet7RemoteQRRenderer
{

    public ResourceLocation qrCode;

    @SubscribeEvent
    public void render(RenderWorldLastEvent event)
    {
    	Minecraft instance = Minecraft.getInstance();
    	PlayerEntity player = instance.player;
        if (player == null)
            return;
        Minecraft mc = Minecraft.getInstance();
        if (mc.screen != null && qrCode != null)
        {
        	
        	mc.textureManager.bind(qrCode);
        	GL11.glPushMatrix();
            GL11.glScalef(0.5F, 0.5F, 0);
            GL11.glColor4f(1, 1, 1, 1);
            mc.gui.blit(event.getMatrixStack(), 0, 0, 0, 0, 256, 256);
            GL11.glPopMatrix();
            
        }
        else if (qrCode != null)
        {
        	mc.textureManager.release(qrCode);
            qrCode = null;
        }
    } 
}
