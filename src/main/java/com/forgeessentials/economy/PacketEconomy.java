package com.forgeessentials.economy;

import com.forgeessentials.api.APIRegistry;
import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;
import io.netty.buffer.ByteBuf;

import java.util.UUID;

public class PacketEconomy implements IMessageHandler<PacketEconomy.Message, IMessage> {

    @Override public IMessage onMessage(PacketEconomy.Message message, MessageContext ctx)
    {
        return new Message(ctx.getServerHandler().playerEntity.getPersistentID());
    }

    public static class Message implements IMessage {

        private UUID player;

        public Message()
        {
        }

        public Message(UUID player)
        {
            this.player = player;
        }

        @Override
        public void fromBytes(ByteBuf buf)
        {
        }

        @Override public void toBytes(ByteBuf buf)
        {
            buf.writeInt(APIRegistry.wallet.getWallet(player));

        }
    }
}

