package com.forgeessentials.commons.network.packets;

import net.minecraft.network.PacketBuffer;
import net.minecraftforge.fml.network.NetworkEvent.Context;

import com.forgeessentials.commons.network.IFEPacket;

public class Packet5Noclip implements IFEPacket
{
    public boolean noclip;

    public Packet5Noclip()
    {
    }

    public Packet5Noclip(boolean noclip)
    {
        this.noclip = noclip;
    }

    public static Packet5Noclip decode(PacketBuffer buf)
    {
        return new Packet5Noclip(buf.readBoolean());
    }

    @Override
    public void encode(PacketBuffer buf)
    {
        buf.writeBoolean(noclip);
    }

    public boolean getNoclip()
    {
        return noclip;
    }

    @Override
    public void handle(Context context)
    {
        // TODO Auto-generated method stub
    }
}
