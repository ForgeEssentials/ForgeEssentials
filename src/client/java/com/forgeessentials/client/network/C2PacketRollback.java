package com.forgeessentials.client.network;

import com.forgeessentials.client.ForgeEssentialsClient;
import com.forgeessentials.client.util.ClientPoint;
import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;
import io.netty.buffer.ByteBuf;

public class C2PacketRollback implements IMessageHandler<C2PacketRollback.Message, IMessage> {

    @Override
    public IMessage onMessage(C2PacketRollback.Message message, MessageContext ctx)
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
            byte id = buf.readByte();
            if (id == 0)
            {
                ForgeEssentialsClient.getInfo().rbList.clear();
                System.out.println("Clear list");
            }
            else if (id == 1)
            {
                ForgeEssentialsClient.getInfo().rbList.clear();
                System.out.println("Clear list");
                int amount = buf.readInt();
                for (int i = 0; i < amount; i++)
                {
                    try
                    {
                        ClientPoint p = new ClientPoint(buf.readInt(), buf.readInt(), buf.readInt());
                        System.out.println(p.x + "; " + p.y + "; " + p.z);
                        ForgeEssentialsClient.getInfo().rbList.put(p, buf.readInt());
                    }
                    catch (Exception e)
                    {
                        e.printStackTrace();
                    }
                }
            }

        }

        @Override
        public void toBytes(ByteBuf buf)
        {
        }
    }
}
