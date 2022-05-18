package com.forgeessentials.commons.network.packets;

import io.netty.buffer.ByteBuf;

import java.util.HashSet;
import java.util.Set;

import com.forgeessentials.commons.network.IFEPacket;

import net.minecraft.network.PacketBuffer;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;

public class Packet3PlayerPermissions implements IFEPacket
{

    public boolean reset;

    public Set<Integer> placeIds;

    public Set<Integer> breakIds;

    public Packet3PlayerPermissions()
    {
        placeIds = new HashSet<Integer>();
        breakIds = new HashSet<Integer>();
    }

    public Packet3PlayerPermissions(boolean reset, Set<Integer> placeIds, Set<Integer> breakeIds)
    {
        this.reset = reset;
        this.placeIds = placeIds;
        this.breakIds = breakeIds;
    }

    public void decode(PacketBuffer buf)
    {
        reset = buf.readBoolean();

        int count = buf.readShort();
        for (int i = 0; i < count; i++)
            placeIds.add(buf.readInt());

        count = buf.readShort();
        for (int i = 0; i < count; i++)
            breakIds.add(buf.readInt());
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

}
