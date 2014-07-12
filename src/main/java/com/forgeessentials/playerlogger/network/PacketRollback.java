package com.forgeessentials.playerlogger.network;

import com.forgeessentials.playerlogger.blockChange;
import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;
import io.netty.buffer.ByteBuf;

import java.util.ArrayList;
import java.util.List;

public class PacketRollback implements IMessageHandler<PacketRollback.Message, IMessage> {

    @Override
    public IMessage onMessage(PacketRollback.Message message, MessageContext ctx)
    {
        return null;
    }

    public static class Message implements IMessage {
        private int dim;
        private List<blockChange> changes;

        public Message(int dim, ArrayList<blockChange> changes)
        {
            this.dim = dim;
            this.changes = changes;
        }

        public Message()
        {
        }

        @Override
        public void fromBytes(ByteBuf buf)
        {

        }

        @Override
        public void toBytes(ByteBuf buf)
        {
            if (changes == null)
            {
                buf.writeByte(0);
            }
            else
            {
                buf.writeByte(1);
                buf.writeInt(changes.size());
                System.out.println("Sending " + changes.size());
                for (blockChange bc : changes)
                {
                    if (bc.dim == dim)
                    {
                        System.out.println(bc.toString());
                        buf.writeInt(bc.X);
                        buf.writeInt(bc.Y);
                        buf.writeInt(bc.Z);
                        // True if the change was a placement.
                        buf.writeInt(bc.type);
                    }
                }
            }

        }
    }
}
