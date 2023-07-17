package com.forgeessentials.commons.network.packets;

import java.util.function.Supplier;

import com.forgeessentials.commons.network.IFEPacket;
import com.forgeessentials.commons.network.NetworkUtils;

import net.minecraft.network.PacketBuffer;
import net.minecraftforge.fml.network.NetworkEvent;

public class Packet10ClientTransfer implements IFEPacket
{
    String destinationAddress;
    String fallbackAddress;
    
    public Packet10ClientTransfer(){}

    public Packet10ClientTransfer(String destinationAddress, String fallbackAddress){
        this.destinationAddress = destinationAddress;
        this.fallbackAddress = fallbackAddress;
    }

    public static Packet10ClientTransfer decode(PacketBuffer buf)
    {
        return new Packet10ClientTransfer(buf.readUtf(), buf.readUtf());
    }

    @Override
    public void encode(PacketBuffer buf)
    {
        buf.writeUtf(destinationAddress);
        buf.writeUtf(fallbackAddress);
    }

    @Override
    public void handle(NetworkEvent.Context context)
    {
        NetworkUtils.feletworklog.warn("Packet10Redirect was not handled properly");
    }

    public static void handler(final Packet10ClientTransfer message, Supplier<NetworkEvent.Context> ctx)
    {
        NetworkUtils.feletworklog.info("Recieved Packet10Redirect");
        ctx.get().enqueueWork(() -> message.handle(ctx.get()));
        ctx.get().setPacketHandled(true);
    }
}
