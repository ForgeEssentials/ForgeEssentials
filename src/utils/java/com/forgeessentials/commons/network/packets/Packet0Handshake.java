package com.forgeessentials.commons.network.packets;

import com.forgeessentials.commons.network.IFEPacket;

import net.minecraft.network.PacketBuffer;
import net.minecraftforge.fml.network.NetworkEvent.Context;

public class Packet0Handshake implements IFEPacket
{

    public int key;

    public Packet0Handshake()
    {
    }

    public Packet0Handshake(int key)
    {
        this.key = key;
    }

    public static Packet0Handshake decode(PacketBuffer buf)
    {
        return new Packet0Handshake(buf.readInt());
    }

    @Override
    public void encode(PacketBuffer buf)
    {
        buf.writeInt(key);
    }

    @Override
    public void handle(Context context) {}
}
