package com.forgeessentials.commons.network;

import cpw.mods.fml.common.network.simpleimpl.IMessage;
import io.netty.buffer.ByteBuf;

public class Packet6Speed implements IMessage
{

    private float speed;

    public Packet6Speed(){}

    public Packet6Speed(float speed)
    {
        this.speed = speed;

    }

    @Override
    public void fromBytes(ByteBuf buf)
    {
        float speed = buf.readFloat();
    }

    @Override
    public void toBytes(ByteBuf buf)
    {
        buf.writeFloat(speed);
    }

    public float getSpeed()
{
    return speed;
}

}
