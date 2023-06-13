package com.forgeessentials.commons.network.packets;

import java.util.function.Supplier;

import com.forgeessentials.commons.network.IFEPacket;
import com.forgeessentials.commons.network.NetworkUtils;
import com.forgeessentials.commons.selections.Point;
import com.forgeessentials.commons.selections.Selection;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.fml.network.NetworkEvent;

public class Packet1SelectionUpdate implements IFEPacket
{
    protected Selection selection;

    public Packet1SelectionUpdate() {}

    public Packet1SelectionUpdate(Selection sel)
    {
        this.selection = sel;
    }

    public static Packet1SelectionUpdate decode(PacketBuffer byteBuf)
    {
    	Selection selection = new Selection(
                byteBuf.readUtf(),
                byteBuf.readBoolean() ? new Point(byteBuf.readDouble(), byteBuf.readDouble(), byteBuf.readDouble()) : null,
                byteBuf.readBoolean() ? new Point(byteBuf.readDouble(), byteBuf.readDouble(), byteBuf.readDouble()) : null);
        return new Packet1SelectionUpdate(selection);
    }

    @Override
    public void encode(PacketBuffer byteBuf)
    {
        if (selection == null)
        {
            byteBuf.writeBoolean(false);
            byteBuf.writeBoolean(false);
            return;
        }
        byteBuf.writeUtf(selection.getDimension());

        if (selection.getStart() != null)
        {
            byteBuf.writeBoolean(true);
            byteBuf.writeDouble(selection.getStart().getX());
            byteBuf.writeDouble(selection.getStart().getY());
            byteBuf.writeDouble(selection.getStart().getZ());
        }
        else
        {
            byteBuf.writeBoolean(false);
        }

        if (selection.getEnd() != null)
        {
            byteBuf.writeBoolean(true);
            byteBuf.writeDouble(selection.getEnd().getX());
            byteBuf.writeDouble(selection.getEnd().getY());
            byteBuf.writeDouble(selection.getEnd().getZ());
        }
        else
        {
            byteBuf.writeBoolean(false);
        }
    }

    public Selection getSelection()
    {
        return selection;
    }

    @Override
    public void handle(NetworkEvent.Context context) {
        NetworkUtils.feletworklog.warn("Packet1SelectionUpdate was not handled properly");
    }

    public static void handler(final Packet1SelectionUpdate message, Supplier<NetworkEvent.Context> ctx)
    {
        ctx.get().enqueueWork(() -> message.handle(ctx.get()));
        ctx.get().setPacketHandled(true);
    }
}
