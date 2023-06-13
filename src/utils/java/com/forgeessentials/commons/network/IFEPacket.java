package com.forgeessentials.commons.network;

import java.util.function.Supplier;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.fml.network.NetworkEvent;

public interface IFEPacket
{

    void handle(NetworkEvent.Context context);

    void encode(PacketBuffer buffer);

    static <PACKET extends IFEPacket> void handler(final PACKET message, Supplier<NetworkEvent.Context> ctx)
    {
        ctx.get().enqueueWork(() -> message.handle(ctx.get()));
        ctx.get().setPacketHandled(true);
    }
}