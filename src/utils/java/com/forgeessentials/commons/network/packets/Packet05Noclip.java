package com.forgeessentials.commons.network.packets;

import java.util.function.Supplier;

import com.forgeessentials.commons.network.IFEPacket;
import com.forgeessentials.commons.network.NetworkUtils;

import net.minecraft.network.PacketBuffer;
import net.minecraftforge.fml.network.NetworkEvent;

public class Packet05Noclip implements IFEPacket
{
    public boolean noclip;

    public Packet05Noclip(boolean noclip)
    {
        this.noclip = noclip;
    }

    public static Packet05Noclip decode(PacketBuffer buf)
    {
        return new Packet05Noclip(buf.readBoolean());
    }

    @Override
    public void encode(PacketBuffer buf)
    {
        buf.writeBoolean(noclip);
    }

    public boolean getNoclip()
    {
        return noclip;
    }

    @Override
    public void handle(NetworkEvent.Context context){
        NetworkUtils.handleNotHandled(this);
    }

    public static void handler(final Packet05Noclip message, Supplier<NetworkEvent.Context> ctx)
    {
        NetworkUtils.handleGetLog(message);
        ctx.get().enqueueWork(() -> message.handle(ctx.get()));
        ctx.get().setPacketHandled(true);
    }
}
