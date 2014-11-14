package com.forgeessentials.commands.network;

import com.forgeessentials.util.FunctionHelper;
import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;
import io.netty.buffer.ByteBuf;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;

public class S5PacketNoclip implements IMessageHandler<S5PacketNoclip, IMessage>, IMessage
{
    @Override public IMessage onMessage(S5PacketNoclip message, MessageContext ctx)
    {
        return null; // the server only sends instructions
    }

    private boolean mode; //true for turn on noclip, false for turn off

    public S5PacketNoclip(){}

    public S5PacketNoclip(boolean mode)
    {
        this.mode = mode;
    }

    @Override
    public void fromBytes(ByteBuf buf){}

    @Override public void toBytes(ByteBuf buf)
    {
        buf.writeBoolean(mode);
    }
}
