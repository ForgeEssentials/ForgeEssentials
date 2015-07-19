package com.forgeessentials.client.network;

import net.minecraft.util.ChatComponentText;
import net.minecraft.util.EnumChatFormatting;

import com.forgeessentials.commons.network.Packet5Noclip;

import net.minecraftforge.fml.client.FMLClientHandler;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

public class C5HandlerNoclip implements IMessageHandler<Packet5Noclip, IMessage>
{

    @Override
    public IMessage onMessage(Packet5Noclip message, MessageContext ctx)
    {
        FMLClientHandler.instance().getClientPlayerEntity().noClip = message.getNoclip();
        ChatComponentText cmsg = new ChatComponentText("NoClip " + (FMLClientHandler.instance().getClientPlayerEntity().noClip ? "enabled." : "disabled."));
        cmsg.getChatStyle().setColor(EnumChatFormatting.AQUA);
        FMLClientHandler.instance().getClientPlayerEntity().addChatMessage(cmsg);
        return null;
    }
    
}
