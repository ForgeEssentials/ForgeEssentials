package com.forgeessentials.client.network;

import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;
import io.netty.buffer.ByteBuf;

public class C4PacketEconomy implements IMessageHandler<C4PacketEconomy.Message, IMessage> {

    @Override public IMessage onMessage(C4PacketEconomy.Message message, MessageContext ctx)
    {
        return null;
    }

    public static class Message implements IMessage {

        public Message()
        {
        }

        @Override
        public void fromBytes(ByteBuf buf)
        {
            //GuiEconomy.amount = buf.readInt();
        }

        @Override public void toBytes(ByteBuf buf)
        {
        }
    }
}
