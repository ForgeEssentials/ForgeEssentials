package com.forgeessentials.playerlogger.network;

import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;
import io.netty.buffer.ByteBuf;
import net.minecraft.entity.player.EntityPlayer;

public class S2PacketPlayerLogger implements IMessageHandler<S2PacketPlayerLogger, IMessage>, IMessage
{

    @Override public IMessage onMessage(S2PacketPlayerLogger message, MessageContext ctx)
    {
        return null;
    }

    private EntityPlayer player;

    public S2PacketPlayerLogger(){}

    public S2PacketPlayerLogger(EntityPlayer player)
    {
        this.player = player;
    }

    @Override public void fromBytes(ByteBuf buf){}

    @Override
    public void toBytes(ByteBuf buf)
    {
        buf.writeBoolean(player.getEntityData().getBoolean("lb"));

    }
}
