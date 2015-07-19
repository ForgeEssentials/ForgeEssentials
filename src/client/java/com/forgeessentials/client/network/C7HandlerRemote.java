package com.forgeessentials.client.network;

import java.io.IOException;
import java.net.URL;

import javax.imageio.ImageIO;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.texture.DynamicTexture;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.EnumChatFormatting;

import com.forgeessentials.client.core.ClientProxy;
import com.forgeessentials.commons.network.Packet7Remote;

import net.minecraftforge.fml.client.FMLClientHandler;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

public class C7HandlerRemote implements IMessageHandler<Packet7Remote, IMessage>
{

    @Override
    public IMessage onMessage(Packet7Remote message, MessageContext ctx)
    {
        try
        {
            DynamicTexture qrCode = new DynamicTexture(ImageIO.read(new URL(message.link)));
            ClientProxy.setQRCode(Minecraft.getMinecraft().renderEngine.getDynamicTextureLocation("qr_code", qrCode));
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
