package com.forgeessentials.commons.network.packets;

import java.util.function.Supplier;

import com.forgeessentials.commons.network.IFEPacket;
import com.forgeessentials.commons.network.NetworkUtils;

import net.minecraft.network.PacketBuffer;
import net.minecraftforge.fml.network.NetworkEvent;

public class Packet06AuthLogin implements IFEPacket
{
    /*
     * request to get hash from client
     */

    public static Packet06AuthLogin decode(PacketBuffer buf)
    {
        return new Packet06AuthLogin();
    }

    @Override
    public void encode(PacketBuffer buf)
    {
    }

    @Override
    public void handle(NetworkEvent.Context context){
        NetworkUtils.handleNotHandled(this);
    }

    public static void handler(final Packet06AuthLogin message, Supplier<NetworkEvent.Context> ctx)
    {
        NetworkUtils.handleGetLog(message);
        ctx.get().enqueueWork(() -> message.handle(ctx.get()));
        ctx.get().setPacketHandled(true);
    }
}