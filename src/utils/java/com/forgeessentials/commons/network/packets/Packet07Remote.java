package com.forgeessentials.commons.network.packets;

import java.util.function.Supplier;

import com.forgeessentials.commons.network.IFEPacket;
import com.forgeessentials.commons.network.NetworkUtils;

import net.minecraft.network.PacketBuffer;
import net.minecraftforge.fml.network.NetworkEvent;

public class Packet07Remote implements IFEPacket
{
    public String link;

    public Packet07Remote(String link)
    {
        this.link = link;
    }

    public static Packet07Remote decode(PacketBuffer buf)
    {
        return new Packet07Remote(buf.readUtf());
    }

    @Override
    public void encode(PacketBuffer buf)
    {
        buf.writeUtf(link);
    }

    @Override
    public void handle(NetworkEvent.Context context){
        NetworkUtils.handleNotHandled(this);
    }

    public static void handler(final Packet07Remote message, Supplier<NetworkEvent.Context> ctx)
    {
        NetworkUtils.handleGetLog(message);
        ctx.get().enqueueWork(() -> message.handle(ctx.get()));
        ctx.get().setPacketHandled(true);
    }
}
