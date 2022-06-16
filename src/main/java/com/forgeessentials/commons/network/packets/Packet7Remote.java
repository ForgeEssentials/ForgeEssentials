package com.forgeessentials.commons.network.packets;

import net.minecraft.network.PacketBuffer;
import net.minecraftforge.fml.network.NetworkEvent.Context;

import com.forgeessentials.commons.network.IFEPacket;

public class Packet7Remote implements IFEPacket
{
    public String link;

    public Packet7Remote()
    {
    }

    public Packet7Remote(String link)
    {
        this.link = link;
    }

    public static Packet7Remote decode(PacketBuffer buf)
    {
        String link = buf.readUtf();
        return new Packet7Remote(link);
    }

    @Override
    public void encode(PacketBuffer buf)
    {
        buf.writeUtf(link);
    }

    @Override
    public void handle(Context context)
    {
        // TODO Auto-generated method stub
    }
}
