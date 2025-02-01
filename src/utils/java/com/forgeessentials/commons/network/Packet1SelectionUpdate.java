package com.forgeessentials.commons.network;

import com.forgeessentials.commons.selections.Point;
import com.forgeessentials.commons.selections.Selection;
import io.netty.buffer.ByteBuf;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;

public class Packet1SelectionUpdate implements IMessage
{
    private Selection sel;

    public Packet1SelectionUpdate() {}

    public Packet1SelectionUpdate(Selection sel)
    {
        this.sel = sel;
    }

    @Override
    public void fromBytes(ByteBuf byteBuf)
    {
        sel = new Selection(
                byteBuf.readInt(),
                byteBuf.readBoolean() ? new Point(byteBuf.readDouble(), byteBuf.readDouble(), byteBuf.readDouble()) : null,
                byteBuf.readBoolean() ? new Point(byteBuf.readDouble(), byteBuf.readDouble(), byteBuf.readDouble()) : null);
    }

    @Override
    public void toBytes(ByteBuf byteBuf)
    {
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

    public Selection getSelection()
    {
        return sel;
    }
}
