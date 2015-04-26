package com.forgeessentials.core.network;

import io.netty.buffer.ByteBuf;
import net.minecraft.entity.player.EntityPlayerMP;

import com.forgeessentials.commons.selections.Selection;
import com.forgeessentials.util.selections.SelectionHandler;

import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;

public class S1PacketSelectionUpdate implements IMessageHandler<S1PacketSelectionUpdate, IMessage>, IMessage {

    @Override
    public IMessage onMessage(S1PacketSelectionUpdate message, MessageContext context)
    {
        return null;
    }

    private EntityPlayerMP player;

        public S1PacketSelectionUpdate(){}

        public S1PacketSelectionUpdate(EntityPlayerMP player)
        {
            this.player = player;
        }

        @Override
        public void fromBytes(ByteBuf byteBuf){}

        @Override
        public void toBytes(ByteBuf byteBuf)
        {
            Selection sel = SelectionHandler.selectionProvider.getSelection(player);
            if (sel == null)
            {
                byteBuf.writeInt(0);
                byteBuf.writeBoolean(false);
                byteBuf.writeBoolean(false);
                return;
            }
            
            byteBuf.writeInt(sel.getDimension());
            
            if (sel.getStart() != null)
            {
                byteBuf.writeBoolean(true);
                byteBuf.writeDouble(sel.getStart().getX());
                byteBuf.writeDouble(sel.getStart().getY());
                byteBuf.writeDouble(sel.getStart().getZ());
            }
            else
            {
                byteBuf.writeBoolean(false);
            }
            
            if (sel.getEnd() != null)
            {
                byteBuf.writeBoolean(true);
                byteBuf.writeDouble(sel.getEnd().getX());
                byteBuf.writeDouble(sel.getEnd().getY());
                byteBuf.writeDouble(sel.getEnd().getZ());
            }
            else
            {
                byteBuf.writeBoolean(false);
            }
        }

}
