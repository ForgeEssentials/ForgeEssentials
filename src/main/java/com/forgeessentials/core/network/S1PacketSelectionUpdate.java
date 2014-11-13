package com.forgeessentials.core.network;

import com.forgeessentials.util.OutputHandler;
import com.forgeessentials.util.PlayerInfo;
import com.forgeessentials.util.selections.Point;
import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;
import io.netty.buffer.ByteBuf;

public class S1PacketSelectionUpdate implements IMessageHandler<S1PacketSelectionUpdate, IMessage>, IMessage {

    @Override
    public IMessage onMessage(S1PacketSelectionUpdate message, MessageContext context)
    {
        return null;
    }

    private PlayerInfo info;

        public S1PacketSelectionUpdate(){}

        public S1PacketSelectionUpdate(PlayerInfo info)
        {
            this.info = info;
        }

        @Override
        public void fromBytes(ByteBuf byteBuf){}

        @Override
        public void toBytes(ByteBuf byteBuf)
        {
            try
            {
                if (info != null && info.getPoint1() != null)
                {
                    Point p1 = info.getPoint1();
                    byteBuf.writeBoolean(true);
                    byteBuf.writeDouble(p1.getX());
                    byteBuf.writeDouble(p1.getY());
                    byteBuf.writeDouble(p1.getZ());
                }
                else
                {
                    byteBuf.writeBoolean(false);
                }

                if (info != null && info.getPoint2() != null)
                {
                    Point p2 = info.getPoint2();
                    byteBuf.writeBoolean(true);
                    byteBuf.writeDouble(p2.getX());
                    byteBuf.writeDouble(p2.getY());
                    byteBuf.writeDouble(p2.getZ());
                }
                else
                {
                    byteBuf.writeBoolean(false);
                }
            }

            catch (Exception e)
            {
                OutputHandler.felog.info("Error creating packet >> " + this.getClass());
            }

        }


}
