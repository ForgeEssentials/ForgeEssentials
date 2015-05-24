package com.forgeessentials.client.network;

import com.forgeessentials.commons.network.Packet6Speed;
import cpw.mods.fml.client.FMLClientHandler;
import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;
import io.netty.buffer.ByteBuf;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.EnumChatFormatting;

public class C6HandlerSpeed implements IMessageHandler<Packet6Speed, IMessage>
{

    @Override
    public IMessage onMessage(Packet6Speed message, MessageContext ctx)
    {
        float speed = message.getSpeed();

        // special reset code
        if (speed == 0.0F)
        {
            FMLClientHandler.instance().getClientPlayerEntity().capabilities.setPlayerWalkSpeed(0.05F);
            FMLClientHandler.instance().getClientPlayerEntity().capabilities.setFlySpeed(0.1F);
            FMLClientHandler.instance().getClientPlayerEntity().sendPlayerAbilities();
        }
        else
        {
            FMLClientHandler.instance().getClientPlayerEntity().capabilities.setPlayerWalkSpeed(speed);
            FMLClientHandler.instance().getClientPlayerEntity().capabilities.setFlySpeed(speed);
            FMLClientHandler.instance().getClientPlayerEntity().sendPlayerAbilities();
        }

        ChatComponentText cmsg = new ChatComponentText("Walk/fly speed set to" + FMLClientHandler.instance().getClientPlayerEntity().capabilities.getWalkSpeed());
        cmsg.getChatStyle().setColor(EnumChatFormatting.AQUA);
        FMLClientHandler.instance().getClientPlayerEntity().addChatMessage(cmsg);

        return null;
    }
}
