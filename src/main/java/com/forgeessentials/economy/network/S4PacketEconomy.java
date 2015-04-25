package com.forgeessentials.economy.network;

import io.netty.buffer.ByteBuf;
import net.minecraft.entity.player.EntityPlayerMP;

import com.forgeessentials.api.APIRegistry;

import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;

public class S4PacketEconomy implements IMessageHandler<S4PacketEconomy, IMessage>, IMessage
{

    private EntityPlayerMP player;

    @Override
    public IMessage onMessage(S4PacketEconomy message, MessageContext ctx)
    {
        return new S4PacketEconomy(ctx.getServerHandler().playerEntity);
    }

    public S4PacketEconomy(EntityPlayerMP player)
    {
        this.player = player;
    }

    @Override
    public void fromBytes(ByteBuf buf)
    {
        /* do nothing */
    }

    @Override
    public void toBytes(ByteBuf buf)
    {
        buf.writeLong(APIRegistry.economy.getWallet(player).get());
    }

}
