package com.forgeessentials.economy.network;

import io.netty.buffer.ByteBuf;

import java.util.UUID;

import com.forgeessentials.api.APIRegistry;

import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;

public class S4PacketEconomy implements IMessageHandler<S4PacketEconomy, IMessage>, IMessage
{

    @Override public IMessage onMessage(S4PacketEconomy message, MessageContext ctx)
    {
        return new S4PacketEconomy(ctx.getServerHandler().playerEntity.getPersistentID());
    }

    private UUID player;

    public S4PacketEconomy(){}

    public S4PacketEconomy(UUID player)
    {
        this.player = player;
    }

    @Override
    public void fromBytes(ByteBuf buf){}

    @Override public void toBytes(ByteBuf buf)
    {
        buf.writeLong(APIRegistry.wallet.getWallet(player));
    }

}

