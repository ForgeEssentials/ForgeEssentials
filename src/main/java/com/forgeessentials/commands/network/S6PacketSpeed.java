package com.forgeessentials.commands.network;

import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;
import io.netty.buffer.ByteBuf;

public class S6PacketSpeed implements IMessage, IMessageHandler<S6PacketSpeed, IMessage>
{
    private float speed;

    public S6PacketSpeed(){}

    public S6PacketSpeed(float speed)
    {
        this.speed = speed;
    }

    @Override
    public void fromBytes(ByteBuf buf)
    {

    }

    @Override
    public void toBytes(ByteBuf buf)
    {
        buf.writeFloat(speed);
    }

    @Override
    public IMessage onMessage(S6PacketSpeed message, MessageContext ctx)
    {
        return null;
    }
}
