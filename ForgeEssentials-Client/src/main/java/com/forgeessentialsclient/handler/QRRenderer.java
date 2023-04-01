package com.forgeessentialsclient.handler;

import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

import javax.imageio.ImageIO;

import net.minecraft.client.Minecraft;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraft.client.renderer.texture.DynamicTexture;
import net.minecraft.client.renderer.texture.NativeImage;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.event.RenderWorldLastEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.network.NetworkEvent.Context;

import org.lwjgl.opengl.GL11;

import com.forgeessentialsclient.utils.commons.network.packets.Packet7Remote;


@OnlyIn(Dist.CLIENT)
public class QRRenderer extends Packet7Remote
{

    private static ResourceLocation qrCode;

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

    @Override
	public void handle(Context context) {
    	Packet7Remote packet7Remote = new Packet7Remote();
    	try
        {
    		BufferedImage img =ImageIO.read(new URL(packet7Remote.link));
    		ByteArrayOutputStream baos = new ByteArrayOutputStream();
    		ImageIO.write(img, "png", baos);
    		InputStream is = new ByteArrayInputStream(baos.toByteArray());
            DynamicTexture qrCodeTexture = new DynamicTexture(NativeImage.read(is));
            qrCode = Minecraft.getInstance().getTextureManager().register("qr_code", qrCodeTexture);
        }
        catch (IOException e)
        {
        	TextComponent cmsg = new StringTextComponent("Could not load QR Code. " + e.getMessage());
            cmsg.withStyle(TextFormatting.RED);
            Minecraft instance = Minecraft.getInstance();
            instance.player.sendMessage(cmsg,instance.player.getUUID());
            e.printStackTrace();
        }
    	context.setPacketHandled(true);
	}
    
}
