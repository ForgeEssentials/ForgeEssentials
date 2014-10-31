package com.forgeessentials.client.network;

import com.forgeessentials.client.ForgeEssentialsClient;
import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import io.netty.buffer.ByteBuf;
import net.minecraft.entity.player.EntityPlayer;

@SideOnly(Side.CLIENT)
public class C2PacketPlayerLogger implements IMessageHandler<C2PacketPlayerLogger, IMessage>, IMessage
{

    @Override public IMessage onMessage(C2PacketPlayerLogger message, MessageContext ctx)
    {
        return null;
    }

    private EntityPlayer player;

    public C2PacketPlayerLogger(){}

    public C2PacketPlayerLogger(EntityPlayer player)
    {
        this.player = player;
    }

    @Override
    public void fromBytes(ByteBuf buf)
    {
        ForgeEssentialsClient.info.playerLogger = buf.readBoolean();
    }

    @Override
    public void toBytes(ByteBuf buf){}

}
