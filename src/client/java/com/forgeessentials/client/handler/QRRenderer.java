package com.forgeessentials.client.handler;

import java.io.IOException;
import java.net.URL;

import javax.imageio.ImageIO;

import net.minecraft.client.Minecraft;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraft.client.renderer.texture.DynamicTexture;
import net.minecraft.client.renderer.texture.SimpleTexture;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.resources.IResourceManager;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.Util;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.event.RenderWorldLastEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

import org.lwjgl.opengl.GL11;

import com.forgeessentials.commons.network.packets.Packet7Remote;
import com.mojang.blaze3d.matrix.MatrixStack;

@OnlyIn(Dist.CLIENT)
public class QRRenderer implements IMessageHandler<Packet7Remote, IMessage>
{

    private static ResourceLocation qrCode;

    @SubscribeEvent
    public void render(RenderWorldLastEvent event)
    {
    	PlayerEntity player = Minecraft.getInstance().player;
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

    @Override
    public IMessage onMessage(Packet7Remote message, MessageContext ctx)
    {
        try
        {
            DynamicTexture qrCodeTexture = new DynamicTexture(ImageIO.read(new URL(message.link)));
            qrCode = Minecraft.getInstance().getTextureManager().register("qr_code", qrCodeTexture);
        }
        catch (IOException e)
        {
        	ITextComponent cmsg = new StringTextComponent("Could not load QR Code. " + e.getMessage());
            cmsg.getStyle().withColor(TextFormatting.RED);
            Minecraft.getInstance().player.sendMessage(cmsg,Util.NIL_UUID);
            e.printStackTrace();
        }
        return null;
    }
    
}
