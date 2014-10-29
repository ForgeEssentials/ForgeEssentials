package com.forgeessentials.core.network;

import com.forgeessentials.core.network.S0PacketHandshake.Message;
import com.forgeessentials.util.PlayerInfo;
import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;
import io.netty.buffer.ByteBuf;

public class S0PacketHandshake implements IMessageHandler<Message, IMessage>
{
    @Override public IMessage onMessage(Message message, MessageContext ctx)
    {
        System.out.println("Received handshake packet");
        PlayerInfo.getPlayerInfo(ctx.getServerHandler().playerEntity).setHasFEClient(true);
        return null;// server
    }

    public static class Message implements IMessage
    {
        private boolean mode;

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
