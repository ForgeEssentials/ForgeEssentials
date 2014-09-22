package com.forgeessentials.client.network;

import com.forgeessentials.client.ForgeEssentialsClient;
import com.forgeessentials.client.util.ClientPoint;
import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import io.netty.buffer.ByteBuf;

@SideOnly(Side.CLIENT)
public class C0PacketSelectionUpdate implements IMessageHandler<C0PacketSelectionUpdate.Message, IMessage> {

    @Override
    public IMessage onMessage(C0PacketSelectionUpdate.Message message, MessageContext context)
    {
        return null;
    }

    public static class Message implements IMessage {
        public Message()
        {
        }

        @Override
        public void fromBytes(ByteBuf byteBuf)
        {
            if (byteBuf.readBoolean())
            {
                double x = byteBuf.readDouble();
                double y = byteBuf.readDouble();
                double z = byteBuf.readDouble();

                ForgeEssentialsClient.getInfo().setPoint1(new ClientPoint(x, y, z));
            }
            else
            {
                ForgeEssentialsClient.getInfo().setPoint1(null);
            }

            // podouble 2 available
            if (byteBuf.readBoolean())
            {
                double x = byteBuf.readDouble();
                double y = byteBuf.readDouble();
                double z = byteBuf.readDouble();

                ForgeEssentialsClient.getInfo().setPoint2(new ClientPoint(x, y, z));
            }
            else
            {
                ForgeEssentialsClient.getInfo().setPoint2(null);
            }
        }

        @Override
        public void toBytes(ByteBuf byteBuf)
        {
        } // noop - receiving only
    }

}
