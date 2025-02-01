package com.forgeessentials.client.handler;

import java.io.IOException;
import java.net.URL;

import javax.imageio.ImageIO;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.texture.DynamicTexture;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.event.RenderWorldLastEvent;
import net.minecraftforge.fml.client.FMLClientHandler;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import org.lwjgl.opengl.GL11;

import com.forgeessentials.commons.network.Packet7Remote;

@SideOnly(value = Side.CLIENT)
public class QRRenderer implements IMessageHandler<Packet7Remote, IMessage>
{

    private static ResourceLocation qrCode;

    @SubscribeEvent
    public void render(RenderWorldLastEvent event)
    {
        EntityPlayer player = FMLClientHandler.instance().getClient().thePlayer;
        if (player == null)
            return;
        Minecraft mc = Minecraft.getMinecraft();
        if (mc.currentScreen != null && qrCode != null)
        {
            mc.entityRenderer.setupOverlayRendering();
            mc.renderEngine.bindTexture(qrCode);
            GL11.glPushMatrix();
            GL11.glScalef(0.5F, 0.5F, 0);
            GL11.glColor4f(1, 1, 1, 1);
            mc.currentScreen.drawTexturedModalRect(0, 0, 0, 0, 256, 256);
            GL11.glPopMatrix();
        }
        else if (qrCode != null)
        {
            mc.renderEngine.deleteTexture(qrCode);
            qrCode = null;
        }
    }

    @Override
    public IMessage onMessage(Packet7Remote message, MessageContext ctx)
    {
        try
        {
            DynamicTexture qrCodeTexture = new DynamicTexture(ImageIO.read(new URL(message.link)));
            qrCode = Minecraft.getMinecraft().renderEngine.getDynamicTextureLocation("qr_code", qrCodeTexture);
        }
        catch (IOException e)
        {
            ChatComponentText cmsg = new ChatComponentText("Could not load QR Code. " + e.getMessage());
            cmsg.getChatStyle().setColor(EnumChatFormatting.RED);
            FMLClientHandler.instance().getClientPlayerEntity().addChatMessage(cmsg);
            e.printStackTrace();
        }
        return null;
    }
    
}
