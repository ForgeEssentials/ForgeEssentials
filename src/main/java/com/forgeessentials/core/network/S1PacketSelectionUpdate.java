package com.forgeessentials.core.network;

import com.forgeessentials.commons.selections.Point;
import com.forgeessentials.util.OutputHandler;
import com.forgeessentials.util.PlayerInfo;
import com.forgeessentials.util.selections.SelectionHandler;
import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;
import io.netty.buffer.ByteBuf;
import net.minecraft.entity.player.EntityPlayerMP;

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
            try
            {
                if (player != null && SelectionHandler.selectionProvider.getPoint1(player) != null)
                {
                    Point p1 = SelectionHandler.selectionProvider.getPoint1(player);
                    byteBuf.writeBoolean(true);
                    byteBuf.writeDouble(p1.getX());
                    byteBuf.writeDouble(p1.getY());
                    byteBuf.writeDouble(p1.getZ());
                }
                else
                {
                    byteBuf.writeBoolean(false);
                }

                if (player != null && SelectionHandler.selectionProvider.getPoint2(player) != null)
                {
                    Point p2 = SelectionHandler.selectionProvider.getPoint2(player);
                    byteBuf.writeBoolean(true);
                    byteBuf.writeDouble(p2.getX());
                    byteBuf.writeDouble(p2.getY());
                    byteBuf.writeDouble(p2.getZ());
                }
                else
                {
                    byteBuf.writeBoolean(false);
                }
            }

            catch (Exception e)
            {
                OutputHandler.felog.info("Error creating packet >> " + this.getClass());
            }

        }


}
