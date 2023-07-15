package com.forgeessentials.commons.network.packets;

import java.util.function.Supplier;

import com.forgeessentials.commons.network.IFEPacket;
import com.forgeessentials.commons.network.NetworkUtils;

import net.minecraft.network.PacketBuffer;
import net.minecraftforge.fml.network.NetworkEvent;

public class Packet00Handshake implements IFEPacket
{

    public Packet00Handshake()
    {
    }

    public static Packet00Handshake decode(PacketBuffer buf)
    {
        return new Packet00Handshake();
    }

    @Override
    public void encode(PacketBuffer buf)
    {
    }

    @Override
    public void handle(NetworkEvent.Context context)
    {
        NetworkUtils.feletworklog.warn("Packet0Handshake was not handled properly");
    }

    public static void handler(final Packet00Handshake message, Supplier<NetworkEvent.Context> ctx)
    {
        NetworkUtils.feletworklog.info("Recieved Packet0Handshake");
        ctx.get().enqueueWork(() -> message.handle(ctx.get()));
        ctx.get().setPacketHandled(true);
    }
}
