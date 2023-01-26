package com.forgeessentials.commons.network.packets;

import com.forgeessentials.commons.network.IFEPacket;
import com.forgeessentials.commons.selections.Selection;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.fml.network.NetworkEvent.Context;

public class Packet1SelectionUpdate implements IFEPacket
{
    protected Selection sel;

    public Packet1SelectionUpdate() {}

    public Packet1SelectionUpdate(Selection sel)
    {
        this.sel = sel;
    }

    public static Packet1SelectionUpdate decode(PacketBuffer byteBuf)
    {
    	/* Since dimensions are not by int anymore, i can't receive by int.
    	Selection selection = new Selection(
                byteBuf.readUtf(),
                byteBuf.readBoolean() ? new Point(byteBuf.readDouble(), byteBuf.readDouble(), byteBuf.readDouble()) : null,
                byteBuf.readBoolean() ? new Point(byteBuf.readDouble(), byteBuf.readDouble(), byteBuf.readDouble()) : null);
        */
    	Selection selection = new Selection(null,null,null);
        return new Packet1SelectionUpdate(selection);
    }

    @Override
    public void encode(PacketBuffer byteBuf)
    {
        if (sel == null)
        {
            byteBuf.writeBoolean(false);
            byteBuf.writeBoolean(false);
            return;
        }
        String s = sel.getDimension().toString();
        byteBuf.writeUtf(s);

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

	@Override
	public void handle(Context context) {
		// TODO Auto-generated method stub
	}
}
