package com.forgeessentials.core.network;

import io.netty.buffer.ByteBuf;

import com.forgeessentials.util.OutputHandler;
import com.forgeessentials.util.PlayerInfo;
import com.forgeessentials.util.selections.Point;

import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;

public class PacketSelectionUpdate implements IMessageHandler<PacketSelectionUpdate.Message, IMessage> {

    @Override
    public IMessage onMessage(PacketSelectionUpdate.Message message, MessageContext context)
    {
        return null;
    }

    public static class Message implements IMessage {
        private PlayerInfo info;

        public Message()
        {
        }

        public Message(PlayerInfo info)
        {
            this.info = info;
        }

        @Override
        public void fromBytes(ByteBuf byteBuf)
        {
            // noop - sending only
        }

        @Override
        public void toBytes(ByteBuf byteBuf)
        {
            try
            {
                if (info != null && info.getPoint1() != null)
                {
                    Point p1 = info.getPoint1();
                    byteBuf.writeBoolean(true);
                    byteBuf.writeDouble(p1.x);
                    byteBuf.writeDouble(p1.y);
                    byteBuf.writeDouble(p1.z);
                }
                else
                {
                    byteBuf.writeBoolean(false);
                }

                if (info != null && info.getPoint2() != null)
                {
                    Point p2 = info.getPoint2();
                    byteBuf.writeBoolean(true);
                    byteBuf.writeDouble(p2.x);
                    byteBuf.writeDouble(p2.y);
                    byteBuf.writeDouble(p2.z);
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

}
