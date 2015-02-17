package com.forgeessentials.client.network;

import cpw.mods.fml.client.FMLClientHandler;
import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;
import io.netty.buffer.ByteBuf;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.EnumChatFormatting;

public class C6PacketSpeed implements IMessage, IMessageHandler<C6PacketSpeed, IMessage>
{
    @Override
    public void fromBytes(ByteBuf buf)
    {
        float speed = buf.readFloat();
        FMLClientHandler.instance().getClientPlayerEntity().capabilities.setPlayerWalkSpeed(speed);
        FMLClientHandler.instance().getClientPlayerEntity().capabilities.setFlySpeed(speed);
        FMLClientHandler.instance().getClientPlayerEntity().sendPlayerAbilities();
    }

    @Override
    public void toBytes(ByteBuf buf)
    {

    }

    @Override
    public IMessage onMessage(C6PacketSpeed message, MessageContext ctx)
    {
        ChatComponentText cmsg = new ChatComponentText("Walk/fly speed set to" + FMLClientHandler.instance().getClientPlayerEntity().capabilities.getWalkSpeed());
        cmsg.getChatStyle().setColor(EnumChatFormatting.AQUA);
        FMLClientHandler.instance().getClientPlayerEntity().addChatMessage(cmsg);

        return null;
    }
}
