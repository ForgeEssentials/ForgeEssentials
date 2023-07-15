package com.forgeessentials.commons.network.packets;

import java.util.function.Supplier;

import com.forgeessentials.commons.network.IFEPacket;
import com.forgeessentials.commons.network.NetworkUtils;

import net.minecraft.network.PacketBuffer;
import net.minecraftforge.fml.network.NetworkEvent;

public class Packet09AuthRequest implements IFEPacket
{
    /*
     * request to put hash in client keystore
     */
    public String hash;

    public Packet09AuthRequest(String hash)
    {
        this.hash = hash;
    }

    public static Packet09AuthRequest decode(PacketBuffer buf)
    {
        return new Packet09AuthRequest(buf.readUtf());
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

    public static void handler(final Packet09AuthRequest message, Supplier<NetworkEvent.Context> ctx)
    {
        NetworkUtils.feletworklog.info("Recieved Packet9AuthRequest");
        ctx.get().enqueueWork(() -> message.handle(ctx.get()));
        ctx.get().setPacketHandled(true);
    }
}