package com.forgeessentials.commons.network.packets;

import java.util.function.Supplier;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.fmllegacy.network.NetworkEvent;

import com.forgeessentials.commons.network.IFEPacket;
import com.forgeessentials.commons.network.NetworkUtils;

public class Packet07Remote implements IFEPacket
{
    public String link;

    public Packet07Remote(String link)
    {
        this.link = link;
    }

    public static Packet07Remote decode(FriendlyByteBuf buf)
    {
        return new Packet07Remote(buf.readUtf());
    }

    @Override
    public void encode(FriendlyByteBuf buf)
    {
        buf.writeUtf(link);
    }

    @Override
    public void handle(NetworkEvent.Context context){
        NetworkUtils.handleNotHandled(this);
    }

    public static void handler(final Packet07Remote message, Supplier<NetworkEvent.Context> ctx)
    {
        NetworkUtils.handleGetLog(message);
        ctx.get().enqueueWork(() -> message.handle(ctx.get()));
        ctx.get().setPacketHandled(true);
    }
}
