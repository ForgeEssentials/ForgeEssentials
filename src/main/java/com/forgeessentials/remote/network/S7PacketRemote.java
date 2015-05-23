package com.forgeessentials.remote.network;

import io.netty.buffer.ByteBuf;
import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;

public class S7PacketRemote implements IMessageHandler<S7PacketRemote, IMessage>, IMessage
{

    private byte[] url;

    @Override
    public IMessage onMessage(S7PacketRemote message, MessageContext ctx)
    {
        return null;
    }

    public S7PacketRemote()
    {
    }

    public S7PacketRemote(String url)
    {
        this.url = url.getBytes();
    }

    @Override
    public void fromBytes(ByteBuf buf)
    {
    }

    @Override
    public void toBytes(ByteBuf buf)
    {
        buf.writeBytes(url);
    }

}
