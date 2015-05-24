package com.forgeessentials.client.network;

import io.netty.buffer.ByteBuf;

import java.io.IOException;
import java.net.URL;

import javax.imageio.ImageIO;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.texture.DynamicTexture;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.EnumChatFormatting;

import com.forgeessentials.client.core.ClientProxy;

import cpw.mods.fml.client.FMLClientHandler;
import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;

public class C7PacketRemote implements IMessageHandler<C7PacketRemote, IMessage>, IMessage
{

    @Override
    public IMessage onMessage(C7PacketRemote message, MessageContext ctx)
    {
        return null;
    }

    public C7PacketRemote()
    {
    }

    @Override
    public void fromBytes(ByteBuf buf)
    {
        byte[] bytes = new byte[buf.readableBytes()];
        buf.getBytes(buf.readerIndex(), bytes);
        String link = new String(bytes);
        try
        {
            DynamicTexture qrCode = new DynamicTexture(ImageIO.read(new URL(link)));
            ClientProxy.setQRCode(Minecraft.getMinecraft().renderEngine.getDynamicTextureLocation("qr_code", qrCode));
        }
        catch (IOException e)
        {
            ChatComponentText cmsg = new ChatComponentText("Could not load QR Code. " + e.getMessage());
            cmsg.getChatStyle().setColor(EnumChatFormatting.RED);
            FMLClientHandler.instance().getClientPlayerEntity().addChatMessage(cmsg);
            e.printStackTrace();
        }
    }

    @Override
    public void toBytes(ByteBuf buf)
    {
    }

}
