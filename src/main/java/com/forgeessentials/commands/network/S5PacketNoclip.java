package com.forgeessentials.commands.network;

import com.forgeessentials.commands.network.S5PacketNoclip.Message;
import com.forgeessentials.util.FunctionHelper;
import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;
import io.netty.buffer.ByteBuf;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;

public class S5PacketNoclip implements IMessageHandler<Message, IMessage>
{
    @Override public IMessage onMessage(Message message, MessageContext ctx)
    {
        return null; // the server only sends instructions
    }

    public static class Message implements IMessage
    {
        private boolean mode; //true for turn on noclip, false for turn off

        public Message()
        {
        }

        protected Message(boolean mode)
        {
            this.mode = mode;
        }

        @Override
        public void fromBytes(ByteBuf buf)
        {
        }

        @Override public void toBytes(ByteBuf buf)
        {
            buf.writeBoolean(mode);
        }
    }

    public static void setPlayerNoclipStatus(EntityPlayer player, boolean status)
    {
        FunctionHelper.netHandler.sendTo(new Message(status), (EntityPlayerMP)player);
    }
}
