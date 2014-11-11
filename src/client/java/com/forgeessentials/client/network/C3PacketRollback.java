package com.forgeessentials.client.network;

import com.forgeessentials.client.ForgeEssentialsClient;
import com.forgeessentials.commons.Point;
import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;
import io.netty.buffer.ByteBuf;

public class C3PacketRollback implements IMessageHandler<C3PacketRollback, IMessage>, IMessage
{

    @Override
    public IMessage onMessage(C3PacketRollback message, MessageContext ctx)
    {
        return null;
    }

    public C3PacketRollback(){}

    @Override
    public void fromBytes(ByteBuf buf)
    {
        byte id = buf.readByte();
        if (id == 0)
        {
            ForgeEssentialsClient.info.rbList.clear();
            System.out.println("Clear list");
        }
        else if (id == 1)
        {
            ForgeEssentialsClient.info.rbList.clear();
            System.out.println("Clear list");
            int amount = buf.readInt();
            for (int i = 0; i < amount; i++)
            {
                try
                {
                    Point p = new Point(buf.readInt(), buf.readInt(), buf.readInt());
                    System.out.println(p.x + "; " + p.y + "; " + p.z);
                    ForgeEssentialsClient.info.rbList.put(p, buf.readInt());
                }
                catch (Exception e)
                {
                    e.printStackTrace();
                }
            }
        }

    }

    @Override
    public void toBytes(ByteBuf buf){}

}
