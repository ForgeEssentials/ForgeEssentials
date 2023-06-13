package com.forgeessentials.commons.network.packets;


import net.minecraft.network.PacketBuffer;
import net.minecraftforge.fml.network.NetworkEvent;

import java.util.function.Supplier;

import com.forgeessentials.commons.network.IFEPacket;
import com.forgeessentials.commons.network.NetworkUtils;


public class Packet6AuthLogin implements IFEPacket
{
    /*
    0 = request to get hash from client (hash will be empty!)
    1 = reply from client with hash (empty if client does not have hash)
    2 = request to put hash in client keystore
    3 = reply from client on keystore save (hash will be either SUCCESS or FAILURE)
     */
    public int mode;

    public String hash;

    public Packet6AuthLogin(int mode, String hash) {
        this.mode = mode;
        this.hash = hash;
    }

    public static Packet6AuthLogin decode(PacketBuffer buf) {
    	return new Packet6AuthLogin(buf.readInt(), buf.readUtf());
    }

    @Override
    public void encode(PacketBuffer buf) {
        buf.writeInt(mode);
        buf.writeUtf(hash);
    }

    @Override
    public void handle(NetworkEvent.Context context) {
        NetworkUtils.feletworklog.warn("Packet6AuthLogin was not handled properly");
    }

    public static void handler(final Packet6AuthLogin message, Supplier<NetworkEvent.Context> ctx)
    {
        NetworkUtils.feletworklog.info("Recieved Packet6AuthLogin");
        ctx.get().enqueueWork(() -> message.handle(ctx.get()));
        ctx.get().setPacketHandled(true);
    }
}