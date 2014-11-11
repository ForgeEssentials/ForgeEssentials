package com.forgeessentials.client.network;

import com.forgeessentials.client.ForgeEssentialsClient;
import com.forgeessentials.commons.Point;
import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import io.netty.buffer.ByteBuf;

@SideOnly(Side.CLIENT)
public class C1PacketSelectionUpdate implements IMessageHandler<C1PacketSelectionUpdate, IMessage>, IMessage
{

    @Override
    public IMessage onMessage(C1PacketSelectionUpdate message, MessageContext context)
    {
        return null;
    }

    @Override
    public void fromBytes(ByteBuf byteBuf)
    {
        if (byteBuf.readBoolean())
        {
            double x = byteBuf.readDouble();
            double y = byteBuf.readDouble();
            double z = byteBuf.readDouble();

            ForgeEssentialsClient.info.setPoint1(new Point(x, y, z));
        }
        else
        {
            ForgeEssentialsClient.info.setPoint1(null);
        }

        // podouble 2 available
        if (byteBuf.readBoolean())
        {
            double x = byteBuf.readDouble();
            double y = byteBuf.readDouble();
            double z = byteBuf.readDouble();

            ForgeEssentialsClient.info.setPoint2(new Point(x, y, z));
        }
        else
        {
            ForgeEssentialsClient.info.setPoint2(null);
        }
    }

    @Override
    public void toBytes(ByteBuf byteBuf){} // noop - receiving only

}
