package com.forgeessentials.client.network;

import com.forgeessentials.client.network.C0PacketHandshake.Message;
import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;
import io.netty.buffer.ByteBuf;

public class C0PacketHandshake implements IMessageHandler<Message, IMessage>
{
    @Override public IMessage onMessage(Message message, MessageContext ctx)
    {
        return null;// server
    }

    public static class Message implements IMessage
    {
        public Message()
        {
        }

        @Override
        public void fromBytes(ByteBuf buf)
        {
            System.out.println(buf.readByte());
        }

        @Override public void toBytes(ByteBuf buf)
        {
            buf.writeByte(1);
        }
    }
}
