package com.forgeessentials.commons.network;

import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import io.netty.buffer.ByteBuf;

public class Packet5Noclip implements IMessage
{
    private boolean noclip;

    public Packet5Noclip(){}

    public Packet5Noclip(boolean noclip)
    {
        this.noclip = noclip;
    }

    @Override
    public void fromBytes(ByteBuf buf)
    {
        noclip = buf.readBoolean();
    }

    @Override
    public void toBytes(ByteBuf buf)
    {
        buf.writeBoolean(noclip);
    }

    public boolean getNoclip()
    {
        return noclip;
    }
}
