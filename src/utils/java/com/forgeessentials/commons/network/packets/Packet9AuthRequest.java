package com.forgeessentials.commons.network.packets;

import java.util.function.Supplier;

import com.forgeessentials.commons.network.IFEPacket;
import com.forgeessentials.commons.network.NetworkUtils;

import net.minecraft.network.PacketBuffer;
import net.minecraftforge.fml.network.NetworkEvent;

public class Packet9AuthRequest implements IFEPacket
{
    /*
     * request to put hash in client keystore
     */
    public String hash;

    public Packet9AuthRequest(String hash)
    {
        this.hash = hash;
    }

    public static Packet9AuthRequest decode(PacketBuffer buf)
    {
        return new Packet9AuthRequest(buf.readUtf());
    }

    @Override
    public void encode(PacketBuffer buf)
    {
        buf.writeUtf(hash);
    }

    @Override
    public void handle(NetworkEvent.Context context)
    {
        NetworkUtils.feletworklog.warn("Packet9AuthRequest was not handled properly");
    }

    public static void handler(final Packet9AuthRequest message, Supplier<NetworkEvent.Context> ctx)
    {
        NetworkUtils.feletworklog.info("Recieved Packet9AuthRequest");
        ctx.get().enqueueWork(() -> message.handle(ctx.get()));
        ctx.get().setPacketHandled(true);
    }
}