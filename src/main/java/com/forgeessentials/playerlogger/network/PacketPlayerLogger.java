package com.forgeessentials.playerlogger.network;

import io.netty.buffer.ByteBuf;
import net.minecraft.entity.player.EntityPlayer;
import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;

public class PacketPlayerLogger implements IMessageHandler<PacketPlayerLogger.Message, IMessage> {

    @Override public IMessage onMessage(PacketPlayerLogger.Message message, MessageContext ctx)
    {
        return null;
    }

    public static class Message implements IMessage {
        private EntityPlayer player;

        public Message()
        {
        }

        public Message(EntityPlayer player)
        {
            this.player = player;
        }

        @Override public void fromBytes(ByteBuf buf)
        {
        }

        @Override
        public void toBytes(ByteBuf buf)
        {
            buf.writeBoolean(player.getEntityData().getBoolean("lb"));

        }
    }
}
