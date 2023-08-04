package com.forgeessentials.commons.network.packets;

import java.util.function.Supplier;

import com.forgeessentials.commons.network.IFEPacket;
import com.forgeessentials.commons.network.NetworkUtils;

import net.minecraft.network.PacketBuffer;
import net.minecraftforge.fml.network.NetworkEvent;

public class Packet08AuthReply implements IFEPacket
{
    /*
     * reply from client with hash (empty if client does not have hash)
     */
    public String hash;

    public Packet08AuthReply(String hash)
    {
        this.hash = hash;
    }

    public static Packet08AuthReply decode(PacketBuffer buf)
    {
        return new Packet08AuthReply(buf.readUtf());
    }

    @Override
    public void encode(PacketBuffer buf)
    {
        buf.writeUtf(hash);
    }

    @Override
    public void handle(NetworkEvent.Context context){
        NetworkUtils.handleNotHandled(this);
    }

    public static void handler(final Packet08AuthReply message, Supplier<NetworkEvent.Context> ctx)
    {
        NetworkUtils.handleGetLog(message);
        ctx.get().enqueueWork(() -> message.handle(ctx.get()));
        ctx.get().setPacketHandled(true);
    }
}