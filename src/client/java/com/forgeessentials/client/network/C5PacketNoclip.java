package com.forgeessentials.client.network;

import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;
import io.netty.buffer.ByteBuf;
import net.minecraft.client.Minecraft;

public class C5PacketNoclip implements IMessageHandler<C5PacketNoclip, IMessage>, IMessage
{

    @Override
    public void fromBytes(ByteBuf buf)
    {
        Minecraft.getMinecraft().thePlayer.noClip = buf.readBoolean();
    }

    @Override
    public void toBytes(ByteBuf buf){}

    @Override
    public IMessage onMessage(C5PacketNoclip message, MessageContext ctx)
    {
        return null;
    }
}
