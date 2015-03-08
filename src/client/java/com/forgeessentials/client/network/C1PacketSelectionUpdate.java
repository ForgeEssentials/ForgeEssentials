package com.forgeessentials.client.network;

import io.netty.buffer.ByteBuf;

import com.forgeessentials.client.ForgeEssentialsClient;
import com.forgeessentials.commons.selections.Point;
import com.forgeessentials.commons.selections.Selection;

import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class C1PacketSelectionUpdate implements IMessageHandler<C1PacketSelectionUpdate, IMessage>, IMessage
{

    @Override
    public IMessage onMessage(C1PacketSelectionUpdate message, MessageContext context)
    {
        return null;
    }

    @Override
    public void fromBytes(ByteBuf byteBuf)
    {
        ForgeEssentialsClient.info.setSelection(
            new Selection(
                byteBuf.readInt(), 
                byteBuf.readBoolean() ? new Point(byteBuf.readDouble(), byteBuf.readDouble(), byteBuf.readDouble()) : null,
                byteBuf.readBoolean() ? new Point(byteBuf.readDouble(), byteBuf.readDouble(), byteBuf.readDouble()) : null));
    }

    @Override
    public void toBytes(ByteBuf byteBuf){} // noop - receiving only

}
