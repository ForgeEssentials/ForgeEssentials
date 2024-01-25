package com.forgeessentials.commons.network.packets;

import java.util.function.Supplier;

import javax.annotation.Nullable;

import com.forgeessentials.commons.network.IFEPacket;
import com.forgeessentials.commons.network.NetworkUtils;
import com.forgeessentials.commons.selections.Selection;

import net.minecraft.network.PacketBuffer;
import net.minecraftforge.fml.network.NetworkEvent;

public class Packet01SelectionUpdate implements IFEPacket
{
    protected Selection selection;

    public Packet01SelectionUpdate(@Nullable Selection sel)
    {
        this.selection = sel;
    }

    public static Packet01SelectionUpdate decode(PacketBuffer byteBuf)
    {
    	//This should never be called on the server and the client has its own handler
    	NetworkUtils.feletworklog.warn("Recieved a deformed Packet01SelectionUpdate on the wrong handler");
        return new Packet01SelectionUpdate(null);
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

    @Nullable
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
