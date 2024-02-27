package com.forgeessentials.commons.network.packets;

import java.util.function.Supplier;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.fmllegacy.network.NetworkEvent;

import com.forgeessentials.commons.network.IFEPacket;
import com.forgeessentials.commons.network.NetworkUtils;
import com.forgeessentials.commons.selections.Point;
import com.forgeessentials.commons.selections.Selection;

public class Packet01SelectionUpdate implements IFEPacket
{
    protected Selection selection;

    public Packet01SelectionUpdate(Selection sel)
    {
        this.selection = sel;
    }

    public static Packet01SelectionUpdate decode(FriendlyByteBuf byteBuf)
    {
        Selection selection = new Selection(byteBuf.readUtf(),
                byteBuf.readBoolean() ? new Point(byteBuf.readDouble(), byteBuf.readDouble(), byteBuf.readDouble())
                        : null,
                byteBuf.readBoolean() ? new Point(byteBuf.readDouble(), byteBuf.readDouble(), byteBuf.readDouble())
                        : null);
        return new Packet01SelectionUpdate(selection);
    }

    @Override
    public void encode(FriendlyByteBuf byteBuf)
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
    public void handle(NetworkEvent.Context context){
        NetworkUtils.handleNotHandled(this);
    }

    public static void handler(final Packet01SelectionUpdate message, Supplier<NetworkEvent.Context> ctx)
    {
        NetworkUtils.handleGetLog(message);
        ctx.get().enqueueWork(() -> message.handle(ctx.get()));
        ctx.get().setPacketHandled(true);
    }
}
