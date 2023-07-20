package com.forgeessentials.commons.network.packets;

import java.util.HashSet;
import java.util.Set;
import java.util.function.Supplier;

import com.forgeessentials.commons.network.IFEPacket;
import com.forgeessentials.commons.network.NetworkUtils;

import net.minecraft.network.PacketBuffer;
import net.minecraftforge.fml.network.NetworkEvent;

public class Packet03PlayerPermissions implements IFEPacket
{

    public boolean reset;

    public Set<Integer> placeIds;

    public Set<Integer> breakIds;

    public Packet03PlayerPermissions()
    {
        placeIds = new HashSet<>();
        breakIds = new HashSet<>();
    }

    public Packet03PlayerPermissions(boolean reset, Set<Integer> placeIds, Set<Integer> breakeIds)
    {
        this.reset = reset;
        this.placeIds = placeIds;
        this.breakIds = breakeIds;
    }

    public static Packet03PlayerPermissions decode(PacketBuffer buf)
    {
        boolean reset1 = buf.readBoolean();
        Set<Integer> placeIds1 = new HashSet<>();
        Set<Integer> breakIds1 = new HashSet<>();
        int count = buf.readShort();
        for (int i = 0; i < count; i++)
            placeIds1.add(buf.readInt());

        count = buf.readShort();
        for (int i = 0; i < count; i++)
            breakIds1.add(buf.readInt());
        return new Packet03PlayerPermissions(reset1, placeIds1, breakIds1);
    }

    @Override
    public void encode(PacketBuffer buf)
    {
        buf.writeBoolean(reset);
        if (placeIds != null)
        {
            buf.writeShort(placeIds.size());
            for (int id : placeIds)
                buf.writeInt(id);
        }
        else
            buf.writeShort(0);
        if (breakIds != null)
        {
            buf.writeShort(breakIds.size());
            for (int id : breakIds)
                buf.writeInt(id);
        }
        else
            buf.writeShort(0);
    }

    @Override
    public void handle(NetworkEvent.Context context){
        NetworkUtils.handleNotHandled(this);
    }

    public static void handler(final Packet03PlayerPermissions message, Supplier<NetworkEvent.Context> ctx)
    {
        NetworkUtils.handleGetLog(message);
        ctx.get().enqueueWork(() -> message.handle(ctx.get()));
        ctx.get().setPacketHandled(true);
    }
}
