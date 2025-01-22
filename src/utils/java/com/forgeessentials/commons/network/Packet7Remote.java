package com.forgeessentials.commons.network;

import io.netty.buffer.ByteBuf;
import net.minecraftforge.fml.common.network.ByteBufUtils;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;

public class Packet7Remote implements IMessage
{
    public String link;

    public Packet7Remote() {}

    public Packet7Remote(String link)
    {
        this.link = link;
    }

    @Override
    public void fromBytes(ByteBuf buf)
    {
        link = ByteBufUtils.readUTF8String(buf);
    }

    @Override
    public void toBytes(ByteBuf buf)
    {
        ByteBufUtils.writeUTF8String(buf, link);
    }
}
