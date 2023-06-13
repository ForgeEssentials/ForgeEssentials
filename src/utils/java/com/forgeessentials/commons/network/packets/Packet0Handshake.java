package com.forgeessentials.commons.network.packets;

import java.util.function.Supplier;

import com.forgeessentials.commons.network.IFEPacket;
import com.forgeessentials.commons.network.NetworkUtils;

import net.minecraft.network.PacketBuffer;
import net.minecraftforge.fml.network.NetworkEvent;

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
    public void handle(NetworkEvent.Context context) {
        NetworkUtils.feletworklog.warn("Packet0Handshake was not handled properly");
    }

    public static void handler(final Packet0Handshake message, Supplier<NetworkEvent.Context> ctx)
    {
        ctx.get().enqueueWork(() -> message.handle(ctx.get()));
        ctx.get().setPacketHandled(true);
    }
}
