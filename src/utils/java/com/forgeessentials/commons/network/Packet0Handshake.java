package com.forgeessentials.commons.network;

import io.netty.buffer.ByteBuf;
import cpw.mods.fml.common.network.simpleimpl.IMessage;

public class Packet0Handshake implements IMessage
{
    
    public Packet0Handshake() {}

    @Override
    public void fromBytes(ByteBuf buf)
    {
        /* do nothing */
    }

    @Override
    public void toBytes(ByteBuf buf)
    {
        /* do nothing */
    }
    
}
