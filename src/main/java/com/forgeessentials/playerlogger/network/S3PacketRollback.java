package com.forgeessentials.playerlogger.network;

import io.netty.buffer.ByteBuf;

import java.util.ArrayList;
import java.util.List;

import com.forgeessentials.playerlogger.BlockChange;

import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;

public class S3PacketRollback implements IMessageHandler<S3PacketRollback.Message, IMessage> {

    @Override
    public IMessage onMessage(S3PacketRollback.Message message, MessageContext ctx)
    {
        return null;
    }

    public static class Message implements IMessage {
        private int dim;
        private List<BlockChange> changes;

        public Message(int dim, ArrayList<BlockChange> changes)
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
                for (BlockChange bc : changes)
                {
                    if (bc.getDimension() == dim)
                    {
                        System.out.println(bc.toString());
                        buf.writeInt(bc.getX());
                        buf.writeInt(bc.getY());
                        buf.writeInt(bc.getZ());
                        // True if the change was a placement.
                        buf.writeInt(bc.getType());
                    }
                }
            }

        }
    }
}
