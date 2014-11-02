package com.forgeessentials.client.network;

import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;
import io.netty.buffer.ByteBuf;

public class C0PacketHandshake implements IMessageHandler<C0PacketHandshake, IMessage>, IMessage
{
    public C0PacketHandshake(){
        System.out.println("Sending handshake packet");
    }

    @Override
    public IMessage onMessage(C0PacketHandshake message, MessageContext ctx)
    {
        return null;// server
    }

    @Override
    public void fromBytes(ByteBuf buf)
    {
        System.out.println(buf.readByte());
    }

    @Override
    public void toBytes(ByteBuf buf)
    {
        buf.writeByte(1);
    }

}
