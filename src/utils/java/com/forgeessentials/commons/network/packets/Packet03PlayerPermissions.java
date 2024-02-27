package com.forgeessentials.commons.network.packets;

import java.util.HashSet;
import java.util.Set;
import java.util.function.Supplier;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.fmllegacy.network.NetworkEvent;

import com.forgeessentials.commons.network.IFEPacket;
import com.forgeessentials.commons.network.NetworkUtils;

public class Packet03PlayerPermissions implements IFEPacket
{

    public boolean reset;

    public Set<String> placeIds;

    public Set<String> breakIds;

    public Packet03PlayerPermissions()
    {
        placeIds = new HashSet<>();
        breakIds = new HashSet<>();
    }

    public Packet03PlayerPermissions(boolean reset, Set<String> placeIds, Set<String> breakeIds)
    {
        this.reset = reset;
        this.placeIds = placeIds;
        this.breakIds = breakeIds;
    }

    public static Packet03PlayerPermissions decode(FriendlyByteBuf buf)
    {
        boolean reset1 = buf.readBoolean();
        Set<String> placeIds1 = new HashSet<>();
        Set<String> breakIds1 = new HashSet<>();
        int count = buf.readShort();
        for (int i = 0; i < count; i++)
            placeIds1.add(buf.readUtf());

        count = buf.readShort();
        for (int i = 0; i < count; i++)
            breakIds1.add(buf.readUtf());
        return new Packet03PlayerPermissions(reset1, placeIds1, breakIds1);
    }

    @Override
    public void encode(FriendlyByteBuf buf)
    {
        buf.writeBoolean(reset);
        if (placeIds != null)
        {
            buf.writeShort(placeIds.size());
            for (String id : placeIds)
                buf.writeUtf(id);
        }
        else
            buf.writeShort(0);
        if (breakIds != null)
        {
            buf.writeShort(breakIds.size());
            for (String id : breakIds)
                buf.writeUtf(id);
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
