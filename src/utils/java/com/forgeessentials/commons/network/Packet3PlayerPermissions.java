package com.forgeessentials.commons.network;

import io.netty.buffer.ByteBuf;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;

import java.util.HashSet;
import java.util.Set;

public class Packet3PlayerPermissions implements IMessage
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

    @Override
    public void fromBytes(ByteBuf buf)
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
    public void toBytes(ByteBuf buf)
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
