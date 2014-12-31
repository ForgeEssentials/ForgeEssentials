package com.forgeessentials.client.network;

import io.netty.buffer.ByteBuf;
import cpw.mods.fml.client.FMLClientHandler;
import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.EnumChatFormatting;

public class C5PacketNoclip implements IMessageHandler<C5PacketNoclip, IMessage>, IMessage
{

    @Override
    public void fromBytes(ByteBuf buf)
    {
        FMLClientHandler.instance().getClientPlayerEntity().noClip = buf.readBoolean();
    }

    @Override
    public void toBytes(ByteBuf buf){}

    @Override
    public IMessage onMessage(C5PacketNoclip message, MessageContext ctx)
    {
        ChatComponentText cmsg = new ChatComponentText("NoClip " + (FMLClientHandler.instance().getClientPlayerEntity().noClip ? "enabled." : "disabled."));
        cmsg.getChatStyle().setColor(EnumChatFormatting.AQUA);
        FMLClientHandler.instance().getClientPlayerEntity().addChatMessage(cmsg);
        return null;
    }
}
