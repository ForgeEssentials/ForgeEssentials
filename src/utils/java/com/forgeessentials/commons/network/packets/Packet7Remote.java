package com.forgeessentials.commons.network.packets;

import net.minecraft.network.PacketBuffer;
import net.minecraftforge.fml.network.NetworkEvent;

import java.util.function.Supplier;

import com.forgeessentials.commons.network.IFEPacket;
import com.forgeessentials.commons.network.NetworkUtils;

public class Packet7Remote implements IFEPacket
{
    public String link;

    public Packet7Remote(String link)
    {
        this.link = link;
    }

    public static Packet7Remote decode(PacketBuffer buf)
    {
        return new Packet7Remote(buf.readUtf());
    }

    @Override
    public void encode(PacketBuffer buf)
    {
        buf.writeUtf(link);
    }

    @Override
    public void handle(NetworkEvent.Context context) {
        NetworkUtils.feletworklog.warn("Packet7Remote was not handled properly");
    }

    public static void handler(final Packet7Remote message, Supplier<NetworkEvent.Context> ctx)
    {
        ctx.get().enqueueWork(() -> message.handle(ctx.get()));
        ctx.get().setPacketHandled(true);
    }
}
